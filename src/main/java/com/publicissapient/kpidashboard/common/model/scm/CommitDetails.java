package com.publicissapient.kpidashboard.common.model.scm;

import com.publicissapient.kpidashboard.common.model.generic.BasicModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain model representing a Git commit.
 * 
 * This entity stores comprehensive information about commits including
 * metadata, statistics, and file changes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "commit_details")
@CompoundIndex(def = "{'processorItemId': 1, 'sha': 1}", unique = true)
@CompoundIndex(def = "{'processorItemId': 1, 'commitTimestamp': -1}")
@CompoundIndex(def = "{'commitAuthorId': 1, 'commitTimestamp': -1}")
@CompoundIndex(def = "{'repositoryName': 1, 'commitTimestamp': -1}")
public class CommitDetails extends BasicModel {

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
    private LocalDateTime commitTimestamp;

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
    private List<FileChange> fileChanges;

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
     * Timestamp when the commit was committed (may differ from author timestamp)
     */
    private Long timestamp;

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

        public static FileChangeBuilder builder() {
            return new FileChangeBuilder();
        }

        public static class FileChangeBuilder {
            private String filePath;
            private Integer addedLines;
            private Integer removedLines;
            private Integer changedLines;
            private String changeType;
            private String previousPath;
            private Boolean isBinary = false;
            private List<Integer> changedLineNumbers;

            public FileChangeBuilder filePath(String filePath) {
                this.filePath = filePath;
                return this;
            }

            public FileChangeBuilder addedLines(Integer addedLines) {
                this.addedLines = addedLines;
                return this;
            }

            public FileChangeBuilder removedLines(Integer removedLines) {
                this.removedLines = removedLines;
                return this;
            }

            public FileChangeBuilder changedLines(Integer changedLines) {
                this.changedLines = changedLines;
                return this;
            }

            public FileChangeBuilder changeType(String changeType) {
                this.changeType = changeType;
                return this;
            }

            public FileChangeBuilder previousPath(String previousPath) {
                this.previousPath = previousPath;
                return this;
            }

            public FileChangeBuilder isBinary(Boolean isBinary) {
                this.isBinary = isBinary;
                return this;
            }

            public FileChangeBuilder changedLineNumbers(List<Integer> changedLineNumbers) {
                this.changedLineNumbers = changedLineNumbers;
                return this;
            }

            public FileChange build() {
                FileChange fileChange = new FileChange();
                fileChange.filePath = this.filePath;
                fileChange.addedLines = this.addedLines;
                fileChange.removedLines = this.removedLines;
                fileChange.changedLines = this.changedLines;
                fileChange.changeType = this.changeType;
                fileChange.previousPath = this.previousPath;
                fileChange.isBinary = this.isBinary;
                fileChange.changedLineNumbers = this.changedLineNumbers;
                return fileChange;
            }
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

    public List<FileChange> getFileChanges() {
        return fileChanges;
    }

    public void setFileChanges(List<FileChange> fileChanges) {
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

    public LocalDateTime getCommitTimestamp() {
        return commitTimestamp;
    }

    public void setCommitTimestamp(LocalDateTime commitTimestamp) {
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

    // Manual builder method since Lombok is not working properly
    public static CommitBuilder builder() {
        return new CommitBuilder();
    }

    public static class CommitBuilder {
        private String id;
        private String sha;
        private ObjectId processorItemId;
        private String repositoryName;
        private String commitMessage;
        private String commitAuthorId;
        private User commitAuthor;
        private String committerId;
        private User committer;
        private Long timestamp;
        private Integer addedLines;
        private Integer removedLines;
        private Integer changedLines;
        private List<FileChange> fileChanges;
        private String branchName;
        private String targetBranch;
        private List<String> parentShas;
        private Boolean isMergeCommit = false;
        private Integer filesChanged;
        private String authorName;
        private String authorEmail;
        private String committerName;
        private String committerEmail;
        private LocalDateTime committerTimestamp;
        private String commitUrl;
        private List<String> tags;
        private String platformData;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public CommitBuilder id(String id) {
            this.id = id;
            return this;
        }

        public CommitBuilder sha(String sha) {
            this.sha = sha;
            return this;
        }

        public CommitBuilder toolConfigId(ObjectId processorItemId) {
            this.processorItemId = processorItemId;
            return this;
        }

        public CommitBuilder repositoryName(String repositoryName) {
            this.repositoryName = repositoryName;
            return this;
        }

        public CommitBuilder commitMessage(String commitMessage) {
            this.commitMessage = commitMessage;
            return this;
        }

        public CommitBuilder commitAuthorId(String commitAuthorId) {
            this.commitAuthorId = commitAuthorId;
            return this;
        }

        public CommitBuilder commitAuthor(User commitAuthor) {
            this.commitAuthor = commitAuthor;
            return this;
        }

        public CommitBuilder committerId(String committerId) {
            this.committerId = committerId;
            return this;
        }

        public CommitBuilder committer(User committer) {
            this.committer = committer;
            return this;
        }

        public CommitBuilder commitTimestamp(Long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public CommitBuilder addedLines(Integer addedLines) {
            this.addedLines = addedLines;
            return this;
        }

        public CommitBuilder removedLines(Integer removedLines) {
            this.removedLines = removedLines;
            return this;
        }

        public CommitBuilder changedLines(Integer changedLines) {
            this.changedLines = changedLines;
            return this;
        }

        public CommitBuilder fileChanges(List<FileChange> fileChanges) {
            this.fileChanges = fileChanges;
            return this;
        }

        public CommitBuilder branchName(String branchName) {
            this.branchName = branchName;
            return this;
        }

        public CommitBuilder targetBranch(String targetBranch) {
            this.targetBranch = targetBranch;
            return this;
        }

        public CommitBuilder parentShas(List<String> parentShas) {
            this.parentShas = parentShas;
            return this;
        }

        public CommitBuilder isMergeCommit(Boolean isMergeCommit) {
            this.isMergeCommit = isMergeCommit;
            return this;
        }

        public CommitBuilder filesChanged(Integer filesChanged) {
            this.filesChanged = filesChanged;
            return this;
        }

        public CommitBuilder authorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        public CommitBuilder authorEmail(String authorEmail) {
            this.authorEmail = authorEmail;
            return this;
        }

        public CommitBuilder committerName(String committerName) {
            this.committerName = committerName;
            return this;
        }

        public CommitBuilder committerEmail(String committerEmail) {
            this.committerEmail = committerEmail;
            return this;
        }

        public CommitBuilder committerTimestamp(LocalDateTime committerTimestamp) {
            this.committerTimestamp = committerTimestamp;
            return this;
        }

        public CommitBuilder commitUrl(String commitUrl) {
            this.commitUrl = commitUrl;
            return this;
        }

        public CommitBuilder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public CommitBuilder platformData(String platformData) {
            this.platformData = platformData;
            return this;
        }

        public CommitBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CommitBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CommitDetails build() {
            CommitDetails commitDetails = new CommitDetails();
            commitDetails.sha = this.sha;
            commitDetails.processorItemId = this.processorItemId;
            commitDetails.repositoryName = this.repositoryName;
            commitDetails.commitMessage = this.commitMessage;
            commitDetails.commitAuthorId = this.commitAuthorId;
            commitDetails.commitAuthor = this.commitAuthor;
            commitDetails.committerId = this.committerId;
            commitDetails.committer = this.committer;
            commitDetails.timestamp = this.timestamp;
            commitDetails.addedLines = this.addedLines;
            commitDetails.removedLines = this.removedLines;
            commitDetails.changedLines = this.changedLines;
            commitDetails.fileChanges = this.fileChanges;
            commitDetails.branchName = this.branchName;
            commitDetails.targetBranch = this.targetBranch;
            commitDetails.parentShas = this.parentShas;
            commitDetails.isMergeCommit = this.isMergeCommit;
            commitDetails.filesChanged = this.filesChanged;
            commitDetails.authorName = this.authorName;
            commitDetails.authorEmail = this.authorEmail;
            commitDetails.committerName = this.committerName;
            commitDetails.committerEmail = this.committerEmail;
            commitDetails.timestamp = this.timestamp;
            commitDetails.commitUrl = this.commitUrl;
            commitDetails.tags = this.tags;
            commitDetails.platformData = this.platformData;
            commitDetails.createdAt = this.createdAt;
            commitDetails.updatedAt = this.updatedAt;
            return commitDetails;
        }
    }
}