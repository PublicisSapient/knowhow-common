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

package com.publicissapient.kpidashboard.common.repository.kpimaturity.organization;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.publicissapient.kpidashboard.common.model.kpimaturity.organization.KpiMaturity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class KpiMaturityCustomRepositoryImpl implements KpiMaturityCustomRepository {

	private static final String COLLECTION_NAME_KPI_MATURITY = "kpi_maturity";
	private static final String HIERARCHY_ENTITY_NODE_ID_FIELD_NAME = "hierarchyEntityNodeId";

	private final MongoTemplate mongoTemplate;

	@Override
	public List<KpiMaturity> getLatestKpiMaturityByCalculationDateForProjects(Set<String> hierarchyNodeIds) {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where(HIERARCHY_ENTITY_NODE_ID_FIELD_NAME).in(hierarchyNodeIds)),
				Aggregation.sort(Sort.by(Sort.Direction.DESC, "calculationDate")),
				Aggregation.group(HIERARCHY_ENTITY_NODE_ID_FIELD_NAME).first(Aggregation.ROOT).as("latestCalculation"),
				Aggregation.replaceRoot("latestCalculation"));
		return this.mongoTemplate.aggregate(aggregation, COLLECTION_NAME_KPI_MATURITY, KpiMaturity.class)
				.getMappedResults();
	}
}
