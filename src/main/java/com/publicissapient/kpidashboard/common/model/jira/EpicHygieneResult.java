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

import java.time.Instant;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.publicissapient.kpidashboard.common.model.generic.BasicModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persisted result of the Epic Hygiene LLM evaluation for a single Epic.
 *
 * <p>
 * One document per (basicProjectConfigId, epicKey). Because Epics are long
 * lived and are re-groomed continuously, caching is done per Epic rather than
 * per batch: a request only pays the LLM cost for the Epics that actually
 * changed since the last evaluation.
 *
 * <p>
 * A cached verdict is considered fresh only when BOTH:
 *
 * <ul>
 * <li>{@code ruleSetHash} still matches the SHA-256 of
 * {@code jiraFieldsSelectionKPI312} - the readiness dimensions were not
 * re-configured, and
 * <li>{@code epicChangeDate} still matches the Epic's {@code changeDate} - the
 * Epic itself was not edited in Jira.
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "epic_hygiene_results")
@CompoundIndex(name = "project_epic_idx", def = "{'basicProjectConfigId': 1, 'epicKey': 1}", unique = true)
public class EpicHygieneResult extends BasicModel {

	private String basicProjectConfigId;

	/** Jira issue key of the Epic, e.g. {@code DTS-48971}. */
	private String epicKey;

	private String epicName;

	/**
	 * SHA-256 of the sorted, serialized readiness rule-set
	 * (jiraFieldsSelectionKPI312).
	 */
	private String ruleSetHash;

	/**
	 * The Epic's {@code changeDate} at compute time - invalidates the cache on
	 * edit.
	 */
	private String epicChangeDate;

	/**
	 * Raw LLM verdict for this Epic. All derived metrics are re-computed from this.
	 */
	private EpicHygieneResponseDTO verdict;

	private Instant computedAt;
}
