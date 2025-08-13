package com.publicissapient.kpidashboard.common.model.scm;

import com.publicissapient.kpidashboard.common.model.generic.BasicModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "scm_merge_requests")
@CompoundIndex(def = "{'processorItemId': 1, 'externalId': 1}", unique = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ScmMergeRequests extends BasicModel {

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
    private Long pickedForReviewOn;

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


    private Long createdDate;

    private Long updatedDate;

    private String date;
    private Long count;

    private boolean isOpen;
    private boolean isClosed;
    private String repoSlug;
    private String projKey;

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
