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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publicissapient.kpidashboard.common.model.productivity.calculation.Productivity;
import com.publicissapient.kpidashboard.common.repository.productivity.dto.ProductivityTemporalGrouping;
import com.publicissapient.kpidashboard.common.shared.enums.TemporalAggregationUnit;

import lombok.RequiredArgsConstructor;

/**
 * Custom repository implementation for productivity data operations in MongoDB.
 *
 * <p>
 * This repository provides specialized query operations for retrieving
 * productivity calculations that are not available through standard Spring Data
 * MongoDB repository methods. It focuses on complex aggregation queries that
 * require custom MongoDB pipeline operations.
 *
 * @author vladinu
 * @see ProductivityCustomRepository
 * @see Productivity
 */
@Repository
@RequiredArgsConstructor
public class ProductivityCustomRepositoryImpl implements ProductivityCustomRepository {
	private static final String COLLECTION_NAME_PRODUCTIVITY = "productivity";
	private static final String HIERARCHY_ENTITY_NODE_ID_FIELD_NAME = "hierarchyEntityNodeId";
	private static final String ENTRIES = "entries";

	private final MongoTemplate mongoTemplate;

	private final ObjectMapper objectMapper;

	/**
	 * Retrieves the latest productivity calculation for each specified hierarchy
	 * entity.
	 *
	 * <p>
	 * This method uses MongoDB aggregation pipeline to efficiently find the most
	 * recent productivity calculation for each hierarchy entity node. The
	 * aggregation performs the following operations:
	 *
	 * @param hierarchyNodeIds
	 *          Set of hierarchy entity node IDs for which to retrieve the latest
	 *          productivity calculations. Must not be null or empty. The
	 *          productivities are only stored at the organization level 'project'
	 * @return List of Productivity objects representing the latest calculation for
	 *         each requested hierarchy entity.
	 */
	@Override
	public List<Productivity> getLatestProductivityByCalculationDateForProjects(Set<String> hierarchyNodeIds) {
		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where(HIERARCHY_ENTITY_NODE_ID_FIELD_NAME).in(hierarchyNodeIds)),
				Aggregation.sort(Sort.by(Sort.Direction.DESC, "calculationDate")),
				Aggregation.group(HIERARCHY_ENTITY_NODE_ID_FIELD_NAME).first(Aggregation.ROOT).as("latestCalculation"),
				Aggregation.replaceRoot("latestCalculation"));
		return this.mongoTemplate.aggregate(aggregation, COLLECTION_NAME_PRODUCTIVITY, Productivity.class)
				.getMappedResults();
	}

	/**
	 * Retrieves productivity data grouped by temporal aggregation units (week,
	 * month etc.) for specified hierarchy entities.
	 *
	 * <p>
	 * This method performs MongoDB aggregation to group productivity calculations
	 * by time periods using the {@code $dateTrunc} operator. Each grouped result
	 * contains all productivity entries that fall within the same time period, with
	 * only essential fields included to optimize performance.
	 *
	 * @param hierarchyNodeIds
	 *          Set of hierarchy entity node IDs for which to retrieve productivity
	 *          data. Must not be null or empty. These typically represent
	 *          project-level entities where productivity calculations are stored.
	 * @param temporalAggregationUnit
	 *          The temporal unit for grouping data (e.g., WEEK, MONTH). Determines
	 *          the granularity of time-based aggregation using MongoDB's $dateTrunc
	 *          operator.
	 * @param limit
	 *          Maximum number of temporal periods to return. Must be positive.
	 * @return List of {@link ProductivityTemporalGrouping} objects containing
	 *         productivity data grouped by time periods, ordered chronologically
	 *         from oldest to newest. Each grouping contains the period start date,
	 *         temporal unit, and list of productivity entries for that period.
	 *         Returns empty list if no data is found for the specified criteria.
	 */
	@SuppressWarnings("java:S3740")
	@Override
	public List<ProductivityTemporalGrouping> getProductivitiesGroupedByTemporalUnit(Set<String> hierarchyNodeIds,
			TemporalAggregationUnit temporalAggregationUnit, int limit) {
		Document dateTruncExpression = new Document("$dateTrunc", new Document("date", "$calculationDate")
				.append("unit", temporalAggregationUnit.getUnit()).append("timezone", "UTC"));

		Document groupId = new Document(temporalAggregationUnit.getUnit(), dateTruncExpression);

		AggregationOperation groupOperation = context -> new Document("$group",
				new Document("_id", groupId).append(ENTRIES,
						new Document("$push",
								new Document("calculationDate", "$calculationDate").append("_id", "$_id")
										.append(HIERARCHY_ENTITY_NODE_ID_FIELD_NAME, "$hierarchyEntityNodeId")
										.append("categoryScores", "$categoryScores"))));

		Aggregation aggregation = Aggregation.newAggregation(
				Aggregation.match(Criteria.where(HIERARCHY_ENTITY_NODE_ID_FIELD_NAME).in(hierarchyNodeIds)), groupOperation,
				Aggregation.sort(Sort.by(Sort.Direction.DESC, "_id." + temporalAggregationUnit.getUnit())),
				Aggregation.limit(limit));

		List<Map> response = this.mongoTemplate.aggregate(aggregation, COLLECTION_NAME_PRODUCTIVITY, Map.class)
				.getMappedResults();

		List<ProductivityTemporalGrouping> productivityTemporalGroupings = mapResponseToProductivityTemporalGroupingList(
				response, temporalAggregationUnit);

		// Reversing is required to have the data in an ASC order (oldest -> latest)
		Collections.reverse(productivityTemporalGroupings);
		return productivityTemporalGroupings;
	}

	@SuppressWarnings("java:S3740")
	private List<ProductivityTemporalGrouping> mapResponseToProductivityTemporalGroupingList(
			List<Map> mongoAggregationResponse, TemporalAggregationUnit temporalAggregationUnit) {
		List<ProductivityTemporalGrouping> productivityTemporalGroupings = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(mongoAggregationResponse)) {
			for (Map map : mongoAggregationResponse) {
				if (map.containsKey("_id") && map.containsKey(ENTRIES)) {
					ProductivityTemporalGrouping productivityTemporalGrouping = new ProductivityTemporalGrouping();
					productivityTemporalGrouping.setTemporalAggregationUnit(temporalAggregationUnit);
					Map timeUnitDateMap = (Map) (map.get("_id"));
					Date date = (Date) timeUnitDateMap.get(temporalAggregationUnit.getUnit().toLowerCase());
					productivityTemporalGrouping.setPeriodStart(date.toInstant());
					productivityTemporalGrouping
							.setProductivities(objectMapper.convertValue(map.get(ENTRIES), new TypeReference<>() {
							}));
					productivityTemporalGroupings.add(productivityTemporalGrouping);
				}
			}
		}
		return productivityTemporalGroupings;
	}
}
