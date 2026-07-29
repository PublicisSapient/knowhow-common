package com.publicissapient.kpidashboard.common.util;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HashSet;
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
 */
@Slf4j
@UtilityClass
public class HygienePromptBuilder {

	public static final String FINAL_HYGIENE_PROMPT = """
			You are an Expert Story Hygiene Analyzer Agent.
			Your job is to evaluate Jira issues against a Definition-of-Ready (DoR)
			style hygiene checklist and produce a strict, evidence-based verdict
			for each issue.

			=== Non-Negotiable Principles ===
			- Rely STRICTLY on the fields provided in the Jira Issue JSON below.
			- NEVER assume, infer, or fabricate a value that is not present.
			- If evidence is missing, mark the rule as "Failed" (never "Passed").
			- Differentiate REQUESTS from CONFIRMATIONS —
					a request for sign-off is NOT approval.
			- Every verdict MUST cite the exact field name and observed value.

			=== Hygiene Rules ===
			Each rule below names the exact Jira field to evaluate, followed by the
			verdict criteria. Apply every rule independently, in the order given,
			using ONLY the named field.
			%1$s

			=== Jira Issues (JSON array) ===
			%2$s

			=== Per-Rule Verdict Vocabulary ===
			- "Passed"  → rule is fully satisfied by explicit evidence
																	in the listed fields
			- "Failed"  → rule is not met OR required evidence is missing
			- "Partial" → rule is partially met — present but
																	incomplete / unclear / unconfirmed
			- "N/A"     → rule does not apply to this issue type/status
																	per its own criteria

			=== Overall Status Rules ===
			- "READY"     → every applicable rule (i.e. excluding "N/A")
																			has status "Passed"
			- "NOT READY" → any applicable rule is "Failed" or "Partial"

			=== Hygiene Score ===
			- totalApplicableRules = count of rules whose status is not "N/A"
			- passedRules          = count of rules whose status is "Passed"
			- hygieneScore         = passedRules * 100 / totalApplicableRules
																											(if totalApplicableRules == 0 → 100)
			- hygieneGrade         = "GOOD"    when hygieneScore >= 80
																											"AVERAGE" when 50 <= hygieneScore < 80
																											"POOR"    when hygieneScore < 50

			=== Improvement Recommendations ===
			- Provide 3 to 5 short, actionable suggestions that would raise the
					hygiene score for this issue.
			- Each suggestion must reference a specific field or missing evidence.
			- Return them as ONE string with items joined by " | ".

			=== Output Contract ===
			Return a JSON ARRAY — one element per input Jira issue, in the same
			order as the input. No markdown, no prose, no code fences, no trailing
			commentary. Schema per element:
			[
					{
							"issueKey":             "<jiraIssue.number>",
							"issueType":            "<jiraIssue.typeName>",
							"sprintId":             "<jiraIssue.sprintID>",
							"assignee":             "<jiraIssue.assigneeName or 'Unassigned'>",
							"results": [
									{
											"rule":     "<ruleName from map key>",
											"field":    "<jiraIssue field(s) evaluated>",
											"observed": "<actual field value or 'null'>",
											"status":   "Passed | Failed | Partial | N/A",
											"reason":   "<one-line justification citing the observed value>"
									}
							],
							"totalApplicableRules": <int>,
							"passedRules":          <int>,
							"failedRules":          <int>,
							"partialRules":         <int>,
							"hygieneScore":         <int 0-100>,
							"hygieneGrade":         "GOOD | AVERAGE | POOR",
							"overallStatus":        "READY | NOT READY",
							"topFailures":          ["<up to 3 ruleNames of most impactful non-Passed rules>"],
							"recommendations":      "<3-5 fixes joined by ' | '>"
					}
			]

			=== Hard Constraints ===
			- Evaluate EVERY rule in the map for EVERY issue; never skip a rule
					and never skip an issue.
			- status MUST be exactly one of "Passed", "Failed", "Partial", "N/A"
					(case sensitive, spelled exactly).
			- overallStatus MUST be exactly "READY" or "NOT READY".
			- reason MUST cite the exact field name and value observed.
			- Never invent field values that are not present in the input JSON.
			- Return the JSON array and nothing else.
			""";

	/**
	 * Builds the LLM prompt for a list of Jira issues in a single sprint.
	 *
	 * @param prompts
	 *          rule-name → evaluation-criteria map (from field mapping)
	 * @param issueNodes
	 *          serialisable ObjectNode list, one per issue
	 * @param labelToFieldName
	 *          rule-name → Jira field name map; used to inject the field name into
	 *          the rules section so the criteria text itself need not mention it
	 * @param objectMapper
	 *          Jackson mapper for serializing the issue array
	 * @return formatted prompt string ready to send to the AI Gateway
	 */
	public static String buildPrompt(Map<String, String> prompts, List<ObjectNode> issueNodes,
			Map<String, String> labelToFieldName, ObjectMapper objectMapper) {
		try {
			String issuesJson = objectMapper.writeValueAsString(issueNodes);
			String rulesSection = buildRulesSection(prompts, labelToFieldName);
			return String.format(FINAL_HYGIENE_PROMPT, rulesSection, issuesJson);
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize issue nodes for hygiene prompt: {}", e.getMessage());
			return null;
		}
	}

	private static String buildRulesSection(Map<String, String> prompts, Map<String, String> labelToFieldName) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : prompts.entrySet()) {
			String label = entry.getKey();
			String criteria = entry.getValue();
			String fieldName = labelToFieldName != null ? labelToFieldName.get(label) : null;
			sb.append("- ").append(label);
			if (fieldName != null) {
				sb.append(" (field: ").append(fieldName).append(")");
			}
			sb.append(": ").append(criteria).append("\n");
		}
		return sb.toString();
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
