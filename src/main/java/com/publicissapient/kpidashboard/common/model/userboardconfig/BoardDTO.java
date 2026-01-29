/*******************************************************************************
 * Copyright 2014 CapitalOne, LLC.
 * Further development Copyright 2022 Sapient Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package com.publicissapient.kpidashboard.common.model.userboardconfig;

import java.util.List;

import com.publicissapient.kpidashboard.common.model.application.Filters;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO of board
 *
 * @author narsingh9
 */
@Data
@Schema(description = "Data Transfer Object representing a Board configuration")
public class BoardDTO {
	@Schema(description = "Unique identifier of the board", example = "101")
	private int boardId;

	@Schema(description = "Name of the board", example = "Sprint Board")
	private String boardName;

	@Schema(description = "Slug of the board", example = "sprint-board")
	private String boardSlug;

	@Schema(description = "List of KPIs associated with the board", implementation = BoardKpisDTO.class)
	private List<BoardKpisDTO> kpis;

	@Schema(description = "Filters applied to the board", implementation = Filters.class)
	private Filters filters;
}
