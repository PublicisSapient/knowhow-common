package com.publicissapient.kpidashboard.common.model.jira;

import java.util.List;

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
public class HappinessKpiDTO {
	@Schema(description = "List of User Rating Data Transfer Objects")
	List<UserRatingDTO> userRatingList;

	@Schema(description = "Basic project configuration identifier", example = "60d21b8667d0d8992e610c85")
	private String basicProjectConfigId;

	@Schema(description = "Sprint identifier", example = "sprint12345")
	private String sprintID;

	@Schema(description = "Date of submission in yyyy-mm-dd format", example = "2024-06-15")
	private String dateOfSubmission;
}
