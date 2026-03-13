/*
 * Copyright 2014 CapitalOne, LLC.
 * Further development Copyright 2022 Sapient Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.publicissapient.kpidashboard.common.model.application;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.publicissapient.kpidashboard.common.model.generic.BasicModel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * store all filters needed for a dashboard
 *
 * @author purgupta2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "filters")
@Schema(description = "Filters model representing filter configurations for a board")
public class Filters extends BasicModel {
	@Schema(description = "Identifier of the board associated with the filters", example = "101")
	private Integer boardId;

	@Schema(description = "Name of the board associated with the filters", example = "Project Alpha Board")
	private ProjectTypeSwitch projectTypeSwitch;

	@Schema(description = "Primary filter configuration", implementation = BasicFilter.class)
	private BasicFilter primaryFilter;

	@Schema(description = "Parent filter configuration", implementation = ParentFilter.class)
	private ParentFilter parentFilter;

	@Schema(description = "List of additional filter configurations", implementation = BasicFilter.class)
	private List<BasicFilter> additionalFilters;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ProjectTypeSwitch {
		@Schema(description = "Indicates if the project type switch is enabled", example = "true")
		private boolean enabled;

		@Schema(description = "Indicates if the project type switch is visible to users", example = "true")
		private boolean visible;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BasicFilter {
		@Schema(description = "Type of the filter", example = "Sprint")
		private String type;

		@Schema(description = "Label name of the filter", example = "Sprint 1")
		private DefaultLevel defaultLevel;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DefaultLevel {
		@Schema(description = "Name of the label for the filter", example = "Sprint 1")
		private String labelName;

		@Schema(description = "Sorting criteria for the filter", example = "ASCENDING")
		private String sortBy;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ParentFilter {
		@Schema(description = "Type of the parent filter", example = "Release")
		private String labelName;

		@Schema(description = "Emitted level of the parent filter", example = "Major Release")
		private String emittedLevel;
	}
}
