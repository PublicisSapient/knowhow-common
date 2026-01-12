package com.publicissapient.kpidashboard.common.service.recommendation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.publicissapient.kpidashboard.common.constant.PromptKeys;
import com.publicissapient.kpidashboard.common.model.application.PromptDetails;
import com.publicissapient.kpidashboard.common.model.recommendation.batch.Persona;
import com.publicissapient.kpidashboard.common.repository.application.PromptDetailsRepository;

@ExtendWith(MockitoExtension.class)
class PromptServiceTest {

	@Mock
	private PromptDetailsRepository promptDetailsRepository;

	@InjectMocks
	private PromptService promptService;

	private PromptDetails promptDetails;
	private PromptDetails correlationPrompt;
	private PromptDetails batchPrompt;
	private Persona persona;
	private Map<String, Object> kpiData;

	@BeforeEach
	void setUp() {
		promptDetails = new PromptDetails();
		promptDetails.setKey("test-key");
		promptDetails.setContext("Test context");
		promptDetails.setTask("Test task");

		correlationPrompt = new PromptDetails();
		correlationPrompt.setContext("KPI correlation analysis");
		correlationPrompt.setTask("Analyze KPI correlations");

		batchPrompt = new PromptDetails();
		batchPrompt.setContext("Batch recommendation context");
		batchPrompt.setTask(
				"Generate recommendations with KPI_CORRELATION_REPORT_PLACEHOLDER and KPI_DATA_BY_PROJECT_PLACEHOLDER for Persona_PLACEHOLDER");

		kpiData = Map.of("project1", "data1", "project2", "data2");
	}

	@Test
	void testGetPromptDetails_Success() {
		// Given
		when(promptDetailsRepository.findByKey("test-key")).thenReturn(promptDetails);

		// When
		PromptDetails result = promptService.getPromptDetails("test-key");

		// Then
		assertNotNull(result);
		assertEquals(promptDetails, result);
		verify(promptDetailsRepository).findByKey("test-key");
	}

	@Test
	void testGetPromptDetails_NotFound() {
		// Given
		when(promptDetailsRepository.findByKey("invalid-key")).thenReturn(null);

		// When & Then
		IllegalArgumentException exception =
				assertThrows(
						IllegalArgumentException.class, () -> promptService.getPromptDetails("invalid-key"));
		assertEquals("Prompt not found for key: invalid-key", exception.getMessage());
		verify(promptDetailsRepository).findByKey("invalid-key");
	}

	@Test
	void testGetKpiRecommendationPrompt_Success() {
		// Given
		persona = mock(Persona.class);
		when(persona.getDisplayName()).thenReturn("Developer");
		when(promptDetailsRepository.findByKey(PromptKeys.KPI_CORRELATION_ANALYSIS_REPORT)).thenReturn(correlationPrompt);
		when(promptDetailsRepository.findByKey(PromptKeys.BATCH_RECOMMENDATION_PROMPT)).thenReturn(batchPrompt);

		// When
		String result = promptService.getKpiRecommendationPrompt(kpiData, persona);

		// Then
		assertNotNull(result);
		assertTrue(result.contains("KPI correlation analysis"));
		assertTrue(result.contains(kpiData.toString()));
		assertTrue(result.contains("Developer"));
		assertFalse(result.contains("KPI_CORRELATION_REPORT_PLACEHOLDER"));
		assertFalse(result.contains("KPI_DATA_BY_PROJECT_PLACEHOLDER"));
		assertFalse(result.contains("Persona_PLACEHOLDER"));
	}

	@Test
	void testGetKpiRecommendationPrompt_EmptyKpiData() {
		// Given
		persona = mock(Persona.class);
		when(persona.getDisplayName()).thenReturn("Developer");
		Map<String, Object> emptyData = Collections.emptyMap();
		when(promptDetailsRepository.findByKey(PromptKeys.KPI_CORRELATION_ANALYSIS_REPORT)).thenReturn(correlationPrompt);
		when(promptDetailsRepository.findByKey(PromptKeys.BATCH_RECOMMENDATION_PROMPT)).thenReturn(batchPrompt);

		// When
		String result = promptService.getKpiRecommendationPrompt(emptyData, persona);

		// Then
		assertNotNull(result);
		assertTrue(result.contains("{}"));
		assertTrue(result.contains("Developer"));
	}
}
