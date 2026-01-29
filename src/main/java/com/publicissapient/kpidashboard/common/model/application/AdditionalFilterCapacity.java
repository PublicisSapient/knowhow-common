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
package com.publicissapient.kpidashboard.common.model.application;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author shi6
 *         <p>
 *         for each additonalfilter id like "sqd", saving the ncapacity of each
 *         node
 */
@Data
@Schema(description = "Additional Filter Capacity Details")
public class AdditionalFilterCapacity {
	@Schema(description = "Filter ID", example = "filter123")
	private String filterId;

	@Schema(description = "List of Leaf Node Capacities")
	private List<LeafNodeCapacity> nodeCapacityList;
}
