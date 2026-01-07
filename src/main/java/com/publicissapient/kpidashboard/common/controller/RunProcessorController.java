/*******************************************************************************
 * Copyright 2014 CapitalOne, LLC.
 * Further development Copyright 2022 Sapient Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package com.publicissapient.kpidashboard.common.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.publicissapient.kpidashboard.common.context.ExecutionLogContext;
import com.publicissapient.kpidashboard.common.executor.ProcessorJobExecutor;
import com.publicissapient.kpidashboard.common.model.ProcessorExecutionBasicConfig;
import com.publicissapient.kpidashboard.common.repository.application.ProjectBasicConfigRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Rest Controller to handle bit bucket specific requests.
 *
 * @author swati.lamba
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RunProcessorController {

	private static final ExecutorService PROCESSOR_EXECUTORS = Executors.newFixedThreadPool(5);

	@Autowired(required = false)
	private ProcessorJobExecutor<?> jobExecutor;

	@Autowired
	private ProjectBasicConfigRepository projectBasicConfigRepository;

	@RequestMapping(value = "/processor/run", method = RequestMethod.POST, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, String>> runProcessorForProjects(
			@RequestBody ProcessorExecutionBasicConfig processorExecutionBasicConfig) {
		if (jobExecutor == null) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("status", "error");
			errorResponse.put("message", "No processor available");
			return ResponseEntity.badRequest().body(errorResponse);
		}

		ExecutionLogContext.set(processorExecutionBasicConfig.getLogContext());
		MDC.put("Processor Name", jobExecutor.getProcessor().getProcessorName());
		MDC.put("RequestStartTime", String.valueOf(System.currentTimeMillis()));
		log.info("Received request to run the processor: {} for projects {}", jobExecutor.getProcessor().getProcessorName(),
				processorExecutionBasicConfig.getProjectBasicConfigIds());

		if (processorExecutionBasicConfig.getScmProcessorName() != null) {
			jobExecutor.setProcessorLabel(processorExecutionBasicConfig.getScmProcessorName());
		}
		projectBasicConfigRepository.findActiveProjects(false);
		jobExecutor.setProjectsBasicConfigIds(processorExecutionBasicConfig.getProjectBasicConfigIds());
		jobExecutor.setExecutionLogContext(ExecutionLogContext.getContext());
		PROCESSOR_EXECUTORS.execute(jobExecutor);

		MDC.put("RequestEndTime", String.valueOf(System.currentTimeMillis()));
		log.info("Processor execution called");
		ExecutionLogContext.getContext().destroy();
		jobExecutor.getExecutionLogContext().destroy();
		MDC.clear();
		Map<String, String> response = new HashMap<>();
		response.put("status", "processing");
		return ResponseEntity.ok().body(response);
	}
}
