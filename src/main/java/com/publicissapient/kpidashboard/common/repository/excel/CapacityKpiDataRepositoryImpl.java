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

package com.publicissapient.kpidashboard.common.repository.excel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.publicissapient.kpidashboard.common.model.application.LeafNodeCapacity;
import com.publicissapient.kpidashboard.common.model.excel.CapacityKpiData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Repository for {@link CapacityKpiData} operations. */
@Slf4j
@RequiredArgsConstructor
public class CapacityKpiDataRepositoryImpl implements CapacityKpiDataCustomRepository {

	private static final String FIELD_BASIC_PROJECT_CONFIG_ID = "basicProjectConfigId";
	private static final String FIELD_ADDITIONAL_FILTER_CAPACITY_NODE_ID = "additionalFilterCapacityList.nodeCapacityList.additionalFilterId";
	private static final String FIELD_ADDITIONAL_FILTER_ID = "additionalFilterCapacityList.filterId";

	private final MongoOperations mongoOperations;

	@SuppressWarnings("unchecked")
	@Override
	public List<CapacityKpiData> findByFilters(Map<String, Object> filters,
			Map<String, Map<String, Object>> uniqueProjectMap) {
		Criteria criteria = buildCommonCriteria(filters);
		Query query = buildQueryWithProjectFilters(criteria, uniqueProjectMap);

		List<CapacityKpiData> data = mongoOperations.find(query, CapacityKpiData.class);

		if (filters.containsKey(FIELD_ADDITIONAL_FILTER_CAPACITY_NODE_ID)) {
			processAdditionalFilters(data, filters);
		}

		if (CollectionUtils.isEmpty(data)) {
			log.info("No Data found for filters");
		}
		return data;
	}

	private Criteria buildCommonCriteria(Map<String, Object> filters) {
		Criteria criteria = new Criteria();
		for (Map.Entry<String, Object> entry : filters.entrySet()) {
			String key = entry.getKey();
			if (!key.equalsIgnoreCase(FIELD_ADDITIONAL_FILTER_CAPACITY_NODE_ID) && !key.equalsIgnoreCase(
					FIELD_ADDITIONAL_FILTER_ID) && CollectionUtils.isNotEmpty((List<Pattern>) entry.getValue())) {
				criteria = criteria.and(key).in((List<Pattern>) entry.getValue());
			}
		}
		return criteria;
	}

	private Query buildQueryWithProjectFilters(Criteria criteria, Map<String, Map<String, Object>> uniqueProjectMap) {
		List<Criteria> projectCriteriaList = new ArrayList<>();
		uniqueProjectMap.forEach((project, filterMap) -> {
			Criteria projectCriteria = new Criteria();
			projectCriteria.and(FIELD_BASIC_PROJECT_CONFIG_ID).is(project);
			filterMap.forEach((subk, subv) -> projectCriteria.and(subk).in((List<Pattern>) subv));
			projectCriteriaList.add(projectCriteria);
		});

		if (CollectionUtils.isNotEmpty(projectCriteriaList)) {
			Criteria projectLevelCriteria = new Criteria().orOperator(projectCriteriaList.toArray(new Criteria[0]));
			Criteria combinedCriteria = new Criteria().andOperator(criteria, projectLevelCriteria);
			return new Query(combinedCriteria);
		}
		return new Query(criteria);
	}

	private void processAdditionalFilters(List<CapacityKpiData> data, Map<String, Object> filters) {
		List<String> additionalFilter = (List<String>) filters.get(FIELD_ADDITIONAL_FILTER_CAPACITY_NODE_ID);
		List<String> upperCaseKeys = ((List<String>) filters.get(FIELD_ADDITIONAL_FILTER_ID)).stream()
				.map(String::toUpperCase).toList();

		data.forEach(capacityKpiData -> {
			if (CollectionUtils.isNotEmpty(capacityKpiData.getAdditionalFilterCapacityList())) {
				double capacity = capacityKpiData.getAdditionalFilterCapacityList().stream()
						.filter(additionalFilterCapacity -> upperCaseKeys
								.contains(additionalFilterCapacity.getFilterId().toUpperCase()))
						.flatMap(additionalFilterCapacity -> additionalFilterCapacity.getNodeCapacityList().stream())
						.filter(leaf -> additionalFilter.contains(leaf.getAdditionalFilterId()))
						.mapToDouble(LeafNodeCapacity::getAdditionalFilterCapacity).sum();
				capacityKpiData.setCapacityPerSprint(capacity);
			} else {
				capacityKpiData.setCapacityPerSprint(0D);
			}
		});
	}
}
