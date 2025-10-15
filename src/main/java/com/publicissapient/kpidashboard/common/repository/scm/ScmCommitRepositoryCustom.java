/*******************************************************************************
 * Copyright 2014 CapitalOne, LLC.
 * Further development Copyright 2022 Sapient Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package com.publicissapient.kpidashboard.common.repository.scm;

import java.util.List;

import com.mongodb.BasicDBList;
import com.publicissapient.kpidashboard.common.model.scm.ScmCommits;

/**
 * Custom repository interface for `ScmCommitRepository` to support complex
 * queries and aggregations.
 */
public interface ScmCommitRepositoryCustom {

	/**
	 * Retrieves a list of `ScmCommits` filtered by commit timestamp and additional
	 * criteria.
	 *
	 * @param startDate
	 *          the start of the commit timestamp range(inclusive) in epoch
	 *          milliseconds
	 * @param endDate
	 *          the end of the commit timestamp range(inclusive), in epoch
	 *          milliseconds
	 * @param filterList
	 *          a `BasicDBList` containing filter criteria, such as processor item
	 *          IDs or other fields
	 * @return a list of `ScmCommits` matching the specified date range and filters
	 */
	List<ScmCommits> findCommitList(Long startDate, Long endDate, BasicDBList filterList);
}
