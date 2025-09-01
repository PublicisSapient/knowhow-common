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

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.publicissapient.kpidashboard.common.model.scm.ScmCommits;

@Slf4j
@Repository
@AllArgsConstructor
public class ScmCommitRepositoryCustomImpl implements ScmCommitRepositoryCustom {

	private static final String IDENT_SCM_COMMIT_TIMESTAMP = "$commitTimestamp";
	private static final String SCM_COMMIT_TIMESTAMP = "commitTimestamp";
	private static final String COUNT = "count";
	private static final String IDENT_PROJECT = "$project";
	private static final String PROCESSOR_ITEM_ID = "processorItemId";
	private static final String ID = "_id";
	private static final String DATE = "date";

	private static final String MATCH = "$match";
	private static final String OR = "$or";
	private static final String GTE = "$gte";
	private static final String LTE = "$lte";
	private static final String GROUP = "$group";
	private static final String SUM = "$sum";
	private static final String SORT = "$sort";
	private static final String DATE_TO_STRING = "$dateToString";
	private static final String FORMAT = "format";
	private static final String DATE_FORMAT = "%Y-%m-%d";
	private static final String ADD = "$add";
	private static final String COLLECTION_NAME = "scm_commit_details";

	private MongoOperations operations;

	@Override
	public List<ScmCommits> findCommitList(Long startDate, Long endDate, BasicDBList filterList) {
		List<BasicDBObject> pipeline = buildAggregationPipeline(startDate, endDate, filterList);
		return executeAggregation(pipeline);
	}

	private List<BasicDBObject> buildAggregationPipeline(Long startDate, Long endDate, BasicDBList filterList) {
		return Arrays.asList(buildMatchStage(startDate, endDate, filterList), buildProjectStage(), buildGroupStage(),
				buildFinalProjectStage(), buildSortStage());
	}

	private BasicDBObject buildMatchStage(Long startDate, Long endDate, BasicDBList filterList) {
		return new BasicDBObject(MATCH, new BasicDBObject(OR, filterList).append(SCM_COMMIT_TIMESTAMP,
				new BasicDBObject(GTE, startDate).append(LTE, endDate)));
	}

	private BasicDBObject buildProjectStage() {
		return new BasicDBObject(IDENT_PROJECT,
				new BasicDBObject(SCM_COMMIT_TIMESTAMP,
						new BasicDBObject(ADD, new Object[] { new Date(0), IDENT_SCM_COMMIT_TIMESTAMP }))
						.append(PROCESSOR_ITEM_ID, 1));
	}

	private BasicDBObject buildGroupStage() {
		return new BasicDBObject(GROUP,
				new BasicDBObject(ID, new BasicDBObject(DATE,
						new BasicDBObject(DATE_TO_STRING,
								new BasicDBObject(FORMAT, DATE_FORMAT).append(DATE, IDENT_SCM_COMMIT_TIMESTAMP)))
						.append(PROCESSOR_ITEM_ID, "$processorItemId")).append(COUNT, new BasicDBObject(SUM, 1)));
	}

	private BasicDBObject buildFinalProjectStage() {
		return new BasicDBObject(IDENT_PROJECT, new BasicDBObject(ID, 0).append(DATE, "$_id.date")
				.append(PROCESSOR_ITEM_ID, "$_id.processorItemId").append(COUNT, 1));
	}

	private BasicDBObject buildSortStage() {
		return new BasicDBObject(SORT, new BasicDBObject(DATE, 1));
	}

	private List<ScmCommits> executeAggregation(List<BasicDBObject> pipeline) {
		AggregateIterable<Document> cursor = operations.getCollection(COLLECTION_NAME).aggregate(pipeline);
		return convertDocumentsToScmCommits(cursor);
	}

	private List<ScmCommits> convertDocumentsToScmCommits(AggregateIterable<Document> cursor) {
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
