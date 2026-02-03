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
package com.publicissapient.kpidashboard.common.model.userboardconfig;

import com.publicissapient.kpidashboard.common.model.application.KpiMaster;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO for boardkpis
 *
 * @author narsingh9
 */
@Data
@Schema(description = "Data Transfer Object representing the configuration of KPIs on a user board.")
public class BoardKpisDTO {
	@Schema(description = "Unique identifier of the KPI", example = "kpi123")
	private String kpiId;

	@Schema(description = "Name of the KPI", example = "Velocity")
	private String kpiName;

	@Schema(description = "Indicates if the KPI is enabled", example = "true")
	private boolean isEnabled;

	@Schema(description = "Indicates if the KPI is shown", example = "true")
	private boolean isShown;

	@Schema(description = "Order of the KPI", example = "1")
	private int order;

	@Schema(description = "Category of the board", example = "Agile")
	private String subCategoryBoard;

	@Schema(description = "Details of the KPI", implementation = KpiMaster.class)
	private KpiMaster kpiDetail;

	/**
	 * getter for isEnabled
	 *
	 * @return boolean
	 */
	public boolean getIsEnabled() {
		return this.isEnabled;
	}

	/**
	 * setter for isEnabled
	 *
	 * @param isEnabled
	 *          isEnabled
	 */
	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
}
