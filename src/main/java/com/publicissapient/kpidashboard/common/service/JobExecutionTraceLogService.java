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

package com.publicissapient.kpidashboard.common.service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;

import com.publicissapient.kpidashboard.common.model.tracelog.JobExecutionTraceLog;

/**
 * Service interface for managing job execution trace logs.
 *
 * @author shunaray
 */
public interface JobExecutionTraceLogService {

	/**
	 * Creates a new job execution trace log entry for a specific job under a processor.
	 *
	 * @param processorName
	 *          the name of the processor (e.g., "ai-data", "jira", "scm")
	 * @param jobName
	 *          the name of the job (e.g., "calculate-kpi-maturity", "calculate-productivity")
	 * @return the created execution trace log
	 */
	JobExecutionTraceLog createProcessorJobExecution(String processorName, String jobName);

	/**
	 * Updates an existing job execution trace log.
	 *
	 * @param executionTraceLog
	 *          the execution trace log to update
	 */
	void updateJobExecution(JobExecutionTraceLog executionTraceLog);

	/**
	 * Finds an execution trace log by ID.
	 *
	 * @param id
	 *          the execution trace log ID
	 * @return optional execution trace log
	 */
	Optional<JobExecutionTraceLog> findById(ObjectId id);

	/**
	 * Finds the last N execution trace logs for a specific job under a processor.
	 *
	 * @param processorName
	 *          the processor name (e.g., "ai-data", "jira")
	 * @param jobName
	 *          the specific job name to search for
	 * @param numberOfExecutions
	 *          number of recent executions to fetch
	 * @return list of execution trace logs
	 */
	List<JobExecutionTraceLog> findLastExecutionsByProcessorAndJobName(String processorName, String jobName, int numberOfExecutions);

	/**
	 * Checks if a job under a specific processor is currently running.
	 *
	 * @param processorName
	 *          the processor name (e.g., "ai-data", "jira")
	 * @param jobName
	 *          the specific job name under the processor
	 * @return true if job is currently running, false otherwise
	 */
	boolean isJobCurrentlyRunning(String processorName, String jobName);
}
