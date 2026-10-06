/*
 * Copyright 2014 CapitalOne, LLC.
 * Further development Copyright 2022 Sapient Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.publicissapient.kpidashboard.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import com.publicissapient.kpidashboard.common.model.application.FieldMapping;

/**
 * Covers the selective backfill used by the processors to top up an already
 * configured mapping.
 */
class FieldMappingHelperTest {

	private static final String ACCEPTANCE_CRITERIA = "jiraAcceptanceCriteriaCustomField";
	private static final String STORY_POINTS = "jiraStoryPointsCustomField";

	@Test
	void mergeUnsetFieldsFillsOnlyTheRequestedField() {
		FieldMapping db = new FieldMapping();
		db.setSprintName("customfield_12700");
		FieldMapping source = new FieldMapping();
		source.setJiraAcceptanceCriteriaCustomField("customfield_11101");
		source.setSprintName("customfield_99999");
		source.setEpicLink("customfield_10002");

		List<String> updated = FieldMappingHelper.mergeUnsetFields(db, source, List.of(ACCEPTANCE_CRITERIA));

		assertEquals(List.of(ACCEPTANCE_CRITERIA), updated);
		assertEquals("customfield_11101", db.getJiraAcceptanceCriteriaCustomField());
		// outside the requested set, so it is not touched even though the db value is
		// empty
		assertNull(db.getEpicLink());
		assertEquals("customfield_12700", db.getSprintName());
	}

	@Test
	void mergeUnsetFieldsNeverOverwritesAConfiguredValue() {
		FieldMapping db = new FieldMapping();
		db.setJiraAcceptanceCriteriaCustomField("customfield_55555");
		FieldMapping source = new FieldMapping();
		source.setJiraAcceptanceCriteriaCustomField("customfield_11101");

		List<String> updated = FieldMappingHelper.mergeUnsetFields(db, source, List.of(ACCEPTANCE_CRITERIA));

		assertTrue(updated.isEmpty());
		assertEquals("customfield_55555", db.getJiraAcceptanceCriteriaCustomField());
	}

	@Test
	void mergeUnsetFieldsTreatsABlankStoredValueAsUnset() {
		FieldMapping db = new FieldMapping();
		// this is what a mapping saved from a form with the input left empty looks like
		db.setJiraStoryPointsCustomField("");
		FieldMapping source = new FieldMapping();
		source.setJiraStoryPointsCustomField("customfield_20803");

		List<String> updated = FieldMappingHelper.mergeUnsetFields(db, source, List.of(STORY_POINTS));

		assertEquals(List.of(STORY_POINTS), updated);
		assertEquals("customfield_20803", db.getJiraStoryPointsCustomField());
	}

	@Test
	void mergeUnsetFieldsSkipsABlankDiscoveredValue() {
		FieldMapping db = new FieldMapping();
		FieldMapping source = new FieldMapping();
		source.setJiraAcceptanceCriteriaCustomField("   ");

		List<String> updated = FieldMappingHelper.mergeUnsetFields(db, source, List.of(ACCEPTANCE_CRITERIA));

		assertTrue(updated.isEmpty());
		assertNull(db.getJiraAcceptanceCriteriaCustomField());
	}

	@Test
	void mergeUnsetFieldsLeavesIdsAlone() {
		ObjectId storedId = new ObjectId();
		FieldMapping db = new FieldMapping();
		FieldMapping source = new FieldMapping();
		source.setBasicProjectConfigId(storedId);

		List<String> updated = FieldMappingHelper.mergeUnsetFields(db, source, List.of("basicProjectConfigId"));

		assertTrue(updated.isEmpty());
		assertNull(db.getBasicProjectConfigId());
	}

	@Test
	void mergeUnsetFieldsSkipsAnUnknownFieldInsteadOfFailing() {
		FieldMapping db = new FieldMapping();
		FieldMapping source = new FieldMapping();
		source.setJiraAcceptanceCriteriaCustomField("customfield_11101");

		List<String> updated = FieldMappingHelper.mergeUnsetFields(db, source,
				Arrays.asList("fieldFromAnotherRelease", null, "  ", ACCEPTANCE_CRITERIA));

		assertEquals(List.of(ACCEPTANCE_CRITERIA), updated);
		assertEquals("customfield_11101", db.getJiraAcceptanceCriteriaCustomField());
	}

	@Test
	void mergeUnsetFieldsHandlesMissingInput() {
		FieldMapping mapping = new FieldMapping();
		assertTrue(FieldMappingHelper.mergeUnsetFields(null, mapping, List.of(ACCEPTANCE_CRITERIA)).isEmpty());
		assertTrue(FieldMappingHelper.mergeUnsetFields(mapping, null, List.of(ACCEPTANCE_CRITERIA)).isEmpty());
		assertTrue(FieldMappingHelper.mergeUnsetFields(mapping, mapping, null).isEmpty());
		assertTrue(FieldMappingHelper.mergeUnsetFields(mapping, mapping, Collections.emptyList()).isEmpty());
	}

	@Test
	void resolveStringFieldNamesMatchesExactlyThenIgnoringCase() {
		Map<String, String> resolved = FieldMappingHelper
				.resolveStringFieldNames(Arrays.asList(ACCEPTANCE_CRITERIA, "rootcause", "epicLink"));

		assertEquals(ACCEPTANCE_CRITERIA, resolved.get(ACCEPTANCE_CRITERIA));
		assertEquals("rootCause", resolved.get("rootcause"));
		assertEquals("epicLink", resolved.get("epicLink"));
	}

	@Test
	void resolveStringFieldNamesIgnoresKeysThatAreNotAStringProperty() {
		Map<String, String> resolved = FieldMappingHelper.resolveStringFieldNames(Arrays.asList("sprint", "costOfDelay",
				"jiradefecttype", "jiraIssueTypeNames", "basicProjectConfigId", null, "  "));

		// legacy identifier types keep their explicit setters, and a non String
		// property must never
		// be fed with a single custom field id
		assertTrue(resolved.isEmpty(), "unexpected matches: " + resolved);
	}

	@Test
	void resolveStringFieldNamesHandlesMissingInput() {
		assertTrue(FieldMappingHelper.resolveStringFieldNames(null).isEmpty());
		assertTrue(FieldMappingHelper.resolveStringFieldNames(Collections.emptyList()).isEmpty());
	}

	@Test
	void isUnsetRecognisesEmptyValues() {
		assertTrue(FieldMappingHelper.isUnset(null));
		assertTrue(FieldMappingHelper.isUnset(""));
		assertTrue(FieldMappingHelper.isUnset("   "));
		assertTrue(FieldMappingHelper.isUnset(Collections.emptyList()));
		assertTrue(FieldMappingHelper.isUnset(Collections.emptyMap()));
		assertTrue(FieldMappingHelper.isUnset(new String[0]));

		assertFalse(FieldMappingHelper.isUnset("customfield_11101"));
		assertFalse(FieldMappingHelper.isUnset(List.of("Story")));
		assertFalse(FieldMappingHelper.isUnset(Map.of("k", "v")));
		assertFalse(FieldMappingHelper.isUnset(new String[]{"Story"}));
		assertFalse(FieldMappingHelper.isUnset(Boolean.FALSE));
	}

	@Test
	void mergeIntoTargetStillFillsOnlyTheEmptyFields() {
		FieldMapping db = new FieldMapping();
		db.setSprintName("customfield_12700");
		db.setJiradefecttype(Collections.emptyList());
		FieldMapping source = new FieldMapping();
		source.setSprintName("customfield_99999");
		source.setEpicLink("customfield_10002");
		source.setJiradefecttype(List.of("Defect"));

		FieldMappingHelper.mergeIntoTarget(db, source);

		assertEquals("customfield_12700", db.getSprintName());
		assertEquals("customfield_10002", db.getEpicLink());
		assertEquals(List.of("Defect"), db.getJiradefecttype());
	}
}
