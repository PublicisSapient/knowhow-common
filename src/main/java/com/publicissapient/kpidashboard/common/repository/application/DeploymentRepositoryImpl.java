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

package com.publicissapient.kpidashboard.common.repository.application;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.publicissapient.kpidashboard.common.model.application.Deployment;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link DeploymentRepositoryCustom} that provides custom
 * repository operations for deployment data queries.
 */
@RequiredArgsConstructor
public class DeploymentRepositoryImpl implements DeploymentRepositoryCustom {

	// Field names
	private static final String FIELD_START_TIME = "startTime";
	private static final String FIELD_END_TIME = "endTime";
	private static final String FIELD_BASIC_PROJECT_CONFIG_ID = "basicProjectConfigId";

	private final MongoTemplate mongoTemplate;

	@Override
	public List<Deployment> findDeploymentList(Map<String, List<String>> filters, Set<ObjectId> projectBasicConfigIds,
			String startDate, String endDate) {
		Criteria criteria = buildCriteria(filters, projectBasicConfigIds, startDate, endDate);
		Query query = new Query(criteria);
		return mongoTemplate.find(query, Deployment.class);
	}

	private Criteria buildCriteria(Map<String, List<String>> filters, Set<ObjectId> projectConfigIds, String startDate,
			String endDate) {
		Criteria criteria = new Criteria();

		criteria = applyCommonFilters(criteria, filters);
		criteria = applyDateRangeFilter(criteria, startDate, endDate);
		criteria = applyProjectFilter(criteria, projectConfigIds);

		return criteria;
	}

	private Criteria applyCommonFilters(Criteria criteria, Map<String, List<String>> filters) {
		if (MapUtils.isEmpty(filters)) {
			return criteria;
		}
		for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
			if (CollectionUtils.isNotEmpty(entry.getValue())) {
				criteria = criteria.and(entry.getKey()).in(entry.getValue());
			}
		}
		return criteria;
	}

	private Criteria applyDateRangeFilter(Criteria criteria, String startDate, String endDate) {
		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
			return criteria;
		}
		return criteria.and(FIELD_START_TIME).gte(startDate).and(FIELD_END_TIME).lte(endDate);
	}

	private Criteria applyProjectFilter(Criteria criteria, Set<ObjectId> projectConfigIds) {
		return criteria.and(FIELD_BASIC_PROJECT_CONFIG_ID).in(projectConfigIds);
	}
}
