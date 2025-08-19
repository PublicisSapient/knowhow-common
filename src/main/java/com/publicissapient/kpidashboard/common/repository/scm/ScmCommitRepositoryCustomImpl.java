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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.publicissapient.kpidashboard.common.model.scm.ScmCommits;
import org.springframework.stereotype.Repository;

@Repository
public class ScmCommitRepositoryCustomImpl implements ScmCommitRepositoryCustom {
	private static final String IDENT_SCM_COMMIT_TIMESTAMP = "$commitTimestamp";
	private static final String SCM_COMMIT_TIMESTAMP = "commitTimestamp";
	private static final String COUNT = "count";
	private static final String IDENT_PROJECT = "$project";
	private static final String PROCESSOR_ITEM_ID = "processorItemId";
	private static final String ID = "_id";
	private static final String DATE = "date";

	@Autowired
	private MongoOperations operations;

	@Override
	public List<ScmCommits> findCommitList(List<ObjectId> collectorItemIdList, Long startDate, Long endDate,
			BasicDBList filterList) {
		// Define pipeline stages
		List<BasicDBObject> pipeline = Arrays.asList(
				// Match stage: Filter documents based on timestamp and filter list
				new BasicDBObject("$match",
						new BasicDBObject("$or", filterList).append(SCM_COMMIT_TIMESTAMP,
								new BasicDBObject("$gte", startDate).append("$lte", endDate))),

				// Lookup stage: Join with scm_users collection to fetch commitAuthor details
				new BasicDBObject("$lookup",
						new BasicDBObject("from", "scm_users").append("localField", "commitAuthorId")
								.append("foreignField", "_id").append("as", "commitAuthorDetails")),

				// Project stage: Add commit timestamp and include processor item ID
				new BasicDBObject(IDENT_PROJECT,
						new BasicDBObject(SCM_COMMIT_TIMESTAMP,
								new BasicDBObject("$add", new Object[] { new Date(0), IDENT_SCM_COMMIT_TIMESTAMP }))
								.append(PROCESSOR_ITEM_ID, 1)),

				// Group stage: Group by date and processor item ID, and calculate count
				new BasicDBObject("$group", new BasicDBObject(ID, new BasicDBObject(DATE,
						new BasicDBObject("$dateToString",
								new BasicDBObject("format", "%Y-%m-%d").append(DATE, IDENT_SCM_COMMIT_TIMESTAMP)))
						.append(PROCESSOR_ITEM_ID, "$processorItemId")).append(COUNT, new BasicDBObject("$sum", 1))),

				// Project stage: Restructure the output fields
				new BasicDBObject(IDENT_PROJECT,
						new BasicDBObject(ID, 0).append(DATE, "$_id.date")
								.append(PROCESSOR_ITEM_ID, "$_id.processorItemId").append(COUNT, 1)),

				// Sort stage: Sort by date in ascending order
				new BasicDBObject("$sort", new BasicDBObject(DATE, 1)));

		// Execute aggregation query
		AggregateIterable<Document> cursor = operations.getCollection("scm_commit_details").aggregate(pipeline);

		// Convert results to ScmCommits objects
		List<ScmCommits> commitList = new ArrayList<>();
		try (MongoCursor<Document> iterator = cursor.iterator()) {
			while (iterator.hasNext()) {
				Document document = iterator.next();
				ScmCommits scmCommit = operations.getConverter().read(ScmCommits.class, document);
				commitList.add(scmCommit);
			}
		}

		return commitList;
	}
}
