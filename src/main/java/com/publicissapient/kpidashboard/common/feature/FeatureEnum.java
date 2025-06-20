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
package com.publicissapient.kpidashboard.common.feature;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

/**
 * @author purgupta2
 */
public enum FeatureEnum implements Feature {
	@EnabledByDefault
	@Label("Custom-api Daily Standup")
	DAILY_STANDUP,

	@EnabledByDefault
	@Label("Google Analytics")
	GOOGLE_ANALYTICS,

	@Label("Recommendations")
	RECOMMENDATIONS,

	@EnabledByDefault
	@Label("New UI")
	NEW_UI_SWITCH,

	@EnabledByDefault
	@Label("Rally")
	RALLY;

	public boolean isActive() {
		return FeatureContext.getFeatureManager().isActive(this);
	}
}
