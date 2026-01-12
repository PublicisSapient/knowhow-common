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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.publicissapient.kpidashboard.common.model.jira.SprintDetails;

/*
author @shi6
 */
@ExtendWith(SpringExtension.class)
public class SprintRepositoryCustomImplTest {

	@InjectMocks
	private SprintRepositoryCustomImpl sprintRepositoryCustomImpl;

	@Mock
	private MongoOperations operations;

	@Mock
	private AggregationResults<SprintDetails> aggregationResults;

	private ObjectId projectConfigId1;
	private ObjectId projectConfigId2;
	private SprintDetails sprintDetails1;
	private SprintDetails sprintDetails2;

	@BeforeEach
	void setUp() {
		projectConfigId1 = new ObjectId();
		projectConfigId2 = new ObjectId();

		sprintDetails1 = new SprintDetails();
		sprintDetails1.setSprintID("sprint1");
		sprintDetails1.setSprintName("Sprint 1");
		sprintDetails1.setBasicProjectConfigId(projectConfigId1);
		sprintDetails1.setState("CLOSED");
		sprintDetails1.setCompleteDate("2023-12-01");

		sprintDetails2 = new SprintDetails();
		sprintDetails2.setSprintID("sprint2");
		sprintDetails2.setSprintName("Sprint 2");
		sprintDetails2.setBasicProjectConfigId(projectConfigId2);
		sprintDetails2.setState("ACTIVE");
		sprintDetails2.setCompleteDate("2023-12-15");
	}

	@Test
	public void testFindByBasicProjectConfigIdInAndStateInOrderByStartDateDesc() {
		// Set up test data
		Set<ObjectId> basicProjectConfigIds = new HashSet<>();
		basicProjectConfigIds.add(projectConfigId1);
		basicProjectConfigIds.add(projectConfigId2);

		List<String> sprintStatusList = Arrays.asList("ACTIVE", "CLOSED");
		long limit = 5;

		List<SprintDetails> expectedResults = Arrays.asList(sprintDetails1, sprintDetails2);
		when(aggregationResults.getMappedResults()).thenReturn(expectedResults);
		when(operations.aggregate(any(Aggregation.class), eq("sprint_details"), eq(SprintDetails.class)))
				.thenReturn(aggregationResults);

		// Call the method
		List<SprintDetails> result = sprintRepositoryCustomImpl
				.findByBasicProjectConfigIdInAndStateInOrderByStartDateDesc(basicProjectConfigIds, sprintStatusList, limit);

		// Verify results
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(expectedResults, result);

		// Verify aggregation was called
		verify(operations).aggregate(any(Aggregation.class), eq("sprint_details"), eq(SprintDetails.class));
	}

	@Test
	public void testFindByBasicProjectConfigIdInAndStateInOrderByStartDateDesc_EmptyConfigIds() {
		Set<ObjectId> basicProjectConfigIds = new HashSet<>();
		List<String> sprintStatusList = Arrays.asList("ACTIVE", "CLOSED");
		long limit = 5;

		when(aggregationResults.getMappedResults()).thenReturn(Collections.emptyList());
		when(operations.aggregate(any(Aggregation.class), eq("sprint_details"), eq(SprintDetails.class)))
				.thenReturn(aggregationResults);

		List<SprintDetails> result = sprintRepositoryCustomImpl
				.findByBasicProjectConfigIdInAndStateInOrderByStartDateDesc(basicProjectConfigIds, sprintStatusList, limit);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testFindByBasicProjectConfigIdInOrderByCompletedDateDesc() {
		List<ObjectId> basicProjectConfigIds = Arrays.asList(projectConfigId1, projectConfigId2);
		int limit = 10;

		List<SprintDetails> expectedResults = Arrays.asList(sprintDetails1, sprintDetails2);
		when(aggregationResults.getMappedResults()).thenReturn(expectedResults);
		when(operations.aggregate(any(Aggregation.class), eq("sprint_details"), eq(SprintDetails.class)))
				.thenReturn(aggregationResults);

		List<SprintDetails> result = sprintRepositoryCustomImpl
				.findByBasicProjectConfigIdInOrderByCompletedDateDesc(basicProjectConfigIds, limit);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(expectedResults, result);

		// Verify aggregation was called with correct parameters
		ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
		verify(operations).aggregate(aggregationCaptor.capture(), eq("sprint_details"), eq(SprintDetails.class));

		Aggregation capturedAggregation = aggregationCaptor.getValue();
		assertNotNull(capturedAggregation);
	}

	@Test
	public void testFindByBasicProjectConfigIdInOrderByCompletedDateDesc_EmptyConfigIds() {
		List<ObjectId> basicProjectConfigIds = Collections.emptyList();
		int limit = 10;

		when(aggregationResults.getMappedResults()).thenReturn(Collections.emptyList());
		when(operations.aggregate(any(Aggregation.class), eq("sprint_details"), eq(SprintDetails.class)))
				.thenReturn(aggregationResults);

		List<SprintDetails> result = sprintRepositoryCustomImpl
				.findByBasicProjectConfigIdInOrderByCompletedDateDesc(basicProjectConfigIds, limit);

		assertNotNull(result);
		assertEquals(0, result.size());
		verify(operations).aggregate(any(Aggregation.class), eq("sprint_details"), eq(SprintDetails.class));
	}

	@Test
	public void testFindByBasicProjectConfigIdInOrderByCompletedDateDesc_SingleConfigId() {
		List<ObjectId> basicProjectConfigIds = Arrays.asList(projectConfigId1);
		int limit = 5;

		List<SprintDetails> expectedResults = Arrays.asList(sprintDetails1);
		when(aggregationResults.getMappedResults()).thenReturn(expectedResults);
		when(operations.aggregate(any(Aggregation.class), eq("sprint_details"), eq(SprintDetails.class)))
				.thenReturn(aggregationResults);

		List<SprintDetails> result = sprintRepositoryCustomImpl
				.findByBasicProjectConfigIdInOrderByCompletedDateDesc(basicProjectConfigIds, limit);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(sprintDetails1, result.get(0));
	}

	@Test
	public void testFindByBasicProjectConfigIdInOrderByCompletedDateDesc_ZeroLimit() {
		List<ObjectId> basicProjectConfigIds = Arrays.asList(projectConfigId1, projectConfigId2);
		int limit = 0;

		when(aggregationResults.getMappedResults()).thenReturn(Collections.emptyList());
		when(operations.aggregate(any(Aggregation.class), eq("sprint_details"), eq(SprintDetails.class)))
				.thenReturn(aggregationResults);

		List<SprintDetails> result = sprintRepositoryCustomImpl
				.findByBasicProjectConfigIdInOrderByCompletedDateDesc(basicProjectConfigIds, limit);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void testFindByBasicProjectConfigIdInOrderByCompletedDateDesc_LargeLimit() {
		List<ObjectId> basicProjectConfigIds = Arrays.asList(projectConfigId1, projectConfigId2);
		int limit = 1000;

		List<SprintDetails> expectedResults = Arrays.asList(sprintDetails1, sprintDetails2);
		when(aggregationResults.getMappedResults()).thenReturn(expectedResults);
		when(operations.aggregate(any(Aggregation.class), eq("sprint_details"), eq(SprintDetails.class)))
				.thenReturn(aggregationResults);

		List<SprintDetails> result = sprintRepositoryCustomImpl
				.findByBasicProjectConfigIdInOrderByCompletedDateDesc(basicProjectConfigIds, limit);

		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(expectedResults, result);
	}
}
