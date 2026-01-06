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

package com.publicissapient.kpidashboard.common.repository.jira;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.client.result.UpdateResult;
import com.publicissapient.kpidashboard.common.model.jira.KanbanJiraIssue;

/*
author @shi6
 */
@ExtendWith(SpringExtension.class)
public class KanbanJiraIssueRepositoryImplTest {
	@Mock
	private MongoOperations operations;

	private KanbanJiraIssueRepositoryImpl kanbanJiraIssueRepository;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		kanbanJiraIssueRepository = new KanbanJiraIssueRepositoryImpl(operations);
	}

	@Test
	public void testFindIssuesByType() {
		// Arrange
		List<String> projectIds = Arrays.asList("projectId1", "projectId2");
		String dateFrom = "2022-01-01";
		String dateTo = "2022-01-10";

		// Act
		when(operations.find(any(Query.class), eq(KanbanJiraIssue.class))).thenReturn(Arrays.asList(
				KanbanJiraIssue.builder().issueId("issue1").projectID("projectId1").changeDate("2022-01-05").build(),
				KanbanJiraIssue.builder().issueId("issue2").projectID("projectId2").changeDate("2022-01-08").build()));

		Map<String, List<String>> mapOfFilters = new HashMap<>();
		mapOfFilters.put("basicProjectConfigId", projectIds);
		List<KanbanJiraIssue> result = kanbanJiraIssueRepository.findIssuesByType(mapOfFilters, dateFrom, dateTo);

		// Assert
		assertEquals(2, result.size());
		// Add more assertions based on your expectations
	}

	@Test
	public void testFindIssuesByDateAndType() {
		// Arrange
		List<String> projectIds = Arrays.asList("projectId1", "projectId2");
		Map<String, List<String>> mapOfFilters = new HashMap<>();
		mapOfFilters.put("basicProjectConfigId", projectIds);

		Map<String, Object> projectConfig = new HashMap<>();
		projectConfig.put("typeName", Arrays.asList("Bug", "Story"));
		Map<String, Map<String, Object>> uniqueProjectMap = new HashMap<>();
		uniqueProjectMap.put("projectId1", projectConfig);

		// Act
		when(operations.find(any(Query.class), eq(KanbanJiraIssue.class))).thenReturn(Arrays.asList(
				KanbanJiraIssue.builder().issueId("issue1").projectID("projectId1").changeDate("2022-01-05").build(),
				KanbanJiraIssue.builder().issueId("issue2").projectID("projectId2").changeDate("2022-01-08").build()));

		List<KanbanJiraIssue> result = kanbanJiraIssueRepository.findIssuesByDateAndType(mapOfFilters, uniqueProjectMap,
				"2022-01-01", "2022-01-10", "range");

		// Assert
		assertEquals(2, result.size());
	}

	@Test
	public void testFindIssuesByDateAndTypeAndStatus() {
		// Arrange
		List<String> projectIds = Arrays.asList("projectId1", "projectId2");
		Map<String, List<String>> mapOfFilters = new HashMap<>();
		mapOfFilters.put("basicProjectConfigId", projectIds);

		Map<String, Object> projectConfig = new HashMap<>();
		projectConfig.put("typeName", Arrays.asList("Bug", "Story"));
		Map<String, Map<String, Object>> uniqueProjectMap = new HashMap<>();
		uniqueProjectMap.put("projectId1", projectConfig);

		// Act
		when(operations.find(any(Query.class), eq(KanbanJiraIssue.class))).thenReturn(Arrays.asList(
				KanbanJiraIssue.builder().issueId("issue1").projectID("projectId1").changeDate("2022-01-05").build(),
				KanbanJiraIssue.builder().issueId("issue2").projectID("projectId2").changeDate("2022-01-08").build()));

		List<KanbanJiraIssue> result = kanbanJiraIssueRepository.findIssuesByDateAndTypeAndStatus(mapOfFilters,
				uniqueProjectMap, "2022-01-01", "2022-01-10", "range", "nin");

		// Assert
		assertEquals(2, result.size());
	}

	@Test
	public void testFindCostOfDelayByType() {
		// Arrange
		Map<String, List<String>> mapOfFilters = Collections.singletonMap("typeName", Arrays.asList("Bug", "Story"));

		// Act
		when(operations.find(any(Query.class), eq(KanbanJiraIssue.class))).thenReturn(Arrays.asList(
				KanbanJiraIssue.builder().issueId("issue1").projectID("projectId1").changeDate("2022-01-05").build(),
				KanbanJiraIssue.builder().issueId("issue2").projectID("projectId2").changeDate("2022-01-08").build()));

		List<KanbanJiraIssue> result = kanbanJiraIssueRepository.findCostOfDelayByType(mapOfFilters);

		// Assert
		assertEquals(2, result.size());
		// Add more assertions based on your expectations
	}

	@Test
	public void testUpdateByBasicProjectConfigId() {
		// Arrange
		String basicProjectConfigId = "projectId1";
		List<String> fieldsToUnset = Arrays.asList("field1", "field2");

		UpdateResult mock = mock(UpdateResult.class);
		// Act
		doReturn(mock).when(operations).updateMulti(any(Query.class), any(Update.class), eq(KanbanJiraIssue.class));
		kanbanJiraIssueRepository.updateByBasicProjectConfigId(basicProjectConfigId, fieldsToUnset);
	}
}
