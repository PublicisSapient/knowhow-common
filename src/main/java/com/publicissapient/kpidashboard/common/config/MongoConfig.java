/*
 *   Copyright 2014 CapitalOne, LLC.
 *   Further development Copyright 2022 Sapient Corporation.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.publicissapient.kpidashboard.common.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.publicissapient.kpidashboard.common.converter.DateToJodaDateTimeConverter;
import com.publicissapient.kpidashboard.common.converter.ZonedDateTimeReadConverter;
import com.publicissapient.kpidashboard.common.converter.ZonedDateTimeWriteConverter;

/** MongoDB configuration for custom type conversions. */
@Configuration
public class MongoConfig {

	/**
	 * Registers custom converters for MongoDB type conversions.
	 *
	 * @return configured MongoCustomConversions
	 */
	@Bean
	public MongoCustomConversions customConversions() {
		return new MongoCustomConversions(List.of(new DateToJodaDateTimeConverter(), new ZonedDateTimeReadConverter(),
				new ZonedDateTimeWriteConverter()));
	}
}
