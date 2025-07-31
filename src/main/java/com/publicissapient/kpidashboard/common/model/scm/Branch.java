package com.publicissapient.kpidashboard.common.model.scm;

import com.publicissapient.kpidashboard.common.constant.BranchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain model representing a Git branch.
 * 
 * This entity stores information about repository branches including
 * metadata, protection settings, and commit statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "branches")
@CompoundIndex(def = "{'toolConfigId': 1, 'name': 1}", unique = true)
@CompoundIndex(def = "{'toolConfigId': 1, 'isDefault': 1}")
public class Branch {

    /**
     * Unique identifier for the branch record
     */
    @Id
    private String id;

    /**
     * Tool configuration ID that identifies the repository/project
     */
    @Indexed
    private String toolConfigId;

    /**
     * Name of the branch
     */
    @Indexed
    private String name;

    /**
     * Type of the branch based on naming conventions
     */
    private BranchType branchType;

    /**
     * SHA of the latest commit on this branch
     */
    private String latestCommitSha;

    /**
     * Timestamp of the latest commit on this branch
     */
    private LocalDateTime latestCommitTimestamp;

    /**
     * Whether this is the default branch (main/master)
     */
    @Indexed
    @Builder.Default
    private Boolean isDefault = false;

    /**
     * Whether this branch is protected
     */
    @Builder.Default
    private Boolean isProtected = false;

    /**
     * Whether this branch is active (has recent commits)
     */
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Total number of commits on this branch
     */
    private Integer totalCommits;

    /**
     * Number of commits ahead of the default branch
     */
    private Integer commitsAhead;

    /**
     * Number of commits behind the default branch
     */
    private Integer commitsBehind;

    /**
     * List of contributors to this branch
     */
    private List<String> contributorIds;

    /**
     * Number of unique contributors to this branch
     */
    private Integer contributorCount;

    /**
     * Timestamp when the branch was created
     */
    private LocalDateTime branchCreatedAt;

    /**
     * Timestamp when the branch was last updated (last commit)
     */
    private LocalDateTime lastUpdatedAt;

    /**
     * ID of the user who created the branch
     */
    private String createdByUserId;

    /**
     * URL to view the branch on the platform
     */
    private String branchUrl;

    /**
     * Whether the branch has been merged
     */
    @Builder.Default
    private Boolean isMerged = false;

    /**
     * SHA of the commit where this branch was merged (if applicable)
     */
    private String mergedCommitSha;

    /**
     * Timestamp when the branch was merged
     */
    private LocalDateTime mergedAt;

    /**
     * ID of the user who merged the branch
     */
    private String mergedByUserId;

    /**
     * Target branch for merge (usually main/master)
     */
    private String mergeTargetBranch;

    /**
     * Platform-specific additional data stored as JSON
     */
    private String platformData;

    /**
     * Timestamp when the branch record was created in our system
     */
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Timestamp when the branch record was last modified
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Checks if the branch is stale (no recent activity).
     * 
     * @param staleDays number of days to consider a branch stale
     * @return true if the branch is stale, false otherwise
     */
    public boolean isStale(int staleDays) {
        if (lastUpdatedAt == null) {
            return true;
        }
        return lastUpdatedAt.isBefore(LocalDateTime.now().minusDays(staleDays));
    }

    /**
     * Checks if the branch is significantly ahead of the default branch.
     * 
     * @param threshold minimum number of commits to be considered significantly ahead
     * @return true if significantly ahead, false otherwise
     */
    public boolean isSignificantlyAhead(int threshold) {
        return commitsAhead != null && commitsAhead >= threshold;
    }

    /**
     * Checks if the branch is significantly behind the default branch.
     * 
     * @param threshold minimum number of commits to be considered significantly behind
     * @return true if significantly behind, false otherwise
     */
    public boolean isSignificantlyBehind(int threshold) {
        return commitsBehind != null && commitsBehind >= threshold;
    }

    /**
     * Gets the branch status based on its state and activity.
     * 
     * @return a descriptive status string
     */
    public String getStatus() {
        if (isMerged != null && isMerged) {
            return "Merged";
        }
        if (isDefault != null && isDefault) {
            return "Default";
        }
        if (isStale(30)) {
            return "Stale";
        }
        if (isActive != null && isActive) {
            return "Active";
        }
        return "Inactive";
    }
}