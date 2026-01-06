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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.publicissapient.kpidashboard.common.model.jira.IssueHistoryMappedData;
import com.publicissapient.kpidashboard.common.model.jira.JiraIssueCustomHistory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JiraIssueCustomHistoryRepositoryImpl implements JiraIssueHistoryCustomQueryRepository {

	private static final String STATUS_CHANGE_LOG = "statusUpdationLog";
	private static final String VERSION_CHANGE_LOG = "fixVersionUpdationLog";
	private static final String UPDATED_ON = "statusUpdationLog.updatedOn";
	private static final String STORY_ID = "storyID";
	private static final String STORY_TYPE = "storyType";
	private static final String TICKET_CREATED_DATE_FIELD = "createdDate";
	private static final String STATUS = "statusUpdationLog.changedTo";
	private static final String START_TIME = "T00:00:00.000Z";
	private static final String END_TIME = "T23:59:59.000Z";
	private static final String BASIC_PROJ_CONF_ID = "basicProjectConfigId";
	private static final String FIXVERSION_CHANGEDTO = "fixVersionUpdationLog.changedTo";
	private static final String FIXVERSION_CHANGEDFROM = "fixVersionUpdationLog.changedFrom";
	public static final String STATUS_UPDATION_LOG_STORY_CHANGED_TO = "statusUpdationLog.story.changedTo";
	public static final String URL = "url";
	public static final String DESCRIPTION = "description";
	public static final String ESTIMATE = "estimate";

	private final MongoOperations mongoOperations;

	@Override
	public List<JiraIssueCustomHistory> findFeatureCustomHistoryStoryProjectWise(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, Sort.Direction sortMethod) {
		List<AggregationOperation> aggregationOps = buildHistoryAggregation(mapOfFilters, uniqueProjectMap, sortMethod);
		TypedAggregation<JiraIssueCustomHistory> aggregation = Aggregation.newAggregation(JiraIssueCustomHistory.class,
				aggregationOps);
		List<IssueHistoryMappedData> data = mongoOperations
				.aggregate(aggregation, JiraIssueCustomHistory.class, IssueHistoryMappedData.class).getMappedResults();
		return mapToJiraHistory(data);
	}

	@Override
	public List<JiraIssueCustomHistory> findIssuesByCreatedDateAndType(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, String dateFrom, String dateTo) {
		Criteria criteria = buildCommonCriteria(mapOfFilters);
		criteria = addCreatedDateCriteria(criteria, dateFrom, dateTo);
		Query query = buildQueryWithProjectCriteria(criteria, uniqueProjectMap);
		addFieldInclusions(query, STORY_ID, STORY_TYPE, BASIC_PROJ_CONF_ID, STATUS_CHANGE_LOG, TICKET_CREATED_DATE_FIELD,
				URL, DESCRIPTION);
		return mongoOperations.find(query, JiraIssueCustomHistory.class);
	}

	@Override
	public List<JiraIssueCustomHistory> findByFilterAndFromStatusMap(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap) {
		Criteria criteria = buildCommonCriteria(mapOfFilters);
		Query query = buildQueryWithStatusMap(criteria, uniqueProjectMap);
		addFieldInclusions(query, STORY_ID, BASIC_PROJ_CONF_ID, STATUS_CHANGE_LOG);
		return mongoOperations.find(query, JiraIssueCustomHistory.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<JiraIssueCustomHistory> findByFilterAndFromReleaseMap(List<String> basicProjectConfigId,
			List<Pattern> releaseList) {
		Criteria criteria = new Criteria();
		criteria = criteria.and(BASIC_PROJ_CONF_ID).in(basicProjectConfigId);
		List<Criteria> projectCriteriaList = new ArrayList<>();
		Criteria projectCriteria1 = new Criteria();
		Criteria projectCriteria2 = new Criteria();
		projectCriteria1.and(FIXVERSION_CHANGEDTO).in(releaseList);
		projectCriteria2.and(FIXVERSION_CHANGEDFROM).in(releaseList);
		projectCriteriaList.add(projectCriteria1);
		projectCriteriaList.add(projectCriteria2);

		Criteria criteriaAggregatedAtProjectLevel = new Criteria().orOperator(projectCriteriaList.toArray(new Criteria[0]));
		Criteria criteriaProjectLevelAdded = new Criteria().andOperator(criteria, criteriaAggregatedAtProjectLevel);
		Query query = new Query(criteriaProjectLevelAdded);
		query.fields().include(STORY_ID);
		query.fields().include(BASIC_PROJ_CONF_ID);
		query.fields().include(STATUS_CHANGE_LOG);
		query.fields().include(VERSION_CHANGE_LOG);
		return mongoOperations.find(query, JiraIssueCustomHistory.class);
	}

	@Override
	public List<JiraIssueCustomHistory> findByFilterAndFromStatusMapWithDateFilter(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, String dateFrom, String dateTo) {
		Criteria criteria = buildCommonCriteria(mapOfFilters);
		Query query = buildQueryWithStatusMapAndDateFilter(criteria, uniqueProjectMap, dateFrom, dateTo);
		addFieldInclusions(query, STORY_ID, STORY_TYPE, BASIC_PROJ_CONF_ID, STATUS_CHANGE_LOG, TICKET_CREATED_DATE_FIELD,
				URL, DESCRIPTION, ESTIMATE);
		return mongoOperations.find(query, JiraIssueCustomHistory.class);
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
		DateTime startDate = new DateTime(dateFrom + START_TIME, DateTimeZone.UTC);
		DateTime endDate = new DateTime(dateTo + END_TIME, DateTimeZone.UTC);
		return criteria.and(TICKET_CREATED_DATE_FIELD).gte(startDate).lte(endDate);
	}

	@SuppressWarnings("unchecked")
	private Query buildQueryWithProjectCriteria(Criteria baseCriteria,
			Map<String, Map<String, Object>> uniqueProjectMap) {
		List<Criteria> projectCriteriaList = new ArrayList<>();
		uniqueProjectMap.forEach((project, filterMap) -> {
			Criteria projectCriteria = new Criteria().and(BASIC_PROJ_CONF_ID).is(project);
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
	private Query buildQueryWithStatusMap(Criteria baseCriteria, Map<String, Map<String, Object>> uniqueProjectMap) {
		List<Criteria> projectCriteriaList = new ArrayList<>();
		uniqueProjectMap.forEach((project, filterMap) -> {
			Criteria projectCriteria = new Criteria();
			projectCriteria.and(STATUS).in((List<Pattern>) filterMap.get(STATUS_UPDATION_LOG_STORY_CHANGED_TO));
			if (filterMap.get(STORY_TYPE) != null) {
				projectCriteria.and(STORY_TYPE).in((List<Pattern>) filterMap.get(STORY_TYPE));
			}
			projectCriteriaList.add(projectCriteria);
		});

		if (CollectionUtils.isEmpty(projectCriteriaList)) {
			return new Query(baseCriteria);
		}

		Criteria projectLevelCriteria = new Criteria().andOperator(projectCriteriaList.toArray(new Criteria[0]));
		Criteria finalCriteria = new Criteria().andOperator(baseCriteria, projectLevelCriteria);
		return new Query(finalCriteria);
	}

	@SuppressWarnings("unchecked")
	private Query buildQueryWithStatusMapAndDateFilter(Criteria baseCriteria,
			Map<String, Map<String, Object>> uniqueProjectMap, String dateFrom, String dateTo) {
		DateTime startDate = new DateTime(dateFrom + START_TIME, DateTimeZone.UTC);
		DateTime endDate = new DateTime(dateTo + END_TIME, DateTimeZone.UTC);

		List<Criteria> projectCriteriaList = new ArrayList<>();
		uniqueProjectMap.forEach((project, filterMap) -> {
			Criteria projectCriteria = new Criteria();
			if (filterMap.get(STATUS_UPDATION_LOG_STORY_CHANGED_TO) != null) {
				projectCriteria.and(STATUS).in((List<Pattern>) filterMap.get(STATUS_UPDATION_LOG_STORY_CHANGED_TO));
				projectCriteria.and(UPDATED_ON).gte(startDate).lte(endDate);
			}
			projectCriteriaList.add(projectCriteria);
		});

		uniqueProjectMap.forEach((project, filterMap) -> {
			Criteria projectCriteria = new Criteria();
			if (filterMap.get(STORY_TYPE) != null) {
				projectCriteria.and(STORY_TYPE).in((List<Pattern>) filterMap.get(STORY_TYPE));
			}
			projectCriteriaList.add(projectCriteria);
		});

		Criteria projectLevelCriteria = new Criteria().andOperator(projectCriteriaList.toArray(new Criteria[0]));
		Criteria finalCriteria = new Criteria().andOperator(baseCriteria, projectLevelCriteria);
		return new Query(finalCriteria);
	}

	@SuppressWarnings("unchecked")
	private List<AggregationOperation> buildHistoryAggregation(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, Sort.Direction sortMethod) {
		List<AggregationOperation> operations = new ArrayList<>();
		Criteria criteria = buildCommonCriteria(mapOfFilters);

		List<Criteria> projectCriteriaList = new ArrayList<>();
		uniqueProjectMap.forEach((project, filterMap) -> {
			Criteria projectCriteria = new Criteria().and(BASIC_PROJ_CONF_ID).is(project);
			projectCriteria.and(STORY_TYPE).in((List<Pattern>) filterMap.get(STORY_TYPE));
			projectCriteriaList.add(projectCriteria);
		});

		Criteria projectLevelCriteria = new Criteria().orOperator(projectCriteriaList.toArray(new Criteria[0]));
		Criteria firstMatchCriteria = new Criteria().andOperator(criteria, projectLevelCriteria);

		operations.add(Aggregation.match(firstMatchCriteria));
		operations.add(Aggregation.unwind(STATUS_CHANGE_LOG));

		List<Criteria> statusCriteriaList = new ArrayList<>();
		uniqueProjectMap.forEach((project, filterMap) -> {
			Criteria statusCriteria = new Criteria().and(BASIC_PROJ_CONF_ID).is(project);
			statusCriteria.and(STATUS).in((List<Pattern>) filterMap.get(STATUS_UPDATION_LOG_STORY_CHANGED_TO));
			statusCriteriaList.add(statusCriteria);
		});

		Criteria statusLevelCriteria = new Criteria().orOperator(statusCriteriaList.toArray(new Criteria[0]));
		operations.add(Aggregation.match(statusLevelCriteria));
		operations.add(Aggregation.sort(sortMethod, UPDATED_ON));
		operations.add(Aggregation.group(STORY_ID, BASIC_PROJ_CONF_ID, URL).push(STATUS_CHANGE_LOG).as(STATUS_CHANGE_LOG));
		operations.add(Aggregation.project(STATUS_CHANGE_LOG));

		return operations;
	}

	private List<JiraIssueCustomHistory> mapToJiraHistory(List<IssueHistoryMappedData> data) {
		List<JiraIssueCustomHistory> resultList = new ArrayList<>();
		data.forEach(result -> {
			JiraIssueCustomHistory history = new JiraIssueCustomHistory();
			history.setStoryID(result.getId().getStoryID());
			history.setBasicProjectConfigId(result.getId().getBasicProjectConfigId());
			history.setStatusUpdationLog(result.getStatusUpdationLog());
			history.setUrl(result.getId().getUrl());
			resultList.add(history);
		});
		return resultList;
	}

	private void addFieldInclusions(Query query, String... fields) {
		for (String field : fields) {
			query.fields().include(field);
		}
	}
}
