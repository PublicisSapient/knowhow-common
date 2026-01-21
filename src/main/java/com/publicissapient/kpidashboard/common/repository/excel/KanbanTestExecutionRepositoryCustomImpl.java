package com.publicissapient.kpidashboard.common.repository.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.publicissapient.kpidashboard.common.model.testexecution.KanbanTestExecution;

import lombok.RequiredArgsConstructor;

/** Repository implementation for Kanban test execution operations. */
@RequiredArgsConstructor
public class KanbanTestExecutionRepositoryCustomImpl implements KanbanTestExecutionRepositoryCustom {

	private static final String FIELD_PROJECT_ID = "basicProjectConfigId";
	private static final String FIELD_EXECUTION_DATE = "executionDate";

	private final MongoOperations mongoOperations;

	@SuppressWarnings("unchecked")
	@Override
	public List<KanbanTestExecution> findTestExecutionDetailByFilters(Map<String, List<String>> filters,
			Map<String, Map<String, Object>> uniqueProjectMap, String dateFrom, String dateTo) {

		Criteria criteria = applyCommonFilters(new Criteria(), filters);
		criteria = applyDateRangeFilter(criteria, dateFrom, dateTo);
		Query query = buildQueryWithProjectFilters(criteria, uniqueProjectMap);

		return mongoOperations.find(query, KanbanTestExecution.class);
	}

	private Criteria applyCommonFilters(Criteria criteria, Map<String, List<String>> filters) {
		for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
			if (CollectionUtils.isNotEmpty(entry.getValue())) {
				criteria = criteria.and(entry.getKey()).in(entry.getValue());
			}
		}
		return criteria;
	}

	private Criteria applyDateRangeFilter(Criteria criteria, String dateFrom, String dateTo) {
		return criteria.and(FIELD_EXECUTION_DATE).gte(dateFrom).lte(dateTo);
	}

	private Query buildQueryWithProjectFilters(Criteria criteria, Map<String, Map<String, Object>> uniqueProjectMap) {
		if (MapUtils.isEmpty(uniqueProjectMap)) {
			return new Query(criteria);
		}

		List<Criteria> projectCriteriaList = new ArrayList<>();
		uniqueProjectMap.forEach((project, filterMap) -> {
			Criteria projectCriteria = new Criteria();
			projectCriteria.and(FIELD_PROJECT_ID).is(project);
			filterMap.forEach((subk, subv) -> projectCriteria.and(subk).in((List<Pattern>) subv));
			projectCriteriaList.add(projectCriteria);
		});

		Criteria projectLevelCriteria = new Criteria().orOperator(projectCriteriaList.toArray(new Criteria[0]));
		Criteria combinedCriteria = new Criteria().andOperator(criteria, projectLevelCriteria);
		return new Query(combinedCriteria);
	}
}
