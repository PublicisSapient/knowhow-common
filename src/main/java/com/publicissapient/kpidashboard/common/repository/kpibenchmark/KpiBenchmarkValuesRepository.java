package com.publicissapient.kpidashboard.common.repository.kpibenchmark;

import com.publicissapient.kpidashboard.common.model.kpibenchmark.KpiBenchmarkValues;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface KpiBenchmarkValuesRepository extends MongoRepository<KpiBenchmarkValues, String> {

    Optional<KpiBenchmarkValues> findByKpiId(String kpiId);

}
