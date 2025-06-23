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


package com.publicissapient.kpidashboard.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * This class is used to bind external configurations to a bean in application
 * code. You can inject and use this bean throughout your application code just
 * like any other spring bean.
 *
 * @author rendk
 */

@Data
@Component
@ConfigurationProperties
public class NotificationConfig {

	@Value("${brokenConnection.MaximumEmailNotificationCount}")
	private String brokenConnectionMaximumEmailNotificationCount;

	@Value("${brokenConnection.EmailNotificationFrequency}")
	private String brokenConnectionEmailNotificationFrequency;

	@Value("${brokenConnection.EmailNotificationSubject}")
	private String brokenConnectionEmailNotificationSubject;

	@Value("${brokenConnection.fix.url}")
	private String brokenConnectionFixUrl;

	@Value("${brokenConnection.help.url}")
	private String brokenConnectionHelpUrl;

	@Value("${kafka.mailtopic}")
	private String kafkaMailTopic;

	@Value("${forgotPassword.uiHost}")
	private String uiHost;

	private Map<String, String> mailTemplate;

	@Value("${notification.switch}")
	private boolean notificationSwitch;

	@Value("${flag.mailWithoutKafka}")
	private boolean mailWithoutKafka;
}
