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

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.publicissapient.kpidashboard.common.model.application.ErrorDetail;
import com.publicissapient.kpidashboard.common.model.generic.BasicModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** MongoDB entity for tracking job executions. */
@Data
@Document("job_execution_trace_log")
@EqualsAndHashCode(callSuper = true)
public class JobExecutionTraceLog extends BasicModel {

	/**
	 * Name of the processor (e.g., "ai-data-processor", "jira", "scm").
	 * A processor can contain multiple jobs.
	 */
	private String processorName;

	/**
	 * Name of the specific job being executed (e.g., "calculate-kpi-maturity",
	 * "calculate-productivity", "calculate-recommendation").
	 * Multiple jobs can exist under a single processor.
	 */
	private String jobName;

	/** Job execution start time as Instant */
	private Instant executionStartedAt;

	/** Job execution end time as Instant */
	private Instant executionEndedAt;

	/** Indicates if the job execution is currently ongoing */
	private boolean executionOngoing;

	/** Indicates if the job execution was successful */
	private boolean executionSuccess;

	/** List of errors encountered during job execution */
	private List<ErrorDetail> errorDetailList;
}
