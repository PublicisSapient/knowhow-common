/*
 *   Copyright 2014 CapitalOne, LLC.
 *   Further development Copyright 2022 Sapient Corporation.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.publicissapient.kpidashboard.common.model.notification;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * This class contains various fields that can be used to customize the email
 * content
 */
@Data
@Schema(description = "Payload for Email Request containing various fields for email customization")
public class EmailRequestPayload {
	@Schema(description = "List of recipient email addresses", example = "[\"example1@example.com\", \"example2@example.com\"]")
	@NotEmpty(message = "Recipients list cannot be empty.")
	private List<@Email(message = "Invalid email address") String> recipients;

	@Schema(description = "User name of the email recipient", example = "john.doe")
	private String userName;

	@Schema(description = "User email", example = "john.doe@example.com")
	@Email(message = "User email is invalid.")
	private String userEmail;

	@Schema(description = "User access level", example = "admin")
	private String accessLevel;

	@Schema(description = "Items the user has access to", example = "Dashboard, Reports")
	private String accessItems;

	@Schema(description = "Projects associated with the user", example = "Project A, Project B")
	private String userProjects;

	@Schema(description = "Roles assigned to the user", example = "ROLE_ADMIN, ROLE_USER")
	private String userRoles;

	@Schema(description = "Account name", example = "Acme Corp")
	private String accountName;

	@Schema(description = "Team name", example = "Development Team")
	private String teamName;

	@Schema(description = "Uploaded by", example = "jane.smith")
	private String uploadedBy;

	@Schema(description = "Server host", example = "kpidashboard.example.com")
	private String serverHost;

	@Schema(description = "Feedback content provided by the user", example = "Great tool, very useful!")
	private String feedbackContent;

	@Schema(description = "Category of the feedback", example = "Usability")
	private String feedbackCategory;

	@Schema(description = "Type of the feedback", example = "Suggestion")
	private String feedbackType;

	@Schema(description = "The email of the admin", example = "admin1@example.com")
	@Email(message = "Admin email is invalid.")
	private String adminEmail;

	@Schema(description = "Tool name", example = "JIRA")
	private String toolName;

	@Schema(description = "URL in case of help needed", example = "http://help.example.com")
	private String helpUrl;

	@Schema(description = "URL in case of fix needed", example = "http://fix.example.com")
	private String fixUrl;

	@Schema(description = "Expiry time for the reset link", example = "24 hours")
	private String expiryTime;

	@Schema(description = "Password reset URL", example = "http://example.com/reset?token=abc123")
	private String resetUrl;

	@Schema(description = "PDF attachment in Base64 format", example = "JVBERi0xLjQKJcfs...")
	private String pdfAttachment;
}
