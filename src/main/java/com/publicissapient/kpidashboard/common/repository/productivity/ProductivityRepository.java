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

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.publicissapient.kpidashboard.common.model.productivity.calculation.Productivity;

@Repository
public interface ProductivityRepository extends MongoRepository<Productivity, ObjectId> {
	List<Productivity> findByHierarchyEntityName(String entityName);

	@Query(value = "{'hierarchyEntityName': ?0}", fields = "{'hierarchyEntityName': 1, 'hierarchyLevelId': 1, 'categoryScores': 1}")
	List<Productivity> findCategoryScoresByHierarchyEntityName(String hierarchyEntity);

	@Query(value = "{'hierarchyEntityName': ?0}", fields = "{'hierarchyEntityName': 1, 'hierarchyLevelId': 1, 'kpis': 1, 'calculationDate': 1}")
	List<Productivity> findKpiDataPointsByHierarchyEntityName(String hierarchyEntity);

	@Query(value = "{'hierarchyEntityName': ?0}", fields = "{'hierarchyEntityName': 1, 'hierarchyLevelId': 1, 'kpis': 1, 'calculationDate': 1}", sort = "{'calculationDate': -1}")
	Productivity findLatestKpiDataPointsByHierarchyEntityName(String hierarchyEntity);

	@Query(value = "{'hierarchyEntityName': ?0}", fields = "{'hierarchyEntityName': 1, 'hierarchyLevelId': 1, 'categoryScores': 1}", sort = "{'calculationDate': -1}")
	Productivity findLatestCategoryScoresByHierarchyEntityName(String hierarchyEntity);

	Productivity findFirstByHierarchyEntityNameOrderByCalculationDateDesc(String hierarchyEntity);
}
