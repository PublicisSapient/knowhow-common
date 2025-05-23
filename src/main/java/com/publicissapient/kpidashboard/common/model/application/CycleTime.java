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

import java.time.LocalDateTime;

import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model class to hold different params to define cycle time on scrum and kanban
 * board
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CycleTime {
	private DateTime intakeTime;
	private DateTime readyTime;
	private DateTime deliveryTime;
	private DateTime liveTime;
	private LocalDateTime liveLocalDateTime;
	private LocalDateTime readyLocalDateTime;
	private LocalDateTime deliveryLocalDateTime;

	private Long intakeDor;
	private Long dorDod;
	private Long dodLive;
	private Long inProductiveState;
	private Long inWasteState;
}
