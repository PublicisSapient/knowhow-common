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

package com.publicissapient.kpidashboard.common.repository.tracelog;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.publicissapient.kpidashboard.common.model.ProcessorExecutionTraceLog;

/**
 * @author anisingh4
 */
@Repository
public interface ProcessorExecutionTraceLogRepository extends MongoRepository<ProcessorExecutionTraceLog, ObjectId> {

	Optional<ProcessorExecutionTraceLog> findByProcessorNameAndBasicProjectConfigId(String processorName,
			String basicProjectConfigId);

	void deleteByBasicProjectConfigId(String basicProjectConfigId);

	void deleteByBasicProjectConfigIdAndProcessorName(String basicProjectConfigId, String toolName);

	Optional<ProcessorExecutionTraceLog> findByProcessorNameAndBasicProjectConfigIdAndBoardId(String toolName,
			String basicProjectConfigId, String boardId);

	List<ProcessorExecutionTraceLog> findByProcessorNameAndBasicProjectConfigIdIn(String toolName,
			List<String> basicProjectConfigIdList);

	List<ProcessorExecutionTraceLog> findByProcessorName(String processorName);

	List<ProcessorExecutionTraceLog> findByBasicProjectConfigId(String basicProjectConfigId);

	Optional<ProcessorExecutionTraceLog> findByProcessorNameAndBasicProjectConfigIdAndProgressStatsTrue(
			String processorName, String basicProjectConfigId);

	List<ProcessorExecutionTraceLog> findByProcessorNameAndBasicProjectConfigIdAndProgressStatsFalse(String processorName,
			String basicProjectConfigId);
}
