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

package com.publicissapient.kpidashboard.common.repository.application.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.publicissapient.kpidashboard.common.model.application.ProjectToolConfigProcessorItem;
import com.publicissapient.kpidashboard.common.model.application.Tool;

import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link ProjectToolConfigRepositoryCustom} that provides
 * custom repository operations for project tool configurations.
 */
@RequiredArgsConstructor
public class ProjectToolConfigRepositoryImpl implements ProjectToolConfigRepositoryCustom {

	private static final String LOOKUP_OPERATOR = "$lookup";
	private static final String FROM_FIELD = "from";
	private static final String CONNECTIONS_COLLECTION = "connections";
	private static final String PROCESSOR_ITEMS_COLLECTION = "processor_items";
	private static final String PROJECT_TOOL_CONFIGS_COLLECTION = "project_tool_configs";
	private static final String CONNECTION_ID_FIELD = "connectionId";
	private static final String ID_FIELD = "_id";
	private static final String TOOL_CONFIG_ID_FIELD = "toolConfigId";
	private static final String LOCAL_FIELD = "localField";
	private static final String FOREIGN_FIELD = "foreignField";
	private static final String AS_FIELD = "as";
	private static final String CONNECTION_AS = "connection";
	private static final String PROCESSOR_ITEM_LIST_AS = "processorItemList";

	private final MongoOperations mongoOperations;

	@Override
	public List<Tool> getToolList() {
		BasicDBObject connectionObj = new BasicDBObject(LOOKUP_OPERATOR,
				new BasicDBObject(FROM_FIELD, CONNECTIONS_COLLECTION).append(LOCAL_FIELD, CONNECTION_ID_FIELD)
						.append(FOREIGN_FIELD, ID_FIELD).append(AS_FIELD, CONNECTION_AS));

		BasicDBObject processorItemObj = new BasicDBObject(LOOKUP_OPERATOR,
				new BasicDBObject(FROM_FIELD, PROCESSOR_ITEMS_COLLECTION).append(LOCAL_FIELD, ID_FIELD)
						.append(FOREIGN_FIELD, TOOL_CONFIG_ID_FIELD).append(AS_FIELD, PROCESSOR_ITEM_LIST_AS));

		List<BasicDBObject> pipeline = Lists.newArrayList(connectionObj, processorItemObj);
		AggregateIterable<Document> cursor = mongoOperations.getCollection(PROJECT_TOOL_CONFIGS_COLLECTION)
				.aggregate(pipeline);
		MongoCursor<Document> itr = cursor.iterator();
		List<ProjectToolConfigProcessorItem> returnList = new ArrayList<>();
		while (itr.hasNext()) {
			Document obj = itr.next();
			MongoConverter converter = mongoOperations.getConverter();
			ProjectToolConfigProcessorItem item = converter.read(ProjectToolConfigProcessorItem.class, obj);
			returnList.add(item);
		}
		return transform(returnList);
	}

	private List<Tool> transform(List<ProjectToolConfigProcessorItem> list) {
		List<Tool> tools = new ArrayList<>();
		for (ProjectToolConfigProcessorItem item : list) {
			Tool toolObj = new Tool();
			toolObj.setProjectIds(item.getBasicProjectConfigId());
			toolObj.setTool(item.getToolName());
			toolObj.setBranch(item.getBranch());
			toolObj.setRepoSlug(item.getRepoSlug());
			toolObj.setRepositoryName(item.getRepositoryName());
			toolObj.setProcessorItemList(item.getProcessorItemList());
			tools.add(toolObj);
		}
		return tools;
	}
}
