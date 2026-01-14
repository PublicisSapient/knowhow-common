package com.publicissapient.kpidashboard.common.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.publicissapient.kpidashboard.common.constant.CommonConstant;
import com.publicissapient.kpidashboard.common.model.application.ProjectBasicConfig;
import com.publicissapient.kpidashboard.common.model.application.ProjectHierarchy;
import com.publicissapient.kpidashboard.common.repository.application.ProjectHierarchyRepository;

@ExtendWith(MockitoExtension.class)
class ProjectHierarchyServiceImplTest {

	@Mock
	private ProjectHierarchyRepository projectHierarchyRepository;

	@InjectMocks
	private ProjectHierarchyServiceImpl projectHierarchyService;

	private ObjectId projectConfigId;
	private ProjectHierarchy projectHierarchy;
	private List<ProjectHierarchy> projectHierarchyList;

	@BeforeEach
	void setUp() {
		projectConfigId = new ObjectId();
		projectHierarchy = new ProjectHierarchy();
		projectHierarchy.setNodeId("node1");
		projectHierarchy.setBasicProjectConfigId(projectConfigId);
		projectHierarchy.setHierarchyLevelId(CommonConstant.HIERARCHY_LEVEL_ID_RELEASE);
		projectHierarchyList = Arrays.asList(projectHierarchy);
	}

	@Test
	void testFindAll() {
		when(projectHierarchyRepository.findAll()).thenReturn(projectHierarchyList);

		List<ProjectHierarchy> result = projectHierarchyService.findAll();

		assertEquals(projectHierarchyList, result);
		verify(projectHierarchyRepository).findAll();
	}

	@Test
	void testDeleteByBasicProjectConfigId() {
		projectHierarchyService.deleteByBasicProjectConfigId(projectConfigId);

		verify(projectHierarchyRepository).deleteByBasicProjectConfigId(projectConfigId);
	}

	@Test
	void testGetProjectHierarchyMapByConfigId() {
		when(projectHierarchyRepository.findByBasicProjectConfigId(projectConfigId))
				.thenReturn(projectHierarchyList);

		Map<String, ProjectHierarchy> result =
				projectHierarchyService.getProjectHierarchyMapByConfigId(projectConfigId.toString());

		assertEquals(1, result.size());
		assertEquals(projectHierarchy, result.get("node1"));
		verify(projectHierarchyRepository).findByBasicProjectConfigId(projectConfigId);
	}

	@Test
	void testGetProjectHierarchyMapByConfigIdWithDuplicateKeys() {
		ProjectHierarchy duplicate = new ProjectHierarchy();
		duplicate.setNodeId("node1");
		duplicate.setBasicProjectConfigId(projectConfigId);
		List<ProjectHierarchy> duplicateList = Arrays.asList(projectHierarchy, duplicate);

		when(projectHierarchyRepository.findByBasicProjectConfigId(projectConfigId)).thenReturn(duplicateList);

		Map<String, ProjectHierarchy> result = projectHierarchyService
				.getProjectHierarchyMapByConfigId(projectConfigId.toString());

		assertEquals(1, result.size());
		assertEquals(projectHierarchy, result.get("node1"));
	}

	@Test
	void testGetProjectHierarchyMapByConfig() {
		when(projectHierarchyRepository.findByBasicProjectConfigId(projectConfigId))
				.thenReturn(projectHierarchyList);

		Map<String, List<ProjectHierarchy>> result =
				projectHierarchyService.getProjectHierarchyMapByConfig(projectConfigId.toString());

		assertEquals(1, result.size());
		assertEquals(projectHierarchyList, result.get("node1"));
		verify(projectHierarchyRepository).findByBasicProjectConfigId(projectConfigId);
	}

	@Test
	void testGetProjectHierarchyMapByConfigIdAndHierarchyLevelId() {
		when(projectHierarchyRepository.findByBasicProjectConfigId(projectConfigId))
				.thenReturn(projectHierarchyList);

		Map<String, ProjectHierarchy> result =
				projectHierarchyService.getProjectHierarchyMapByConfigIdAndHierarchyLevelId(
						projectConfigId.toString(), CommonConstant.HIERARCHY_LEVEL_ID_RELEASE);

		assertEquals(1, result.size());
		assertEquals(projectHierarchy, result.get("node1"));
		verify(projectHierarchyRepository).findByBasicProjectConfigId(projectConfigId);
	}

	@Test
	void testGetProjectHierarchyMapByConfigIdAndHierarchyLevelIdNoMatch() {
		when(projectHierarchyRepository.findByBasicProjectConfigId(projectConfigId))
				.thenReturn(projectHierarchyList);

		Map<String, ProjectHierarchy> result =
				projectHierarchyService.getProjectHierarchyMapByConfigIdAndHierarchyLevelId(
						projectConfigId.toString(), "differentLevel");

		assertTrue(result.isEmpty());
	}

	@Test
	void testSaveAll() {
		Set<ProjectHierarchy> hierarchies = Set.of(projectHierarchy);

		projectHierarchyService.saveAll(hierarchies);

		verify(projectHierarchyRepository).saveAll(hierarchies);
	}

	@Test
	void testFindAllByBasicProjectConfigIds() {
		List<ObjectId> configIds = Arrays.asList(projectConfigId);
		when(projectHierarchyRepository.findByBasicProjectConfigIdIn(configIds)).thenReturn(projectHierarchyList);

		List<ProjectHierarchy> result = projectHierarchyService.findAllByBasicProjectConfigIds(configIds);

		assertEquals(projectHierarchyList, result);
		verify(projectHierarchyRepository).findByBasicProjectConfigIdIn(configIds);
	}

	@Test
	void testAppendProjectNameForReleaseLevel() {
		ProjectBasicConfig config = new ProjectBasicConfig();
		config.setId(projectConfigId);
		config.setProjectName("TestProject");
		config.setProjectDisplayName("Test Project Display");

		projectHierarchy.setNodeName("Release1");
		projectHierarchy.setNodeDisplayName("Release 1");
		projectHierarchy.setHierarchyLevelId(CommonConstant.HIERARCHY_LEVEL_ID_RELEASE);

		projectHierarchyService.appendProjectName(Arrays.asList(config), Arrays.asList(projectHierarchy));

		assertEquals("Release1" + CommonConstant.ADDITIONAL_FILTER_VALUE_ID_SEPARATOR + "TestProject",
				projectHierarchy.getNodeName());
		assertEquals("Release 1" + CommonConstant.ADDITIONAL_FILTER_VALUE_ID_SEPARATOR + "Test Project Display",
				projectHierarchy.getNodeDisplayName());
	}

	@Test
	void testAppendProjectNameForSprintLevel() {
		ProjectBasicConfig config = new ProjectBasicConfig();
		config.setId(projectConfigId);
		config.setProjectName("TestProject");
		config.setProjectDisplayName("Test Project Display");

		projectHierarchy.setNodeName("Sprint1");
		projectHierarchy.setNodeDisplayName("Sprint 1");
		projectHierarchy.setHierarchyLevelId(CommonConstant.HIERARCHY_LEVEL_ID_SPRINT);

		projectHierarchyService.appendProjectName(Arrays.asList(config), Arrays.asList(projectHierarchy));

		assertEquals("Sprint1" + CommonConstant.ADDITIONAL_FILTER_VALUE_ID_SEPARATOR + "TestProject",
				projectHierarchy.getNodeName());
		assertEquals("Sprint 1" + CommonConstant.ADDITIONAL_FILTER_VALUE_ID_SEPARATOR + "Test Project Display",
				projectHierarchy.getNodeDisplayName());
	}

	@Test
	void testAppendProjectNameForNonTargetLevel() {
		ProjectBasicConfig config = new ProjectBasicConfig();
		config.setId(projectConfigId);
		config.setProjectName("TestProject");
		config.setProjectDisplayName("Test Project Display");

		projectHierarchy.setNodeName("Project1");
		projectHierarchy.setNodeDisplayName("Project 1");
		projectHierarchy.setHierarchyLevelId("project");

		String originalNodeName = projectHierarchy.getNodeName();
		String originalDisplayName = projectHierarchy.getNodeDisplayName();

		projectHierarchyService.appendProjectName(Arrays.asList(config), Arrays.asList(projectHierarchy));

		assertEquals(originalNodeName, projectHierarchy.getNodeName());
		assertEquals(originalDisplayName, projectHierarchy.getNodeDisplayName());
	}

	@Test
	void testAppendProjectNameWithNullConfig() {
		projectHierarchy.setNodeName("Release1");
		projectHierarchy.setNodeDisplayName("Release 1");
		projectHierarchy.setHierarchyLevelId(CommonConstant.HIERARCHY_LEVEL_ID_RELEASE);
		projectHierarchy.setBasicProjectConfigId(new ObjectId());

		String originalNodeName = projectHierarchy.getNodeName();
		String originalDisplayName = projectHierarchy.getNodeDisplayName();

		projectHierarchyService.appendProjectName(Arrays.asList(), Arrays.asList(projectHierarchy));

		assertEquals(originalNodeName, projectHierarchy.getNodeName());
		assertEquals(originalDisplayName, projectHierarchy.getNodeDisplayName());
	}

	@Test
	void testUpdateNodeNameWithExistingProjectName() {
		ProjectBasicConfig config = new ProjectBasicConfig();
		config.setId(projectConfigId);
		config.setProjectName("TestProject");
		config.setProjectDisplayName("Test Project Display");

		projectHierarchy.setNodeName("Release1" + CommonConstant.ADDITIONAL_FILTER_VALUE_ID_SEPARATOR + "TestProject");
		projectHierarchy.setNodeDisplayName("Release 1");
		projectHierarchy.setHierarchyLevelId(CommonConstant.HIERARCHY_LEVEL_ID_RELEASE);

		projectHierarchyService.appendProjectName(Arrays.asList(config), Arrays.asList(projectHierarchy));

		assertEquals("Release1" + CommonConstant.ADDITIONAL_FILTER_VALUE_ID_SEPARATOR + "TestProject",
				projectHierarchy.getNodeName());
	}

	@Test
	void testUpdateNodeDisplayNameWithExistingProjectName() {
		ProjectBasicConfig config = new ProjectBasicConfig();
		config.setId(projectConfigId);
		config.setProjectName("TestProject");
		config.setProjectDisplayName("Test Project Display");

		projectHierarchy.setNodeName("Release1");
		projectHierarchy
				.setNodeDisplayName("Release 1" + CommonConstant.ADDITIONAL_FILTER_VALUE_ID_SEPARATOR + "TestProject");
		projectHierarchy.setHierarchyLevelId(CommonConstant.HIERARCHY_LEVEL_ID_RELEASE);

		projectHierarchyService.appendProjectName(Arrays.asList(config), Arrays.asList(projectHierarchy));

		assertEquals("Release 1" + CommonConstant.ADDITIONAL_FILTER_VALUE_ID_SEPARATOR + "Test Project Display",
				projectHierarchy.getNodeDisplayName());
	}

	@Test
	void testUpdateNodeDisplayNameWithExistingDisplayName() {
		ProjectBasicConfig config = new ProjectBasicConfig();
		config.setId(projectConfigId);
		config.setProjectName("TestProject");
		config.setProjectDisplayName("Test Project Display");

		projectHierarchy.setNodeName("Release1");
		projectHierarchy
				.setNodeDisplayName("Release 1" + CommonConstant.ADDITIONAL_FILTER_VALUE_ID_SEPARATOR + "Test Project Display");
		projectHierarchy.setHierarchyLevelId(CommonConstant.HIERARCHY_LEVEL_ID_RELEASE);

		projectHierarchyService.appendProjectName(Arrays.asList(config), Arrays.asList(projectHierarchy));

		assertEquals("Release 1" + CommonConstant.ADDITIONAL_FILTER_VALUE_ID_SEPARATOR + "Test Project Display",
				projectHierarchy.getNodeDisplayName());
	}
}
