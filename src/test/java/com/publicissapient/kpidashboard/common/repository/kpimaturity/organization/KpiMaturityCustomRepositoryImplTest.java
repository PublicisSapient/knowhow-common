package com.publicissapient.kpidashboard.common.repository.kpimaturity.organization;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import com.publicissapient.kpidashboard.common.model.kpimaturity.organization.KpiMaturity;

@ExtendWith(MockitoExtension.class)
class KpiMaturityCustomRepositoryImplTest {

	@Mock
	private MongoTemplate mongoTemplate;

	@Mock
	private AggregationResults<KpiMaturity> aggregationResults;

	@InjectMocks
	private KpiMaturityCustomRepositoryImpl kpiMaturityCustomRepository;

	private Set<String> hierarchyNodeIds;
	private List<KpiMaturity> kpiMaturityList;
	private KpiMaturity kpiMaturity;

	@BeforeEach
	void setUp() {
		hierarchyNodeIds = Set.of("node1", "node2");
		kpiMaturity = KpiMaturity.builder().build();
		kpiMaturity.setHierarchyEntityNodeId("node1");
		kpiMaturityList = Arrays.asList(kpiMaturity);
	}

	@Test
	void testGetLatestKpiMaturityByCalculationDateForProjects() {
		when(mongoTemplate.aggregate(any(Aggregation.class), eq("kpi_maturity"), eq(KpiMaturity.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(kpiMaturityList);

		List<KpiMaturity> result =
				kpiMaturityCustomRepository.getLatestKpiMaturityByCalculationDateForProjects(
						hierarchyNodeIds);

		assertEquals(kpiMaturityList, result);
		verify(mongoTemplate)
				.aggregate(any(Aggregation.class), eq("kpi_maturity"), eq(KpiMaturity.class));
		verify(aggregationResults).getMappedResults();
	}

	@Test
	void testGetLatestKpiMaturityByCalculationDateForProjectsEmptyResult() {
		when(mongoTemplate.aggregate(any(Aggregation.class), eq("kpi_maturity"), eq(KpiMaturity.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(Collections.emptyList());

		List<KpiMaturity> result =
				kpiMaturityCustomRepository.getLatestKpiMaturityByCalculationDateForProjects(
						hierarchyNodeIds);

		assertTrue(result.isEmpty());
		verify(mongoTemplate)
				.aggregate(any(Aggregation.class), eq("kpi_maturity"), eq(KpiMaturity.class));
		verify(aggregationResults).getMappedResults();
	}

	@Test
	void testGetLatestKpiMaturityByCalculationDateForProjectsEmptyInput() {
		Set<String> emptySet = Collections.emptySet();
		when(mongoTemplate.aggregate(any(Aggregation.class), eq("kpi_maturity"), eq(KpiMaturity.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(Collections.emptyList());

		List<KpiMaturity> result = kpiMaturityCustomRepository.getLatestKpiMaturityByCalculationDateForProjects(emptySet);

		assertTrue(result.isEmpty());
		verify(mongoTemplate).aggregate(any(Aggregation.class), eq("kpi_maturity"), eq(KpiMaturity.class));
	}
}
