/*
 *   Copyright 2014 CapitalOne, LLC.
 *   Further development Copyright 2022 Sapient Corporation.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.publicissapient.kpidashboard.common.repository.scm;

import com.publicissapient.kpidashboard.common.model.scm.ScmCommits;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScmCommitsRepository extends MongoRepository<ScmCommits, String>, ScmCommitRepositoryCustom {

    /**
     * Finds a commit by SHA hash.
     *
     * @param sha the SHA hash to search for
     * @return Optional containing the commit if found
     */
    Optional<ScmCommits> findBySha(String sha);

    /**
     * Finds a commit by repository name and SHA hash (legacy method).
     *
     * @param repositoryName the repository name
     * @param sha the SHA hash to search for
     * @return Optional containing the commit if found
     */
    Optional<ScmCommits> findByRepositoryNameAndSha(String repositoryName, String sha);

    /**
     * Finds a commit by tool configuration ID and SHA hash (unique constraint).
     *
     * @param processorItemId the tool configuration ID
     * @param sha the SHA hash to search for
     * @return Optional containing the commit if found
     */
    Optional<ScmCommits> findByProcessorItemIdAndSha(ObjectId processorItemId, String sha);

    /**
     * Finds commits by repository name.
     *
     * @param repositoryName the repository name
     * @param pageable pagination information
     * @return page of commits for the specified repository
     */
    Page<ScmCommits> findByRepositoryName(String repositoryName, Pageable pageable);

    /**
     * Finds commits by tool configuration ID.
     *
     * @param processorItemId the tool configuration ID
     * @param pageable pagination information
     * @return page of commits for the specified tool configuration
     */
    Page<ScmCommits> findByProcessorItemId(ObjectId processorItemId, Pageable pageable);

    /**
     * Finds commits by author ID.
     *
     * @param authorId the author ID
     * @param pageable pagination information
     * @return page of commits by the specified author
     */
    Page<ScmCommits> findByCommitAuthorId(String authorId, Pageable pageable);

    /**
     * Finds commits by tool configuration ID and author ID.
     *
     * @param processorItemId the tool configuration ID
     * @param authorId the author ID
     * @param pageable pagination information
     * @return page of commits for the specified tool configuration and author
     */
    Page<ScmCommits> findByProcessorItemIdAndCommitAuthorId(String processorItemId, String authorId, Pageable pageable);

    /**
     * Finds commits within a date range.
     *
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @param pageable pagination information
     * @return page of commits within the date range
     */
    Page<ScmCommits> findByCommitTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Finds commits by tool configuration ID within a date range.
     *
     * @param processorItemId the tool configuration ID
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @param pageable pagination information
     * @return page of commits for the tool configuration within the date range
     */
    Page<ScmCommits> findByProcessorItemIdAndCommitTimestampBetween(
            String processorItemId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Finds commits by branch name.
     *
     * @param branchName the branch name
     * @param pageable pagination information
     * @return page of commits on the specified branch
     */
    Page<ScmCommits> findByBranchName(String branchName, Pageable pageable);

    /**
     * Finds commits by tool configuration ID and branch name.
     *
     * @param processorItemId the tool configuration ID
     * @param branchName the branch name
     * @param pageable pagination information
     * @return page of commits for the tool configuration and branch
     */
    Page<ScmCommits> findByProcessorItemIdAndBranchName(String processorItemId, String branchName, Pageable pageable);

    /**
     * Finds commits by target branch name.
     *
     * @param targetBranch the target branch name
     * @param pageable pagination information
     * @return page of commits with the specified target branch
     */
    Page<ScmCommits> findByTargetBranch(String targetBranch, Pageable pageable);

    /**
     * Finds commits by tool configuration ID and target branch name.
     *
     * @param processorItemId the tool configuration ID
     * @param targetBranch the target branch name
     * @param pageable pagination information
     * @return page of commits for the tool configuration and target branch
     */
    Page<ScmCommits> findByProcessorItemIdAndTargetBranch(String processorItemId, String targetBranch, Pageable pageable);

    /**
     * Finds merge commits.
     *
     * @param pageable pagination information
     * @return page of merge commits
     */
    Page<ScmCommits> findByIsMergeCommitTrue(Pageable pageable);

    /**
     * Finds commits with significant changes (above a threshold).
     *
     * @param threshold minimum number of total lines affected
     * @param pageable pagination information
     * @return page of commits with significant changes
     */
    @Query("{'$expr': {'$gte': [{'$add': [{'$ifNull': ['$addedLines', 0]}, {'$ifNull': ['$removedLines', 0]}, {'$ifNull': ['$changedLines', 0]}]}, ?0]}}")
    Page<ScmCommits> findCommitsWithSignificantChanges(int threshold, Pageable pageable);

    /**
     * Counts commits by tool configuration ID.
     *
     * @param processorItemId the tool configuration ID
     * @return count of commits for the tool configuration
     */
    long countByProcessorItemId(String processorItemId);

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
     * @param processorItemId the tool configuration ID
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @return count of commits within the date range
     */
    long countByProcessorItemIdAndCommitTimestampBetween(String processorItemId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds the latest commits for a tool configuration.
     *
     * @param processorItemId the tool configuration ID
     * @param limit maximum number of commits to return
     * @return list of latest commits
     */
    @Query(value = "{'processorItemId': ?0}", sort = "{'commitTimestamp': -1}")
    List<ScmCommits> findLatestCommitsByProcessorItemId(String processorItemId, int limit);

    /**
     * Finds commits by multiple SHA hashes.
     *
     * @param shas list of SHA hashes
     * @return list of matching commits
     */
    List<ScmCommits> findByShaIn(List<String> shas);

    /**
     * Aggregates commit statistics by author for a tool configuration.
     *
     * @param processorItemId the tool configuration ID
     * @return list of author commit statistics
     */
    @Aggregation(pipeline = {
            "{'$match': {'processorItemId': ?0}}",
            "{'$group': {" +
                    "'_id': '$commitAuthorId'," +
                    "'commitCount': {'$sum': 1}," +
                    "'totalAddedLines': {'$sum': {'$ifNull': ['$addedLines', 0]}}," +
                    "'totalRemovedLines': {'$sum': {'$ifNull': ['$removedLines', 0]}}," +
                    "'totalChangedLines': {'$sum': {'$ifNull': ['$changedLines', 0]}}," +
                    "'firstCommit': {'$min': '$commitTimestamp'}," +
                    "'lastCommit': {'$max': '$commitTimestamp'}" +
                    "}}",
            "{'$sort': {'commitCount': -1}}"
    })
    List<AuthorCommitStats> getCommitStatsByAuthor(String processorItemId);

    /**
     * Aggregates daily commit counts for a tool configuration within a date range.
     *
     * @param processorItemId the tool configuration ID
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @return list of daily commit counts
     */
    @Aggregation(pipeline = {
            "{'$match': {" +
                    "'processorItemId': ?0," +
                    "'commitTimestamp': {'$gte': ?1, '$lte': ?2}" +
                    "}}",
            "{'$group': {" +
                    "'_id': {'$dateToString': {'format': '%Y-%m-%d', 'date': '$commitTimestamp'}}," +
                    "'commitCount': {'$sum': 1}," +
                    "'totalLinesChanged': {'$sum': {'$add': [{'$ifNull': ['$addedLines', 0]}, {'$ifNull': ['$removedLines', 0]}]}}" +
                    "}}",
            "{'$sort': {'_id': 1}}"
    })
    List<DailyCommitStats> getDailyCommitStats(String processorItemId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds commits that modify specific file paths.
     *
     * @param processorItemId the tool configuration ID
     * @param filePath the file path to search for
     * @param pageable pagination information
     * @return page of commits that modify the specified file
     */
    @Query("{'processorItemId': ?0, 'fileChanges.filePath': ?1}")
    Page<ScmCommits> findCommitsByFileChange(String processorItemId, String filePath, Pageable pageable);

    /**
     * Checks if a commit exists by SHA and tool configuration ID.
     *
     * @param sha the SHA hash
     * @param processorItemId the tool configuration ID
     * @return true if the commit exists
     */
    boolean existsByShaAndProcessorItemId(String sha, String processorItemId);

    void deleteByProcessorItemIdIn(List<ObjectId> processorItemId);

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