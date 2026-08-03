package com.publicissapient.kpidashboard.common.util;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.publicissapient.kpidashboard.common.model.application.dto.CycleTimeGroup;
import com.publicissapient.kpidashboard.common.model.jira.JiraIssue;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Shared utilities for building KPI311 Story Hygiene LLM prompts. Used by both
 * the API service (on-demand, sync path) and the data-processor batch job
 * (scheduled pre-compute path).
 */
@Slf4j
@UtilityClass
public class HygienePromptBuilder {

	/**
	 * Matches the optional {@code [weight]:} prefix a configured rule prompt may
	 * carry, e.g. {@code
	 * [10]: Acceptance criteria must be present} or
	 * {@code [null]: Priority must be set}. DOTALL so multi-line criteria survive.
	 */
	private static final Pattern WEIGHT_PREFIX = Pattern.compile("^\\s*\\[\\s*([^\\]]*?)\\s*\\]\\s*:\\s*(.*)$",
			Pattern.DOTALL);

	/**
	 * Weight applied when a rule declares {@code [null]}, omits the prefix, or
	 * declares an unusable value - the rule then simply counts once, exactly like
	 * every other unweighted rule.
	 */
	public static final double DEFAULT_RULE_WEIGHT = 1d;

	/**
	 * A rule prompt split into its weight and its criteria text.
	 *
	 * @param weight
	 *          the effective weight - always positive, defaulted when not declared
	 * @param criteria
	 *          the criteria text with the {@code [weight]:} prefix stripped
	 * @param explicit
	 *          {@code true} when a usable weight was actually declared
	 */
	public record WeightedCriteria(double weight, String criteria, boolean explicit) {
	}

	/**
	 * Splits a configured prompt of the form {@code [10]: criteria text} into its
	 * weight and criteria parts.
	 *
	 * <p>
	 * {@code [null]}, a missing prefix, or a non-numeric / non-positive value all
	 * mean "no explicit weight" and fall back to {@link #DEFAULT_RULE_WEIGHT}, so
	 * mixing weighted and unweighted rules is safe.
	 */
	public static WeightedCriteria parseWeightedCriteria(String prompt) {
		String raw = prompt == null ? "" : prompt.trim();
		Matcher matcher = WEIGHT_PREFIX.matcher(raw);
		if (!matcher.matches()) {
			// No prefix at all - the whole prompt is the criteria.
			return new WeightedCriteria(DEFAULT_RULE_WEIGHT, raw, false);
		}

		String weightToken = matcher.group(1).trim();
		String criteria = matcher.group(2).trim();

		if (weightToken.isEmpty() || "null".equalsIgnoreCase(weightToken)) {
			return new WeightedCriteria(DEFAULT_RULE_WEIGHT, criteria, false);
		}
		try {
			double weight = Double.parseDouble(weightToken);
			if (weight <= 0) {
				log.warn("kpi311: rule weight '{}' is not positive - defaulting to {}", weightToken, DEFAULT_RULE_WEIGHT);
				return new WeightedCriteria(DEFAULT_RULE_WEIGHT, criteria, false);
			}
			return new WeightedCriteria(weight, criteria, true);
		} catch (NumberFormatException e) {
			log.warn("kpi311: rule weight '{}' is not a number - defaulting to {}", weightToken, DEFAULT_RULE_WEIGHT);
			return new WeightedCriteria(DEFAULT_RULE_WEIGHT, criteria, false);
		}
	}

	/** Renders a weight without a pointless trailing {@code .0}. */
	private static String formatWeight(double weight) {
		return weight == Math.rint(weight) ? String.valueOf((long) weight) : String.valueOf(weight);
	}

	/**
	 * Renders the configured KPI311 rule sets as a numbered, plain-text listing.
	 *
	 * <p>
	 * Every {@link CycleTimeGroup} is an INDEPENDENT rule entry, so the same field
	 * may legitimately carry several rule sets - for example an acceptance-criteria
	 * check and a BDD-definition check both written against {@code description}.
	 * Each entry is emitted separately so the LLM returns one verdict per entry.
	 *
	 * <p>
	 * Rule names must be unique because the downstream per-rule maps (the
	 * drill-down {@code
	 * passedPercentageByRule} and the Excel {@code groupMap}) are keyed by rule
	 * name and would otherwise collapse. A field carrying a single rule keeps its
	 * plain label; a field carrying several is suffixed {@code (1)}, {@code (2)},
	 * ... in declaration order.
	 *
	 * <p>
	 * Each prompt may carry a {@code [weight]:} prefix declaring how much the rule
	 * contributes to the issue's hygiene score - {@code [10]: ...} weights the rule
	 * ten times as heavily as an unweighted one, while {@code [null]: ...} (or no
	 * prefix) falls back to {@link #DEFAULT_RULE_WEIGHT}. The prefix is stripped
	 * here so the LLM only ever sees clean criteria text plus an explicit
	 * {@code weight} line.
	 *
	 * <p>
	 * Plain text (rather than JSON) is used deliberately so user-authored criteria
	 * need no escaping.
	 *
	 * @param cycleTimeGroups
	 *          the jiraFieldsSelectionKPI311 list from field mapping
	 * @return the rules section, or an empty string when nothing is configured
	 */
	public static String buildHygieneRules(List<CycleTimeGroup> cycleTimeGroups) {
		List<CycleTimeGroup> validGroups = cycleTimeGroups == null
				? List.of()
				: cycleTimeGroups.stream()
						.filter(
								ctg -> ctg != null && ctg.getLabel() != null && !ctg.getLabel().isBlank() && ctg.getPrompt() != null)
						.toList();

		if (validGroups.isEmpty()) {
			return "";
		}

		// How many rule sets target each field? Drives the "(n)" disambiguation.
		Map<String, Integer> totalPerLabel = new LinkedHashMap<>();
		validGroups.forEach(ctg -> totalPerLabel.merge(ctg.getLabel(), 1, Integer::sum));

		Map<String, Integer> seenPerLabel = new LinkedHashMap<>();
		List<String> renderedRules = new ArrayList<>();
		for (CycleTimeGroup ctg : validGroups) {
			String label = ctg.getLabel();
			int occurrence = seenPerLabel.merge(label, 1, Integer::sum);
			String ruleName = totalPerLabel.get(label) > 1 ? label + " (" + occurrence + ")" : label;
			WeightedCriteria weighted = parseWeightedCriteria(ctg.getPrompt());

			renderedRules.add("Rule " + (renderedRules
					.size() + 1) + "\n" + "  ruleName: " + ruleName + "\n" + "  field: " + label + "\n" + "  weight: " + formatWeight(
							weighted.weight()) + "\n" + "  criteria: " + weighted.criteria());
		}
		return String.join("\n\n", renderedRules);
	}

	/**
	 * Serialises the slim issue nodes into the JSON array handed to the LLM.
	 *
	 * @return the JSON array string, or {@code null} when serialization fails
	 */
	public static String buildIssuesJson(List<ObjectNode> issueNodes, ObjectMapper objectMapper) {
		try {
			return objectMapper.writeValueAsString(issueNodes);
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize issue nodes for hygiene prompt: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Builds a slim JSON object for one {@link JiraIssue} containing only the
	 * fields the LLM needs. Anchor fields are always written first; configured rule
	 * fields follow, skipping duplicates.
	 */
	public static ObjectNode buildIssueNode(JiraIssue ji, List<String> anchorFieldNames,
			List<CycleTimeGroup> configuredFields, ObjectMapper objectMapper) {
		ObjectNode node = objectMapper.createObjectNode();
		Set<String> writtenFields = new HashSet<>();

		if (anchorFieldNames != null) {
			for (String fieldName : anchorFieldNames) {
				writtenFields.add(fieldName);
				Object value = getFieldValue(ji, fieldName);
				if (value != null) {
					node.set(fieldName, objectMapper.valueToTree(value));
				}
			}
		}

		if (configuredFields != null) {
			for (CycleTimeGroup ctg : configuredFields) {
				if (ctg == null || ctg.getLabel() == null || ctg.getFieldName() == null)
					continue;
				if (writtenFields.contains(ctg.getFieldName()))
					continue;
				Object value = getFieldValue(ji, ctg.getFieldName());
				if (value != null) {
					node.set(ctg.getLabel(), objectMapper.valueToTree(value));
					writtenFields.add(ctg.getFieldName());
				}
			}
		}
		return node;
	}

	/**
	 * Computes a deterministic SHA-256 hash of the KPI311 rule-set. The list is
	 * sorted by label before serialization so reordering rules does not invalidate
	 * the cache.
	 *
	 * @param cycleTimeGroups
	 *          the jiraFieldsSelectionKPI311 list from field mapping
	 * @param objectMapper
	 *          Jackson mapper for serialization
	 * @return hex SHA-256 string, or empty string on error
	 */
	public static String computeRuleSetHash(List<CycleTimeGroup> cycleTimeGroups, ObjectMapper objectMapper) {
		try {
			List<CycleTimeGroup> sorted = cycleTimeGroups == null
					? List.of()
					: cycleTimeGroups.stream().filter(g -> g != null && g.getLabel() != null)
							.sorted(Comparator.comparing(CycleTimeGroup::getLabel)).toList();
			String json = objectMapper.writeValueAsString(sorted);
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(json.getBytes(StandardCharsets.UTF_8));
			StringBuilder hex = new StringBuilder();
			for (byte b : hashBytes) {
				hex.append(String.format("%02x", b));
			}
			return hex.toString();
		} catch (JsonProcessingException | NoSuchAlgorithmException e) {
			log.error("Failed to compute ruleSetHash: {}", e.getMessage());
			return "";
		}
	}

	/**
	 * Maps a Jira priority string to a sort rank (lower = higher priority).
	 * Critical/Highest → 0, High → 1, Medium → 2, Low/Lowest → 3, unknown → 4.
	 */
	public static int priorityRank(String priority) {
		if (priority == null)
			return 4;
		return switch (priority.trim().toLowerCase()) {
			case "critical", "highest" -> 0;
			case "high" -> 1;
			case "medium" -> 2;
			case "low", "lowest" -> 3;
			default -> 4;
		};
	}

	// ── private helpers ──────────────────────────────────────────────────────

	private static Object getFieldValue(JiraIssue issue, String fieldName) {
		try {
			Field f = findDeclaredField(issue.getClass(), fieldName);
			if (f != null) {
				f.setAccessible(true);
				return f.get(issue);
			}
		} catch (IllegalAccessException e) {
			log.debug("kpi311: could not read field '{}' from JiraIssue", fieldName);
		}
		return null;
	}

	private static Field findDeclaredField(Class<?> clazz, String fieldName) {
		while (clazz != null && clazz != Object.class) {
			try {
				return clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException ignored) {
				clazz = clazz.getSuperclass();
			}
		}
		return null;
	}
}
