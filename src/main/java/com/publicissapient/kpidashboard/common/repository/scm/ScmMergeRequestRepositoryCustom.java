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

import com.mongodb.BasicDBList;
import com.publicissapient.kpidashboard.common.model.scm.ScmMergeRequests;

/**
 * Custom repository interface for performing agg operations on SCM merge
 * requests.
 */
public interface ScmMergeRequestRepositoryCustom {
	/**
	 * Retrieves a list of SCM merge requests based on the provided filters and date
	 * range.
	 *
	 * @param startDate
	 *            The start date (inclusive) for filtering merge requests, in
	 *            milliseconds since epoch.
	 * @param endDate
	 *            The end date (inclusive) for filtering merge requests, in
	 *            milliseconds since epoch.
	 * @param filterList
	 *            Additional filters to apply to the query.
	 * @return A list of {@link ScmMergeRequests} matching the specified criteria.
	 */
	List<ScmMergeRequests> findMergeList(Long startDate, Long endDate, BasicDBList filterList);

}
