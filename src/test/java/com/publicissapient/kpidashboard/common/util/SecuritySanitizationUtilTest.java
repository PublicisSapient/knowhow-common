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

class SecuritySanitizationUtilTest {

	// ------------------------------------------------------------
	// ---------------- Random Password Generation ----------------
	// ------------------------------------------------------------

	@Test
	@DisplayName("Should generate random password of correct length")
	void testGenerateRandomPassword_length() {
		int length = 12;
		String password = SecuritySanitizationUtil.generateRandomPassword(length);
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

		String password = SecuritySanitizationUtil.generateRandomPassword(50);
		assertTrue(password.chars().allMatch(c -> allowedSet.contains((char) c)),
				"Password should contain only allowed characters");
	}

	@RepeatedTest(3)
	@DisplayName("Should generate different random passwords on multiple calls")
	void testGenerateRandomPassword_uniqueness() {
		String p1 = SecuritySanitizationUtil.generateRandomPassword(10);
		String p2 = SecuritySanitizationUtil.generateRandomPassword(10);
		assertNotEquals(p1, p2, "Random passwords should differ");
	}

	@Test
	@DisplayName("Should handle zero-length password gracefully")
	void testGenerateRandomPassword_zeroLength() {
		String password = SecuritySanitizationUtil.generateRandomPassword(0);
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
		String result = SecuritySanitizationUtil.getSanitizedProjectConfigId(config);
		assertEquals("proj-123_ABC", result, "Valid project ID should remain unchanged");
	}

	@Test
	@DisplayName("Should sanitize invalid characters in project ID")
	void testExtractSafeProjectConfigId_invalidCharacters() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(Arrays.asList("proj@123#$%^"));
		String result = SecuritySanitizationUtil.getSanitizedProjectConfigId(config);
		assertEquals("proj123", result, "Invalid characters should be removed");
	}

	@Test
	@DisplayName("Should return empty string when config is null")
	void testExtractSafeProjectConfigId_nullConfig() {
		String result = SecuritySanitizationUtil.getSanitizedProjectConfigId(null);
		assertEquals("", result, "Null config should return empty string");
	}

	@Test
	@DisplayName("Should return empty string when project ID list is null")
	void testExtractSafeProjectConfigId_nullList() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(null);
		String result = SecuritySanitizationUtil.getSanitizedProjectConfigId(config);
		assertEquals("", result, "Null list should return empty string");
	}

	@Test
	@DisplayName("Should return empty string when project ID list is empty")
	void testExtractSafeProjectConfigId_emptyList() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(Collections.emptyList());
		String result = SecuritySanitizationUtil.getSanitizedProjectConfigId(config);
		assertEquals("", result, "Empty list should return empty string");
	}

	@Test
	@DisplayName("Should return empty string when project ID is null")
	void testExtractSafeProjectConfigId_nullId() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(Collections.singletonList(null));
		String result = SecuritySanitizationUtil.getSanitizedProjectConfigId(config);
		assertEquals("", result, "Null ID should return empty string");
	}

	@Test
	@DisplayName("Should return empty string when project ID is empty")
	void testExtractSafeProjectConfigId_emptyId() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(Collections.singletonList(""));
		String result = SecuritySanitizationUtil.getSanitizedProjectConfigId(config);
		assertEquals("", result, "Empty ID should return empty string");
	}

	@Test
	@DisplayName("Should return empty string when project ID contains only invalid characters")
	void testExtractSafeProjectConfigId_allInvalid() {
		ProcessorExecutionBasicConfig config = new ProcessorExecutionBasicConfig();
		config.setProjectBasicConfigIds(Collections.singletonList("!@#$%^&*()"));
		String result = SecuritySanitizationUtil.getSanitizedProjectConfigId(config);
		assertEquals("", result, "All invalid characters should produce empty string");
	}

	// ------------------------------------------------------------
	// ---------------- Sanitization Utility Direct ----------------
	// ------------------------------------------------------------

	@Test
	@DisplayName("Should sanitize string with invalid characters")
	void testSanitize_invalidCharacters() {
		String result = SecuritySanitizationUtil.sanitize("abc@123#xyz!");
		assertEquals("abc123xyz", result, "Invalid characters should be removed");
	}

	@Test
	@DisplayName("Should handle null input gracefully")
	void testSanitize_nullInput() {
		assertEquals("", SecuritySanitizationUtil.sanitize(null), "Null input should return empty string");
	}

	@Test
	@DisplayName("Should handle empty input gracefully")
	void testSanitize_emptyInput() {
		assertEquals("", SecuritySanitizationUtil.sanitize(""), "Empty input should return empty string");
	}

	@Test
	@DisplayName("Should keep valid alphanumeric and safe symbols (-,_) intact")
	void testSanitize_validSymbols() {
		String result = SecuritySanitizationUtil.sanitize("proj-123_ABC");
		assertEquals("proj-123_ABC", result, "Valid characters should remain unchanged");
	}
}
