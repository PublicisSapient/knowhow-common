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

package com.publicissapient.kpidashboard.common.model.application;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import com.publicissapient.kpidashboard.common.model.generic.BasicModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "test_suite_execution")
@CompoundIndex(def = "{'basicProjectConfigId': 1, 'buildTimestamp': 1}")
public class TestSuiteExecution extends BasicModel {

	private String basicProjectConfigId;
	private ObjectId processorItemId;
	private String toolType;
	private String jobName;
	private String buildNumber;
	private String buildUrl;
	private Long buildTimestamp;
	private String buildBranch;
	private String suiteName;
	private Integer totalTests;
	private Integer passedTests;
	private Integer failedTests;
	private Integer skippedTests;
}
