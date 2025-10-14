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

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.publicissapient.kpidashboard.common.model.scm.ScmMergeRequests;

public interface ScmMergeRequestsRepository
		extends
			MongoRepository<ScmMergeRequests, String>,
			ScmMergeRequestRepositoryCustom {

	/**
	 * Finds a merge request by tool configuration ID and external ID (unique
	 * constraint).
	 *
	 * @param processorItemId
	 *          the tool configuration ID
	 * @param externalId
	 *          the external ID from the platform
	 * @return Optional containing the merge request if found
	 */
	Optional<ScmMergeRequests> findByProcessorItemIdAndExternalId(ObjectId processorItemId, String externalId);

	/**
	 * Finds merge requests by repository name.
	 *
	 * @param repositoryName
	 *          the repository name
	 * @param pageable
	 *          pagination information
	 * @return page of merge requests for the specified repository
	 */
	Page<ScmMergeRequests> findByRepositoryName(String repositoryName, Pageable pageable);

	/**
	 * Finds merge requests by tool configuration ID.
	 *
	 * @param processorItemId
	 *          the tool configuration ID
	 * @param pageable
	 *          pagination information
	 * @return page of merge requests for the specified tool configuration
	 */
	Page<ScmMergeRequests> findByProcessorItemId(String processorItemId, Pageable pageable);

	/**
	 * Finds merge requests by tool configuration ID and state.
	 *
	 * @param processorItemId
	 *          the tool configuration ID
	 * @param state
	 *          the merge request state
	 * @param pageable
	 *          pagination information
	 * @return page of merge requests for the tool configuration with the specified
	 *         state
	 */
	Page<ScmMergeRequests> findByProcessorItemIdAndState(ObjectId processorItemId,
			ScmMergeRequests.MergeRequestState state, Pageable pageable);

	void deleteByProcessorItemIdIn(List<ObjectId> processorItemId);
}
