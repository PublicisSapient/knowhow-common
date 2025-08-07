package com.publicissapient.kpidashboard.common.model.zephyr;

import lombok.Data;

@Data
public class TestCaseExecutionData {

	private Integer executionId;
	private String key;
	private Integer executionTime;
	private Integer estimatedTime;
	private String actualEndDate;
	private String comment;
	private Integer testCycleId;
	private Integer testCaseId;
	private String executedById;
	private String assignedToId;
	private boolean automated;
}
