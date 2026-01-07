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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.publicissapient.kpidashboard.common.model.jira.IssueHistoryMappedData;
import com.publicissapient.kpidashboard.common.model.jira.KanbanIssueCustomHistory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KanbanJiraIssueHistoryRepositoryImpl implements KanbanJiraIssueHistoryRepoCustom {

	private static final String TICKET_STATUS_FIELD = "historyDetails.status";
	private static final String TICKET_ACTIVITY_DATE = "historyDetails.activityDate";
	private static final String TICKET_PROJECT_ID_FIELD = "basicProjectConfigId";
	private static final String HISTORY_DETAILS = "historyDetails";
	private static final String STORY_ID = "storyID";
	private static final String STORY_TYPE = "storyType";
	private static final String TICKET_CREATED_DATE_FIELD = "createdDate";
	private static final String PRIORITY = "priority";
	private static final String ESTIMATE_TIME = "estimate";
	private static final String NIN = "nin";
	private static final String START_TIME = "T00:00:00.000Z";
	private static final String END_TIME = "T23:59:59.000Z";
	private static final String URL = "url";

	private final MongoOperations mongoOperations;

	@Override
	public List<KanbanIssueCustomHistory> findIssuesByStatusAndDate(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, String dateFrom, String dateTo, String mapStatusCriteria) {
		List<AggregationOperation> aggregationOps = buildStatusAndDateAggregation(mapOfFilters, uniqueProjectMap, dateFrom,
				dateTo, mapStatusCriteria);
		TypedAggregation<KanbanIssueCustomHistory> aggregation = Aggregation.newAggregation(KanbanIssueCustomHistory.class,
				aggregationOps);
		AggregationResults<IssueHistoryMappedData> results = mongoOperations.aggregate(aggregation,
				KanbanIssueCustomHistory.class, IssueHistoryMappedData.class);
		return mapToKanbanHistory(results.getMappedResults());
	}

	@Override
	public List<KanbanIssueCustomHistory> findIssuesByCreatedDateAndType(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, String dateFrom, String dateTo) {
		Criteria criteria = buildCommonCriteria(mapOfFilters);
		criteria = addCreatedDateCriteria(criteria, dateFrom, dateTo);
		Query query = buildQueryWithProjectCriteria(criteria, uniqueProjectMap);
		return mongoOperations.find(query, KanbanIssueCustomHistory.class);
	}

	@Override
	public List<KanbanIssueCustomHistory> findIssuesInWipByDate(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, Map<String, Map<String, Object>> uniqueWipProjectMap,
			String dateFrom, String dateTo) {
		List<AggregationOperation> aggregationOps = buildWipAggregation(mapOfFilters, uniqueProjectMap, uniqueWipProjectMap,
				dateFrom, dateTo);
		TypedAggregation<KanbanIssueCustomHistory> aggregation = Aggregation.newAggregation(KanbanIssueCustomHistory.class,
				aggregationOps);
		return mongoOperations.aggregate(aggregation, KanbanIssueCustomHistory.class, KanbanIssueCustomHistory.class)
				.getMappedResults();
	}

	private Criteria buildCommonCriteria(Map<String, List<String>> mapOfFilters) {
		Criteria criteria = new Criteria();
		mapOfFilters.forEach((key, values) -> {
			if (CollectionUtils.isNotEmpty(values)) {
				criteria.and(key).in(values);
			}
		});
		return criteria;
	}

	private Criteria addCreatedDateCriteria(Criteria criteria, String dateFrom, String dateTo) {
		String startDate = dateFrom + START_TIME;
		String endDate = dateTo + END_TIME;
		return criteria.and(TICKET_CREATED_DATE_FIELD).gte(startDate).lte(endDate);
	}

	@SuppressWarnings("unchecked")
	private Query buildQueryWithProjectCriteria(Criteria baseCriteria,
			Map<String, Map<String, Object>> uniqueProjectMap) {
		List<Criteria> projectCriteriaList = new ArrayList<>();
		uniqueProjectMap.forEach((project, filterMap) -> {
			Criteria projectCriteria = new Criteria().and(TICKET_PROJECT_ID_FIELD).is(project);
			filterMap.forEach((key, value) -> {
				List<Pattern> patterns = (List<Pattern>) value;
				projectCriteria.and(key).in(patterns);
			});
			projectCriteriaList.add(projectCriteria);
		});
		Criteria projectLevelCriteria = new Criteria().orOperator(projectCriteriaList.toArray(new Criteria[0]));
		Criteria finalCriteria = new Criteria().andOperator(baseCriteria, projectLevelCriteria);
		return new Query(finalCriteria);
	}

	@SuppressWarnings("unchecked")
	private List<AggregationOperation> buildStatusAndDateAggregation(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, String dateFrom, String dateTo, String mapStatusCriteria) {
		List<AggregationOperation> operations = new ArrayList<>();
		Criteria criteria = buildCommonCriteria(mapOfFilters);

		operations.add(Aggregation.match(criteria));
		operations.add(Aggregation.unwind(HISTORY_DETAILS));

		if (MapUtils.isNotEmpty(uniqueProjectMap)) {
			String startDate = dateFrom + START_TIME;
			String endDate = dateTo + END_TIME;
			List<Criteria> projectCriteriaList = new ArrayList<>();

			uniqueProjectMap.forEach((project, filterMap) -> {
				Criteria projectCriteria = new Criteria().and(TICKET_PROJECT_ID_FIELD).is(project);
				filterMap.forEach((key, value) -> {
					List<Pattern> patterns = (List<Pattern>) value;
					if (TICKET_STATUS_FIELD.equals(key) && NIN.equalsIgnoreCase(mapStatusCriteria)) {
						projectCriteria.and(key).nin(patterns);
					} else {
						projectCriteria.and(key).in(patterns);
					}
				});
				projectCriteria.and(TICKET_ACTIVITY_DATE).gte(startDate).lte(endDate);
				projectCriteriaList.add(projectCriteria);
			});

			Criteria projectLevelCriteria = new Criteria().orOperator(projectCriteriaList.toArray(new Criteria[0]));
			operations.add(Aggregation.match(projectLevelCriteria));
		}

		operations.add(Aggregation
				.group(STORY_ID, STORY_TYPE, TICKET_PROJECT_ID_FIELD, TICKET_CREATED_DATE_FIELD, PRIORITY, ESTIMATE_TIME, URL)
				.push(HISTORY_DETAILS).as(HISTORY_DETAILS));
		operations.add(Aggregation.project(HISTORY_DETAILS));

		return operations;
	}

	@SuppressWarnings("unchecked")
	private List<AggregationOperation> buildWipAggregation(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, Map<String, Map<String, Object>> uniqueWipProjectMap,
			String dateFrom, String dateTo) {
		List<AggregationOperation> operations = new ArrayList<>();
		Criteria criteria = buildCommonCriteria(mapOfFilters);
		String startDate = dateFrom + START_TIME;
		String endDate = dateTo + END_TIME;

		operations.add(Aggregation.match(criteria));

		List<Criteria> projectCriteriaList = new ArrayList<>();
		uniqueWipProjectMap.forEach((project, filterMap) -> {
			Criteria projectCriteria = new Criteria().and(TICKET_PROJECT_ID_FIELD).is(project);
			Criteria closedIssueCriteria = new Criteria();
			Criteria historyCriteria = new Criteria();
			Map<String, Object> closedIssueMap = uniqueProjectMap.get(project);

			filterMap.forEach((key, value) -> {
				if (TICKET_STATUS_FIELD.equals(key)) {
					List<Pattern> patterns = (List<Pattern>) value;
					List<Pattern> closedPatterns = (List<Pattern>) closedIssueMap.get(TICKET_STATUS_FIELD);
					historyCriteria.andOperator(Criteria.where(key).in(patterns), Criteria.where(key).nin(closedPatterns));
				}
			});
			historyCriteria.and(TICKET_ACTIVITY_DATE).lte(endDate);

			closedIssueMap.forEach((key, value) -> {
				List<Pattern> patterns = (List<Pattern>) value;
				if (TICKET_STATUS_FIELD.equals(key)) {
					closedIssueCriteria.and(key).in(patterns);
				} else {
					projectCriteria.and(key).in(patterns);
				}
			});
			closedIssueCriteria.and(TICKET_ACTIVITY_DATE).gt(startDate);
			projectCriteria.orOperator(historyCriteria, closedIssueCriteria);
			projectCriteriaList.add(projectCriteria);
		});

		Criteria projectLevelCriteria = new Criteria().orOperator(projectCriteriaList.toArray(new Criteria[0]));
		operations.add(Aggregation.match(projectLevelCriteria));

		return operations;
	}

	private List<KanbanIssueCustomHistory> mapToKanbanHistory(List<IssueHistoryMappedData> data) {
		List<KanbanIssueCustomHistory> resultList = new ArrayList<>();
		data.forEach(result -> {
			KanbanIssueCustomHistory history = new KanbanIssueCustomHistory();
			history.setStoryID(result.getId().getStoryID());
			history.setStoryType(result.getId().getStoryType());
			history.setProjectComponentId(result.getId().getProjectComponentId());
			history.setBasicProjectConfigId(result.getId().getBasicProjectConfigId());
			history.setCreatedDate(result.getId().getCreatedDate());
			history.setPriority(result.getId().getPriority());
			history.setEstimate(result.getId().getEstimate());
			history.setHistoryDetails(result.getHistoryDetails());
			history.setUrl(result.getId().getUrl());
			resultList.add(history);
		});
		return resultList;
	}
}
