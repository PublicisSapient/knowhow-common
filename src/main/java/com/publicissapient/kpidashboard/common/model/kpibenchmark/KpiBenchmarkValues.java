package com.publicissapient.kpidashboard.common.model.kpibenchmark;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "kpi_benchmark_values")
public class KpiBenchmarkValues {

    @Indexed(name = "kpiId", unique = true)
    private String kpiId;
    private long lastUpdatedTimestamp;
    List<BenchmarkPercentiles> filterWiseBenchmarkValues;

}
