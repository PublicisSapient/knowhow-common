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

package com.publicissapient.kpidashboard.common.repository.excel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.publicissapient.kpidashboard.common.model.application.AdditionalFilterCapacity;
import com.publicissapient.kpidashboard.common.model.application.LeafNodeCapacity;
import com.publicissapient.kpidashboard.common.model.excel.KanbanCapacity;

/**
 * @author shi6
 */
@ExtendWith(SpringExtension.class)
public class KanbanCapacityRepositoryImplTest {
	@Mock
	private MongoOperations mongoOperations;

	@InjectMocks
	private KanbanCapacityRepositoryImpl kanbanCapacityRepository;

	@Test
	public void testFindIssuesByType() {
		// Mock data
		Map<String, Object> mapOfFilters = new HashMap<>();
		List<ObjectId> projectList = new ArrayList<>();
		projectList.add(new ObjectId("61d6d4235c76563333369f02"));
		mapOfFilters.put("additionalFilterCapacityList.filterId", Arrays.asList("sqd"));
		mapOfFilters.put("additionalFilterCapacityList.nodeCapacityList.additionalFilterId",
				Arrays.asList("Java_61d6d4235c76563333369f02"));
		mapOfFilters.put("projectId", projectList.stream().distinct().collect(Collectors.toList()));

		String dateFrom = "2022-01-01";
		String dateTo = "2022-01-10";

		// Mock behavior
		when(mongoOperations.find(any(Query.class), eq(KanbanCapacity.class))).thenReturn(Collections.emptyList());

		// Test
		kanbanCapacityRepository.findIssuesByType(mapOfFilters, dateFrom, dateTo);

		// Verify that the find method is called with the correct parameters
		verify(mongoOperations, times(1)).find(any(Query.class), eq(KanbanCapacity.class));
	}

	@Test
	public void testFindByFilterMapAndDate() {
		// Mock data
		Map<String, String> mapOfFilters = new HashMap<>();
		mapOfFilters.put("config1", "date1");
		String dateFrom = "2022-01-01";

		// Mock behavior
		when(mongoOperations.find(any(Query.class), eq(KanbanCapacity.class))).thenReturn(Collections.emptyList());

		// Test
		kanbanCapacityRepository.findByFilterMapAndDate(mapOfFilters, dateFrom);

		// Verify that the find method is called with the correct parameters
		verify(mongoOperations, times(1)).find(any(Query.class), eq(KanbanCapacity.class));
	}

	@Test
	public void testProcessAdditionalFilters_WithValidData() {
		KanbanCapacity kanbanCapacity = createKanbanCapacityWithFilters();
		List<KanbanCapacity> capacityList = Arrays.asList(kanbanCapacity);

		Map<String, Object> filters = new HashMap<>();
		filters.put("additionalFilterCapacityList.nodeCapacityList.additionalFilterId", Arrays.asList("node1", "node2"));
		filters.put("additionalFilterCapacityList.filterId", Arrays.asList("filter1"));

		when(mongoOperations.find(any(Query.class), eq(KanbanCapacity.class))).thenReturn(capacityList);

		List<KanbanCapacity> result = kanbanCapacityRepository.findIssuesByType(filters, "2022-01-01", "2022-01-10");

		assertEquals(1, result.size());
		assertEquals(150.0, result.get(0).getCapacity());
	}

	@Test
	public void testProcessAdditionalFilters_WithEmptyAdditionalFilterList() {
		KanbanCapacity kanbanCapacity = new KanbanCapacity();
		kanbanCapacity.setAdditionalFilterCapacityList(null);
		List<KanbanCapacity> capacityList = Arrays.asList(kanbanCapacity);

		Map<String, Object> filters = new HashMap<>();
		filters.put("additionalFilterCapacityList.nodeCapacityList.additionalFilterId", Arrays.asList("node1"));
		filters.put("additionalFilterCapacityList.filterId", Arrays.asList("filter1"));

		when(mongoOperations.find(any(Query.class), eq(KanbanCapacity.class))).thenReturn(capacityList);

		List<KanbanCapacity> result = kanbanCapacityRepository.findIssuesByType(filters, "2022-01-01", "2022-01-10");

		assertEquals(1, result.size());
		assertEquals(0.0, result.get(0).getCapacity());
	}

	@Test
	public void testProcessAdditionalFilters_WithNoMatchingFilters() {
		KanbanCapacity kanbanCapacity = createKanbanCapacityWithFilters();
		List<KanbanCapacity> capacityList = Arrays.asList(kanbanCapacity);

		Map<String, Object> filters = new HashMap<>();
		filters.put("additionalFilterCapacityList.nodeCapacityList.additionalFilterId", Arrays.asList("nonexistent"));
		filters.put("additionalFilterCapacityList.filterId", Arrays.asList("nonexistent"));

		when(mongoOperations.find(any(Query.class), eq(KanbanCapacity.class))).thenReturn(capacityList);

		List<KanbanCapacity> result = kanbanCapacityRepository.findIssuesByType(filters, "2022-01-01", "2022-01-10");

		assertEquals(1, result.size());
		assertEquals(0.0, result.get(0).getCapacity());
	}

	@Test
	public void testProcessAdditionalFilters_CaseInsensitiveFilterId() {
		KanbanCapacity kanbanCapacity = createKanbanCapacityWithFilters();
		List<KanbanCapacity> capacityList = Arrays.asList(kanbanCapacity);

		Map<String, Object> filters = new HashMap<>();
		filters.put("additionalFilterCapacityList.nodeCapacityList.additionalFilterId", Arrays.asList("node1"));
		filters.put("additionalFilterCapacityList.filterId", Arrays.asList("FILTER1")); // uppercase

		when(mongoOperations.find(any(Query.class), eq(KanbanCapacity.class))).thenReturn(capacityList);

		List<KanbanCapacity> result = kanbanCapacityRepository.findIssuesByType(filters, "2022-01-01", "2022-01-10");

		assertEquals(1, result.size());
		assertEquals(100.0, result.get(0).getCapacity());
	}

	@Test
	public void testProcessAdditionalFilters_WithoutAdditionalFilterNodeId() {
		KanbanCapacity kanbanCapacity = createKanbanCapacityWithFilters();
		List<KanbanCapacity> capacityList = Arrays.asList(kanbanCapacity);

		Map<String, Object> filters = new HashMap<>();
		// Not adding additionalFilterCapacityList.nodeCapacityList.additionalFilterId
		filters.put("additionalFilterCapacityList.filterId", Arrays.asList("filter1"));

		when(mongoOperations.find(any(Query.class), eq(KanbanCapacity.class))).thenReturn(capacityList);

		List<KanbanCapacity> result = kanbanCapacityRepository.findIssuesByType(filters, "2022-01-01", "2022-01-10");

		assertEquals(1, result.size());
		// Capacity should remain unchanged since processAdditionalFilters returns early
	}

	private KanbanCapacity createKanbanCapacityWithFilters() {
		LeafNodeCapacity node1 = new LeafNodeCapacity("node1", 100.0);
		LeafNodeCapacity node2 = new LeafNodeCapacity("node2", 50.0);
		LeafNodeCapacity node3 = new LeafNodeCapacity("node3", 25.0);

		AdditionalFilterCapacity filter1 = new AdditionalFilterCapacity();
		filter1.setFilterId("filter1");
		filter1.setNodeCapacityList(Arrays.asList(node1, node2));

		AdditionalFilterCapacity filter2 = new AdditionalFilterCapacity();
		filter2.setFilterId("filter2");
		filter2.setNodeCapacityList(Arrays.asList(node3));

		KanbanCapacity kanbanCapacity = new KanbanCapacity();
		kanbanCapacity.setAdditionalFilterCapacityList(Arrays.asList(filter1, filter2));
		return kanbanCapacity;
	}
}
