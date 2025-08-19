package com.publicissapient.kpidashboard.common.repository.scm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.publicissapient.kpidashboard.common.model.scm.ScmMergeRequests;
import com.publicissapient.kpidashboard.common.model.scm.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import org.springframework.stereotype.Repository;

@Repository
public class ScmMergeRequestRepositoryCustomImpl implements ScmMergeRequestRepositoryCustom {

	private static final String IDENT_CREATED_DATE = "$createdDate";
	private static final String SCM_CREATED_DATE = "createdDate";
	private static final String COUNT = "count";
	private static final String IDENT_PROJECT = "$project";
	private static final String PROCESSOR_ITEM_ID = "processorItemId";
	private static final String ID = "_id";
	private static final String DATE = "date";
	private static final String SCM_MERGED_TIMESTAMP = "closedDate";

	@Autowired
	private MongoOperations operations;

	@Override
	public List<ScmMergeRequests> findMergeList(List<ObjectId> collectorItemIdList, Long startDate, Long endDate,
			BasicDBList filterList) {
		List<BasicDBObject> pipeline = Arrays.asList(
				new BasicDBObject("$match",
						new BasicDBObject("$or", filterList).append(SCM_MERGED_TIMESTAMP,
								new BasicDBObject("$gte", startDate).append("$lte", endDate))),
				new BasicDBObject("$lookup", new BasicDBObject("from", "scm_users").append("localField", "authorUserId")
						.append("foreignField", "username").append("as", "authorDetails")));

		List<ScmMergeRequests> returnList = new ArrayList<>();
		try (MongoCursor<Document> itr = operations.getCollection("scm_merge_requests").aggregate(pipeline)
				.iterator()) {
			while (itr.hasNext()) {
				Document obj = itr.next();
				ScmMergeRequests mergeRequest = operations.getConverter().read(ScmMergeRequests.class, obj);

				// Manually map authorDetails if necessary
				List<Document> authorDetailsDocs = (List<Document>) obj.get("authorDetails");
				if (authorDetailsDocs != null && !authorDetailsDocs.isEmpty()) {
					mergeRequest.setAuthorDetails(authorDetailsDocs.stream()
							.map(doc -> operations.getConverter().read(User.class, doc)).findFirst().orElse(null));
				}

				returnList.add(mergeRequest);
			}
		}
		return returnList;
	}
}