package com.publicissapient.kpidashboard.common.util;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

	// -------------------- Secure String Utilities --------------------
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	// -------------------- Project Config Utilities --------------------
	private static final Pattern SAFE_PATTERN = Pattern.compile("[^a-zA-Z0-9-_]");
	private static final String EMPTY = "";

	// -------------------- Private Constructor --------------------
	private SecurityUtils() {
		// Utility class — prevent instantiation
	}

	// ================================================================
	// ========== Secure Random String / Password Generation ==========
	// ================================================================

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

	// ================================================================
	// ====== Safe Extraction & Sanitization of Project Config ID ======
	// ================================================================

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
		String sanitizedId = Optional
				.ofNullable(config).flatMap(cfg -> Optional.ofNullable(cfg.getProjectBasicConfigIds())
						.filter(list -> !list.isEmpty()).map(list -> list.get(0)))
				.filter(id -> !id.trim().isEmpty()).map(SecurityUtils::sanitize).orElse(EMPTY);

		if (sanitizedId.isEmpty()) {
			log.warn("Failed to extract valid Project Basic Config ID from ProcessorExecutionBasicConfig: {}", config);
		}

		return sanitizedId;
	}

	/**
	 * Sanitizes a single project ID string (for direct inputs). Removes all
	 * characters except letters, digits, '-' and '_'.
	 *
	 * @param projectId
	 *          input project ID
	 * @return sanitized project ID or empty string if null/invalid
	 */
	public static String sanitize(String projectId) {
		return Optional.ofNullable(projectId).map(id -> SAFE_PATTERN.matcher(id).replaceAll(EMPTY)).orElse(EMPTY);
	}
}
