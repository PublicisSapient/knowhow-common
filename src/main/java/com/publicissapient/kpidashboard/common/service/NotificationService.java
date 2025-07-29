package com.publicissapient.kpidashboard.common.service;

import java.util.List;
import java.util.Map;

public interface NotificationService {

	void sendNotificationEvent(List<String> emailAddresses, Map<String, String> customData, String notSubject,
			boolean notificationSwitch, String templateKey);
}
