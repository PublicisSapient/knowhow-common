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

package com.publicissapient.kpidashboard.common.model.tracelog;

import java.util.List;

import com.publicissapient.kpidashboard.common.model.generic.BasicModel;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import com.publicissapient.kpidashboard.common.model.application.ErrorDetail;

import lombok.Data;

/**
 * MongoDB entity for tracking job executions.
 */
@Data
@Document("job_execution_trace_log")
@EqualsAndHashCode(callSuper = true)
public class JobExecutionTraceLog extends BasicModel {

	/**
	 * Name of the processor/job (e.g., "KpiMaturityCalculationJob",
	 * "RecommendationCalculationJob")
	 */
	private String processorName;

	/**
	 * Job execution start time in milliseconds
	 */
	private long executionStartedAt;

	/**
	 * Job execution end time in milliseconds
	 */
	private long executionEndedAt;

	/**
	 * Indicates if the job execution is currently ongoing
	 */
	private boolean executionOngoing;

	/**
	 * Indicates if the job execution was successful
	 */
	private boolean executionSuccess;

	/**
	 * List of errors encountered during job execution
	 */
	private List<ErrorDetail> errorDetailList;
}
