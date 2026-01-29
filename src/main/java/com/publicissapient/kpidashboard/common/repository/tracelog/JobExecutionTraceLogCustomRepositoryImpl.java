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

package com.publicissapient.kpidashboard.common.repository.tracelog;

import java.util.Set;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.publicissapient.kpidashboard.common.model.tracelog.JobExecutionTraceLog;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JobExecutionTraceLogCustomRepositoryImpl implements JobExecutionTraceLogCustomRepository {

	private final MongoTemplate mongoTemplate;

	@Override
	public void updateJobExecutionOngoing(Set<String> jobNames, String processorName, boolean executionOngoing) {
		Query query = new Query(Criteria.where("jobName").in(jobNames).and("processorName").is(processorName));
		Update update = new Update().set("executionOngoing", executionOngoing);

		this.mongoTemplate.updateMulti(query, update, JobExecutionTraceLog.class);
	}
}
