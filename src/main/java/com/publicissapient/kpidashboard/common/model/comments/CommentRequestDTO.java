package com.publicissapient.kpidashboard.common.model.comments;

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
@Schema(description = "DTO for Comment Request")
public class CommentRequestDTO {

	@Schema(description = "Unique identifier for the project", example = "proj12345", required = true)
	private String node;

	@Schema(description = "Level of the comment", example = "PROJECT", required = true)
	private String level;

	@Schema(description = "Child identifier of the node", example = "child12345", required = true)
	private String nodeChildId;

	@Schema(description = "KPI identifier associated with the comment", example = "kpi12345", required = true)
	private String kpiId;
}
