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

import java.util.ArrayList;
import java.util.List;

import com.publicissapient.kpidashboard.common.model.recommendation.batch.RecommendationLevel;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Switch.CaseOperator;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.publicissapient.kpidashboard.common.model.recommendation.batch.RecommendationsActionPlan;
import com.publicissapient.kpidashboard.common.model.recommendation.batch.Severity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom repository implementation for complex RecommendationsActionPlan
 * queries
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRepositoryCustomImpl implements RecommendationRepositoryCustom {

    private static final String COLLECTION_NAME = "recommendations_action_plan";
	private static final String FIELD_BASIC_PROJECT_CONFIG_ID = "basicProjectConfigId";
	private static final String FIELD_CREATED_AT = "createdAt";
	private static final String FIELD_RECOMMENDATIONS = "recommendations";
	private static final String FIELD_SEVERITY = "recommendations.severity";
	private static final String FIELD_SEVERITY_PRIORITY = "severityPriority";
    public static final String KPI_ID = "kpiId";
    public static final String LEVEL = "level";

    private final MongoOperations operations;

	@Override
	public List<RecommendationsActionPlan> findLatestRecommendationsByProjectIds(List<String> projectIds, int limit, RecommendationLevel level) {
		validateInputParameters(projectIds, limit);

		log.debug("Executing aggregation pipeline to fetch {} latest recommendation(s) for {} projects with level filter: {}", limit,
				projectIds.size(), level);

		Aggregation aggregation = buildAggregation(projectIds, limit, level);
		List<RecommendationsActionPlan> recommendations = operations
				.aggregate(aggregation, COLLECTION_NAME, RecommendationsActionPlan.class).getMappedResults();

		log.debug("Successfully retrieved {} recommendation(s) using aggregation framework", recommendations.size());

		return recommendations;
	}

	@Override
	public RecommendationsActionPlan findLatestRecommendationByProjectAndKpi(
			String basicProjectConfigId, String kpiId, RecommendationLevel level) {

		if (basicProjectConfigId == null || kpiId == null || level == null) {
			log.warn("Invalid parameters: basicProjectConfigId={}, kpiId={}, level={}",
					basicProjectConfigId, kpiId, level);
			return null;
		}

		log.debug("Fetching latest {} recommendation for project {} and KPI {}",
				level, basicProjectConfigId, kpiId);

		Criteria criteria = Criteria.where(FIELD_BASIC_PROJECT_CONFIG_ID).is(basicProjectConfigId)
				.and(KPI_ID).is(kpiId)
				.and(LEVEL).is(level);

		Query query = new Query(criteria)
				.with(Sort.by(Sort.Direction.DESC, FIELD_CREATED_AT))
				.limit(1);

		RecommendationsActionPlan recommendation = operations.findOne(query, RecommendationsActionPlan.class, COLLECTION_NAME);

		if (recommendation != null) {
			log.debug("Found recommendation with ID {} for project {} and KPI {}",
					recommendation.getId(), basicProjectConfigId, kpiId);
		} else {
			log.debug("No {} recommendation found for project {} and KPI {}",
					level, basicProjectConfigId, kpiId);
		}

		return recommendation;
	}

	/**
	 * Validates input parameters for aggregation query.
	 *
	 * @param projectIds
	 *          list of project identifiers
	 * @param limit
	 *          number of recommendations per project
	 * @throws IllegalArgumentException
	 *           if validation fails
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
	 * Builds MongoDB aggregation pipeline with severity-based sorting and optional level filtering.
	 * Pipeline stages: match (projectIds) → match (level, conditional) → addFields → sort → group → slice → unwind → replaceRoot → sort
	 */
	private Aggregation buildAggregation(List<String> projectIds, int limit, RecommendationLevel level) {
		List<AggregationOperation> stages = new ArrayList<>();
		
		// Stage 1: Match documents by project IDs
		stages.add(Aggregation.match(Criteria.where(FIELD_BASIC_PROJECT_CONFIG_ID).in(projectIds)));

		// Stage 2: Optional level filter - only add if level is specified
		if (level != null) {
			stages.add(Aggregation.match(Criteria.where(LEVEL).is(level)));
			log.debug("Added level filter stage for: {}", level);
		}

		// Stage 3: Add computed field for severity priority (CRITICAL=1, HIGH=2, MEDIUM=3, LOW=4)
		stages.add(buildSeverityPriorityMapping());

		// Stage 4: Sort by severity priority ASC (CRITICAL first), then createdAt DESC (latest first) - within each project
		stages.add(Aggregation.sort(Sort.by(Sort.Order.asc(FIELD_SEVERITY_PRIORITY), Sort.Order.desc(FIELD_CREATED_AT))));

		// Stage 5: Group by project and push all documents into array
		stages.add(Aggregation.group(FIELD_BASIC_PROJECT_CONFIG_ID).push("$$ROOT").as(FIELD_RECOMMENDATIONS));

		// Stage 6: Slice array to limit to N documents per project
		stages.add(Aggregation.project().and(FIELD_RECOMMENDATIONS).slice(limit).as(FIELD_RECOMMENDATIONS));

		// Stage 7: Unwind the recommendations array
		stages.add(Aggregation.unwind(FIELD_RECOMMENDATIONS));

		// Stage 8: Replace root to return clean recommendation documents
		stages.add(Aggregation.replaceRoot(FIELD_RECOMMENDATIONS));

		// Stage 9: Final sort across all projects by severity priority (CRITICAL projects first)
		stages.add(Aggregation.sort(Sort.by(Sort.Order.asc(FIELD_SEVERITY_PRIORITY), Sort.Order.desc(FIELD_CREATED_AT))));

		return Aggregation.newAggregation(stages);
	}

	/**
	 * Creates $addFields stage to map severity enum to numeric priority using
	 * $switch operator. Mapping: CRITICAL=1, HIGH=2, MEDIUM=3, LOW=4, unknown=999
	 * (dynamically from {@link Severity} enum).
	 *
	 * @return AddFieldsOperation with severityPriority computed field
	 */
	private AddFieldsOperation buildSeverityPriorityMapping() {
		CaseOperator criticalCase = CaseOperator
				.when(ComparisonOperators.Eq.valueOf(FIELD_SEVERITY).equalToValue(Severity.CRITICAL.name()))
				.then(Severity.CRITICAL.getPriority());

		CaseOperator highCase = CaseOperator
				.when(ComparisonOperators.Eq.valueOf(FIELD_SEVERITY).equalToValue(Severity.HIGH.name()))
				.then(Severity.HIGH.getPriority());

		CaseOperator mediumCase = CaseOperator
				.when(ComparisonOperators.Eq.valueOf(FIELD_SEVERITY).equalToValue(Severity.MEDIUM.name()))
				.then(Severity.MEDIUM.getPriority());

		CaseOperator lowCase = CaseOperator
				.when(ComparisonOperators.Eq.valueOf(FIELD_SEVERITY).equalToValue(Severity.LOW.name()))
				.then(Severity.LOW.getPriority());

		ConditionalOperators.Switch switchBuilder = ConditionalOperators
				.switchCases(criticalCase, highCase, mediumCase, lowCase).defaultTo(999); // Fallback for null/unknown
		// severity - sorts last

		return Aggregation.addFields().addField(FIELD_SEVERITY_PRIORITY).withValue(switchBuilder).build();
	}
}
