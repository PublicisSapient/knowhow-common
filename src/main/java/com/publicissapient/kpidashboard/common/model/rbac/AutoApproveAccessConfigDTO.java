package com.publicissapient.kpidashboard.common.model.rbac;

import java.util.List;

import org.bson.types.ObjectId;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Auto Approve Access Configuration Data Transfer Object")
public class AutoApproveAccessConfigDTO {
	@Schema(description = "Unique identifier for the Auto Approve Access Configuration", example = "64b7f8e2c9e77a6f4d3e8b9a")
	private ObjectId id;

	@Schema(description = "Indicates whether auto approve is enabled", example = "true")
	private String enableAutoApprove;

	@Schema(description = "List of roles eligible for auto approval")
	private List<RoleData> roles;

	@Override
	public String toString() {
		return "AutoAccessApprovalDTO [isAutoApproved=" + enableAutoApprove + ", roles=" + roles + "]";
	}
}
