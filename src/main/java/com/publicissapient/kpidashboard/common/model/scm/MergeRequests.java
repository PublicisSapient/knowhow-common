package com.publicissapient.kpidashboard.common.model.scm;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.publicissapient.kpidashboard.common.model.generic.BasicModel;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a merge request (pull request) in a Git repository.
 * 
 * This entity stores metadata about merge requests including:
 * - Basic information (title, description, state)
 * - Branch information (source and target branches)
 * - User information (author, assignees, reviewers)
 * - Timestamps (created, updated, merged, closed)
 * - Statistics (lines changed, files changed, commit count)
 * - Platform-specific data
 * 
 * Uses compound unique index on repositoryName and externalId to ensure
 * uniqueness within each repository while allowing the same external ID
 * across different repositories.
 */

/**
 * Domain model representing a merge request (pull request) in a Git repository.
 *
 * This entity stores metadata about merge requests including:
 * - Basic information (title, description, state)
 * - Branch information (source and target branches)
 * - User information (author, assignees, reviewers)
 * - Timestamps (created, updated, merged, closed)
 * - Statistics (lines changed, files changed, commit count)
 * - Platform-specific data
 *
 * Uses compound unique index on processorItemId and externalId to ensure
 * uniqueness within each project configuration while allowing the same external ID
 * across different project configurations.
 */
@Document(collection = "merge_requests")
@CompoundIndex(def = "{'processorItemId': 1, 'externalId': 1}", unique = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MergeRequests extends BasicModel {

    /**
     * Tool configuration ID that identifies the repository/project
     */
    @Indexed
    private ObjectId processorItemId;

    /**
     * Repository name for unique constraint with externalId
     */
    @Indexed
    private String repositoryName;

    /**
     * External ID from the platform (PR number, MR IID, etc.)
     */
    @Indexed
    private String externalId;

    /**
     * Title of the merge request
     */
    private String title;

    /**
     * Description or summary of the merge request
     */
    private String summary;

    /**
     * Current state of the merge request (OPEN, CLOSED, MERGED, etc.)
     */
    private String state;

    /**
     * Source branch name
     */
    private String fromBranch;

    /**
     * Target branch name
     */
    private String toBranch;

    /**
     * Reference to the User who created the merge request
     */
    @DBRef
    private User authorId;

    /**
     * Direct reference to the User ID who created the merge request
     * This field stores the User's id as a String for easier querying
     */
    @Indexed
    private String authorUserId;

    /**
     * Author name (kept for backward compatibility)
     */
    private String author;

    /**
     * Reference to the User who closed the merge request
     */
    @DBRef
    private User closedById;

    /**
     * Direct reference to the User ID who closed the merge request
     * This field stores the User's id as a String for easier querying
     */
    @Indexed
    private String closedByUserId;

    /**
     * Closed by name (kept for backward compatibility)
     */
    private String closedBy;

    /**
     * List of assignee IDs (kept for backward compatibility)
     */
    private List<String> assigneeIds;

    /**
     * List of assignee names (kept for backward compatibility)
     */
    private List<String> assignees;

    /**
     * List of assignee User references
     */
    @DBRef
    private List<User> assigneeUsers;

    /**
     * List of assignee User IDs for easier querying
     * This field stores the User ids as Strings for better performance
     */
    @Indexed
    private List<ObjectId> assigneeUserIds;

    /**
     * List of reviewer IDs (kept for backward compatibility)
     */
    private List<String> reviewerIds;

    /**
     * List of reviewer names (kept for backward compatibility)
     */
    private List<String> reviewers;

    /**
     * List of reviewer User references
     */
    @DBRef
    private List<User> reviewerUsers;

    /**
     * List of reviewer User IDs for easier querying
     * This field stores the User ids as Strings for better performance
     */
    @Indexed
    private List<String> reviewerUserIds;

    /**
     * Timestamp when the merge request was merged
     */
    private LocalDateTime mergedAt;

    /**
     * Timestamp when the merge request was closed
     */
    private Long closedDate;

    /**
     * Timestamp when the merge request was last updated
     */
    private LocalDateTime updatedOn;

    /**
     * Total number of lines changed (added + removed)
     */
    private Integer linesChanged;

    /**
     * Timestamp when the merge request was picked for review
     */
    private LocalDateTime pickedForReviewOn;

    /**
     * Timestamp of the first commit in this merge request
     */
    private LocalDateTime firstCommitDate;

    /**
     * SHA of the merge commit
     */
    private String mergeCommitSha;

    /**
     * List of commit SHAs included in this merge request
     */
    private List<String> commitShas;

    /**
     * Number of commits in this merge request
     */
    private Integer commitCount;

    private String revisionNumber;

    /**
     * Number of files changed
     */
    private Integer filesChanged;

    /**
     * Number of lines added
     */
    private Integer addedLines;

    /**
     * Number of lines removed
     */
    private Integer removedLines;

    /**
     * Labels associated with the merge request
     */
    private List<String> labels;

    /**
     * Milestone associated with the merge request
     */
    private String milestone;

    /**
     * Whether this is a draft merge request
     */
    private Boolean isDraft;

    /**
     * Whether the merge request has conflicts



     /**
     * Enum representing the state of a merge request
     */
    public enum MergeRequestState {
        OPEN,
        CLOSED,
        MERGED,
        DRAFT
    }

    /**
     * Whether the merge request has conflicts
     */
    private Boolean hasConflicts;

    /**
     * Whether the merge request is mergeable
     */
    private Boolean isMergeable;

    /**
     * URL to the merge request on the platform
     */
    private String mergeRequestUrl;

    /**
     * Platform-specific additional data
     */
    private Map<String, Object> platformData;

    /**
     * Number of comments on the merge request
     */
    private Integer commentCount;

    /**
     * Timestamp when this record was created
     */
    @CreatedDate
    private Long createdDate;

    /**
     * Timestamp when this record was last updated
     */
    @LastModifiedDate
    private Long updatedDate;

    private String date;
    private Long count;

    // Manual getters and setters for fields that might not be generated by Lombok
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public ObjectId getProcessorItemId() {
        return processorItemId;
    }

    public void setProcessorItemId(ObjectId processorItemId) {
        this.processorItemId = processorItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public User getAuthorId() {
        return authorId;
    }

    public void setAuthorId(User authorId) {
        this.authorId = authorId;
    }

    public String getAuthorUserId() {
        return authorUserId;
    }

    public void setAuthorUserId(String authorUserId) {
        this.authorUserId = authorUserId;
    }

    public User getClosedById() {
        return closedById;
    }

    public void setClosedById(User closedById) {
        this.closedById = closedById;
    }

    public String getClosedByUserId() {
        return closedByUserId;
    }

    public void setClosedByUserId(String closedByUserId) {
        this.closedByUserId = closedByUserId;
    }

    public List<User> getAssigneeUsers() {
        return assigneeUsers;
    }

    public void setAssigneeUsers(List<User> assigneeUsers) {
        this.assigneeUsers = assigneeUsers;
    }

    public List<ObjectId> getAssigneeUserIds() {
        return assigneeUserIds;
    }

    public void setAssigneeUserIds(List<ObjectId> assigneeUserIds) {
        this.assigneeUserIds = assigneeUserIds;
    }

    public List<String> getReviewerIds() {
        return reviewerIds;
    }

    public void setReviewerIds(List<String> reviewerIds) {
        this.reviewerIds = reviewerIds;
    }

    public List<String> getReviewers() {
        return reviewers;
    }

    public void setReviewers(List<String> reviewers) {
        this.reviewers = reviewers;
    }

    public List<User> getReviewerUsers() {
        return reviewerUsers;
    }

    public void setReviewerUsers(List<User> reviewerUsers) {
        this.reviewerUsers = reviewerUsers;
    }

    public List<String> getReviewerUserIds() {
        return reviewerUserIds;
    }

    public void setReviewerUserIds(List<String> reviewerUserIds) {
        this.reviewerUserIds = reviewerUserIds;
    }

    public LocalDateTime getMergedAt() {
        return mergedAt;
    }

    public void setMergedAt(LocalDateTime mergedAt) {
        this.mergedAt = mergedAt;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Integer getLinesChanged() {
        return linesChanged;
    }

    public void setLinesChanged(Integer linesChanged) {
        this.linesChanged = linesChanged;
    }

    public Integer getCommitCount() {
        return commitCount;
    }

    public void setCommitCount(Integer commitCount) {
        this.commitCount = commitCount;
    }

    public Integer getFilesChanged() {
        return filesChanged;
    }

    public void setFilesChanged(Integer filesChanged) {
        this.filesChanged = filesChanged;
    }

    public Map<String, Object> getPlatformData() {
        return platformData;
    }

    public void setPlatformData(Map<String, Object> platformData) {
        this.platformData = platformData;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    // Additional getters that are missing
    public Boolean getHasConflicts() {
        // This might be stored in platformData or as a separate field
        // For now, return null or add a field for it
        return null;
    }

    public Boolean getIsMergeable() {
        // This might be stored in platformData or as a separate field
        // For now, return null or add a field for it
        return null;
    }

    public String getMergeRequestUrl() {
        // This might be stored in platformData or as a separate field
        // For now, return null or add a field for it
        return null;
    }

    public List<String> getLabels() {
        // This might be stored in platformData or as a separate field
        // For now, return null or add a field for it
        return null;
    }

    public String getMilestone() {
        // This might be stored in platformData or as a separate field
        // For now, return null or add a field for it
        return null;
    }

    public Boolean getIsDraft() {
        // This might be stored in platformData or as a separate field
        // For now, return null or add a field for it
        return null;
    }

    public User getAuthor() {
        return authorId; // Return the authorId field which is a User object
    }

    public List<User> getAssignees() {
        return assigneeUsers; // Return the assigneeUsers field
    }

    public User getClosedBy() {
        return closedById; // Return the closedById field which is a User object
    }

    // Additional setter methods
    public void setHasConflicts(Boolean hasConflicts) {
        // This might be stored in platformData or as a separate field
        // For now, do nothing or add a field for it
    }

    public void setIsMergeable(Boolean isMergeable) {
        // This might be stored in platformData or as a separate field
        // For now, do nothing or add a field for it
    }

    public void setMergeRequestUrl(String mergeRequestUrl) {
        // This might be stored in platformData or as a separate field
        // For now, do nothing or add a field for it
    }

    // Additional missing getters and setters
    public List<String> getCommitShas() {
        // This might be stored in platformData or as a separate field
        // For now, return null or add a field for it
        return null;
    }

    public Integer getAddedLines() {
        // This might be part of linesChanged calculation
        // For now, return null or add a field for it
        return addedLines;
    }

    public Integer getRemovedLines() {
        // This might be part of linesChanged calculation
        // For now, return null or add a field for it
        return removedLines;
    }

    public void setLabels(List<String> labels) {
        // This might be stored in platformData or as a separate field
        // For now, do nothing or add a field for it
    }

    public void setMilestone(String milestone) {
        // This might be stored in platformData or as a separate field
        // For now, do nothing or add a field for it
    }

    public void setIsDraft(Boolean isDraft) {
        // This might be stored in platformData or as a separate field
        // For now, do nothing or add a field for it
    }

    // Additional missing getters and setters
    public LocalDateTime getPickedForReviewOn() {
        // This might be stored in platformData or as a separate field
        // For now, return null or add a field for it
        return null;
    }

    public LocalDateTime getFirstCommitDate() {
        // This might be stored in platformData or as a separate field
        // For now, return null or add a field for it
        return null;
    }

    public String getMergeCommitSha() {
        // This might be stored in platformData or as a separate field
        // For now, return null or add a field for it
        return null;
    }

    public void setCommitShas(List<String> commitShas) {
        // This might be stored in platformData or as a separate field
        // For now, do nothing or add a field for it
    }

    public void setAddedLines(Integer addedLines) {
        this.addedLines = addedLines; // Set the addedLines field
    }

    public void setRemovedLines(Integer removedLines) {
        this.removedLines = removedLines; // Set the removedLines field
    }

    public void setClosedBy(User closedBy) {
        this.closedById = closedBy;
    }

    public List<ObjectId> getAssigneeIds() {
        return assigneeUserIds;
    }

    public void setAssignees(List<User> assignees) {
        this.assigneeUsers = assignees;
        this.assigneeUserIds = assignees.stream().map(User::getId).collect(Collectors.toList());
    }

    public void setPickedForReviewOn(LocalDateTime pickedForReviewOn) {
        // This might be stored in platformData or as a separate field
        // For now, do nothing or add a field for it
    }

    public void setFirstCommitDate(LocalDateTime firstCommitDate) {
        // This might be stored in platformData or as a separate field
        // For now, do nothing or add a field for it
    }

    public void setMergeCommitSha(String mergeCommitSha) {
        // This might be stored in platformData or as a separate field
        // For now, do nothing or add a field for it
    }

    // Additional missing setter methods
    public void setAuthor(User author) {
        this.authorId = author; // Set the authorId field
    }

    public void setAssigneeIds(List<ObjectId> assigneeIds) {
        this.assigneeUserIds = assigneeIds; // Set the assigneeUserIds field
    }

    /**
     * Helper class for pull request statistics.
     */
    public static class PullRequestStats {
        private final int addedLines;
        private final int removedLines;
        private final int changedFiles;

        public PullRequestStats(int addedLines, int removedLines, int changedFiles) {
            this.addedLines = addedLines;
            this.removedLines = removedLines;
            this.changedFiles = changedFiles;
        }

        public int getAddedLines() { return addedLines; }
        public int getRemovedLines() { return removedLines; }
        public int getChangedFiles() { return changedFiles; }
    }
}