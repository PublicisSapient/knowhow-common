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

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.publicissapient.kpidashboard.common.model.application.LeafNodeCapacity;
import com.publicissapient.kpidashboard.common.model.excel.KanbanCapacity;

import lombok.RequiredArgsConstructor;

/** Repository implementation for Kanban capacity operations. */
@RequiredArgsConstructor
public class KanbanCapacityRepositoryImpl implements KanbanCapacityRepoCustom {

	private static final String START_DATE = "startDate";
	private static final String END_DATE = "endDate";
	private static final String DATE_PATTERN = "yyyy-MM-dd";
	private static final String ADDITIONAL_FILTER_NODE_ID = "additionalFilterCapacityList.nodeCapacityList.additionalFilterId";
	private static final String ADDITIONAL_FILTER_ID = "additionalFilterCapacityList.filterId";

	private final MongoOperations mongoOperations;

	@Override
	public List<KanbanCapacity> findIssuesByType(Map<String, Object> filters, String dateFrom, String dateTo) {
		Criteria criteria = applyCommonFilters(new Criteria(), filters);
		criteria = applyDateRangeFilter(criteria, dateFrom, dateTo);

		Query query = new Query(criteria);
		List<KanbanCapacity> kanbanCapacityList = mongoOperations.find(query, KanbanCapacity.class);

		processAdditionalFilters(kanbanCapacityList, filters);
		return kanbanCapacityList;
	}

	@Override
	public List<KanbanCapacity> findByFilterMapAndDate(Map<String, String> filters, String dateFrom) {
		Criteria criteria = applyStringFilters(new Criteria(), filters);
		criteria = applySingleDateFilter(criteria, dateFrom);

		Query query = new Query(criteria);
		return mongoOperations.find(query, KanbanCapacity.class);
	}

	private Criteria applyCommonFilters(Criteria criteria, Map<String, Object> filters) {
		for (Map.Entry<String, Object> entry : filters.entrySet()) {
			String key = entry.getKey();
			if (ObjectUtils.isNotEmpty(entry.getValue()) && !key.equalsIgnoreCase(ADDITIONAL_FILTER_NODE_ID) && !key
					.equalsIgnoreCase(ADDITIONAL_FILTER_ID)) {
				criteria = criteria.and(key).in(entry.getValue());
			}
		}
		return criteria;
	}

	private Criteria applyStringFilters(Criteria criteria, Map<String, String> filters) {
		for (Map.Entry<String, String> entry : filters.entrySet()) {
			if (StringUtils.isNotEmpty(entry.getValue())) {
				criteria = criteria.and(entry.getKey()).in(entry.getValue());
			}
		}
		return criteria;
	}

	private Criteria applyDateRangeFilter(Criteria criteria, String dateFrom, String dateTo) {
		DateTime startDateTime = DateTimeFormat.forPattern(DATE_PATTERN).parseDateTime(dateFrom).withTime(0, 0, 0, 0);
		DateTime endDateTime = DateTimeFormat.forPattern(DATE_PATTERN).parseDateTime(dateTo).withTime(0, 0, 0, 0);
		return criteria.and(START_DATE).lte(endDateTime).and(END_DATE).gte(startDateTime);
	}

	private Criteria applySingleDateFilter(Criteria criteria, String dateFrom) {
		DateTime startDateTime = DateTimeFormat.forPattern(DATE_PATTERN).parseDateTime(dateFrom).withTime(0, 0, 0, 0);
		return criteria.and(START_DATE).lte(startDateTime).and(END_DATE).gte(startDateTime);
	}

	private void processAdditionalFilters(List<KanbanCapacity> kanbanCapacityList, Map<String, Object> filters) {
		if (!filters.containsKey(ADDITIONAL_FILTER_NODE_ID)) {
			return;
		}

		List<String> additionalFilter = (List<String>) filters.get(ADDITIONAL_FILTER_NODE_ID);
		List<String> upperCaseKeys = ((List<String>) filters.get(ADDITIONAL_FILTER_ID)).stream().map(String::toUpperCase)
				.toList();

		kanbanCapacityList.forEach(capacityData -> {
			if (CollectionUtils.isNotEmpty(capacityData.getAdditionalFilterCapacityList())) {
				double capacity = capacityData.getAdditionalFilterCapacityList().stream()
						.filter(additionalFilterCapacity -> upperCaseKeys
								.contains(additionalFilterCapacity.getFilterId().toUpperCase()))
						.flatMap(additionalFilterCapacity -> additionalFilterCapacity.getNodeCapacityList().stream())
						.filter(leaf -> additionalFilter.contains(leaf.getAdditionalFilterId()))
						.mapToDouble(LeafNodeCapacity::getAdditionalFilterCapacity).sum();
				capacityData.setCapacity(capacity);
			} else {
				capacityData.setCapacity(0D);
			}
		});
	}
}
