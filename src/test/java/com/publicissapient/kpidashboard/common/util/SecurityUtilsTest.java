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

package com.publicissapient.kpidashboard.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.publicissapient.kpidashboard.common.model.ProcessorExecutionBasicConfig;

class SecurityUtilsTest {

	// ------------------------------------------------------------
	// ---------------- Random Password Generation ----------------
	// ------------------------------------------------------------

	@Test
	@DisplayName("Should generate random password of correct length")
	void testGenerateRandomPassword_length() {
		int length = 12;
		String password = SecurityUtils.generateRandomPassword(length);
		assertNotNull(password, "Password should not be null");
		assertEquals(length, password.length(), "Password length should match input length");
	}

	@Test
	@DisplayName("Should generate random password with valid character set")
	void testGenerateRandomPassword_validCharacters() {
		String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Set<Character> allowedSet = new HashSet<>();
		for (char c : allowedChars.toCharArray()) {
			allowedSet.add(c);
		}

		String password = SecurityUtils.generateRandomPassword(50);
		assertTrue(password.chars().allMatch(c -> allowedSet.contains((char) c)),
				"Password should contain only allowed characters");
	}

	@RepeatedTest(3)
	@DisplayName("Should generate different random passwords on multiple calls")
	void testGenerateRandomPassword_uniqueness() {
		String p1 = SecurityUtils.generateRandomPassword(10);
		String p2 = SecurityUtils.generateRandomPassword(10);
		assertNotEquals(p1, p2, "Random passwords should differ");
	}

	@Test
	@DisplayName("Should handle zero-length password gracefully")
	void testGenerateRandomPassword_zeroLength() {
		String password = SecurityUtils.generateRandomPassword(0);
		assertEquals("", password, "Zero-length password should return empty string");
	}

	// ------------------------------------------------------------
	// ---------------- Project Config ID Extraction ---------------
	// ------------------------------------------------------------

	@Test
	@DisplayName("Should extract and sanitize valid project config ID")
	void testExtractSafeProjectConfigId_valid() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(Arrays.asList("proj-123_ABC"));
		String result = SecurityUtils.getSanitizedProjectConfigId(config);
		assertEquals("proj-123_ABC", result, "Valid project ID should remain unchanged");
	}

	@Test
	@DisplayName("Should sanitize invalid characters in project ID")
	void testExtractSafeProjectConfigId_invalidCharacters() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(Arrays.asList("proj@123#$%^"));
		String result = SecurityUtils.getSanitizedProjectConfigId(config);
		assertEquals("proj123", result, "Invalid characters should be removed");
	}

	@Test
	@DisplayName("Should return empty string when config is null")
	void testExtractSafeProjectConfigId_nullConfig() {
		String result = SecurityUtils.getSanitizedProjectConfigId(null);
		assertEquals("", result, "Null config should return empty string");
	}

	@Test
	@DisplayName("Should return empty string when project ID list is null")
	void testExtractSafeProjectConfigId_nullList() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(null);
		String result = SecurityUtils.getSanitizedProjectConfigId(config);
		assertEquals("", result, "Null list should return empty string");
	}

	@Test
	@DisplayName("Should return empty string when project ID list is empty")
	void testExtractSafeProjectConfigId_emptyList() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(Collections.emptyList());
		String result = SecurityUtils.getSanitizedProjectConfigId(config);
		assertEquals("", result, "Empty list should return empty string");
	}

	@Test
	@DisplayName("Should return empty string when project ID is null")
	void testExtractSafeProjectConfigId_nullId() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(Collections.singletonList(null));
		String result = SecurityUtils.getSanitizedProjectConfigId(config);
		assertEquals("", result, "Null ID should return empty string");
	}

	@Test
	@DisplayName("Should return empty string when project ID is empty")
	void testExtractSafeProjectConfigId_emptyId() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(Collections.singletonList(""));
		String result = SecurityUtils.getSanitizedProjectConfigId(config);
		assertEquals("", result, "Empty ID should return empty string");
	}

	@Test
	@DisplayName("Should return empty string when project ID contains only invalid characters")
	void testExtractSafeProjectConfigId_allInvalid() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(Collections.singletonList("!@#$%^&*()"));
		String result = SecurityUtils.getSanitizedProjectConfigId(config);
		assertEquals("", result, "All invalid characters should produce empty string");
	}

	// ------------------------------------------------------------
	// ---------------- Sanitization Utility Direct ----------------
	// ------------------------------------------------------------

	@Test
	@DisplayName("Should sanitize string with invalid characters")
	void testSanitize_invalidCharacters() {
		String result = SecurityUtils.sanitize("abc@123#xyz!");
		assertEquals("abc123xyz", result, "Invalid characters should be removed");
	}

	@Test
	@DisplayName("Should handle null input gracefully")
	void testSanitize_nullInput() {
		assertEquals("", SecurityUtils.sanitize(null), "Null input should return empty string");
	}

	@Test
	@DisplayName("Should handle empty input gracefully")
	void testSanitize_emptyInput() {
		assertEquals("", SecurityUtils.sanitize(""), "Empty input should return empty string");
	}

	@Test
	@DisplayName("Should keep valid alphanumeric and safe symbols (-,_) intact")
	void testSanitize_validSymbols() {
		String result = SecurityUtils.sanitize("proj-123_ABC");
		assertEquals("proj-123_ABC", result, "Valid characters should remain unchanged");
	}
}
