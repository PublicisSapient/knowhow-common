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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author narsingh9
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Data Transfer Object for Project Basic Configuration")
public class ProjectBasicConfigDTO {
	@Schema(description = "Unique identifier of the Project Basic Configuration", example = "64b8f0c2e1b2c3a4d5e6f7g8")
	private ObjectId id;

	@Schema(description = "Unique identifier of the Project Node", example = "projectNode12345")
	private String projectNodeId;

	@Schema(description = "Name of the project", example = "Project Alpha")
	private String projectName;

	@Schema(description = "Display name of the project", example = "Alpha Project")
	private String projectDisplayName;

	@Schema(description = "Timestamp of when the Project Basic Configuration was created", example = "2024-06-10T08:30:00Z")
	private String createdAt;

	@Schema(description = "User who created the Project Basic Configuration", example = "adminUser")
	private String createdBy;

	@Schema(description = "Timestamp of the last update to the Project Basic Configuration", example = "2024-06-15T10:00:00Z")
	private String updatedAt;

	@Schema(description = "User who last updated the Project Basic Configuration", example = "editorUser")
	private String updatedBy;

	@Deprecated
	@Schema(description = "Timestamp of when the consumer was created", example = "2024-06-05T09:15:00Z")
	private String consumerCreatedOn;

	@Schema(description = "Indicates if the project follows Kanban methodology", example = "true")
	private boolean kanban;

	@Schema(description = "List of tool configuration IDs associated with the project", example = "[\"toolConfigId1\", \"toolConfigId2\"]")
	private List<HierarchyValueDTO> hierarchy;

	@Schema(description = "Should assignee details be saved", example = "true")
	private boolean saveAssigneeDetails;

	@Schema(description = "Is developer KPI enabled for the project", example = "false")
	private boolean developerKpiEnabled;

	@Schema(description = "Is the project currently on hold", example = "false")
	private boolean projectOnHold;

	@Schema(description = "Strength of the team working on the project", example = "5")
	private int teamStrength;

	@Schema(description = "Identifier of the Project Basic Configuration from which this project was cloned", example = "64b8f0c2e1b2c3a4d5e6f7g7")
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
