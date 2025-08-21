package com.publicissapient.kpidashboard.common.repository.scm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.publicissapient.kpidashboard.common.model.scm.ScmMergeRequests;
import com.publicissapient.kpidashboard.common.model.scm.User;
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

	private static final String SCM_USERS_COLLECTION = "scm_users";
	private static final String SCM_MERGE_REQUESTS_COLLECTION = "scm_merge_requests";
	private static final String AUTHOR_USER_ID = "authorUserId";
	private static final String AUTHOR_DETAILS = "authorDetails";
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
		return List.of(
				new BasicDBObject("$match",
						new BasicDBObject("$or", filterList).append(UPDATED_DATE,
								new BasicDBObject("$gte", startDate).append("$lte", endDate))),
				new BasicDBObject("$lookup",
						new BasicDBObject("from", SCM_USERS_COLLECTION).append("localField", AUTHOR_USER_ID)
								.append("foreignField", "_id").append("as", AUTHOR_DETAILS)));
	}

	private List<ScmMergeRequests> mapMergeRequests(MongoCursor<Document> cursor) {
		List<ScmMergeRequests> mergeRequests = new ArrayList<>();
		while (cursor.hasNext()) {
			Document document = cursor.next();
			ScmMergeRequests mergeRequest = operations.getConverter().read(ScmMergeRequests.class, document);
			mergeRequest.setAuthorDetails(mapAuthorDetails(document));
			mergeRequests.add(mergeRequest);
		}
		return mergeRequests;
	}

	private User mapAuthorDetails(Document document) {
		List<Document> authorDetailsDocs = (List<Document>) document.get(AUTHOR_DETAILS);
		if (authorDetailsDocs != null && !authorDetailsDocs.isEmpty()) {
			return operations.getConverter().read(User.class, authorDetailsDocs.get(0));
		}
		return null;
	}
}