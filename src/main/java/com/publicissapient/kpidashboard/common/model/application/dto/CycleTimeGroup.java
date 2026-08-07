package com.publicissapient.kpidashboard.common.model.application.dto;

import java.util.List;

import lombok.Data;

@Data
public class CycleTimeGroup {
	private String label;
	private String fieldName;
	private Integer weightage;
	private String prompt;
	private List<String> statuses;
}
