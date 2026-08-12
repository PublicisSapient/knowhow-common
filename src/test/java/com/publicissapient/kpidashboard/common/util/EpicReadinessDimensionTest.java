/*
 *  Copyright 2024 <Sapient Corporation>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the
 *  License.
 */

package com.publicissapient.kpidashboard.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.publicissapient.kpidashboard.common.model.application.dto.CycleTimeGroup;

/**
 * Tests the fixed Epic readiness dimensions and the prompt section built from
 * the project's configured rules.
 */
class EpicReadinessDimensionTest {

	// ---------------------------------------------------------------------
	// The fixed dimension set
	// ---------------------------------------------------------------------

	@Test
	void displayNames_areTheFiveFixedColumnsInOrder() {
		assertEquals(
				List.of("Business Clarity", "Scope Definition", "Solution Readiness", "Dependency Readiness", "Risk Readiness"),
				EpicReadinessDimension.displayNames());
	}

	@Test
	void from_matchesExactNamesIgnoringCaseAndPunctuation() {
		assertEquals(EpicReadinessDimension.BUSINESS_CLARITY,
				EpicReadinessDimension.from("business clarity").orElseThrow());
		assertEquals(EpicReadinessDimension.SCOPE_DEFINITION,
				EpicReadinessDimension.from("Scope-Definition").orElseThrow());
		assertEquals(EpicReadinessDimension.RISK_READINESS, EpicReadinessDimension.from("RISK READINESS").orElseThrow());
	}

	@Test
	void from_toleratesTheDuplicateRuleSuffix() {
		assertEquals(EpicReadinessDimension.BUSINESS_CLARITY,
				EpicReadinessDimension.from("Business Clarity (2)").orElseThrow());
	}

	@Test
	void from_matchesConfiguredAliasesAndFreeWording() {
		assertEquals(EpicReadinessDimension.BUSINESS_CLARITY, EpicReadinessDimension.from("Business Value").orElseThrow());
		assertEquals(EpicReadinessDimension.SCOPE_DEFINITION,
				EpicReadinessDimension.from("Acceptance Criteria").orElseThrow());
		assertEquals(EpicReadinessDimension.SOLUTION_READINESS,
				EpicReadinessDimension.from("Technical readiness").orElseThrow());
		assertEquals(EpicReadinessDimension.DEPENDENCY_READINESS,
				EpicReadinessDimension.from("External dependencies").orElseThrow());
		assertEquals(EpicReadinessDimension.RISK_READINESS,
				EpicReadinessDimension.from("Risks & assumptions").orElseThrow());
	}

	@Test
	void from_returnsEmptyForSomethingThatNamesNoDimension() {
		assertTrue(EpicReadinessDimension.from("Delivery Readiness").isEmpty());
		assertTrue(EpicReadinessDimension.from("  ").isEmpty());
		assertTrue(EpicReadinessDimension.from(null).isEmpty());
	}

	@Test
	void canonicalName_snapsKnownNamesAndKeepsUnknownOnes() {
		assertEquals("Business Clarity", EpicReadinessDimension.canonicalName("business-clarity"));
		assertEquals("Delivery Readiness", EpicReadinessDimension.canonicalName(" Delivery Readiness "));
	}

	@Test
	void allDefaultEvidenceFields_areDeduplicated() {
		List<String> fields = EpicReadinessDimension.allDefaultEvidenceFields();

		assertEquals(fields.size(), fields.stream().distinct().count());
		assertTrue(fields.contains("description"));
	}

	// ---------------------------------------------------------------------
	// The rendered prompt section
	// ---------------------------------------------------------------------

	@Test
	void buildEpicReadinessRules_withNoFieldMapping_pointsAtTheDefaultCriteriaOfThePrompt() {
		String rules = HygienePromptBuilder.buildEpicReadinessRules(null);

		EpicReadinessDimension.displayNames().forEach(dimension -> assertTrue(rules.contains("dimension: " + dimension)));
		assertTrue(rules.contains("criteriaSource: DEFAULT (project configured no rule for this dimension)"));
		// the criteria themselves live in the epic-hygiene prompt, never in the code
		assertTrue(rules.contains("Apply the DEFAULT CRITERIA defined for this dimension in your instructions."));
		assertTrue(rules.contains("evidenceFields: description, summary, name"));
		assertFalse(rules.contains("Additional configured checks"));
	}

	@Test
	void buildEpicReadinessRules_usesTheConfiguredRuleForTheDimensionItNames() {
		String rules = HygienePromptBuilder.buildEpicReadinessRules(
				List.of(rule("Business Clarity", "description", 5, "Problem statement and measurable outcome required")));

		assertTrue(rules.contains("criteriaSource: PROJECT FIELD MAPPING"));
		assertTrue(rules.contains("(field: description, weight: 5) Problem statement and measurable outcome required"));
		// the configured weight becomes the weight of that dimension
		assertTrue(rules.contains("dimension: Business Clarity\n  weight: 5"));
		// the other four still fall back to the prompt's default criteria
		assertTrue(rules.contains("criteriaSource: DEFAULT (project configured no rule for this dimension)"));
	}

	@Test
	void buildEpicReadinessRules_sumsTheWeightsOfSeveralRulesOnTheSameDimension() {
		String rules = HygienePromptBuilder.buildEpicReadinessRules(
				List.of(rule("Scope Definition", "description", 3, "In and out of scope must be listed"),
						rule("Scope Definition", "labels", 2, "Child stories must be linked")));

		assertTrue(rules.contains("dimension: Scope Definition\n  weight: 5"));
		assertTrue(rules.contains("evidenceFields: description, labels"));
	}

	@Test
	void buildEpicReadinessRules_honoursTheLegacyWeightPrefix() {
		String rules = HygienePromptBuilder
				.buildEpicReadinessRules(List.of(rule("Risk Readiness", "description", null, "[4]: Risks with mitigations")));

		assertTrue(rules.contains("(field: description, weight: 4) Risks with mitigations"));
		assertTrue(rules.contains("dimension: Risk Readiness\n  weight: 4"));
	}

	@Test
	void buildEpicReadinessRules_keepsRulesThatNameNoDimensionInATrailingSection() {
		String rules = HygienePromptBuilder.buildEpicReadinessRules(
				List.of(rule("Story linkage", "parentStoryId", 2, "Epic must be broken into stories")));

		assertTrue(rules.contains("=== Additional configured checks ==="));
		assertTrue(rules.contains("configuredName: Story linkage"));
		assertTrue(rules.contains("field: parentStoryId"));
		// and the five dimensions are still fully rendered with their defaults
		EpicReadinessDimension.displayNames().forEach(dimension -> assertTrue(rules.contains("dimension: " + dimension)));
	}

	@Test
	void buildEpicReadinessRules_ignoresEntriesWithoutAPrompt() {
		String rules = HygienePromptBuilder.buildEpicReadinessRules(
				List.of(rule("Business Clarity", "description", 5, null), rule("Business Clarity", "description", 5, "   ")));

		assertTrue(rules.contains("criteriaSource: DEFAULT (project configured no rule for this dimension)"));
	}

	private CycleTimeGroup rule(String label, String fieldName, Integer weightage, String prompt) {
		CycleTimeGroup group = new CycleTimeGroup();
		group.setLabel(label);
		group.setFieldName(fieldName);
		group.setWeightage(weightage);
		group.setPrompt(prompt);
		return group;
	}
}
