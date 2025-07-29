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

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 
 * This class contains various fields that can be used to customize the email
 * content
 */
@Data
public class EmailRequestPayload {
	@NotEmpty(message = "Recipients list cannot be empty.")
	private List<@Email(message = "Invalid email address") String> recipients;
	private String userName;
	@Email(message = "User email is invalid.")
	private String userEmail;
	private String accessLevel;
	private String accessItems;
	private String userProjects;
	private String userRoles;
	private String accountName;
	private String teamName;
	private String uploadedBy;
	private String serverHost;
	private String feedbackContent;
	private String feedbackCategory;
	private String feedbackType;
	@Email(message = "Admin email is invalid.")
	private String adminEmail;
	private String toolName;
	private String helpUrl;
	private String fixUrl;
	private String expiryTime;
	private String resetUrl;
	private String pdfAttachment;
}