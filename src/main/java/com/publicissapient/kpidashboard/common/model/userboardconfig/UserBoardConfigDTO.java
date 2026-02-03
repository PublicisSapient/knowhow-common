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

import org.bson.types.ObjectId;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO of userboardconfig
 *
 * @author yasbano
 */
@Data
@Schema(description = "Data Transfer Object representing User Board Configuration")
public class UserBoardConfigDTO {
	@Schema(description = "Unique identifier of the User Board Config", example = "60d5ec49f8d2e30f8c8b4567")
	private ObjectId id;

	@Schema(description = "Username of the user", example = "john.doe")
	private String username;

	@Schema(description = "Level of configuration", example = "PROJECT")
	private String basicProjectConfigId;

	@Schema(description = "List of Scrum boards associated with the user", implementation = BoardDTO.class)
	private List<BoardDTO> scrum;

	@Schema(description = "List of Kanban boards associated with the user", implementation = BoardDTO.class)
	private List<BoardDTO> kanban;

	@Schema(description = "List of other boards associated with the user", implementation = BoardDTO.class)
	private List<BoardDTO> others;
}
