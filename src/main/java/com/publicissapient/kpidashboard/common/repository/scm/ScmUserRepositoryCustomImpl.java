/*
 *  Copyright 2024 <Sapient Corporation>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the
 *  License.
 */

package com.publicissapient.kpidashboard.common.repository.scm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCursor;
import com.publicissapient.kpidashboard.common.model.scm.User;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@AllArgsConstructor
public class ScmUserRepositoryCustomImpl implements ScmUserRepositoryCustom {

	private static final String SCM_USER_COLLECTION = "scm_users";

	private final MongoOperations operations;

	@Override
	public List<User> findScmUserList(BasicDBList filterList) {
		if (filterList == null || filterList.isEmpty()) {
			return Collections.emptyList();
		}

		List<BasicDBObject> pipeline = List.of(new BasicDBObject("$match", new BasicDBObject("$or", filterList)));
		List<User> userList = new ArrayList<>();
		try (MongoCursor<Document> cursor = operations.getCollection(SCM_USER_COLLECTION).aggregate(pipeline).iterator()) {
			userList.addAll(mapScmUsers(cursor));
		} catch (MongoCommandException ex) {
			log.error("No Users found {}", ex.getErrorMessage());
		}
		return userList;
	}

	private List<User> mapScmUsers(MongoCursor<Document> cursor) {
		List<User> scmUsers = new ArrayList<>();
		while (cursor.hasNext()) {
			Document document = cursor.next();
			User scmUser = operations.getConverter().read(User.class, document);
			scmUsers.add(scmUser);
		}
		return scmUsers;
	}
}
