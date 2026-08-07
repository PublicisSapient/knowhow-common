package com.publicissapient.kpidashboard.common.model.jira;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.publicissapient.kpidashboard.common.model.generic.BasicModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persisted result of the Story Hygiene LLM evaluation for a single sprint.
 *
 * <p>
 * One document per (basicProjectConfigId, sprintId). Upserted on every
 * evaluation so the collection stays at exactly one entry per sprint per
 * project.
 *
 * <p>
 * The {@code ruleSetHash} field captures the SHA-256 of the hygiene rule-set at
 * the time of evaluation. A hash mismatch on read signals stale data and
 * triggers a fresh LLM call.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "story_hygiene_sprint_results")
@CompoundIndex(name = "project_sprint_idx", def = "{'basicProjectConfigId': 1, 'sprintId': 1}", unique = true)
public class StoryHygieneSprintResult extends BasicModel {

	private String basicProjectConfigId;
	private String sprintId;
	private String sprintName;

	/**
	 * SHA-256 of the sorted, serialized hygiene rule-set
	 * (jiraFieldsSelectionKPI311).
	 */
	private String ruleSetHash;

	/**
	 * Number of issues actually sent to the LLM (may be capped below
	 * totalIssueCount).
	 */
	private int sampledIssueCount;

	/**
	 * Total issues in the sprint at compute time (used to reconstruct the "N of M
	 * (Capped)" hover).
	 */
	private int totalIssueCount;

	/**
	 * Raw LLM output — one entry per Jira issue. All derived metrics are
	 * re-computed from this.
	 */
	private List<HygieneKpiResponseDTO> issueVerdicts;

	private Instant computedAt;
}
