package com.publicissapient.kpidashboard.common.model.scm;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

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
