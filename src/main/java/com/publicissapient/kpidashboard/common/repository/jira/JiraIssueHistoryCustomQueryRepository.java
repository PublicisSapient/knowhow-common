/*******************************************************************************
 * Copyright 2014 CapitalOne, LLC.
 * Further development Copyright 2022 Sapient Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package com.publicissapient.kpidashboard.common.repository.jira;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.publicissapient.kpidashboard.common.model.jira.JiraIssueCustomHistory;

/** Interface FeatureHistoryCustomQueryRepository. */
@Repository
public interface JiraIssueHistoryCustomQueryRepository {

	/**
	 * Find feature custom history story project wise list.
	 *
	 * @param mapOfFilters
	 *          the map of filters
	 * @param uniqueProjectMap
	 *          the unique project map
	 * @return the list
	 */
	List<JiraIssueCustomHistory> findFeatureCustomHistoryStoryProjectWise(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, Sort.Direction sortMethod);

	/**
	 * find jira issue based on filter and date
	 *
	 * @param mapOfFilters
	 * @param uniqueProjectMap
	 * @param dateFrom
	 * @param dateTo
	 * @return List<JiraIssueCustomHistory>
	 */
	List<JiraIssueCustomHistory> findIssuesByCreatedDateAndType(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, String dateFrom, String dateTo);

	/**
	 * find jira issue based on filter and date
	 *
	 * @param mapOfFilters
	 * @param uniqueProjectMap
	 * @return List<JiraIssueCustomHistory>
	 */
	List<JiraIssueCustomHistory> findByFilterAndFromStatusMap(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap);

	List<JiraIssueCustomHistory> findByFilterAndFromReleaseMap(List<String> basicProjectConfigId,
			List<Pattern> releaseList);

	/**
	 * find jira issue based on filter and date
	 *
	 * @param mapOfFilters
	 * @param uniqueProjectMap
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	List<JiraIssueCustomHistory> findByFilterAndFromStatusMapWithDateFilter(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, String dateFrom, String dateTo);

	/**
	 * Fetches issue history for DOR Acceptance Rate (kpi228), including description
	 * and acceptance criteria revision logs. No createdDate filter is applied — the
	 * service already constrains the window via sprint and weekly time buckets.
	 *
	 * @param mapOfFilters
	 *          project-level filters (e.g. basicProjectConfigId, storyType)
	 * @param uniqueProjectMap
	 *          per-project filter overrides
	 * @return issue history with status, description and acceptance-criteria
	 *         revision logs
	 */
	List<JiraIssueCustomHistory> findForDorAnalysis(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap);
}
