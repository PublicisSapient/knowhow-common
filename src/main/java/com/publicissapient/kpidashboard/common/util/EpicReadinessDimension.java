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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;

/**
 * The FIXED readiness dimensions every Epic is graded on by the Epic Hygiene
 * KPI (kpi312).
 *
 * <p>
 * The Excel drill-down carries exactly one column per dimension plus the
 * derived <em>Readiness Score</em>, so the dimension list is deliberately
 * closed - a project can no longer invent its own column set. What a project
 * CAN configure ({@code jiraFieldsSelectionKPI312}) is <b>how</b> each
 * dimension is scored: every configured entry supplies the Jira field carrying
 * the evidence plus the rule/prompt to check. Entries are matched to a
 * dimension by their label (or an alias of it).
 *
 * <p>
 * This enum holds NO prompt text: the criteria applied when a project
 * configured nothing for a dimension live in the {@code epic-hygiene} prompt in
 * the {@code prompt_details} collection, like every other instruction given to
 * the LLM. Only the dimension identity - name, aliases and the Jira fields to
 * read the fallback evidence from - is modelled here.
 */
public enum EpicReadinessDimension {
	/** WHY the Epic exists - problem, users, value, measurable outcome. */
	BUSINESS_CLARITY("Business Clarity", List.of("description", "summary", "name"),
			Set.of("business", "business value", "business context", "business goal", "business outcome", "problem statement",
					"value clarity", "objective clarity")),

	/** WHAT is being delivered - deliverables, boundaries, acceptance. */
	SCOPE_DEFINITION("Scope Definition", List.of("description", "labels", "summary"),
			Set.of("scope", "scope clarity", "requirement", "requirements", "requirement clarity", "acceptance criteria",
					"definition of done", "deliverables", "story breakdown")),

	/** HOW it will be built - approach, design, NFRs. */
	SOLUTION_READINESS("Solution Readiness", List.of("description", "summary"), Set.of("solution", "solution clarity",
			"technical readiness", "technical clarity", "design", "design readiness", "architecture", "approach")),

	/** WHAT IT WAITS ON - external teams, systems, blocking work. */
	DEPENDENCY_READINESS("Dependency Readiness", List.of("description", "labels", "summary"),
			Set.of("dependency", "dependencies", "dependency management", "integration readiness", "external dependency",
					"upstream", "downstream")),

	/** WHAT COULD GO WRONG - risks, assumptions, constraints, blockers. */
	RISK_READINESS("Risk Readiness", List.of("description", "priority", "dueDate", "status"),
			Set.of("risk", "risks", "risk management", "risks and assumptions", "assumption", "assumptions", "constraints",
					"blocker", "blockers", "compliance"));

	/** Column header and the value copied into {@code results[].dimension}. */
	@Getter
	private final String displayName;

	/**
	 * Jira fields inspected when the project configured no rule for this dimension.
	 */
	@Getter
	private final List<String> defaultEvidenceFields;

	/** Lower-cased spellings a configured rule label may use for this dimension. */
	private final Set<String> aliases;

	EpicReadinessDimension(String displayName, List<String> defaultEvidenceFields, Set<String> aliases) {
		this.displayName = displayName;
		this.defaultEvidenceFields = defaultEvidenceFields;
		this.aliases = aliases;
	}

	/** The dimension column headers, in the fixed order they are reported in. */
	public static List<String> displayNames() {
		List<String> names = new ArrayList<>();
		for (EpicReadinessDimension dimension : values()) {
			names.add(dimension.getDisplayName());
		}
		return names;
	}

	/**
	 * Every Jira field any dimension may fall back to, de-duplicated and in
	 * declaration order. Fetched for every Epic so a project without field mapping
	 * still hands the LLM some evidence to grade.
	 */
	public static List<String> allDefaultEvidenceFields() {
		LinkedHashSet<String> fields = new LinkedHashSet<>();
		for (EpicReadinessDimension dimension : values()) {
			fields.addAll(dimension.getDefaultEvidenceFields());
		}
		return new ArrayList<>(fields);
	}

	/**
	 * Resolves a free-text dimension / rule label onto one of the fixed dimensions.
	 *
	 * <p>
	 * Matching is punctuation and case insensitive and tolerates the {@code (n)}
	 * suffix the rule renderer appends when several rules share a field, so
	 * {@code "business-clarity"}, {@code
	 * "Business Clarity (2)"} and {@code "Epic business value"} all resolve to
	 * {@link #BUSINESS_CLARITY}.
	 *
	 * @param label
	 *          the configured rule label or the dimension name returned by the LLM
	 * @return the matching dimension, or empty when the label names none of them
	 */
	public static Optional<EpicReadinessDimension> from(String label) {
		if (label == null || label.isBlank()) {
			return Optional.empty();
		}
		String normalised = normalise(label.replaceAll("\\s*\\(\\s*\\d+\\s*\\)\\s*$", ""));
		if (normalised.isEmpty()) {
			return Optional.empty();
		}
		// Exact hit on the display name or on a declared alias wins for every
		// dimension before any fuzzy containment check is attempted.
		for (EpicReadinessDimension dimension : values()) {
			if (dimension.matchesExactly(normalised)) {
				return Optional.of(dimension);
			}
		}
		for (EpicReadinessDimension dimension : values()) {
			if (dimension.matchesLoosely(normalised)) {
				return Optional.of(dimension);
			}
		}
		return Optional.empty();
	}

	/**
	 * Returns the canonical display name of {@code label} when it names one of the
	 * fixed dimensions, otherwise the trimmed label itself.
	 */
	public static String canonicalName(String label) {
		return from(label).map(EpicReadinessDimension::getDisplayName).orElseGet(() -> label == null ? null : label.trim());
	}

	private boolean matchesExactly(String normalisedLabel) {
		if (normalisedLabel.equals(normalise(displayName))) {
			return true;
		}
		return aliases.stream().map(EpicReadinessDimension::normalise).anyMatch(normalisedLabel::equals);
	}

	private boolean matchesLoosely(String normalisedLabel) {
		if (normalisedLabel.contains(normalise(displayName))) {
			return true;
		}
		return aliases.stream().map(EpicReadinessDimension::normalise).anyMatch(normalisedLabel::contains);
	}

	private static String normalise(String value) {
		return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
	}
}
