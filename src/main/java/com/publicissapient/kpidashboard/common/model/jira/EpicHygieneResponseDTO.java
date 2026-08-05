/*
 *  Copyright 2024 Sapient Corporation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the
 *  License.
 */

package com.publicissapient.kpidashboard.common.model.jira;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the readiness (hygiene) evaluation returned by the LLM for a
 * single Jira <b>Epic</b>.
 *
 * <p>
 * Unlike {@link HygieneKpiResponseDTO} - which grades every rule with a
 * pass/fail verdict - an Epic is graded per <em>readiness dimension</em> on a
 * 0-100 scale (Business Clarity, Scope Definition, Solution Readiness,
 * Dependency Readiness, Delivery Readiness, Risk Readiness, ...). The
 * dimensions themselves are NOT hardcoded: they are whatever the project
 * configured in {@code
 * jiraFieldsSelectionKPI312}, so a project may add, remove or reword dimensions
 * without a code change.
 *
 * <p>
 * The LLM produces a JSON ARRAY - one element per input Epic. String-typed
 * enum-like fields are intentionally kept as {@link String} to be forgiving of
 * minor LLM formatting drift. Expected vocabulary:
 *
 * <ul>
 * <li>{@code readinessGrade} - {@code "GOOD" | "AVERAGE" | "POOR"}
 * <li>{@code overallStatus} - {@code "READY" | "NOT READY"}
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpicHygieneResponseDTO {

	private String epicKey;

	/**
	 * Deep link to the Epic - embedded at persist time so Excel needs no re-query.
	 */
	private String epicUrl;

	private String epicName;

	/**
	 * Workflow state of the Epic, e.g. {@code Intake / Discovery}, {@code Blocked}.
	 */
	private String status;

	private String assignee;

	/** One entry per configured readiness dimension. */
	private List<DimensionResult> results;

	/** Weighted average of all applicable dimension scores, 0-100. */
	private Integer readinessScore;

	private String readinessGrade;

	private String overallStatus;

	/** Lowest scoring dimensions, heaviest weight first - at most three. */
	private List<String> topGaps;

	/** Up to five actionable fixes joined by {@code " | "}. */
	private String recommendations;

	/** Score awarded to ONE readiness dimension of the Epic. */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DimensionResult {

		/** Dimension name, copied verbatim from the configured rule entry. */
		private String dimension;

		/** Jira field the dimension was evaluated against. */
		private String field;

		/** Relative weight the dimension carried when the Epic was scored. */
		private Double weight;

		/** 0-100 score for this dimension; {@code null} means "not applicable". */
		private Integer score;

		/** Actual field value the LLM based its score on. */
		private String observed;

		/** One-line justification citing the observed value. */
		private String reason;
	}
}
