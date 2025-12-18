package com.publicissapient.kpidashboard.common.constant;

public enum NotificationEnum {
	PROJECT_ACCESS("Project Access Request"), USER_NAME("User_Name"), TOOL_NAME("Tool_Name"), FIX_URL(
			"Fix_Url"), HELP_URL("Help_Url"), USER_APPROVAL("User Access Request");

	private String value;

	NotificationEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
