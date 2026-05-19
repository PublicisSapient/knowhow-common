package com.publicissapient.kpidashboard.common.model.application.dto;

import java.util.List;

import lombok.Data;

@Data
public class CycleTimeGroup {
	private String label;
	private List<String> statuses;
}
