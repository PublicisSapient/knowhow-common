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

package com.publicissapient.kpidashboard.common.client;

import java.io.IOException;
import java.security.Principal;
import java.security.PrivilegedAction;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Kerberos client to connect with SPNEGO JIRA client. Handles authentication
 * and cookie management for Kerberos-based connections.
 */
@Slf4j
@Getter
public class KerberosClient {

	// Constants
	private static final String COOKIE_HEADER = "Cookie";
	private static final String AUTH_LOGIN_CONFIG = "java.security.auth.login.config";
	private static final String KERB_CONFIG = "java.security.krb5.conf";
	private static final String AUTH_USE_SUBJECT_CREDS_ONLY = "javax.security.auth.useSubjectCredsOnly";
	private static final String AUTH_PREFERENCES = "http.auth.preference";
	private static final Credentials CREDENTIALS = new NullCredentials();

	private final String jaasConfigFilePath;
	private final String krb5ConfigFilePath;
	private final String jaasUser;
	private final String samlEndPoint;
	private final String jiraHost;
	private final HttpClient loginHttpClient;
	private final HttpClient httpClient;
	private final BasicCookieStore cookieStore;

	/**
	 * Creates a new KerberosClient for SPNEGO authentication.
	 *
	 * @param jaasConfigFilePath
	 *          path to JAAS configuration file
	 * @param krb5ConfigFilePath
	 *          path to Kerberos configuration file
	 * @param jaasUser
	 *          username for JAAS authentication
	 * @param samlEndPoint
	 *          SAML endpoint URL
	 * @param jiraHost
	 *          JIRA host URL
	 */
	public KerberosClient(String jaasConfigFilePath, String krb5ConfigFilePath, String jaasUser, String samlEndPoint,
			String jiraHost) {
		this.jaasConfigFilePath = jaasConfigFilePath;
		this.krb5ConfigFilePath = krb5ConfigFilePath;
		this.jaasUser = jaasUser;
		this.samlEndPoint = samlEndPoint;
		this.jiraHost = jiraHost;
		this.cookieStore = new BasicCookieStore();
		this.loginHttpClient = buildLoginHttpClient();
		this.httpClient = buildHttpClient();
	}

	// Private methods
	private HttpClient buildLoginHttpClient() {
		Lookup<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
				.register("negotiate", new SPNegoSchemeFactory(true)).build();

		BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(new AuthScope(null, -1, null), CREDENTIALS);

		RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();

		return HttpClientBuilder.create().setDefaultAuthSchemeRegistry(authSchemeRegistry)
				.setDefaultCredentialsProvider(credentialsProvider).setDefaultCookieStore(cookieStore)
				.setDefaultRequestConfig(requestConfig).build();
	}

	private HttpClient buildHttpClient() {
		RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
		return HttpClientBuilder.create().setDefaultCookieStore(cookieStore).setDefaultRequestConfig(requestConfig).build();
	}

	private void setKerberosProperties() {
		System.setProperty(AUTH_LOGIN_CONFIG, jaasConfigFilePath);
		System.setProperty(KERB_CONFIG, krb5ConfigFilePath);
		System.setProperty(AUTH_USE_SUBJECT_CREDS_ONLY, "false");
		System.setProperty(AUTH_PREFERENCES, "SPNEGO");
	}

	private void clearKerberosProperties() {
		System.clearProperty(AUTH_LOGIN_CONFIG);
		System.clearProperty(KERB_CONFIG);
		System.clearProperty(AUTH_USE_SUBJECT_CREDS_ONLY);
		System.clearProperty(AUTH_PREFERENCES);
	}

	// Public methods
	/**
	 * Performs Kerberos login and fetches necessary cookies for JIRA connection.
	 *
	 * @param samlTokenStartString
	 *          start delimiter for SAML token extraction
	 * @param samlTokenEndString
	 *          end delimiter for SAML token extraction
	 * @param samlUrlStartString
	 *          start delimiter for SAML URL extraction
	 * @param samlUrlEndString
	 *          end delimiter for SAML URL extraction
	 * @return login response string
	 * @throws RestClientException
	 *           if login fails
	 */
	public String login(String samlTokenStartString, String samlTokenEndString, String samlUrlStartString,
			String samlUrlEndString) {
		try {
			String loginURL = samlEndPoint + jiraHost;
			setKerberosProperties();
			LoginContext loginContext = new LoginContext(jaasUser);
			loginContext.login();
			Subject serviceSubject = loginContext.getSubject();

			PrivilegedAction<String> action = () -> {
				try {
					return loginCall(loginURL, samlTokenStartString, samlTokenEndString, samlUrlStartString, samlUrlEndString);
				} catch (IOException e) {
					throw new RestClientException("Error during login: " + e.getMessage(), e);
				}
			};
			return Subject.doAs(serviceSubject, action);
		} catch (Exception ex) {
			log.error("Kerberos login failed: {}", ex.getMessage(), ex);
			throw new RestClientException("Kerberos login failed: " + ex.getMessage(), ex);
		}
	}

	private String loginCall(String loginURL, String samlTokenStartString, String samlTokenEndString,
			String samlUrlStartString, String samlUrlEndString) throws IOException {
		HttpUriRequest getRequest = RequestBuilder.get().setUri(loginURL).build();
		HttpResponse response = loginHttpClient.execute(getRequest);
		HttpEntity entity = response.getEntity();
		String loginResponse = EntityUtils.toString(entity, "UTF-8");

		if (StringUtils.hasText(loginResponse)) {
			log.debug("Login response received");
			generateSamlCookies(loginResponse, samlTokenStartString, samlTokenEndString, samlUrlStartString,
					samlUrlEndString);
		} else {
			loginResponse = null;
		}

		clearKerberosProperties();
		return loginResponse;
	}

	public void generateSamlCookies(String loginResponse, String samlTokenStartString, String samlTokenEndString,
			String samlUrlStartString, String samlUrlEndString) throws IOException {
		String samlToken = extractString(loginResponse, samlTokenStartString, samlTokenEndString);
		String samlURL = extractString(loginResponse, samlUrlStartString, samlUrlEndString);
		log.debug("SAML URL extracted from login response: {}", samlURL);

		HttpUriRequest postRequest = RequestBuilder.post().setUri(samlURL).setHeader(HttpHeaders.ACCEPT, "application/json")
				.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
				.addParameter("SAMLResponse", samlToken).build();

		httpClient.execute(postRequest);
	}

	/**
	 * Executes HTTP request and returns response as string.
	 *
	 * @param httpUriRequest
	 *          the HTTP request to execute
	 * @return response body as string
	 * @throws IOException
	 *           if request execution fails
	 */
	public String getResponse(HttpUriRequest httpUriRequest) throws IOException {
		HttpResponse response = getHttpResponse(httpUriRequest);
		HttpEntity entity = response.getEntity();
		return EntityUtils.toString(entity, "UTF-8");
	}

	/**
	 * Executes HTTP request with cookies and returns HTTP response.
	 *
	 * @param httpUriRequest
	 *          the HTTP request to execute
	 * @return HTTP response
	 * @throws IOException
	 *           if request execution fails
	 */
	public HttpResponse getHttpResponse(HttpUriRequest httpUriRequest) throws IOException {
		httpUriRequest.addHeader(COOKIE_HEADER, getCookies());
		return httpClient.execute(httpUriRequest);
	}

	private String extractString(String input, String start, String end) {
		if (!StringUtils.hasText(input) || !StringUtils.hasText(start) || !StringUtils.hasText(end)) {
			return null;
		}

		String[] startSplit = input.split(start);
		if (startSplit.length > 1) {
			String[] endSplit = startSplit[1].split(end);
			if (endSplit.length > 1) {
				return endSplit[0];
			}
		}
		return null;
	}

	/**
	 * Converts cookie store to cookie header string.
	 *
	 * @return formatted cookie header string
	 */
	public String getCookies() {
		StringBuilder cookieBuilder = new StringBuilder();
		cookieStore.getCookies()
				.forEach(cookie -> cookieBuilder.append(cookie.getName()).append("=").append(cookie.getValue()).append(";"));
		return cookieBuilder.toString();
	}

	/** Null credentials implementation for SPNEGO authentication. */
	private static class NullCredentials implements Credentials {
		@Override
		public Principal getUserPrincipal() {
			return null;
		}

		@Override
		public String getPassword() {
			return null;
		}
	}
}
