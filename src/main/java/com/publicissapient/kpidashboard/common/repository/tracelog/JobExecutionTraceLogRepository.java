/*
 * Copyright 2014 CapitalOne, LLC.
 * Further development Copyright 2022 Sapient Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.publicissapient.kpidashboard.common.repository.tracelog;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.publicissapient.kpidashboard.common.model.tracelog.JobExecutionTraceLog;

/**
 * Repository for job execution trace logs.
 *
 * @author shunaray
 */
@Repository
public interface JobExecutionTraceLogRepository extends MongoRepository<JobExecutionTraceLog, ObjectId> {

	/**
	 * Find last execution trace logs by processor and job name ordered by execution
	 * start time descending.
	 *
	 * @param processorName
	 *          the processor name (e.g., "ai-data", "jira")
	 * @param jobName
	 *          the job name (e.g., "calculate-kpi-maturity")
	 * @param pageable
	 *          pagination parameters
	 * @return list of execution trace logs
	 */
	@Query(value = "{ 'processorName': ?0, 'jobName': ?1 }", sort = "{ 'executionStartedAt': -1 }")
	List<JobExecutionTraceLog> findLastExecutionTraceLogsByProcessorAndJobName(String processorName, String jobName,
			Pageable pageable);

	/**
	 * Find all execution trace logs by processor and job name.
	 *
	 * @param processorName
	 *          the processor name (e.g., "ai-data", "jira")
	 * @param jobName
	 *          the job name (e.g., "calculate-kpi-maturity")
	 * @return list of execution trace logs
	 */
	List<JobExecutionTraceLog> findByProcessorNameAndJobName(String processorName, String jobName);

	/**
	 * Find execution trace log by ID.
	 *
	 * @param id
	 *          the execution trace log ID
	 * @return optional execution trace log
	 */
	Optional<JobExecutionTraceLog> findById(ObjectId id);
}
