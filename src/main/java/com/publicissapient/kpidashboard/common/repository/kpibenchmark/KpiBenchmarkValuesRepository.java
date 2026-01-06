package com.publicissapient.kpidashboard.common.repository.kpibenchmark;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.publicissapient.kpidashboard.common.model.kpibenchmark.KpiBenchmarkValues;

public interface KpiBenchmarkValuesRepository extends MongoRepository<KpiBenchmarkValues, String> {

}
