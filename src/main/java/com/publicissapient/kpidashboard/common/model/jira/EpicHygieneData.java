/*
 *  Copyright 2024 <Sapient Corporation>
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

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.publicissapient.kpidashboard.common.model.generic.BasicModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Pre-computed Epic Hygiene (kpi312) snapshot of a single project.
 *
 * <p>
 * One document per project per run: the labelled scalars published by the KPI
 * in its {@code
 * trendValueList} are flattened into first class fields (so they can be
 * queried/aggregated) while the raw label/value pairs are kept in
 * {@link #metrics} to stay forward compatible with new metrics added by the
 * KPI. The per Epic verdicts backing the drill-down are kept in
 * {@link #epicDetails} so a cached snapshot can serve the Excel export without
 * re-running the LLM.
 *
 * <p>
 * The document lives in {@code knowhow-common} because it is written by the
 * {@code
 * epic-hygiene-calculation} batch job (data-processor) and read back, cache
 * first, by the Epic Hygiene KPI service (knowhow-api).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = EpicHygieneData.COLLECTION_NAME)
public class EpicHygieneData extends BasicModel {

	public static final String COLLECTION_NAME = "epic_hygiene_data";

	@Indexed
	private String basicProjectConfigId;

	private String projectNodeId;
	private String projectName;

	private String kpiId;
	private String kpiName;

	/** Number of active Epics considered by the KPI. */
	private Integer totalActiveEpics;

	/** Epics whose overall status came back {@code READY}. */
	private Integer constructionReadyEpics;

	/** Epics whose readiness score is below 50. */
	private Integer atRiskEpics;

	/** Mean readiness score (0-100) across all evaluated Epics. */
	private Double avgReadinessScore;

	/** Every label/value pair published by the KPI, verbatim. */
	private List<EpicHygieneMetric> metrics;

	/**
	 * The per Epic readiness verdicts that back the drill-down / Excel export.
	 * Empty when the snapshot was produced by a consumer that only captured the
	 * aggregated {@link #metrics}.
	 */
	private List<EpicHygieneResponseDTO> epicDetails;

	/**
	 * {@code true} when the KPI could not be retrieved (after all retries) and the
	 * record was produced by the fallback path — values are then neutral, not real
	 * measurements.
	 */
	private boolean fallback;

	/**
	 * Populated only for fallback records; explains why the KPI call did not
	 * succeed.
	 */
	private String failureReason;

	/**
	 * TTL anchor — see {@code mongo.ttl-index.configs.epic-hygiene-calculation}.
	 */
	private Instant calculationDate;

	/** A single label/value pair exactly as published by the KPI. */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class EpicHygieneMetric {
		private String label;
		private Double value;
		private String labelInfo;
		private String unit;
	}
}
