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
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.publicissapient.kpidashboard.common.model.jira.KanbanJiraIssue;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KanbanJiraIssueRepositoryImpl implements KanbanJiraIssueRepoCustom {

	private static final String TICKET_PROJECT_ID_FIELD = "basicProjectConfigId";
	private static final String TICKET_CREATED_DATE_FIELD = "createdDate";
	private static final String RANGE = "range";
	private static final String LESS = "less";
	private static final String PAST = "past";
	private static final String START_TIME = "T00:00:00.0000000";
	private static final String END_TIME = "T23:59:59.0000000";
	private static final String JIRA_ISSUE_STATUS = "jiraStatus";
	private static final String NIN = "nin";

	private final MongoOperations operations;

	@Override
	public List<KanbanJiraIssue> findIssuesByType(Map<String, List<String>> mapOfFilters, String dateFrom,
			String dateTo) {
		Criteria criteria = buildCommonCriteria(mapOfFilters);
		criteria = addDateCriteria(criteria, dateFrom, dateTo, RANGE);
		Query query = new Query(criteria);
		return operations.find(query, KanbanJiraIssue.class);
	}

	@Override
	public List<KanbanJiraIssue> findIssuesByDateAndType(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, String dateFrom, String dateTo, String dateCriteria) {
		Criteria criteria = buildCommonCriteria(mapOfFilters);
		criteria = addDateCriteria(criteria, dateFrom, dateTo, dateCriteria);
		Query query = buildQueryWithProjectCriteria(criteria, uniqueProjectMap);
		return operations.find(query, KanbanJiraIssue.class);
	}

	@Override
	public List<KanbanJiraIssue> findIssuesByDateAndTypeAndStatus(Map<String, List<String>> mapOfFilters,
			Map<String, Map<String, Object>> uniqueProjectMap, String dateFrom, String dateTo, String dateCriteria,
			String mapStatusCriteria) {
		Criteria criteria = buildCommonCriteria(mapOfFilters);
		criteria = addDateCriteria(criteria, dateFrom, dateTo, dateCriteria);
		Query query = buildQueryWithProjectCriteriaAndStatus(criteria, uniqueProjectMap, mapStatusCriteria);
		return operations.find(query, KanbanJiraIssue.class);
	}

	@Override
	public void updateByBasicProjectConfigId(String basicProjectConfigId, List<String> fieldsToUnset) {
		Criteria criteria = new Criteria();
		criteria.and(TICKET_PROJECT_ID_FIELD).is(basicProjectConfigId);
		Query query = new Query(criteria);
		if (CollectionUtils.isNotEmpty(fieldsToUnset)) {
			Update update = new Update();
			fieldsToUnset.stream().forEach(update::unset);
			operations.updateMulti(query, update, KanbanJiraIssue.class);
		}
	}

	public List<KanbanJiraIssue> findCostOfDelayByType(Map<String, List<String>> mapOfFilters) {
		Criteria criteria = buildCommonCriteria(mapOfFilters);
		Query query = new Query(criteria);
		return operations.find(query, KanbanJiraIssue.class);
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

	private Criteria addDateCriteria(Criteria criteria, String dateFrom, String dateTo, String dateCriteria) {
		String startDate = dateFrom + START_TIME;
		String endDate = dateTo + END_TIME;

		if (RANGE.equals(dateCriteria)) {
			return criteria.and(TICKET_CREATED_DATE_FIELD).gte(startDate).lte(endDate);
		} else if (LESS.equals(dateCriteria)) {
			return criteria.and(TICKET_CREATED_DATE_FIELD).lt(startDate);
		} else if (PAST.equals(dateCriteria)) {
			return criteria.and(TICKET_CREATED_DATE_FIELD).lt(endDate);
		}
		return criteria;
	}

	@SuppressWarnings("unchecked")
	private Query buildQueryWithProjectCriteriaAndStatus(Criteria baseCriteria,
			Map<String, Map<String, Object>> uniqueProjectMap, String mapStatusCriteria) {
		List<Criteria> projectCriteriaList = new ArrayList<>();
		uniqueProjectMap.forEach((project, filterMap) -> {
			Criteria projectCriteria = new Criteria().and(TICKET_PROJECT_ID_FIELD).is(project);
			filterMap.forEach((key, value) -> {
				List<Pattern> patterns = (List<Pattern>) value;
				if (JIRA_ISSUE_STATUS.equals(key) && NIN.equalsIgnoreCase(mapStatusCriteria)) {
					projectCriteria.and(key).nin(patterns);
				} else {
					projectCriteria.and(key).in(patterns);
				}
			});
			projectCriteriaList.add(projectCriteria);
		});

		Criteria projectLevelCriteria = new Criteria().orOperator(projectCriteriaList.toArray(new Criteria[0]));
		Criteria finalCriteria = new Criteria().andOperator(baseCriteria, projectLevelCriteria);

		return new Query(finalCriteria);
	}

	@SuppressWarnings("unchecked")
	private Query buildQueryWithProjectCriteria(Criteria baseCriteria,
			Map<String, Map<String, Object>> uniqueProjectMap) {
		List<Criteria> projectCriteriaList = new ArrayList<>();
		uniqueProjectMap.forEach((project, filterMap) -> {
			Criteria projectCriteria = new Criteria();
			projectCriteria.and(TICKET_PROJECT_ID_FIELD).is(project);
			filterMap.forEach((key, value) -> projectCriteria.and(key).in((List<Pattern>) value));
			projectCriteriaList.add(projectCriteria);
		});

		Criteria projectLevelCriteria = new Criteria().orOperator(projectCriteriaList.toArray(new Criteria[0]));
		Criteria finalCriteria = new Criteria().andOperator(baseCriteria, projectLevelCriteria);

		return new Query(finalCriteria);
	}
}
