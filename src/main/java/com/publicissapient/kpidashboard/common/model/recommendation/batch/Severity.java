/*
 *  Copyright 2024 Sapient Corporation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the
 *  License.
 */

package com.publicissapient.kpidashboard.common.model.recommendation.batch;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing the severity level of a recommendation. Priority determines
 * sort order: lower number = higher priority.
 */
@Getter
@RequiredArgsConstructor
public enum Severity {

	CRITICAL("critical", 1), HIGH("high", 2), MEDIUM("medium", 3), LOW("low", 4);

	private final String displayName;
	private final int priority;
}
