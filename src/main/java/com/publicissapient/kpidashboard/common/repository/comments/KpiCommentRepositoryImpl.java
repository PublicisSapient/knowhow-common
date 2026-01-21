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

package com.publicissapient.kpidashboard.common.repository.comments;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.publicissapient.kpidashboard.common.model.comments.KPIComments;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link KpiCommentRepositoryCustom} for KPI comment
 * operations.
 */
@Service
@RequiredArgsConstructor
public class KpiCommentRepositoryImpl implements KpiCommentRepositoryCustom {

	private static final String FIELD_COMMENTS_INFO_COMMENT_ID = "commentsInfo.commentId";
	private static final String FIELD_COMMENT_ID = "commentId";
	private static final String FIELD_COMMENTS_INFO = "commentsInfo";

	private final MongoTemplate mongoTemplate;

	@Override
	public void deleteByCommentId(String commentId) {
		Query query = Query.query(Criteria.where(FIELD_COMMENTS_INFO_COMMENT_ID).is(commentId));
		Query commentIdQuery = Query.query(Criteria.where(FIELD_COMMENT_ID).is(commentId));
		Update update = new Update().pull(FIELD_COMMENTS_INFO, commentIdQuery);
		mongoTemplate.updateMulti(query, update, KPIComments.class);
	}
}
