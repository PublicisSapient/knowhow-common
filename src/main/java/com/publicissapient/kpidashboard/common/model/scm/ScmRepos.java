package com.publicissapient.kpidashboard.common.model.scm;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.publicissapient.kpidashboard.common.model.generic.BasicModel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Document(collection = "scm_repository")
@Getter
@Setter
@Builder
public class ScmRepos extends BasicModel {

	private String url;
	private String repositoryName;
	private long lastUpdated;
	private ObjectId connectionId;
	private List<ScmBranch> branchList;
}
