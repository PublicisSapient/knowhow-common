package com.publicissapient.kpidashboard.common.repository.jira;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.publicissapient.kpidashboard.common.model.jira.StoryHygieneSprintResult;

@Repository
public interface StoryHygieneSprintResultRepository extends MongoRepository<StoryHygieneSprintResult, ObjectId> {

	Optional<StoryHygieneSprintResult> findByBasicProjectConfigIdAndSprintId(String basicProjectConfigId,
			String sprintId);

	List<StoryHygieneSprintResult> findByBasicProjectConfigIdAndSprintIdIn(String basicProjectConfigId,
			List<String> sprintIds);
}
