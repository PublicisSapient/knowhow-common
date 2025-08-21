package com.publicissapient.kpidashboard.common.repository.scm;

import java.util.List;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;

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
