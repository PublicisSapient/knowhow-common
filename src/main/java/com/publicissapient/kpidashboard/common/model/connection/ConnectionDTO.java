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

package com.publicissapient.kpidashboard.common.model.connection;

import java.util.List;

import org.bson.types.ObjectId;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Represents the ConnectionDTO data. */
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents the Connection Data Transfer Object")
public class ConnectionDTO {

	@Schema(description = "Unique identifier of the connection", example = "64b7f8e2c9e77a6f5d3e8b9a")
	private ObjectId id;

	@Schema(description = "Type of the connection", example = "JIRA")
	private String type;

	@Schema(description = "Name of the connection", example = "My JIRA Connection")
	private String connectionName;

	@Schema(description = "Description of the connection", example = "Connection to JIRA for project tracking")
	private boolean cloudEnv;

	@Schema(description = "Indicates if cloud environment is used", example = "true")
	private boolean accessTokenEnabled;

	@Schema(description = "Base URL of the connection", example = "https://jira.example.com")
	private String baseUrl;

	@Schema(description = "Username for the connection", example = "jira_user")
	private String username;

	@Schema(description = "Password for the connection", example = "password123")
	private String password;

	@Schema(description = "Personal Access OAuth Token", example = "oauth_token_abc123")
	private String patOAuthToken;

	@Schema(description = "API Endpoint of the connection", example = "https://api.jira.example.com")
	private String apiEndPoint;

	@Schema(description = "Consumer Key for OAuth", example = "consumer_key_123")
	private String consumerKey;

	@Schema(description = "Private Key for OAuth", example = "private_key_abc")
	private String privateKey;

	@Schema(description = "API Key for the connection", example = "api_key_xyz789")
	private String apiKey;

	@Schema(description = "Client Secret Key for OAuth", example = "client_secret_456")
	private String clientSecretKey;

	@Schema(description = "Indicates if OAuth is used", example = "true")
	private boolean isOAuth;

	@Schema(description = "Client ID for OAuth", example = "client_id_789")
	private String clientId;

	@Schema(description = "Tenant ID for OAuth", example = "tenant_id_012")
	private String tenantId;

	@Schema(description = "Redirect URL for OAuth", example = "https://myapp.example.com/oauth/callback")
	private String pat;

	@Schema(description = "API Key Field Name", example = "X-API-KEY")
	private String apiKeyFieldName;

	@Schema(description = "Access Token for the connection", example = "access_token_efg456")
	private String accessToken;

	@Schema(description = "Indicates if the connection is offline", example = "false")
	private boolean offline;

	@Schema(description = "File path for offline connection", example = "/path/to/offline/file")
	private String offlineFilePath;

	@Schema(description = "Created By user", example = "admin")
	private String createdBy;

	@Schema(description = "Creation timestamp", example = "2024-06-15T10:00:00Z")
	private String createdAt;

	@Schema(description = "Indicates if the connection is shared with all users", example = "true")
	private boolean sharedConnection; // shared with everyone

	@Schema(description = "Last updated timestamp", example = "2024-06-20T15:30:00Z")
	private String updatedBy;

	@Schema(description = "List of users with access to the connection", example = "[\"user1\", \"user2\"]")
	private List<String> connectionUsers;

	@Schema(description = "Indicates if Vault is used for credentials", example = "true")
	private boolean vault; // GS requirement

	@Schema(description = "Indicates if Bearer Token is used for authentication", example = "true")
	private boolean bearerToken; // Kurig requirement

	@Schema(description = "The type of JIRA authentication", example = "OAuth")
	private String jiraAuthType;

	@Schema(description = "Indicates if JAAS Kerberos authentication is used", example = "true")
	private boolean jaasKrbAuth;

	@Schema(description = "File path for JAAS configuration", example = "/path/to/jaas/config")
	private String jaasConfigFilePath;

	@Schema(description = "File path for Kerberos 5 configuration", example = "/path/to/krb5/config")
	private String krb5ConfigFilePath;

	@Schema(description = "JAAS User for Kerberos authentication", example = "jaas_user")
	private String jaasUser;

	@Schema(description = "Kerberos Keytab file path", example = "/path/to/keytab/file")
	private String userPrincipal;

	@Schema(description = "SAML Endpoint for authentication", example = "https://saml.example.com/endpoint")
	private String samlEndPoint;

	@Schema(description = "E-mail address", example = "user@example.com")
	private String email;

	@Schema(description = "Repository Tool Provider", example = "GitHub")
	private String repoToolProvider;

	@Schema(description = "Indicates if the connection has a broken link", example = "false")
	private boolean brokenConnection;

	@Schema(description = "Error message for connection issues", example = "Connection timed out")
	private String connectionErrorMsg;

	public boolean getIsOAuth() {
		return this.isOAuth;
	}

	public void setIsOAuth(boolean isOAuth) {
		this.isOAuth = isOAuth;
	}

	/**
	 * Checks if the parameter object is equal to the class object
	 *
	 * @param obj
	 *          object
	 * @return boolean true or false
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (null == obj) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		ConnectionDTO other = (ConnectionDTO) obj;
		if (null == connectionName) {
			if (null != other.connectionName) {
				return false;
			}
		} else if (!connectionName.equals(other.connectionName)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connectionName == null) ? 0 : connectionName.hashCode());
		return result;
	}
}
