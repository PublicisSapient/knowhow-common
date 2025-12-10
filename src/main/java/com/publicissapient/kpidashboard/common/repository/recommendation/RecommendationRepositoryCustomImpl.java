/*
 *   Copyright 2014 CapitalOne, LLC.
 *   Further development Copyright 2022 Sapient Corporation.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.publicissapient.kpidashboard.common.repository.recommendation;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.ReplaceRootOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.publicissapient.kpidashboard.common.model.recommendation.batch.RecommendationsActionPlan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository implementation for complex RecommendationsActionPlan queries
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRepositoryCustomImpl implements RecommendationRepositoryCustom {

	private static final String COLLECTION_NAME = "recommendations_action_plan";
	private static final String FIELD_BASIC_PROJECT_CONFIG_ID = "basicProjectConfigId";
	private static final String FIELD_CREATED_AT = "createdAt";
	private static final String FIELD_RECOMMENDATIONS = "recommendations";

	private final MongoOperations operations;

	/**
	 * Finds the latest N recommendations for each project using MongoDB
	 * Aggregation pipeline with push/slice pattern (compatible with MongoDB 4.x+).
	 *
	 * @param projectIds list of project identifiers (must not be null or empty)
	 * @param limit      number of recommendations per project (must be > 0)
	 * @return list of latest N recommendations per project
	 * @throws IllegalArgumentException if projectIds is null/empty or limit <= 0
	 */
	@Override
	public List<RecommendationsActionPlan> findLatestRecommendationsByProjectIds(List<String> projectIds, int limit) {
		validateInputParameters(projectIds, limit);

		log.debug("Executing aggregation pipeline to fetch {} latest recommendation(s) for {} projects",
				limit, projectIds.size());

		Aggregation aggregation = buildAggregation(projectIds, limit);
		List<RecommendationsActionPlan> recommendations = operations.aggregate(
				aggregation,
				COLLECTION_NAME,
				RecommendationsActionPlan.class).getMappedResults();

		log.debug("Successfully retrieved {} recommendation(s) using aggregation framework",
				recommendations.size());

		return recommendations;
	}

	/**
	 * Validates input parameters for aggregation query.
	 *
	 * @param projectIds list of project identifiers
	 * @param limit      number of recommendations per project
	 * @throws IllegalArgumentException if validation fails
	 */
	private void validateInputParameters(List<String> projectIds, int limit) {
		if (CollectionUtils.isEmpty(projectIds)) {
			log.error("Aggregation called with empty or null projectIds list");
			throw new IllegalArgumentException("Project IDs list must not be null or empty");
		}

		if (limit <= 0) {
			log.error("Aggregation called with invalid limit: {}", limit);
			throw new IllegalArgumentException("Limit must be greater than 0, got: " + limit);
		}
	}

	/**
	 * Builds Spring Data MongoDB Aggregation for fetching latest recommendations per project.
	 *
	 * @param projectIds list of project identifiers to filter
	 * @param limit      number of recommendations to fetch per project
	 * @return Aggregation object
	 */
	private Aggregation buildAggregation(List<String> projectIds, int limit) {
		// Stage 1: Match documents by project IDs
		MatchOperation matchStage = Aggregation.match(
				Criteria.where(FIELD_BASIC_PROJECT_CONFIG_ID).in(projectIds));

		// Stage 2: Sort by createdAt descending to get latest first
		SortOperation sortStage = Aggregation.sort(Sort.Direction.DESC, FIELD_CREATED_AT);

		// Stage 3: Group by project and push all documents into array
		GroupOperation groupStage = Aggregation.group(FIELD_BASIC_PROJECT_CONFIG_ID)
				.push("$$ROOT").as(FIELD_RECOMMENDATIONS);

		// Stage 4: Slice array to limit to N documents per project
		ProjectionOperation sliceStage = Aggregation.project()
				.and(FIELD_RECOMMENDATIONS).slice(limit).as(FIELD_RECOMMENDATIONS);

		// Stage 5: Unwind the recommendations array
		UnwindOperation unwindStage = Aggregation.unwind(FIELD_RECOMMENDATIONS);

		// Stage 6: Replace root to return clean recommendation documents
		ReplaceRootOperation replaceRootStage = Aggregation.replaceRoot(FIELD_RECOMMENDATIONS);

		return Aggregation.newAggregation(
				matchStage,
				sortStage,
				groupStage,
				sliceStage,
				unwindStage,
				replaceRootStage);
	}
}

