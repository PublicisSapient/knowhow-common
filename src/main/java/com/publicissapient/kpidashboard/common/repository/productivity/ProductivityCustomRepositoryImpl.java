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

package com.publicissapient.kpidashboard.common.repository.productivity;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.publicissapient.kpidashboard.common.model.productivity.calculation.Productivity;

import lombok.RequiredArgsConstructor;

/**
 * Custom repository implementation for productivity data operations in MongoDB.
 *
 * <p>
 * This repository provides specialized query operations for retrieving
 * productivity calculations that are not available through standard Spring Data
 * MongoDB repository methods. It focuses on complex aggregation queries that
 * require custom MongoDB pipeline operations.
 * </p>
 *
 * @author vladinu
 * @see ProductivityCustomRepository
 * @see Productivity
 */
@Repository
@RequiredArgsConstructor
public class ProductivityCustomRepositoryImpl implements ProductivityCustomRepository {
	private static final String COLLECTION_NAME_PRODUCTIVITY = "productivity";

	private final MongoTemplate mongoTemplate;

	/**
	 * Retrieves the latest productivity calculation for each specified hierarchy
	 * entity.
	 *
	 * <p>
	 * This method uses MongoDB aggregation pipeline to efficiently find the most
	 * recent productivity calculation for each hierarchy entity node. The
	 * aggregation performs the following operations:
	 * </p>
	 *
	 * @param hierarchyNodeIds
	 *            Set of hierarchy entity node IDs for which to retrieve the latest
	 *            productivity calculations. Must not be null or empty. The productivities are only stored at the
	 *            organization level 'project'
	 * @return List of Productivity objects representing the latest calculation for
	 *         each requested hierarchy entity.
	 */
	@Override
	public List<Productivity> getLatestProductivityByCalculationDateForProjects(Set<String> hierarchyNodeIds) {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("hierarchyEntityNodeId").in(hierarchyNodeIds)),
				Aggregation.sort(Sort.by(Sort.Direction.DESC, "calculationDate")),
				Aggregation.group("hierarchyEntityNodeId").first(Aggregation.ROOT).as("latestCalculation"),
				Aggregation.replaceRoot("latestCalculation"));
		return this.mongoTemplate.aggregate(aggregation, COLLECTION_NAME_PRODUCTIVITY, Productivity.class)
				.getMappedResults();
	}
}
