package com.publicissapient.kpidashboard.common.model.jira;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the hygiene evaluation returned by the LLM for a single Jira
 * issue.
 *
 * <p>
 * The LLM produces a JSON ARRAY — one element per input issue. Consumers should
 * deserialize into {@code List<HygieneKpiResponseDTO>}; each element carries
 * the per-rule verdict, aggregated counts / score / grade, overall readiness,
 * top failures, and pipe-separated improvement recommendations.
 *
 * <p>
 * String-typed enum-like fields are intentionally kept as {@link String} to be
 * forgiving of minor LLM formatting drift. Expected vocabulary:
 *
 * <ul>
 * <li>{@code results[].status} —
 * {@code "Passed" | "Failed" | "Partial" | "N/A"}
 * <li>{@code hygieneGrade} — {@code "GOOD" | "AVERAGE" | "POOR"}
 * <li>{@code overallStatus} — {@code "READY" | "NOT READY"}
 * </ul>
 */
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class HygieneKpiResponseDTO {

	private String issueKey;
	private String issueType;
	private String sprintId;
	private String assignee;
	private List<RuleResult> results;
	private Integer totalApplicableRules;
	private Integer passedRules;
	private Integer failedRules;
	private Integer partialRules;
	private Integer hygieneScore;
	private String hygieneGrade;
	private String overallStatus;
	private List<String> topFailures;
	private String recommendations;

	/** One row of the per-rule breakdown produced by the LLM. */
	@Data
	@Builder
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RuleResult {

		private String rule;
		private String field;
		private String observed;
		private String status;
		private String reason;
	}
}
