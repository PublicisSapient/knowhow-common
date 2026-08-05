package com.publicissapient.kpidashboard.common.service.recommendation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;
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
	private PromptDetails projectLevelPrompt;
	private PromptDetails kpiLevelPrompt;
	private Persona persona;
	private Map<Pair<String, String>, Object> kpiData;

	@BeforeEach
	void setUp() {
		promptDetails = new PromptDetails();
		promptDetails.setKey("test-key");
		promptDetails.setContext("Test context");
		promptDetails.setTask("Test task");

		correlationPrompt = new PromptDetails();
		correlationPrompt.setContext("KPI correlation analysis");
		correlationPrompt.setTask("Analyze KPI correlations");

		projectLevelPrompt = new PromptDetails();
		projectLevelPrompt.setContext("Batch recommendation context");
		projectLevelPrompt.setTask(
				"Generate recommendations with KPI_CORRELATION_REPORT_PLACEHOLDER and KPI_DATA_BY_PROJECT_PLACEHOLDER for Persona_PLACEHOLDER");

		kpiLevelPrompt = new PromptDetails();
		kpiLevelPrompt.setContext("KPI level recommendation context");
		kpiLevelPrompt.setTask(
				"Generate KPI recommendations with KPI_CORRELATION_REPORT_PLACEHOLDER and KPI_DATA_PLACEHOLDER for Persona_PLACEHOLDER");

		kpiData = Map.of(Pair.of("project1", "metric1"), "data1", Pair.of("project2", "metric2"), "data2");
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
	void testGetBatchProjectLevelPrompt_Success() {
		// Given
		persona = mock(Persona.class);
		when(persona.getDisplayName()).thenReturn("Developer");
		when(promptDetailsRepository.findByKey(PromptKeys.KPI_CORRELATION_ANALYSIS_REPORT)).thenReturn(correlationPrompt);
		when(promptDetailsRepository.findByKey(PromptKeys.BATCH_PROJECT_LEVEL_RECOMMENDATION_PROMPT))
				.thenReturn(projectLevelPrompt);

		// When
		String result = promptService.getBatchProjectLevelPrompt(kpiData, persona);

		// Then
		assertNotNull(result);
		assertTrue(result.contains("KPI correlation analysis"));
		assertTrue(result.contains("Developer"));
		assertFalse(result.contains("KPI_CORRELATION_REPORT_PLACEHOLDER"));
		assertFalse(result.contains("KPI_DATA_BY_PROJECT_PLACEHOLDER"));
		assertFalse(result.contains("Persona_PLACEHOLDER"));
	}

	@Test
	void testGetBatchProjectLevelPrompt_EmptyKpiData() {
		// Given
		persona = mock(Persona.class);
		when(persona.getDisplayName()).thenReturn("Developer");
		Map<Pair<String, String>, Object> emptyData = Collections.emptyMap();
		when(promptDetailsRepository.findByKey(PromptKeys.KPI_CORRELATION_ANALYSIS_REPORT)).thenReturn(correlationPrompt);
		when(promptDetailsRepository.findByKey(PromptKeys.BATCH_PROJECT_LEVEL_RECOMMENDATION_PROMPT))
				.thenReturn(projectLevelPrompt);

		// When
		String result = promptService.getBatchProjectLevelPrompt(emptyData, persona);

		// Then
		assertNotNull(result);
		assertTrue(result.contains("{}"));
		assertTrue(result.contains("Developer"));
	}

	@Test
	void testGetBatchKpiLevelPrompt_Success() {
		// Given
		persona = mock(Persona.class);
		when(persona.getDisplayName()).thenReturn("Developer");
		String kpiId = "kpi1";
		when(promptDetailsRepository.findByKey(PromptKeys.KPI_CORRELATION_ANALYSIS_REPORT)).thenReturn(correlationPrompt);
		when(promptDetailsRepository.findByKey(PromptKeys.BATCH_KPI_LEVEL_RECOMMENDATION_PROMPT))
				.thenReturn(kpiLevelPrompt);

		// When
		String result = promptService.getBatchKpiLevelPrompt(kpiData, persona, kpiId);

		// Then
		assertNotNull(result);
		assertTrue(result.contains("KPI correlation analysis"));
		assertTrue(result.contains("kpi1"));
		assertTrue(result.contains("Developer"));
		assertFalse(result.contains("KPI_CORRELATION_REPORT_PLACEHOLDER"));
		assertFalse(result.contains("KPI_DATA_PLACEHOLDER"));
		assertFalse(result.contains("Persona_PLACEHOLDER"));
	}

	@Test
	void testGetEpicHygienePrompt_ResolvesBothPlaceholders() {
		// Given
		PromptDetails epicHygienePrompt = new PromptDetails();
		epicHygienePrompt.setContext("Epic readiness analyzer");
		epicHygienePrompt.setTask("Dimensions: HYGIENE_RULES_PLACEHOLDER and Epics: JIRA_ISSUES_PLACEHOLDER");
		when(promptDetailsRepository.findByKey(PromptKeys.EPIC_HYGIENE_PROMPT)).thenReturn(epicHygienePrompt);

		// When
		String result = promptService.getEpicHygienePrompt("Rule 1 Business Clarity", "[{\"number\":\"EPIC-1\"}]");

		// Then
		assertNotNull(result);
		assertTrue(result.contains("Epic readiness analyzer"));
		assertTrue(result.contains("Rule 1 Business Clarity"));
		assertTrue(result.contains("EPIC-1"));
		assertFalse(result.contains("HYGIENE_RULES_PLACEHOLDER"));
		assertFalse(result.contains("JIRA_ISSUES_PLACEHOLDER"));
	}

	@Test
	void testGetEpicHygienePrompt_NullArgumentsAreRenderedSafely() {
		// Given
		PromptDetails epicHygienePrompt = new PromptDetails();
		epicHygienePrompt.setTask("HYGIENE_RULES_PLACEHOLDER | JIRA_ISSUES_PLACEHOLDER");
		when(promptDetailsRepository.findByKey(PromptKeys.EPIC_HYGIENE_PROMPT)).thenReturn(epicHygienePrompt);

		// When
		String result = promptService.getEpicHygienePrompt(null, null);

		// Then
		assertNotNull(result);
		assertFalse(result.contains("HYGIENE_RULES_PLACEHOLDER"));
		assertFalse(result.contains("JIRA_ISSUES_PLACEHOLDER"));
		assertTrue(result.contains("null"));
	}

	@Test
	void testGetEpicHygienePrompt_MissingTemplate_Throws() {
		// Given
		when(promptDetailsRepository.findByKey(PromptKeys.EPIC_HYGIENE_PROMPT)).thenReturn(null);

		// When & Then
		RuntimeException exception =
				assertThrows(
						RuntimeException.class, () -> promptService.getEpicHygienePrompt("rules", "epics"));
		assertEquals("Failed to generate epic hygiene prompt", exception.getMessage());
	}
}
