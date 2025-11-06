package com.publicissapient.kpidashboard.common.util;

import java.util.Optional;
import java.util.regex.Pattern;

import com.publicissapient.kpidashboard.common.model.ProcessorExecutionBasicConfig;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for safely extracting and sanitizing Project Basic Config IDs.
 * Handles all null, empty, and invalid cases to avoid NPEs and XSS.
 */
@Slf4j
public final class ProjectConfigIdUtil {

	private static final Pattern SAFE_PATTERN = Pattern.compile("[^a-zA-Z0-9-_]");
	private static final String EMPTY = "";

	private ProjectConfigIdUtil() {
		// prevent instantiation
	}

	/**
	 * Safely extracts and sanitizes the first Project Basic Config ID from a
	 * ProcessorExecutionBasicConfig object.
	 *
	 * <p>
	 * Handles all cases: - Null ProcessorExecutionBasicConfig - Null or empty ID
	 * list - Null or empty ID value - Invalid characters (XSS-safe)
	 *
	 * @param config
	 *          ProcessorExecutionBasicConfig object (nullable)
	 * @return sanitized ID or empty string if invalid
	 */
	public static String extractSafeProjectConfigId(ProcessorExecutionBasicConfig config) {

		String sanitizedId = Optional.ofNullable(config)
				.flatMap(cfg -> Optional.ofNullable(cfg.getProjectBasicConfigIds())
						.filter(list -> !list.isEmpty())
						.map(list -> list.get(0)))
				.filter(id -> !id.trim().isEmpty())
				.map(ProjectConfigIdUtil::sanitize)
				.orElse(EMPTY);

		if (sanitizedId.isEmpty()) {
			log.warn("Failed to extract valid Project Basic Config ID from ProcessorExecutionBasicConfig: {}", config);
		}

		return sanitizedId;
	}

	/**
	 * Sanitizes a single project ID string (for direct inputs).
	 *
	 * <p>
	 * Removes all characters except letters, digits, '-' and '_'.
	 *
	 * @param projectId
	 *          input project ID
	 * @return sanitized project ID or empty string if null/invalid
	 */
	public static String sanitize(String projectId) {
		return Optional.ofNullable(projectId).map(id -> SAFE_PATTERN.matcher(id).replaceAll(EMPTY)).orElse(EMPTY);
	}
}
