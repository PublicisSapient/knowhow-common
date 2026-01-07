package com.publicissapient.kpidashboard.common.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.publicissapient.kpidashboard.common.model.jira.ConfigurationTemplateDocument;
import com.publicissapient.kpidashboard.common.repository.jira.ConfigurationTemplateRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("TemplateConfigurationServiceImpl Tests")
class TemplateConfigurationServiceImplTest {

	@Mock
	private ConfigurationTemplateRepository configurationTemplateRepository;

	@InjectMocks
	private TemplateConfigurationServiceImpl templateConfigurationService;

	private ConfigurationTemplateDocument template1;
	private ConfigurationTemplateDocument template2;
	private List<ConfigurationTemplateDocument> templateList;

	@BeforeEach
	void setUp() {
		template1 = ConfigurationTemplateDocument.builder().tool("Jira").templateName("Scrum Template")
				.templateCode("SCRUM_001").isKanban(false).disabled(false).build();

		template2 = ConfigurationTemplateDocument.builder().tool("Jira").templateName("Kanban Template")
				.templateCode("KANBAN_001").isKanban(true).disabled(false).build();

		templateList = Arrays.asList(template1, template2);
	}

	@Test
	@DisplayName("Should return list of configuration templates when repository has data")
	void testGetConfigurationTemplate_WithData() {
		// Given
		when(configurationTemplateRepository.findAll()).thenReturn(templateList);

		// When
		List<ConfigurationTemplateDocument> result =
				templateConfigurationService.getConfigurationTemplate();

		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(templateList, result);
		assertEquals("Scrum Template", result.get(0).getTemplateName());
		assertEquals("Kanban Template", result.get(1).getTemplateName());
		verify(configurationTemplateRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("Should return empty list when repository has no data")
	void testGetConfigurationTemplate_EmptyList() {
		// Given
		when(configurationTemplateRepository.findAll()).thenReturn(Collections.emptyList());

		// When
		List<ConfigurationTemplateDocument> result =
				templateConfigurationService.getConfigurationTemplate();

		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
		assertEquals(0, result.size());
		verify(configurationTemplateRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("Should return single template when repository has one item")
	void testGetConfigurationTemplate_SingleItem() {
		// Given
		List<ConfigurationTemplateDocument> singleTemplate = Collections.singletonList(template1);
		when(configurationTemplateRepository.findAll()).thenReturn(singleTemplate);

		// When
		List<ConfigurationTemplateDocument> result = templateConfigurationService.getConfigurationTemplate();

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(template1, result.get(0));
		assertEquals("Scrum Template", result.get(0).getTemplateName());
		assertEquals("SCRUM_001", result.get(0).getTemplateCode());
		assertFalse(result.get(0).isKanban());
		verify(configurationTemplateRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("Should handle multiple calls to repository correctly")
	void testGetConfigurationTemplate_MultipleCalls() {
		// Given
		when(configurationTemplateRepository.findAll()).thenReturn(templateList);

		// When
		List<ConfigurationTemplateDocument> result1 =
				templateConfigurationService.getConfigurationTemplate();
		List<ConfigurationTemplateDocument> result2 =
				templateConfigurationService.getConfigurationTemplate();

		// Then
		assertNotNull(result1);
		assertNotNull(result2);
		assertEquals(result1, result2);
		assertEquals(2, result1.size());
		assertEquals(2, result2.size());
		verify(configurationTemplateRepository, times(2)).findAll();
	}

	@Test
	@DisplayName("Should verify service implements interface correctly")
	void testServiceImplementsInterface() {
		// Then
		assertTrue(templateConfigurationService instanceof TemplateConfigurationService);
	}
}
