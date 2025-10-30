package com.publicissapient.kpidashboard.common.repository.scm;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.publicissapient.kpidashboard.common.model.scm.ScmRepos;

@Repository
public interface ScmReposRepository extends MongoRepository<ScmRepos, ObjectId> {

	List<ScmRepos> findAllByConnectionId(ObjectId connectionId);

	Optional<ScmRepos> findByConnectionIdAndRepositoryName(ObjectId connectionId, String repositoryName);
}
