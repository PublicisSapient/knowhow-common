package com.publicissapient.kpidashboard.common.model.application;

import java.util.List;

import org.bson.types.ObjectId;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author narsingh9
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Capacity Master Model")
public class CapacityMaster {
	@Schema(description = "Unique identifier for the Capacity Master record", example = "64b7f8e2c9e77a6f4d3e8b9a")
	private ObjectId id;

	@Schema(description = "Unique identifier for the project", example = "proj12345")
	private String projectNodeId;

	@Schema(description = "Name of the project", example = "Project Alpha")
	private String projectName;

	@Schema(description = "Unique identifier for the sprint", example = "sprint12345")
	private String sprintNodeId;

	@Schema(description = "Name of the sprint", example = "Sprint 1")
	private String sprintName;

	@Schema(description = "State of the sprint", example = "ACTIVE")
	private String sprintState;

	@Schema(description = "Capacity value", example = "40.0")
	private Double capacity;

	@Schema(description = "Start date of the sprint in yyyy-mm-dd format", example = "2024-06-01")
	private String startDate; // format yyyy-mm-dd

	@Schema(description = "End date of the sprint in yyyy-mm-dd format", example = "2024-06-15")
	private String endDate; // format yyyy-mm-dd

	@Schema(description = "Unique identifier for the basic project configuration", example = "60d21b8667d0d8992e610c85")
	private ObjectId basicProjectConfigId;

	@Schema(description = "List of additional filter capacities")
	private List<AdditionalFilterCapacity> additionalFilterCapacityList;

	@Schema(description = "List of assignee capacities")
	private List<AssigneeCapacity> assigneeCapacity;

	@Schema(description = "Indicates if Kanban methodology is used", example = "false")
	private boolean kanban;

	@Schema(description = "Indicates if assignee details are included", example = "true")
	private boolean assigneeDetails;
}
