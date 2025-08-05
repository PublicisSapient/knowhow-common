package com.publicissapient.kpidashboard.common.model.scm;

import com.publicissapient.kpidashboard.common.constant.CommitType;
import com.publicissapient.kpidashboard.common.model.generic.BasicModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "scm_commit_details")
@CompoundIndex(def = "{'processorItemId': 1, 'sha': 1}", unique = true)
@CompoundIndex(def = "{'processorItemId': 1, 'commitTimestamp': -1}")
@CompoundIndex(def = "{'commitAuthorId': 1, 'commitTimestamp': -1}")
@CompoundIndex(def = "{'repositoryName': 1, 'commitTimestamp': -1}")
public class ScmCommits extends BasicModel {

    private List<ObjectId> projectConfigId = new ArrayList<>();
    private String url;
    private String branch;
    private String repoSlug;
    private String revisionNumber;
    private String commitLog;
    private String author;
    private List<String> parentRevisionNumbers;
    private CommitType type;
    private String status;
    private JSONArray reviewers;

    /**
     * SHA hash of the commit (indexed for fast lookups)
     */
    @Indexed
    private String sha;

    /**
     * Tool configuration ID that identifies the repository/project
     */
    @Indexed
    private ObjectId processorItemId;

    /**
     * Repository name for unique constraint with SHA
     */
    @Indexed
    private String repositoryName;

    /**
     * Commit message
     */
    private String commitMessage;

    /**
     * ID of the user who authored the commit
     */
    @Indexed
    private String commitAuthorId;

    /**
     * Reference to the User who authored the commit
     */
    @DBRef
    private User commitAuthor;

    /**
     * ID of the user who committed the commit (if different from author)
     */
    private String committerId;

    /**
     * Reference to the User who committed the commit (if different from author)
     */
    @DBRef
    private User committer;

    /**
     * Timestamp when the commit was created
     */
    @Indexed
    private Long commitTimestamp;

    /**
     * Number of lines added in this commit
     */
    private Integer addedLines;

    /**
     * Number of lines removed in this commit
     */
    private Integer removedLines;

    /**
     * Number of lines changed (modified) in this commit
     */
    private Integer changedLines;

    /**
     * List of file changes in this commit
     * Each FileChange object contains details about changes to a specific file
     */
    private List<ScmCommits.FileChange> fileChanges;

    /**
     * Name of the branch where this commit was made
     */
    @Indexed
    private String branchName;

    /**
     * Target branch name (for merge commits or when commit is intended for a specific target)
     */
    @Indexed
    private String targetBranch;

    /**
     * List of parent commit SHAs
     */
    private List<String> parentShas;

    /**
     * Whether this is a merge commit
     */
    @Builder.Default
    private Boolean isMergeCommit = false;

    /**
     * Number of files changed in this commit
     */
    private Integer filesChanged;

    /**
     * Commit author name (as recorded in Git) - kept for backward compatibility
     */
    private String authorName;

    /**
     * Commit author email (as recorded in Git) - kept for backward compatibility
     */
    private String authorEmail;

    /**
     * Committer name (as recorded in Git) - kept for backward compatibility
     */
    private String committerName;

    /**
     * Committer email (as recorded in Git) - kept for backward compatibility
     */
    private String committerEmail;

    /**
     * URL to view the commit on the platform
     */
    private String commitUrl;

    /**
     * List of tags associated with this commit
     */
    private List<String> tags;

    /**
     * Platform-specific additional data stored as JSON
     */
    private String platformData;

    /**
     * Timestamp when the commit record was created in our system
     */
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Timestamp when the commit record was last modified
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private String date;
    private Long count;

    /**
     * Nested class representing file changes in a commit
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileChange {

        /**
         * Path of the file that was changed
         */
        private String filePath;

        /**
         * Number of lines added in this file
         */
        private Integer addedLines;

        /**
         * Number of lines removed in this file
         */
        private Integer removedLines;

        /**
         * Number of lines changed in this file
         */
        private Integer changedLines;

        /**
         * Type of change (added, modified, deleted, renamed)
         */
        private String changeType;

        /**
         * Previous file path (for renamed files)
         */
        private String previousPath;

        /**
         * Whether this file is binary
         */
        @Builder.Default
        private Boolean isBinary = false;

        /**
         * List of line numbers where changes occurred
         * Contains line numbers for added, removed, or modified lines
         */
        private List<Integer> changedLineNumbers;

        // Manual getters and builder method since Lombok is not working properly
        public Integer getAddedLines() {
            return addedLines;
        }

        public Integer getRemovedLines() {
            return removedLines;
        }

        public Integer getChangedLines() {
            return changedLines;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getChangeType() {
            return changeType;
        }

        public String getPreviousPath() {
            return previousPath;
        }

        public Boolean getIsBinary() {
            return isBinary;
        }

        public List<Integer> getChangedLineNumbers() {
            return changedLineNumbers;
        }
    }

    /**
     * Calculates the total number of lines affected by this commit.
     *
     * @return total lines affected (added + removed + changed)
     */
    public Integer getTotalLinesAffected() {
        int total = 0;
        if (addedLines != null) total += addedLines;
        if (removedLines != null) total += removedLines;
        if (changedLines != null) total += changedLines;
        return total;
    }

    /**
     * Checks if this commit has significant changes (more than a threshold).
     *
     * @param threshold the minimum number of lines to consider significant
     * @return true if the commit has significant changes, false otherwise
     */
    public boolean hasSignificantChanges(int threshold) {
        return getTotalLinesAffected() >= threshold;
    }

    /**
     * Gets the short SHA (first 7 characters) for display purposes.
     *
     * @return short SHA or full SHA if less than 7 characters
     */
    public String getShortSha() {
        return sha != null && sha.length() >= 7 ? sha.substring(0, 7) : sha;
    }


    public Integer getChangedLines() {
        return this.addedLines + this.removedLines;
    }

    public void setChangedLines(Integer changedLines) {
        this.changedLines = changedLines;
    }

    public List<ScmCommits.FileChange> getFileChanges() {
        return fileChanges;
    }

    public void setFileChanges(List<ScmCommits.FileChange> fileChanges) {
        this.fileChanges = fileChanges;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public void setTargetBranch(String targetBranch) {
        this.targetBranch = targetBranch;
    }

    public List<String> getParentShas() {
        return parentShas;
    }

    public void setParentShas(List<String> parentShas) {
        this.parentShas = parentShas;
    }

    public Boolean getIsMergeCommit() {
        return isMergeCommit;
    }

    public void setIsMergeCommit(Boolean isMergeCommit) {
        this.isMergeCommit = isMergeCommit;
    }

    public Integer getFilesChanged() {
        return filesChanged;
    }

    public void setFilesChanged(Integer filesChanged) {
        this.filesChanged = filesChanged;
    }

    public Long getCommitTimestamp() {
        return commitTimestamp;
    }

    public void setCommitTimestamp(Long commitTimestamp) {
        this.commitTimestamp = commitTimestamp;
    }

    public String getCommitUrl() {
        return commitUrl;
    }

    public void setCommitUrl(String commitUrl) {
        this.commitUrl = commitUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getPlatformData() {
        return platformData;
    }

    public void setPlatformData(String platformData) {
        this.platformData = platformData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getCommitterId() {
        return committerId;
    }

    public void setCommitterId(String committerId) {
        this.committerId = committerId;
    }

    public User getCommitter() {
        return committer;
    }

    public void setCommitter(User committer) {
        this.committer = committer;
    }

}
