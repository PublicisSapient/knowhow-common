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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.publicissapient.kpidashboard.common.model.tracelog.JobExecutionTraceLog;
import com.publicissapient.kpidashboard.common.repository.tracelog.JobExecutionTraceLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobExecutionTraceLogServiceImpl implements JobExecutionTraceLogService {

	private final JobExecutionTraceLogRepository jobExecutionTraceLogRepository;

	@Override
	public JobExecutionTraceLog createJobExecution(String jobName) {
		Instant startingTime = Instant.now();
		JobExecutionTraceLog executionTraceLog = new JobExecutionTraceLog();
		executionTraceLog.setProcessorName(jobName);
		executionTraceLog.setExecutionOngoing(true);
		executionTraceLog.setExecutionStartedAt(startingTime.toEpochMilli());
		executionTraceLog.setExecutionSuccess(true);
		
		JobExecutionTraceLog savedLog = this.jobExecutionTraceLogRepository.save(executionTraceLog);
		log.info("Created new job execution trace log for job '{}' with id '{}'", jobName, savedLog.getId());
		return savedLog;
	}

	@Override
	public void updateJobExecution(JobExecutionTraceLog executionTraceLog) {
		JobExecutionTraceLog savedLog = this.jobExecutionTraceLogRepository.save(executionTraceLog);
		log.info("Updated job execution trace log for job '{}' with id '{}', status: {}", 
				savedLog.getProcessorName(), savedLog.getId(), savedLog.isExecutionSuccess());
	}

	@Override
	public Optional<JobExecutionTraceLog> findById(ObjectId id) {
		return this.jobExecutionTraceLogRepository.findById(id);
	}

	@Override
	public List<JobExecutionTraceLog> findLastExecutionsByJobName(String jobName, int numberOfExecutions) {
		return this.jobExecutionTraceLogRepository
				.findLastExecutionTraceLogsByProcessorName(jobName, PageRequest.ofSize(numberOfExecutions));
	}

	@Override
	public List<JobExecutionTraceLog> findByJobName(String jobName) {
		return this.jobExecutionTraceLogRepository.findByProcessorName(jobName);
	}

	@Override
	public boolean isJobCurrentlyRunning(String jobName) {
		List<JobExecutionTraceLog> executionTraceLogs = findLastExecutionsByJobName(jobName, 1);
		return CollectionUtils.isNotEmpty(executionTraceLogs) 
				&& executionTraceLogs.get(0).isExecutionOngoing();
	}
}
