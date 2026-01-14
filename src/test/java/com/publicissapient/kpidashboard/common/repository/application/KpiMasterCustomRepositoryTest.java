package com.publicissapient.kpidashboard.common.repository.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.publicissapient.kpidashboard.common.repository.projection.BasicKpiMasterProjection;

@ExtendWith(MockitoExtension.class)
class KpiMasterCustomRepositoryTest {

	@Mock
	private KpiMasterRepository kpiMasterRepository;

	@InjectMocks
	private KpiMasterCustomRepository kpiMasterCustomRepository;

	private BasicKpiMasterProjection projection1;
	private BasicKpiMasterProjection projection2;

	@BeforeEach
	void setUp() {
		projection1 = mock(BasicKpiMasterProjection.class);
		projection2 = mock(BasicKpiMasterProjection.class);
	}

	@Test
	void testFindKpisSupportingMaturityCalculation_WithData() {
		// Given
		List<BasicKpiMasterProjection> expectedList = Arrays.asList(projection1, projection2);
		when(kpiMasterRepository.findByCalculateMaturity(true)).thenReturn(expectedList);

		// When
		List<BasicKpiMasterProjection> result = kpiMasterCustomRepository.findKpisSupportingMaturityCalculation();

		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(expectedList, result);
		verify(kpiMasterRepository).findByCalculateMaturity(true);
	}

	@Test
	void testFindKpisSupportingMaturityCalculation_EmptyList() {
		// Given
		when(kpiMasterRepository.findByCalculateMaturity(true)).thenReturn(Collections.emptyList());

		// When
		List<BasicKpiMasterProjection> result =
				kpiMasterCustomRepository.findKpisSupportingMaturityCalculation();

		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(kpiMasterRepository).findByCalculateMaturity(true);
	}

	@Test
	void testFindKpisSupportingMaturityCalculation_SingleItem() {
		// Given
		List<BasicKpiMasterProjection> singleItem = Collections.singletonList(projection1);
		when(kpiMasterRepository.findByCalculateMaturity(true)).thenReturn(singleItem);

		// When
		List<BasicKpiMasterProjection> result = kpiMasterCustomRepository.findKpisSupportingMaturityCalculation();

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(projection1, result.get(0));
		verify(kpiMasterRepository).findByCalculateMaturity(true);
	}
}
