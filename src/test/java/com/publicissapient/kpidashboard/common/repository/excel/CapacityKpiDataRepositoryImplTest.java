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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.publicissapient.kpidashboard.common.model.application.AdditionalFilterCapacity;
import com.publicissapient.kpidashboard.common.model.application.LeafNodeCapacity;
import com.publicissapient.kpidashboard.common.model.excel.CapacityKpiData;

/**
 * @author shi6
 */
@ExtendWith(SpringExtension.class)
public class CapacityKpiDataRepositoryImplTest {
	@Mock
	private MongoOperations mongoOperations;

	@InjectMocks
	private CapacityKpiDataRepositoryImpl capacityKpiDataRepository;

	@Test
	public void testFindByWithoutFilters() {
		// Mock data
		Map<String, Object> mapOfFilters = new HashMap<>();
		Map<String, Map<String, Object>> uniqueProjectMap = new HashMap<>();

		// Mock behavior
		when(mongoOperations.find(any(Query.class), eq(CapacityKpiData.class))).thenReturn(Collections.emptyList());

		// Test
		List<CapacityKpiData> result = capacityKpiDataRepository.findByFilters(mapOfFilters, uniqueProjectMap);

		// Verify that the find method is called with the correct parameters
		verify(mongoOperations, times(1)).find(any(Query.class), eq(CapacityKpiData.class));

		// You can add additional assertions based on the expected behavior of your
		// method
	}

	@Test
	public void testFindByWithFilters() {
		// Mock data
		Map<String, Object> mapOfFilters = new HashMap<>();
		List<String> sprintList = Arrays.asList("sprint1", "sprint2");
		List<String> basicProjectConfigIds = Arrays.asList("config1");
		mapOfFilters.put("sprint_id", sprintList.stream().distinct().collect(Collectors.toList()));
		mapOfFilters.put("basicConfigId", basicProjectConfigIds.stream().distinct().collect(Collectors.toList()));

		Map<String, Map<String, Object>> uniqueProjectMap = new HashMap<>();
		uniqueProjectMap.put("config1", mapOfFilters);

		// Mock behavior
		when(mongoOperations.find(any(Query.class), eq(CapacityKpiData.class))).thenReturn(Collections.emptyList());

		// Test
		capacityKpiDataRepository.findByFilters(mapOfFilters, uniqueProjectMap);

		// Verify that the find method is called with the correct parameters
		verify(mongoOperations, times(1)).find(any(Query.class), eq(CapacityKpiData.class));
	}

	@Test
	public void testProcessAdditionalFilters_WithValidData() {
		// Setup test data
		CapacityKpiData capacityData = createCapacityKpiDataWithFilters();
		List<CapacityKpiData> dataList = Arrays.asList(capacityData);

		Map<String, Object> filters = new HashMap<>();
		filters.put("additionalFilterCapacityList.nodeCapacityList.additionalFilterId", Arrays.asList("node1", "node2"));
		filters.put("additionalFilterCapacityList.filterId", Arrays.asList("filter1"));

		when(mongoOperations.find(any(Query.class), eq(CapacityKpiData.class))).thenReturn(dataList);

		List<CapacityKpiData> result = capacityKpiDataRepository.findByFilters(filters, new HashMap<>());

		assertEquals(1, result.size());
		assertEquals(150.0, result.get(0).getCapacityPerSprint());
	}

	@Test
	public void testProcessAdditionalFilters_WithEmptyAdditionalFilterList() {
		CapacityKpiData capacityData = new CapacityKpiData();
		capacityData.setAdditionalFilterCapacityList(null);
		List<CapacityKpiData> dataList = Arrays.asList(capacityData);

		Map<String, Object> filters = new HashMap<>();
		filters.put("additionalFilterCapacityList.nodeCapacityList.additionalFilterId", Arrays.asList("node1"));
		filters.put("additionalFilterCapacityList.filterId", Arrays.asList("filter1"));

		when(mongoOperations.find(any(Query.class), eq(CapacityKpiData.class))).thenReturn(dataList);

		List<CapacityKpiData> result = capacityKpiDataRepository.findByFilters(filters, new HashMap<>());

		assertEquals(1, result.size());
		assertEquals(0.0, result.get(0).getCapacityPerSprint());
	}

	@Test
	public void testProcessAdditionalFilters_WithNoMatchingFilters() {
		CapacityKpiData capacityData = createCapacityKpiDataWithFilters();
		List<CapacityKpiData> dataList = Arrays.asList(capacityData);

		Map<String, Object> filters = new HashMap<>();
		filters.put("additionalFilterCapacityList.nodeCapacityList.additionalFilterId", Arrays.asList("nonexistent"));
		filters.put("additionalFilterCapacityList.filterId", Arrays.asList("nonexistent"));

		when(mongoOperations.find(any(Query.class), eq(CapacityKpiData.class))).thenReturn(dataList);

		List<CapacityKpiData> result = capacityKpiDataRepository.findByFilters(filters, new HashMap<>());

		assertEquals(1, result.size());
		assertEquals(0.0, result.get(0).getCapacityPerSprint());
	}

	@Test
	public void testProcessAdditionalFilters_CaseInsensitiveFilterId() {
		CapacityKpiData capacityData = createCapacityKpiDataWithFilters();
		List<CapacityKpiData> dataList = Arrays.asList(capacityData);

		Map<String, Object> filters = new HashMap<>();
		filters.put("additionalFilterCapacityList.nodeCapacityList.additionalFilterId", Arrays.asList("node1"));
		filters.put("additionalFilterCapacityList.filterId", Arrays.asList("FILTER1")); // uppercase

		when(mongoOperations.find(any(Query.class), eq(CapacityKpiData.class))).thenReturn(dataList);

		List<CapacityKpiData> result = capacityKpiDataRepository.findByFilters(filters, new HashMap<>());

		assertEquals(1, result.size());
		assertEquals(100.0, result.get(0).getCapacityPerSprint());
	}

	private CapacityKpiData createCapacityKpiDataWithFilters() {
		LeafNodeCapacity node1 = new LeafNodeCapacity("node1", 100.0);
		LeafNodeCapacity node2 = new LeafNodeCapacity("node2", 50.0);
		LeafNodeCapacity node3 = new LeafNodeCapacity("node3", 25.0);

		AdditionalFilterCapacity filter1 = new AdditionalFilterCapacity();
		filter1.setFilterId("filter1");
		filter1.setNodeCapacityList(Arrays.asList(node1, node2));

		AdditionalFilterCapacity filter2 = new AdditionalFilterCapacity();
		filter2.setFilterId("filter2");
		filter2.setNodeCapacityList(Arrays.asList(node3));

		CapacityKpiData capacityData = new CapacityKpiData();
		capacityData.setAdditionalFilterCapacityList(Arrays.asList(filter1, filter2));
		return capacityData;
	}
}
