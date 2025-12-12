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

import com.publicissapient.kpidashboard.common.model.recommendation.batch.RecommendationsActionPlan;

/**
 * Custom repository interface for complex RecommendationsActionPlan queries
 * using MongoDB aggregation pipelines.
 */
public interface RecommendationRepositoryCustom {

	/**
	 * Finds latest N recommendations per project sorted by severity priority
	 * (CRITICAL→LOW) then creation date (latest first).
	 *
	 * <p>
	 * <b>Pipeline:</b> $match → $addFields (severityPriority) → $sort → $group →
	 * $slice → $unwind → $replaceRoot → $sort (final cross-project sorting)
	 *
	 * @param projectIds
	 *            list of project identifiers (must not be null or empty)
	 * @param limit
	 *            number of recommendations per project (must be > 0)
	 * @return list of recommendations sorted by severity priority across all
	 *         projects
	 * @throws IllegalArgumentException
	 *             if projectIds is null/empty or limit <= 0
	 */
	List<RecommendationsActionPlan> findLatestRecommendationsByProjectIds(List<String> projectIds, int limit);
}
