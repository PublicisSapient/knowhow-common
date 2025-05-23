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

package com.publicissapient.kpidashboard.common.model.application.dto;

import java.util.List;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author narsingh9
 */
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectBasicConfigDTO {
	private ObjectId id;
	private String projectNodeId;

	private String projectName;
	private String projectDisplayName;

	private String createdAt;
	private String createdBy;
	private String updatedAt;
	private String updatedBy;

	@Deprecated
	private String consumerCreatedOn;
	private boolean kanban;
	private List<HierarchyValueDTO> hierarchy;
	private boolean saveAssigneeDetails;
	private boolean developerKpiEnabled;
	private boolean projectOnHold;
	private ObjectId clonedFrom;

	/**
	 * @return isKanban value
	 */
	public boolean getIsKanban() {
		return this.kanban;
	}

	/**
	 * set isKanban value
	 *
	 * @param isKanban
	 *          boolean value
	 */
	public void setIsKanban(boolean isKanban) {
		this.kanban = isKanban;
	}
}
