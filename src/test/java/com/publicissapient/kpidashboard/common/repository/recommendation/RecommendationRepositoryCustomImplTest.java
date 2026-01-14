package com.publicissapient.kpidashboard.common.repository.recommendation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import com.publicissapient.kpidashboard.common.model.recommendation.batch.RecommendationsActionPlan;

@ExtendWith(MockitoExtension.class)
class RecommendationRepositoryCustomImplTest {

	@Mock
	private MongoOperations operations;

	@Mock
	private AggregationResults<RecommendationsActionPlan> aggregationResults;

	@InjectMocks
	private RecommendationRepositoryCustomImpl repository;

	private List<String> projectIds;
	private List<RecommendationsActionPlan> mockRecommendations;

	@BeforeEach
	void setUp() {
		projectIds = Arrays.asList("project1", "project2");
		mockRecommendations = Arrays.asList(new RecommendationsActionPlan(), new RecommendationsActionPlan());
	}

	@Test
	void testFindLatestRecommendationsByProjectIds_Success() {
		// Given
		when(operations.aggregate(
						any(Aggregation.class),
						eq("recommendations_action_plan"),
						eq(RecommendationsActionPlan.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(mockRecommendations);

		// When
		List<RecommendationsActionPlan> result =
				repository.findLatestRecommendationsByProjectIds(projectIds, 5);

		// Then
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(mockRecommendations, result);
		verify(operations)
				.aggregate(
						any(Aggregation.class),
						eq("recommendations_action_plan"),
						eq(RecommendationsActionPlan.class));
	}

	@Test
	void testFindLatestRecommendationsByProjectIds_EmptyProjectIds() {
		// Given
		List<String> emptyProjectIds = Collections.emptyList();

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> repository.findLatestRecommendationsByProjectIds(emptyProjectIds, 5));
		assertEquals("Project IDs list must not be null or empty", exception.getMessage());
		verifyNoInteractions(operations);
	}

	@Test
	void testFindLatestRecommendationsByProjectIds_NullProjectIds() {
		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> repository.findLatestRecommendationsByProjectIds(null, 5));
		assertEquals("Project IDs list must not be null or empty", exception.getMessage());
		verifyNoInteractions(operations);
	}

	@Test
	void testFindLatestRecommendationsByProjectIds_InvalidLimit() {
		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> repository.findLatestRecommendationsByProjectIds(projectIds, 0));
		assertEquals("Limit must be greater than 0, got: 0", exception.getMessage());
		verifyNoInteractions(operations);
	}

	@Test
	void testFindLatestRecommendationsByProjectIds_NegativeLimit() {
		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> repository.findLatestRecommendationsByProjectIds(projectIds, -1));
		assertEquals("Limit must be greater than 0, got: -1", exception.getMessage());
		verifyNoInteractions(operations);
	}

	@Test
	void testFindLatestRecommendationsByProjectIds_EmptyResults() {
		// Given
		when(operations.aggregate(
						any(Aggregation.class),
						eq("recommendations_action_plan"),
						eq(RecommendationsActionPlan.class)))
				.thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(Collections.emptyList());

		// When
		List<RecommendationsActionPlan> result =
				repository.findLatestRecommendationsByProjectIds(projectIds, 5);

		// Then
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(operations)
				.aggregate(
						any(Aggregation.class),
						eq("recommendations_action_plan"),
						eq(RecommendationsActionPlan.class));
	}

	@Test
	void testFindLatestRecommendationsByProjectIds_SingleProject() {
		// Given
		List<String> singleProject = Collections.singletonList("project1");
		List<RecommendationsActionPlan> singleRecommendation = Collections.singletonList(new RecommendationsActionPlan());
		when(operations.aggregate(any(Aggregation.class), eq("recommendations_action_plan"),
				eq(RecommendationsActionPlan.class))).thenReturn(aggregationResults);
		when(aggregationResults.getMappedResults()).thenReturn(singleRecommendation);

		// When
		List<RecommendationsActionPlan> result = repository.findLatestRecommendationsByProjectIds(singleProject, 1);

		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
		verify(operations).aggregate(any(Aggregation.class), eq("recommendations_action_plan"),
				eq(RecommendationsActionPlan.class));
	}
}
