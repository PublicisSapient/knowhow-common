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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "KPI Column Details")
public class KpiColumnDetails {
	@Schema(description = "Column Name", example = "Velocity")
	private String columnName;

	@Schema(description = "Order of the Column", example = "1")
	private int order;

	@Schema(description = "Is the KPI Shown Flag", example = "true")
	private boolean isShown;

	@Schema(description = "Is the KPI Default Flag", example = "false")
	private boolean isDefault;

	public boolean getIsShown() {
		return isShown;
	}

	public void setIsShown(boolean shown) {
		isShown = shown;
	}

	public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean aDefault) {
		isDefault = aDefault;
	}
}
