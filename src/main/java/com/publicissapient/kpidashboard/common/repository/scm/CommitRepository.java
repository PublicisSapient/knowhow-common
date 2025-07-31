package com.publicissapient.kpidashboard.common.repository.scm;

import com.publicissapient.kpidashboard.common.model.scm.CommitDetails;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Commit entity operations.
 *
 * Provides data access methods for commit-related operations including
 * queries for finding commits by various criteria and aggregation operations.
 */
@Repository
public interface CommitRepository
        extends
        CrudRepository<CommitDetails, ObjectId>,
        QuerydslPredicateExecutor<CommitDetails>,
        CommitRepositoryCustom {

    /**
     * Finds a commit by SHA hash.
     *
     * @param sha the SHA hash to search for
     * @return Optional containing the commit if found
     */
    Optional<CommitDetails> findBySha(String sha);

    /**
     * Finds a commit by repository name and SHA hash (legacy method).
     *
     * @param repositoryName the repository name
     * @param sha the SHA hash to search for
     * @return Optional containing the commit if found
     */
    Optional<CommitDetails> findByRepositoryNameAndSha(String repositoryName, String sha);

    /**
     * Finds a commit by tool configuration ID and SHA hash (unique constraint).
     *
     * @param processorItemId the tool configuration ID
     * @param sha the SHA hash to search for
     * @return Optional containing the commit if found
     */
    Optional<CommitDetails> findByProcessorItemIdAndSha(ObjectId processorItemId, String sha);

    /**
     * Finds commits by repository name.
     *
     * @param repositoryName the repository name
     * @param pageable pagination information
     * @return page of commits for the specified repository
     */
    Page<CommitDetails> findByRepositoryName(String repositoryName, Pageable pageable);

    /**
     * Finds commits by tool configuration ID.
     *
     * @param processorItemId the tool configuration ID
     * @param pageable pagination information
     * @return page of commits for the specified tool configuration
     */
    Page<CommitDetails> findByProcessorItemId(ObjectId processorItemId, Pageable pageable);

    /**
     * Deletes all commits associated with the given list of tool configuration IDs.
     * This method removes all commit records that match any of the provided
     * `processorItemIds`. It is useful for bulk deletion operations when multiple
     * tool configurations need to be cleaned up.
     *
     * @param processorItemIds
     *            List of tool configuration IDs (ObjectId) for which the associated
     *            commits should be deleted.
     */
    void deleteByProcessorItemIdIn(List<ObjectId> processorItemIds);

    /**
     * Finds commits by author ID.
     *
     * @param authorId the author ID
     * @param pageable pagination information
     * @return page of commits by the specified author
     */
    Page<CommitDetails> findByCommitAuthorId(String authorId, Pageable pageable);

    /**
     * Finds commits by tool configuration ID and author ID.
     *
     * @param toolConfigId the tool configuration ID
     * @param authorId the author ID
     * @param pageable pagination information
     * @return page of commits for the specified tool configuration and author
     */

    /**
     * Finds commits within a date range.
     *
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @param pageable pagination information
     * @return page of commits within the date range
     */
    Page<CommitDetails> findByCommitTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Finds commits by tool configuration ID within a date range.
     *
     * @param toolConfigId the tool configuration ID
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @param pageable pagination information
     * @return page of commits for the tool configuration within the date range
     */


    /**
     * Finds commits by branch name.
     *
     * @param branchName the branch name
     * @param pageable pagination information
     * @return page of commits on the specified branch
     */
    Page<CommitDetails> findByBranchName(String branchName, Pageable pageable);

    /**
     * Finds commits by tool configuration ID and branch name.
     *
     * @param toolConfigId the tool configuration ID
     * @param branchName the branch name
     * @param pageable pagination information
     * @return page of commits for the tool configuration and branch
     */

    /**
     * Finds commits by target branch name.
     *
     * @param targetBranch the target branch name
     * @param pageable pagination information
     * @return page of commits with the specified target branch
     */
    Page<CommitDetails> findByTargetBranch(String targetBranch, Pageable pageable);

    /**
     * Finds commits by tool configuration ID and target branch name.
     *
     * @param toolConfigId the tool configuration ID
     * @param targetBranch the target branch name
     * @param pageable pagination information
     * @return page of commits for the tool configuration and target branch
     */

    /**
     * Finds merge commits.
     *
     * @param pageable pagination information
     * @return page of merge commits
     */
    Page<CommitDetails> findByIsMergeCommitTrue(Pageable pageable);

    /**
     * Finds commits with significant changes (above a threshold).
     *
     * @param threshold minimum number of total lines affected
     * @param pageable pagination information
     * @return page of commits with significant changes
     */
    @Query("{'$expr': {'$gte': [{'$add': [{'$ifNull': ['$addedLines', 0]}, {'$ifNull': ['$removedLines', 0]}, {'$ifNull': ['$changedLines', 0]}]}, ?0]}}")
    Page<CommitDetails> findCommitsWithSignificantChanges(int threshold, Pageable pageable);

    /**
     * Counts commits by tool configuration ID.
     *
     * @param toolConfigId the tool configuration ID
     * @return count of commits for the tool configuration
     */

    /**
     * Counts commits by author ID.
     *
     * @param authorId the author ID
     * @return count of commits by the author
     */
    long countByCommitAuthorId(String authorId);

    /**
     * Counts commits by tool configuration ID within a date range.
     *
     * @param toolConfigId the tool configuration ID
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @return count of commits within the date range
     */



    /**
     * Finds commits by multiple SHA hashes.
     *
     * @param shas list of SHA hashes
     * @return list of matching commits
     */
    List<CommitDetails> findByShaIn(List<String> shas);







    /**
     * Interface for author commit statistics projection.
     */
    interface AuthorCommitStats {
        String getId(); // Author ID
        Long getCommitCount();
        Long getTotalAddedLines();
        Long getTotalRemovedLines();
        Long getTotalChangedLines();
        LocalDateTime getFirstCommit();
        LocalDateTime getLastCommit();
    }

    /**
     * Interface for daily commit statistics projection.
     */
    interface DailyCommitStats {
        String getId(); // Date string
        Long getCommitCount();
        Long getTotalLinesChanged();
    }
}