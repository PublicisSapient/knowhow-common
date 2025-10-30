package com.publicissapient.kpidashboard.common.model.scm;

import org.springframework.data.mongodb.core.mapping.Document;

import com.publicissapient.kpidashboard.common.model.generic.BasicModel;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "scm_connection_trace_log")
public class ScmConnectionTraceLog extends BasicModel {

	private String connectionId;
	private boolean fetchSuccessful;
	private boolean errorInFetch;
	private long lastSyncTimeTimeStamp;
}
