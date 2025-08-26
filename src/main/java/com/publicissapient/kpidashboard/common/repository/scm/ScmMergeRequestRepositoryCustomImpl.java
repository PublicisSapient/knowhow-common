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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.publicissapient.kpidashboard.common.model.scm.ScmMergeRequests;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ScmMergeRequestRepositoryCustomImpl implements ScmMergeRequestRepositoryCustom {

	private static final String SCM_MERGE_REQUESTS_COLLECTION = "scm_merge_requests";
	private static final String UPDATED_DATE = "updatedDate";

	private final MongoOperations operations;

	@Override
	public List<ScmMergeRequests> findMergeList(Long startDate, Long endDate, BasicDBList filterList) {
		if (filterList == null || filterList.isEmpty()) {
			return Collections.emptyList();
		}

		List<BasicDBObject> pipeline = buildPipeline(filterList, startDate, endDate);

		try (MongoCursor<Document> cursor = operations.getCollection(SCM_MERGE_REQUESTS_COLLECTION).aggregate(pipeline)
				.iterator()) {
			return mapMergeRequests(cursor);
		}
	}

	private List<BasicDBObject> buildPipeline(BasicDBList filterList, Long startDate, Long endDate) {
		return List.of(new BasicDBObject("$match", new BasicDBObject("$or", filterList).append(UPDATED_DATE,
				new BasicDBObject("$gte", startDate).append("$lte", endDate))));
	}

	private List<ScmMergeRequests> mapMergeRequests(MongoCursor<Document> cursor) {
		List<ScmMergeRequests> mergeRequests = new ArrayList<>();
		while (cursor.hasNext()) {
			Document document = cursor.next();
			ScmMergeRequests mergeRequest = operations.getConverter().read(ScmMergeRequests.class, document);
			mergeRequests.add(mergeRequest);
		}
		return mergeRequests;
	}

}