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

/** */
package com.publicissapient.kpidashboard.common.model.testexecution;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author sansharm13
 */
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object representing Test Execution Data")
public class TestExecutionData {
	@Schema(description = "Unique identifier of the project node", example = "60d5ec49f8d2e30f8c8b4567")
	private String projectNodeId;

	@Schema(description = "Name of the project", example = "Project Alpha")
	private String projectName;

	@Schema(description = "Identifier of the sprint", example = "SPRINT-123")
	private String sprintName;

	@Schema(description = "Unique identifier of the sprint", example = "60d5ec49f8d2e30f8c8b4568")
	private String sprintId;

	@Schema(description = "State of the sprint", example = "ACTIVE")
	private String sprintState;

	@Schema(description = "Total number of test cases", example = "150")
	private Integer totalTestCases;

	@Schema(description = "Number of executed test cases", example = "120")
	private Integer executedTestCase;

	@Schema(description = "Number of passed test cases", example = "100")
	private Integer passedTestCase;

	@Schema(description = "Number of failed test cases", example = "20")
	private Integer automatedTestCases;

	@Schema(description = "Number of automatable test cases", example = "80")
	private Integer automatableTestCases;

	@Schema(description = "Number of automated regression test cases", example = "50")
	private Integer automatedRegressionTestCases;

	@Schema(description = "Total number of regression test cases", example = "70")
	private Integer totalRegressionTestCases;

	@Schema(description = "Date of execution in ISO format", example = "2024-06-15T10:00:00Z")
	private String executionDate;

	@Schema(description = "Identifier of the basic project configuration", example = "60d5ec49f8d2e30f8c8b4569")
	private String basicProjectConfigId;

	@Schema(description = "Indicates if the project follows Kanban methodology", example = "false")
	private boolean kanban;

	@Schema(description = "Flag to enable or disable upload", example = "true")
	private boolean uploadEnable;
}
