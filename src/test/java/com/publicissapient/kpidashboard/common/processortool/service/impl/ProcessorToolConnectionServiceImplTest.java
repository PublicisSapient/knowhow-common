/*******************************************************************************
 * Copyright 2014 CapitalOne, LLC.
 * Further development Copyright 2022 Sapient Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package com.publicissapient.kpidashboard.common.processortool.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.publicissapient.kpidashboard.common.config.NotificationConfig;
import com.publicissapient.kpidashboard.common.model.rbac.UserInfo;
import com.publicissapient.kpidashboard.common.repository.rbac.UserInfoRepository;
import com.publicissapient.kpidashboard.common.service.NotificationService;
import org.bson.types.ObjectId;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.publicissapient.kpidashboard.common.model.application.ProjectToolConfig;
import com.publicissapient.kpidashboard.common.model.connection.Connection;
import com.publicissapient.kpidashboard.common.model.processortool.ProcessorToolConnection;
import com.publicissapient.kpidashboard.common.repository.application.ProjectToolConfigRepository;
import com.publicissapient.kpidashboard.common.repository.connection.ConnectionRepository;

/**
 * @author narsingh9
 */
@ExtendWith(SpringExtension.class)
public class ProcessorToolConnectionServiceImplTest {

	public MockMvc mockMvc;
	@InjectMocks
	ProcessorToolConnectionServiceImpl processorToolConnectionServiceImpl;
	@Mock
	private ProjectToolConfigRepository projectToolConfigRepository;

	@Mock
	private UserInfoRepository userInfoRepository;

	@Mock
	private NotificationConfig notificationConfig;

	@Mock
	private NotificationService notificationService;

	@Mock
	private ConnectionRepository connectionRepository;

	private List<Connection> connectionList = Lists.newArrayList();

	private List<ProjectToolConfig> projectToolList = Lists.newArrayList();
	private Connection connection;
	private final ObjectId connectionId = new ObjectId();

	/** method includes pre processes for test cases */
	@BeforeEach
	public void before() {
		mockMvc = MockMvcBuilders.standaloneSetup(processorToolConnectionServiceImpl).build();

		Connection c1 = new Connection();
		c1.setId(new ObjectId("5f9014743cb73ce896167658"));
		c1.setConnectionName("dummy");
		c1.setType("jsa");
		c1.setBaseUrl("");
		c1.setUsername("does");
		c1.setPassword("dummyPassword");
		Connection c2 = new Connection();
		c2.setId(new ObjectId("5f9014743cb73ce896167659"));
		c2.setConnectionName("dummy2");
		c2.setType("aj");
		c2.setBaseUrl("");
		c2.setUsername("does");
		c2.setPassword("dummyPassword1");
		connectionList.add(c1);
		connectionList.add(c2);

		ProjectToolConfig t1 = new ProjectToolConfig();
		t1.setId(new ObjectId());
		t1.setToolName("Jira");
		t1.setBasicProjectConfigId(new ObjectId("5f9014743cb73ce896167659"));
		t1.setConnectionId(new ObjectId("5f9014743cb73ce896167658"));
		t1.setJobName("dsa");
		ProjectToolConfig t2 = new ProjectToolConfig();
		t2.setToolName("Jira");
		t2.setBasicProjectConfigId(new ObjectId("5f9014743cb73ce896167658"));
		t2.setConnectionId(new ObjectId("5f9014743cb73ce896167659"));
		t2.setJobName("dsab");
		projectToolList.add(t1);
		projectToolList.add(t2);

		connection = new Connection();
		connection.setId(connectionId);
		connection.setCreatedBy("user123");
		connection.setNotificationCount(0);
		connection.setBrokenConnection(false);
	}

	/** method includes post processes for test cases */
	@AfterEach
	public void after() {
		mockMvc = null;
	}

	/** method test successful return of processorToolConnection list */
	@Test
	public void findByToolTest_success() {
		when(projectToolConfigRepository.findByToolName(any())).thenReturn(projectToolList);
		when(connectionRepository.findByIdIn(connectionIdSet())).thenReturn(connectionList);
		List<ProcessorToolConnection> projectToolConnectionList =
				processorToolConnectionServiceImpl.findByTool(any());
		MatcherAssert.assertThat(projectToolConnectionList.size(), equalTo(2));
	}

	/** method test for null project tool case */
	@Test
	public void findByToolTest_nullProjectTool_success() {
		when(projectToolConfigRepository.findByToolName(any())).thenReturn(null);
		List<ProcessorToolConnection> projectToolConnectionList =
				processorToolConnectionServiceImpl.findByTool("Jira");
		MatcherAssert.assertThat(projectToolConnectionList.size(), equalTo(0));
	}

	/** method test for null connections */
	@Test
	public void findByToolTest_nullConnection_success() {
		when(projectToolConfigRepository.findByToolName(any())).thenReturn(projectToolList);
		when(connectionRepository.findByIdIn(connectionIdSet())).thenReturn(null);
		List<ProcessorToolConnection> projectToolConnectionList =
				processorToolConnectionServiceImpl.findByTool(any());
		MatcherAssert.assertThat(projectToolConnectionList.size(), equalTo(0));
	}

	private Set<ObjectId> connectionIdSet() {
		return Sets.newHashSet(new ObjectId("5f9014743cb73ce896167658"), new ObjectId("5f9014743cb73ce896167659"));
	}

	@Test
	void shouldResetConnectionWhenErrorMsgIsEmpty() {
		when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));

		processorToolConnectionServiceImpl.updateBreakingConnection(connectionId, "");

		Assertions.assertFalse(connection.isBrokenConnection());
		Assertions.assertNull(connection.getConnectionErrorMsg());
		Assertions.assertEquals(0, connection.getNotificationCount());
		verify(connectionRepository).save(connection);
	}

	@Test
	void shouldTriggerNotificationWhenErrorMsgExists() {
		connection.setNotificationCount(0);
		connection.setType("Jenkins");
		when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));

		when(notificationConfig.getMaximumEmailNotificationCount()).thenReturn("5");
		when(notificationConfig.getEmailNotificationFrequency()).thenReturn("1");
		when(notificationConfig.getEmailNotificationSubject()).thenReturn("Action Required: Restore Your {{toolName}} Connection");
		when(notificationConfig.getMailTemplate()).thenReturn(Map.of("Broken_Connection", "template-key"));
		when(notificationConfig.isNotificationSwitch()).thenReturn(true);

		UserInfo userInfo = new UserInfo();
		userInfo.setEmailAddress("user@example.com");
		userInfo.setDisplayName("User");
		Map<String, Boolean> alertNotifications = new HashMap<>();
		alertNotifications.put("errorAlertNotification",true);
		userInfo.setNotificationEmail(alertNotifications);



		when(userInfoRepository.findByUsername("user123")).thenReturn(userInfo);

		processorToolConnectionServiceImpl.updateBreakingConnection(connectionId, "Error!");

		Assertions.assertTrue(connection.isBrokenConnection());
		Assertions.assertEquals("Error!", connection.getConnectionErrorMsg());
		Assertions.assertNotNull(connection.getNotifiedOn());
		Assertions.assertEquals(1, connection.getNotificationCount());
		verify(notificationService).sendNotificationEvent(
				eq(List.of("user@example.com")),
				anyMap(),
				eq("Action Required: Restore Your {{toolName}} Connection"),
				eq(true),
				eq("template-key")
		);
	}

	@Test
	void shouldNotNotifyWhenNotificationCountExceedsMax() {
		connection.setNotificationCount(5);
		connection.setType("Jenkins");
		connection.setNotifiedOn(LocalDateTime.now().minusDays(1).toString());
		when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));
		when(notificationConfig.getMaximumEmailNotificationCount()).thenReturn("5");
		when(notificationConfig.getEmailNotificationFrequency()).thenReturn("1");

		processorToolConnectionServiceImpl.updateBreakingConnection(connectionId, "Some error");

		verify(notificationService, never()).sendNotificationEvent(any(), any(), any(), anyBoolean(), any());
	}

	@Test
	void shouldNotifyWhenNotifiedOnIsInvalid() {
		connection.setNotificationCount(0);
		connection.setType("Jenkins");
		connection.setNotifiedOn("invalid-timestamp");
		when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));
		when(notificationConfig.getMaximumEmailNotificationCount()).thenReturn("3");
		when(notificationConfig.getEmailNotificationFrequency()).thenReturn("1");

		processorToolConnectionServiceImpl.updateBreakingConnection(connectionId, "Some error");
		verify(notificationService, never()).sendNotificationEvent(any(), any(), any(), anyBoolean(), any());
	}

	@Test
	void shouldNotNotifyWhenEmailOrSubjectIsBlank() {
		connection.setNotificationCount(0);
		connection.setNotifiedOn(null);
		connection.setType("Jenkins");
		when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));
		when(notificationConfig.getMaximumEmailNotificationCount()).thenReturn("5");
		when(notificationConfig.getEmailNotificationFrequency()).thenReturn("1");
		when(notificationConfig.getEmailNotificationSubject()).thenReturn("Action Required: Restore Your {{toolName}} Connection"); // subject is blank

		UserInfo userInfo = new UserInfo();
		userInfo.setEmailAddress("user@example.com");
		Map<String, Boolean> alertNotifications = new HashMap<>();
		alertNotifications.put("errorAlertNotification",false);
		userInfo.setNotificationEmail(alertNotifications);
		when(userInfoRepository.findByUsername("user123")).thenReturn(userInfo);


		processorToolConnectionServiceImpl.updateBreakingConnection(connectionId, "Some error");

		verify(notificationService, never()).sendNotificationEvent(any(), any(), any(), anyBoolean(), any());
	}
}
