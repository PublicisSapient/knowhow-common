package com.publicissapient.kpidashboard.common.model.comments;

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
@Schema(description = "DTO for Comment View Request")
public class CommentViewRequestDTO {

	@Schema(description = "List of unique identifiers for the projects", example = "[\"proj12345\", \"proj67890\"]")
	private List<String> nodes;

	@Schema(description = "Level of the comments", example = "PROJECT")
	private String level;

	@Schema(description = "Child identifier of the node", example = "child12345")
	private String nodeChildId;

	@Schema(description = "List of KPI identifiers associated with the comments", example = "[\"kpi12345\", \"kpi67890\"]")
	private List<String> kpiIds;

	@Schema(description = "Unique identifier for a specific comment", example = "comment12345")
	private String commentId;
}
