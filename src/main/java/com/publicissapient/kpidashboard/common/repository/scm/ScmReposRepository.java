package com.publicissapient.kpidashboard.common.repository.scm;

import com.publicissapient.kpidashboard.common.model.scm.ScmRepos;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScmReposRepository extends MongoRepository<ScmRepos, ObjectId> {

    List<ScmRepos> findAllByConnectionId(ObjectId connectionId);

    Optional<ScmRepos> findByConnectionIdAndRepositoryName(ObjectId connectionId, String repositoryName);

}
