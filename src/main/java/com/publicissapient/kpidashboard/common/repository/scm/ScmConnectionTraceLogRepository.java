package com.publicissapient.kpidashboard.common.repository.scm;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.publicissapient.kpidashboard.common.model.scm.ScmConnectionTraceLog;

public interface ScmConnectionTraceLogRepository extends MongoRepository<ScmConnectionTraceLog, ObjectId> {
	Optional<ScmConnectionTraceLog> findByConnectionId(String connectionId);
}
