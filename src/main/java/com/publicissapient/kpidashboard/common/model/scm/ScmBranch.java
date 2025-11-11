/*
 *  Copyright 2024 <Sapient Corporation>
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

package com.publicissapient.kpidashboard.common.model.scm;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Domain model representing a Git branch.
 *
 * <p>
 * This entity stores information about repository branches including metadata,
 * protection settings, and commit statistics.
 */
@Data
@Builder
public class ScmBranch {

	private String name;

	/** Timestamp of the latest commit on this branch */
	private LocalDateTime latestCommitTimestamp;

	@Builder.Default
	private Boolean isDefault = false;

	/** Whether this branch is protected */
	@Builder.Default
	private Boolean isProtected = false;

	/** Whether this branch is active (has recent commits) */
	@Builder.Default
	private Boolean isActive = true;

	/** Total number of commits on this branch */
	private Integer totalCommits;

	/** Number of commits ahead of the default branch */
	private Integer commitsAhead;

	/** Number of commits behind the default branch */
	private Integer commitsBehind;

	/** Timestamp when the branch was last updated (last commit) */
	private Long lastUpdatedAt;
}
