package com.publicissapient.kpidashboard.common.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.publicissapient.kpidashboard.common.model.jira.AssigneeDetails;
import com.publicissapient.kpidashboard.common.repository.jira.AssigneeDetailsRepository;

@ExtendWith(MockitoExtension.class)
class AssigneeDetailsServiceImplTest {

	@Mock
	private AssigneeDetailsRepository assigneeDetailsRepository;

	@InjectMocks
	private AssigneeDetailsServiceImpl assigneeDetailsService;

	private AssigneeDetails assigneeDetails;
	private String basicProjectConfigId;

	@BeforeEach
	void setUp() {
		basicProjectConfigId = "project123";
		assigneeDetails = new AssigneeDetails();
		assigneeDetails.setBasicProjectConfigId(basicProjectConfigId);
	}

	@Test
	void testFindByBasicProjectConfigId() {
		when(assigneeDetailsRepository.findByBasicProjectConfigId(basicProjectConfigId))
				.thenReturn(assigneeDetails);

		AssigneeDetails result =
				assigneeDetailsService.findByBasicProjectConfigId(basicProjectConfigId);

		assertEquals(assigneeDetails, result);
		verify(assigneeDetailsRepository).findByBasicProjectConfigId(basicProjectConfigId);
	}

	@Test
	void testFindByBasicProjectConfigIdNotFound() {
		when(assigneeDetailsRepository.findByBasicProjectConfigId(basicProjectConfigId))
				.thenReturn(null);

		AssigneeDetails result =
				assigneeDetailsService.findByBasicProjectConfigId(basicProjectConfigId);

		assertNull(result);
		verify(assigneeDetailsRepository).findByBasicProjectConfigId(basicProjectConfigId);
	}
}
