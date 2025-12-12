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
	 * Finds the latest N recommendations for each project using MongoDB aggregation
	 * pipeline.
	 *
	 * @param projectIds
	 *          list of project identifiers
	 * @param limit
	 *          number of recommendations per project
	 * @return list of latest N recommendations per project
	 */
	List<RecommendationsActionPlan> findLatestRecommendationsByProjectIds(List<String> projectIds, int limit);
}
