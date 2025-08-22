package com.publicissapient.kpidashboard.common.repository.scm;

import com.publicissapient.kpidashboard.common.model.scm.ScmMergeRequests;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ScmMergeRequestsRepository
		extends MongoRepository<ScmMergeRequests, String>, ScmMergeRequestRepositoryCustom {

    /**
     * Finds a merge request by tool configuration ID and external ID (unique constraint).
     *
     * @param processorItemId the tool configuration ID
     * @param externalId the external ID from the platform
     * @return Optional containing the merge request if found
     */
    Optional<ScmMergeRequests> findByProcessorItemIdAndExternalId(ObjectId processorItemId, String externalId);

    /**
     * Finds merge requests by repository name.
     *
     * @param repositoryName the repository name
     * @param pageable pagination information
     * @return page of merge requests for the specified repository
     */
    Page<ScmMergeRequests> findByRepositoryName(String repositoryName, Pageable pageable);

    /**
     * Finds merge requests by tool configuration ID.
     *
     * @param processorItemId the tool configuration ID
     * @param pageable pagination information
     * @return page of merge requests for the specified tool configuration
     */
    Page<ScmMergeRequests> findByProcessorItemId(String processorItemId, Pageable pageable);

    /**
     * Finds merge requests by tool configuration ID and state.
     *
     * @param processorItemId the tool configuration ID
     * @param state the merge request state
     * @param pageable pagination information
     * @return page of merge requests for the tool configuration with the specified state
     */
    Page<ScmMergeRequests> findByProcessorItemIdAndState(ObjectId processorItemId, ScmMergeRequests.MergeRequestState state, Pageable pageable);

    void deleteByProcessorItemIdIn(List<ObjectId> processorItemId);

}