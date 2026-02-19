package com.publicissapient.kpidashboard.common.model.application;

import java.util.Set;

import com.google.common.base.Objects;
import com.publicissapient.kpidashboard.common.constant.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "Assignee Capacity Details")
public class AssigneeCapacity {
	@Schema(description = "Unique identifier for the user", example = "user12345")
	private String userId;

	@Schema(description = "Name of the user", example = "John Doe")
	private String userName;

	@Schema(description = "E-mail addresses", example = "[\"user1@mail.com\", \"use2r@mail.com\"]")
	private Set<String> email;

	@Schema(description = "Role of the user", example = "DEVELOPER")
	private Role role;

	@Schema(description = "Squad of the user", example = "Alpha Squad")
	private String squad;

	@Schema(description = "Planned capacity of the user", example = "80.0")
	private Double plannedCapacity;

	@Schema(description = "Leaves taken by the user", example = "8.0")
	private Double leaves;

	@Schema(description = "Available capacity of the user", example = "72.0")
	private Double availableCapacity;

	@Schema(description = "Happiness rating of the user", example = "5")
	private Integer happinessRating;

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AssigneeCapacity) {
			AssigneeCapacity that = (AssigneeCapacity) obj;
			return Objects.equal(this.userId, that.userId) && Objects.equal(this.userName, that.userName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(userId, role);
	}
}
