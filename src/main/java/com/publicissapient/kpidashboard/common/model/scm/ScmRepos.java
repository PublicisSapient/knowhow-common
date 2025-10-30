package com.publicissapient.kpidashboard.common.model.scm;


import com.publicissapient.kpidashboard.common.model.generic.BasicModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
