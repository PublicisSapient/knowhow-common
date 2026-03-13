package com.publicissapient.kpidashboard.common.model.jira;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "User Rating Data Transfer Object")
public class UserRatingDTO {
	@Schema(description = "Rating given by the user", example = "5")
	private Integer rating;

	@Schema(description = "The username", example = "user123")
	private String userName;

	@Schema(description = "Unique identifier for the user", example = "user-id-12345")
	private String userId;
}
