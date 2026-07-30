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
 *
 * <p>
 * The prompt template itself is NOT defined here - it lives in the
 * {@code prompt_details} collection under the {@code project-hygiene} key and
 * is hydrated by {@code
 * PromptService#getProjectHygienePrompt}. This class only builds the two
 * payloads substituted into that template: the hygiene rules section and the
 * Jira issues JSON.
 */
@Slf4j
@UtilityClass
public class HygienePromptBuilder {

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

			renderedRules.add("Rule " + (renderedRules
					.size() + 1) + "\n" + "  ruleName: " + ruleName + "\n" + "  field: " + label + "\n" + "  criteria: " + ctg
							.getPrompt().trim());
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
			List<CycleTimeGroup> configuredFields, Map<String, String> labelToFieldName, ObjectMapper objectMapper) {
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
				if (ctg == null || ctg.getLabel() == null)
					continue;
				String fieldName = labelToFieldName != null ? labelToFieldName.get(ctg.getLabel()) : null;
				if (fieldName == null || writtenFields.contains(fieldName))
					continue;
				Object value = getFieldValue(ji, fieldName);
				if (value != null) {
					node.set(ctg.getLabel(), objectMapper.valueToTree(value));
					writtenFields.add(fieldName);
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
