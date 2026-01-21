package com.publicissapient.kpidashboard.common.repository.productivity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publicissapient.kpidashboard.common.model.productivity.calculation.Productivity;
import com.publicissapient.kpidashboard.common.repository.productivity.dto.ProductivityTemporalGrouping;
import com.publicissapient.kpidashboard.common.shared.enums.TemporalAggregationUnit;

@ExtendWith(SpringExtension.class)
public class ProductivityCustomRepositoryImplTest {

	@Mock
	private MongoTemplate mongoTemplate;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private AggregationResults<Productivity> productivityResults;

	@Mock
	private AggregationResults<Map> mapResults;

	@InjectMocks
	private ProductivityCustomRepositoryImpl repository;

	private Set<String> hierarchyNodeIds;
	private Productivity productivity;

	@BeforeEach
	void setUp() {
		hierarchyNodeIds = Set.of("project1", "project2");

		productivity = new Productivity();
		productivity.setHierarchyEntityNodeId("project1");
	}

	@Test
	void testGetLatestProductivityByCalculationDateForProjects_Success() {
		when(mongoTemplate.aggregate(
						any(Aggregation.class), eq("productivity"), eq(Productivity.class)))
				.thenReturn(productivityResults);
		when(productivityResults.getMappedResults()).thenReturn(List.of(productivity));

		List<Productivity> result =
				repository.getLatestProductivityByCalculationDateForProjects(hierarchyNodeIds);

		assertEquals(1, result.size());
		assertEquals("project1", result.get(0).getHierarchyEntityNodeId());
	}

	@Test
	void testGetLatestProductivityByCalculationDateForProjects_EmptyResult() {
		when(mongoTemplate.aggregate(
						any(Aggregation.class), eq("productivity"), eq(Productivity.class)))
				.thenReturn(productivityResults);
		when(productivityResults.getMappedResults()).thenReturn(List.of());

		List<Productivity> result =
				repository.getLatestProductivityByCalculationDateForProjects(hierarchyNodeIds);

		assertTrue(result.isEmpty());
	}

	@Test
	void testGetProductivitiesGroupedByTemporalUnit_Success() {
		Map<String, Object> responseMap = new HashMap<>();
		Map<String, Object> idMap = new HashMap<>();
		idMap.put("week", new Date());
		responseMap.put("_id", idMap);
		responseMap.put("entries", List.of());

		when(mongoTemplate.aggregate(any(Aggregation.class), eq("productivity"), eq(Map.class))).thenReturn(mapResults);
		when(mapResults.getMappedResults()).thenReturn(List.of(responseMap));
		when(objectMapper.convertValue(any(), any(TypeReference.class))).thenReturn(List.of());

		List<ProductivityTemporalGrouping> result = repository.getProductivitiesGroupedByTemporalUnit(hierarchyNodeIds,
				TemporalAggregationUnit.WEEK, 10);

		assertEquals(1, result.size());
		assertEquals(TemporalAggregationUnit.WEEK, result.get(0).getTemporalAggregationUnit());
	}

	@Test
	void testGetProductivitiesGroupedByTemporalUnit_EmptyResult() {
		when(mongoTemplate.aggregate(any(Aggregation.class), eq("productivity"), eq(Map.class)))
				.thenReturn(mapResults);
		when(mapResults.getMappedResults()).thenReturn(List.of());

		List<ProductivityTemporalGrouping> result =
				repository.getProductivitiesGroupedByTemporalUnit(
						hierarchyNodeIds, TemporalAggregationUnit.MONTH, 5);

		assertTrue(result.isEmpty());
	}

	@Test
	void testGetProductivitiesGroupedByTemporalUnit_InvalidMapStructure() {
		Map<String, Object> invalidMap = new HashMap<>();
		invalidMap.put("invalid", "data");

		when(mongoTemplate.aggregate(any(Aggregation.class), eq("productivity"), eq(Map.class))).thenReturn(mapResults);
		when(mapResults.getMappedResults()).thenReturn(List.of(invalidMap));

		List<ProductivityTemporalGrouping> result = repository.getProductivitiesGroupedByTemporalUnit(hierarchyNodeIds,
				TemporalAggregationUnit.WEEK, 10);

		assertTrue(result.isEmpty());
	}
}
