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

package com.publicissapient.kpidashboard.common.model.application;

import java.util.List;

import org.bson.types.ObjectId;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "KPI Column Configuration Data Transfer Object")
public class KpiColumnConfigDTO {
	@Schema(description = "Unique Identifier", example = "64b7f8f5e1d2c3a4b5c6d7e8")
	private ObjectId id;

	@Schema(description = "Basic Project Configuration Identifier", example = "64b7f8f5e1d2c3a4b5c6d7e9")
	private ObjectId basicProjectConfigId;

	@Schema(description = "KPI Identifier", example = "kpi12345")
	private String kpiId;

	@Schema(description = "List of KPI Column Details", implementation = KpiColumnDetails.class)
	private List<KpiColumnDetails> kpiColumnDetails;

	@Schema(description = "Save Flag", example = "true")
	private boolean saveFlag;
}
