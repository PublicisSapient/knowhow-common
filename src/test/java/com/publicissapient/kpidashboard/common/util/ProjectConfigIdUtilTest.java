package com.publicissapient.kpidashboard.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.publicissapient.kpidashboard.common.model.ProcessorExecutionBasicConfig;

class ProjectConfigIdUtilTest {

	private ProcessorExecutionBasicConfig config;

	@BeforeEach
	void setup() {
		config = new ProcessorExecutionBasicConfig();
	}

	@Test
	void testExtractSafeProjectConfigId_NullConfig() {
		String result = ProjectConfigIdUtil.extractSafeProjectConfigId(null);
		assertEquals("", result, "Should return empty string for null config");
	}

	@Test
	void testExtractSafeProjectConfigId_NullList() {
		config.setProjectBasicConfigIds(null);
		String result = ProjectConfigIdUtil.extractSafeProjectConfigId(config);
		assertEquals("", result, "Should return empty string for null list");
	}

	@Test
	void testExtractSafeProjectConfigId_EmptyList() {
		config.setProjectBasicConfigIds(Collections.emptyList());
		String result = ProjectConfigIdUtil.extractSafeProjectConfigId(config);
		assertEquals("", result, "Should return empty string for empty list");
	}

	@Test
	void testExtractSafeProjectConfigId_NullIdInList() {
		config.setProjectBasicConfigIds(Collections.singletonList(null));
		String result = ProjectConfigIdUtil.extractSafeProjectConfigId(config);
		assertEquals("", result, "Should return empty string for null ID value");
	}

	@Test
	void testExtractSafeProjectConfigId_EmptyIdInList() {
		config.setProjectBasicConfigIds(Collections.singletonList(""));
		String result = ProjectConfigIdUtil.extractSafeProjectConfigId(config);
		assertEquals("", result, "Should return empty string for empty ID value");
	}

	@Test
	void testExtractSafeProjectConfigId_UnsafeCharacters() {
		config.setProjectBasicConfigIds(Collections.singletonList("<script>alert(1)</script>"));
		String result = ProjectConfigIdUtil.extractSafeProjectConfigId(config);
		assertEquals("scriptalert1script", result, "Should remove unsafe characters");
	}

	@Test
	void testExtractSafeProjectConfigId_ValidId() {
		config.setProjectBasicConfigIds(Collections.singletonList("proj-123_ABC"));
		String result = ProjectConfigIdUtil.extractSafeProjectConfigId(config);
		assertEquals("proj-123_ABC", result, "Should return valid ID unchanged");
	}

	@Test
	void testSanitize_NullInput() {
		assertEquals("", ProjectConfigIdUtil.sanitize(null), "Sanitize should return empty string for null input");
	}

	@Test
	void testSanitize_EmptyInput() {
		assertEquals("", ProjectConfigIdUtil.sanitize(""), "Sanitize should return empty string for empty input");
	}

	@Test
	void testSanitize_UnsafeInput() {
		String result = ProjectConfigIdUtil.sanitize("<b>Test!@#</b>");
		assertEquals("bTestb", result, "Sanitize should remove unsafe characters");
	}

	@Test
	void testSanitize_ValidInput() {
		String result = ProjectConfigIdUtil.sanitize("project-XYZ_001");
		assertEquals("project-XYZ_001", result, "Sanitize should keep valid characters unchanged");
	}

	@Test
	void testExtractSafeProjectConfigId_MultipleIds_UsesFirstOnly() {
		config.setProjectBasicConfigIds(List.of("firstID", "secondID"));
		String result = ProjectConfigIdUtil.extractSafeProjectConfigId(config);
		assertEquals("firstID", result, "Should use the first ID from the list");
	}
}
