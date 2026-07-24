package com.publicissapient.kpidashboard.common.util;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.publicissapient.kpidashboard.common.constant.ProcessorConstants;
import com.publicissapient.kpidashboard.common.model.application.FieldMapping;
import com.publicissapient.kpidashboard.common.processortool.service.ProcessorToolConnectionService;
import com.publicissapient.kpidashboard.common.repository.application.FieldMappingRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Resolves the set of branches to monitor for KPI218 (E2E Test Pass Rate).
 *
 * <p>
 * When {@code e2eTestBranchKPI218} is blank, discovers branches from the
 * project's SCM tool connections, persists them back to field mapping so the
 * user can see what is being used, and returns the set. When the user has
 * explicitly configured the field, that value is used as-is.
 */
@Component
@Slf4j
public class E2EBranchResolver {

	private static final List<String> SCM_TOOLS = List.of(ProcessorConstants.GITHUB, ProcessorConstants.BITBUCKET,
			ProcessorConstants.GITLAB, ProcessorConstants.AZUREREPO);

	@Autowired
	private ProcessorToolConnectionService processorToolConnectionService;

	@Autowired
	private FieldMappingRepository fieldMappingRepository;

	/**
	 * Returns the set of branches to use for E2E test filtering for the given
	 * project.
	 *
	 * <p>
	 * If {@code e2eTestBranchKPI218} is already set, splits it by comma and returns
	 * it unchanged. If blank, discovers branches from the project's SCM
	 * connections, writes them back to the field mapping for visibility, and
	 * returns the discovered set. Returns an empty set when the field is blank and
	 * no SCM branches are found — callers should skip E2E processing in that case.
	 *
	 * @param fieldMapping
	 *          the field mapping for the project (may be null)
	 * @param projectConfigId
	 *          the project's basicProjectConfigId
	 * @return ordered set of branch names; empty means no E2E processing should
	 *         occur
	 */
	public Set<String> resolveAndPersist(FieldMapping fieldMapping, ObjectId projectConfigId) {
		if (fieldMapping == null) {
			return Set.of();
		}

		String configured = StringUtils.trimToEmpty(fieldMapping.getE2eTestBranchKPI218());
		if (StringUtils.isNotBlank(configured)) {
			return Arrays.stream(configured.split(",")).map(String::trim).filter(StringUtils::isNotBlank)
					.collect(Collectors.toCollection(LinkedHashSet::new));
		}

		Set<String> discovered = discoverScmBranches(projectConfigId);
		if (!discovered.isEmpty()) {
			String joined = String.join(",", discovered);
			fieldMapping.setE2eTestBranchKPI218(joined);
			fieldMappingRepository.save(fieldMapping);
			log.info("KPI218: auto-populated e2eTestBranchKPI218='{}' from SCM connections for project {}", joined,
					projectConfigId);
		}
		return discovered;
	}

	private Set<String> discoverScmBranches(ObjectId projectConfigId) {
		Set<String> branches = new LinkedHashSet<>();
		for (String tool : SCM_TOOLS) {
			processorToolConnectionService.findByToolAndBasicProjectConfigId(tool, projectConfigId).stream()
					.map(conn -> StringUtils.trimToEmpty(conn.getBranch())).filter(StringUtils::isNotBlank)
					.forEach(branches::add);
		}
		return branches;
	}
}
