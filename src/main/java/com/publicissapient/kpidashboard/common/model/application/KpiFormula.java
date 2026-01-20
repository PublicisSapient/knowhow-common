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

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author yasbano
 */
@Data
@Schema(description = "KPI Formula details")
public class KpiFormula {

	@Schema(description = "Type of the KPI formula", example = "BINARY")
	private String type;

	@Schema(description = "Left hand side of the formula", example = "kpi1")
	private String lhs;

	@Schema(description = "Right hand side of the formula", example = "kpi2")
	private String rhs;

	@Schema(description = "Operator used in the formula", example = "+")
	private String operator;

	@Schema(description = "List of operands involved in the formula", example = "[\"kpi1\", \"kpi2\"]")
	private List<String> operands;
}
