package com.publicissapient.kpidashboard.common.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.publicissapient.kpidashboard.common.model.application.HierarchyLevelSuggestion;
import com.publicissapient.kpidashboard.common.repository.application.HierarchyLevelSuggestionRepository;

@ExtendWith(MockitoExtension.class)
class HierarchyLevelSuggestionsServiceImplTest {

	@Mock
	private HierarchyLevelSuggestionRepository hierarchyLevelSuggestionRepository;

	@InjectMocks
	private HierarchyLevelSuggestionsServiceImpl hierarchyLevelSuggestionsService;

	private HierarchyLevelSuggestion suggestion;
	private TreeSet<String> values;

	@BeforeEach
	void setUp() {
		suggestion = new HierarchyLevelSuggestion();
		suggestion.setHierarchyLevelId("level1");
		values = new TreeSet<>();
		values.add("value1");
		suggestion.setValues(values);
	}

	@Test
	void testGetSuggestions() {
		List<HierarchyLevelSuggestion> suggestions = Arrays.asList(suggestion);
		when(hierarchyLevelSuggestionRepository.findAll()).thenReturn(suggestions);

		List<HierarchyLevelSuggestion> result = hierarchyLevelSuggestionsService.getSuggestions();

		assertEquals(suggestions, result);
		verify(hierarchyLevelSuggestionRepository).findAll();
	}

	@Test
	void testAddIfNotPresentNewHierarchyLevel() {
		when(hierarchyLevelSuggestionRepository.findByHierarchyLevelId("level1")).thenReturn(null);
		when(hierarchyLevelSuggestionRepository.save(any(HierarchyLevelSuggestion.class)))
				.thenReturn(suggestion);

		HierarchyLevelSuggestion result =
				hierarchyLevelSuggestionsService.addIfNotPresent("level1", "newValue");

		assertNotNull(result);
		verify(hierarchyLevelSuggestionRepository).findByHierarchyLevelId("level1");
		verify(hierarchyLevelSuggestionRepository).save(any(HierarchyLevelSuggestion.class));
	}

	@Test
	void testAddIfNotPresentExistingHierarchyLevelNewValue() {
		when(hierarchyLevelSuggestionRepository.findByHierarchyLevelId("level1"))
				.thenReturn(suggestion);
		when(hierarchyLevelSuggestionRepository.save(suggestion)).thenReturn(suggestion);

		HierarchyLevelSuggestion result =
				hierarchyLevelSuggestionsService.addIfNotPresent("level1", "newValue");

		assertNotNull(result);
		assertTrue(result.getValues().contains("newValue"));
		assertTrue(result.getValues().contains("value1"));
		verify(hierarchyLevelSuggestionRepository).findByHierarchyLevelId("level1");
		verify(hierarchyLevelSuggestionRepository).save(suggestion);
	}

	@Test
	void testAddIfNotPresentExistingHierarchyLevelDuplicateValue() {
		when(hierarchyLevelSuggestionRepository.findByHierarchyLevelId("level1"))
				.thenReturn(suggestion);
		when(hierarchyLevelSuggestionRepository.save(suggestion)).thenReturn(suggestion);

		HierarchyLevelSuggestion result =
				hierarchyLevelSuggestionsService.addIfNotPresent("level1", "value1");

		assertNotNull(result);
		assertEquals(1, result.getValues().size());
		assertTrue(result.getValues().contains("value1"));
		verify(hierarchyLevelSuggestionRepository).findByHierarchyLevelId("level1");
		verify(hierarchyLevelSuggestionRepository).save(suggestion);
	}

	@Test
	void testAddIfNotPresentExistingHierarchyLevelCaseInsensitive() {
		when(hierarchyLevelSuggestionRepository.findByHierarchyLevelId("level1"))
				.thenReturn(suggestion);
		when(hierarchyLevelSuggestionRepository.save(suggestion)).thenReturn(suggestion);

		HierarchyLevelSuggestion result =
				hierarchyLevelSuggestionsService.addIfNotPresent("level1", "VALUE1");

		assertNotNull(result);
		assertEquals(1, result.getValues().size());
		assertTrue(result.getValues().contains("value1"));
		verify(hierarchyLevelSuggestionRepository).findByHierarchyLevelId("level1");
		verify(hierarchyLevelSuggestionRepository).save(suggestion);
	}

	@Test
	void testAddIfNotPresentWithWhitespace() {
		when(hierarchyLevelSuggestionRepository.findByHierarchyLevelId("level1")).thenReturn(null);
		when(hierarchyLevelSuggestionRepository.save(any(HierarchyLevelSuggestion.class)))
				.thenReturn(suggestion);

		HierarchyLevelSuggestion result =
				hierarchyLevelSuggestionsService.addIfNotPresent("level1", "  spaced value  ");

		assertNotNull(result);
		verify(hierarchyLevelSuggestionRepository).findByHierarchyLevelId("level1");
		verify(hierarchyLevelSuggestionRepository).save(any(HierarchyLevelSuggestion.class));
	}

	@Test
	void testAddIfNotPresentExistingHierarchyLevelEmptyValues() {
		suggestion.setValues(new TreeSet<>());
		when(hierarchyLevelSuggestionRepository.findByHierarchyLevelId("level1")).thenReturn(suggestion);

		HierarchyLevelSuggestion result = hierarchyLevelSuggestionsService.addIfNotPresent("level1", "newValue");

		assertNull(result);
		verify(hierarchyLevelSuggestionRepository).findByHierarchyLevelId("level1");
		verify(hierarchyLevelSuggestionRepository, never()).save(any());
	}

	@Test
	void testAddIfNotPresentExistingHierarchyLevelNullValues() {
		suggestion.setValues(null);
		when(hierarchyLevelSuggestionRepository.findByHierarchyLevelId("level1")).thenReturn(suggestion);

		HierarchyLevelSuggestion result = hierarchyLevelSuggestionsService.addIfNotPresent("level1", "newValue");

		assertNull(result);
		verify(hierarchyLevelSuggestionRepository).findByHierarchyLevelId("level1");
		verify(hierarchyLevelSuggestionRepository, never()).save(any());
	}
}
