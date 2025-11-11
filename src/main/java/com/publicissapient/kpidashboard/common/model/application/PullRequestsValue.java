package com.publicissapient.kpidashboard.common.model.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PullRequestsValue implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private String size;
    private String prUrl;
    private transient Map<String, Object> hoverValue;
}
