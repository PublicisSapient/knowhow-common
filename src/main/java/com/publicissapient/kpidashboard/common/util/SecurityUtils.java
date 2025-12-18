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

import java.security.SecureRandom;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.publicissapient.kpidashboard.common.model.ProcessorExecutionBasicConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * Common utility class providing:
 *
 * <ul>
 * <li>Secure random string generation (passwords/tokens)
 * <li>Safe extraction and sanitization of Project Config IDs
 * </ul>
 */
@Slf4j
public final class SecurityUtils {

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static final Pattern SAFE_PATTERN = Pattern.compile("[^a-zA-Z0-9-_]");

	private SecurityUtils() {
		// Utility class — prevent instantiation
	}

	/**
	 * Generates a random password containing uppercase, lowercase, and digits.
	 *
	 * @param length
	 *          length of the desired password
	 * @return random password string
	 */
	public static String generateRandomPassword(int length) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		return SECURE_RANDOM.ints(length, 0, chars.length()).mapToObj(i -> String.valueOf(chars.charAt(i)))
				.collect(Collectors.joining());
	}

	/**
	 * Safely extracts and sanitizes the first Project Basic Config ID from a
	 * ProcessorExecutionBasicConfig object.
	 *
	 * <p>
	 * Handles all cases:
	 *
	 * <ul>
	 * <li>Null ProcessorExecutionBasicConfig
	 * <li>Null or empty ID list
	 * <li>Null or empty ID value
	 * <li>Invalid characters (XSS-safe)
	 * </ul>
	 *
	 * @param config
	 *          ProcessorExecutionBasicConfig object (nullable)
	 * @return sanitized ID or empty string if invalid
	 */
	public static String getSanitizedProjectConfigId(ProcessorExecutionBasicConfig config) {
		String sanitizedId = Optional.ofNullable(config)
				.flatMap(cfg -> Optional.ofNullable(cfg.getProjectBasicConfigIds()).filter(list -> !list.isEmpty())
						.map(list -> list.get(0)))
				.filter(id -> !id.trim().isEmpty()).map(SecurityUtils::sanitize).orElse(StringUtils.EMPTY);

		if (sanitizedId.isEmpty()) {
			log.warn("Failed to extract valid Project Basic Config ID from ProcessorExecutionBasicConfig: {}", config);
		}
		return sanitizedId;
	}

	/**
	 * Sanitizes a given input string by removing all characters except letters,
	 * digits, hyphens ('-'), and underscores ('_').
	 *
	 * <p>
	 * This method is generic and can be used to clean user-provided or
	 * configuration-related strings to ensure they are safe for further processing.
	 *
	 * @param input
	 *          the input string to sanitize (nullable)
	 * @return a sanitized string containing only allowed characters, or
	 *         {@link org.apache.commons.lang3.StringUtils#EMPTY} if the input is
	 *         null or invalid
	 */
	public static String sanitize(String input) {
		return Optional.ofNullable(input).map(str -> SAFE_PATTERN.matcher(str).replaceAll(StringUtils.EMPTY))
				.orElse(StringUtils.EMPTY);
	}
}
