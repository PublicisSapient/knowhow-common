/*
 *  Copyright 2024 Sapient Corporation
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

package com.publicissapient.kpidashboard.common.repository.recommendation;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.publicissapient.kpidashboard.common.model.recommendation.batch.RecommendationsActionPlan;

/** Repository interface for RecommendationsActionPlan entity. */
@Repository
public interface RecommendationRepository extends MongoRepository<RecommendationsActionPlan, ObjectId> {

	/**
	 * Finds latest recommendations for multiple projects. Used for hierarchical
	 * rollup.
	 *
	 * @param projectIds
	 *          list of project identifiers
	 * @return list of latest recommendations for each project
	 */
	List<RecommendationsActionPlan> findByProjectIdInOrderByCreatedAtDesc(List<String> projectIds);
}
