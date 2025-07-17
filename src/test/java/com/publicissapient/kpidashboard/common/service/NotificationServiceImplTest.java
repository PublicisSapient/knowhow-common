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

package com.publicissapient.kpidashboard.common.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.knowhow.retro.notifications.producer.EmailProducer;
import com.knowhow.retro.notifications.utils.TemplateParserHelper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSendException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.publicissapient.kpidashboard.common.model.application.EmailServerDetail;
import com.publicissapient.kpidashboard.common.model.application.GlobalConfig;
import com.publicissapient.kpidashboard.common.repository.application.GlobalConfigRepository;

@ExtendWith(SpringExtension.class)
class NotificationServiceImplTest {

	@InjectMocks
	private NotificationServiceImpl notificationService;

	@Mock
	private GlobalConfigRepository globalConfigRepository;
	@Mock
	private SpringTemplateEngine templateEngine;
	@Mock
	private TemplateParserHelper templateParserHelper;

	private GlobalConfig globalConfig;
	@Mock
	private Environment environment;
   	@Mock
	private ObjectProvider<EmailProducer> emailProducer;
	private List<GlobalConfig> globalConfigs = new ArrayList<>();

	@BeforeEach
	public void setUp() {
		globalConfig = new GlobalConfig();
		globalConfig.setEnv("email");
		EmailServerDetail emailServerDetail = new EmailServerDetail();
		emailServerDetail.setEmailPort(25);
		emailServerDetail.setEmailHost("xyz.smtp.com");
		emailServerDetail.setFromEmail("xyz@abc.com");
		emailServerDetail.setFeedbackEmailIds(Arrays.asList("abc.com", "cde.com"));
		globalConfig.setEmailServerDetail(emailServerDetail);
		globalConfigs.add(globalConfig);
	}

	@Test
	void testSendNotificationEventNull() {
		List<String> emailList = new ArrayList<>();
		emailList.add("abc@xyz.com");
		Map<String, String> customData = new HashMap<>();
		customData.put("abc", "xyz");
		String notSubject = "";
		notificationService.sendNotificationEvent(emailList, customData, notSubject, true, "abc");
		assertTrue(true);
	}

	@Test
	void testSentWithJMS() {
		List<String> emailList = new ArrayList<>();
		emailList.add("abc@xyz.com");
		Map<String, String> customData = new HashMap<>();
		customData.put("abc", "xyz");
		String notSubject = "subject";
		when(globalConfigRepository.findAll()).thenReturn(globalConfigs);
		when(templateEngine.process(anyString(), any())).thenReturn("abc");
		Assert.assertThrows(MailSendException.class, () -> notificationService.sendNotificationEvent(emailList,
				customData, notSubject, true, "Forgot_Password_Template"));
	}

	@Test
	void testSendEmailWithJMSKeyNotFound() {
		List<String> emailList = new ArrayList<>();
		emailList.add("abc@xyz.com");
		Map<String, String> customData = new HashMap<>();
		customData.put("abc", "xyz");
		String notSubject = "subject";
		when(globalConfigRepository.findAll()).thenReturn(globalConfigs);
		when(templateEngine.process(anyString(), any())).thenReturn(null);
		notificationService.sendNotificationEvent(emailList, customData, notSubject, false, "Forgot_Password_Template");
		assertTrue(true);
	}

	@Test
	void testSendNotificationEvent() {
		List<String> emailList = new ArrayList<>();
		emailList.add("abc@xyz.com");
		Map<String, String> customData = new HashMap<>();
		customData.put("abc", "xyz");
		String notSubject = "subject";
		when(globalConfigRepository.findAll()).thenReturn(globalConfigs);
		notificationService.sendNotificationEvent(emailList, customData, notSubject, true, "abc");
		assertTrue(true);
	}
}
