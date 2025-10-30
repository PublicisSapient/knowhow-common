package com.publicissapient.kpidashboard.common.repository.scm;

import com.publicissapient.kpidashboard.common.model.scm.ScmConnectionTraceLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ScmConnectionTraceLogRepository extends MongoRepository<ScmConnectionTraceLog, ObjectId> {
    Optional<ScmConnectionTraceLog> findByConnectionId(String connectionId);
}
