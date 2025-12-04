/*
 *  Copyright 2024 Sapient Corporation
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

package com.publicissapient.kpidashboard.common.service.recommendation;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.publicissapient.kpidashboard.common.constant.PromptKeys;
import com.publicissapient.kpidashboard.common.model.application.PromptDetails;
import com.publicissapient.kpidashboard.common.model.recommendation.batch.Persona;
import com.publicissapient.kpidashboard.common.repository.application.PromptDetailsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Service for prompts. */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromptService {

	public static final String KPI_CORRELATION_REPORT_PLACEHOLDER = "KPI_CORRELATION_REPORT_PLACEHOLDER";
	public static final String KPI_DATA_BY_PROJECT_PLACEHOLDER = "KPI_DATA_BY_PROJECT_PLACEHOLDER";
	public static final String PERSONA_PLACEHOLDER = "Persona_PLACEHOLDER";

	private final PromptDetailsRepository promptDetailsRepository;

	/**
	 * Retrieves prompt template from MongoDB by key.
	 *
	 * @param key
	 *          Prompt key constant
	 * @return PromptDetails object
	 * @throws IllegalArgumentException
	 *           if prompt not found
	 */
	public PromptDetails getPromptDetails(String key) {
		PromptDetails promptDetails = promptDetailsRepository.findByKey(key);
		if (promptDetails == null) {
			throw new IllegalArgumentException("Prompt not found for key: " + key);
		}
		return promptDetails;
	}

	/**
	 * Generates batch recommendation prompt with KPI data and persona.
	 *
	 * @param kpiDataByProject
	 *          Map of KPI data by project
	 * @param persona
	 *          User persona
	 * @return Generated prompt string
	 */
	public String getKpiRecommendationPrompt(Map<String, Object> kpiDataByProject, Persona persona) {
		try {
			PromptDetails kpiCorrelationReport = getPromptDetails(PromptKeys.KPI_CORRELATION_ANALYSIS_REPORT);
			PromptDetails batchRecommendationPrompt = getPromptDetails(PromptKeys.BATCH_RECOMMENDATION_PROMPT);

			return batchRecommendationPrompt.toString()
					.replace(KPI_CORRELATION_REPORT_PLACEHOLDER, kpiCorrelationReport.toString())
					.replace(KPI_DATA_BY_PROJECT_PLACEHOLDER, kpiDataByProject.toString())
					.replace(PERSONA_PLACEHOLDER, persona.getDisplayName());
		} catch (Exception e) {
			log.error("Error building KPI recommendation prompt: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to generate prompt", e);
		}
	}
}
