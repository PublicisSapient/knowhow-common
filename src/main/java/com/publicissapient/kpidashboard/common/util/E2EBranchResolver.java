package com.publicissapient.kpidashboard.common.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
		return resolveAndPersistInternal(fieldMapping, projectConfigId, FieldMapping::getE2eTestBranchKPI218,
				FieldMapping::setE2eTestBranchKPI218, "KPI218", "e2eTestBranchKPI218");
	}

	public Set<String> resolveAndPersistKPI219(FieldMapping fieldMapping, ObjectId projectConfigId) {
		return resolveAndPersistInternal(fieldMapping, projectConfigId, FieldMapping::getE2eTestBranchKPI219,
				FieldMapping::setE2eTestBranchKPI219, "KPI219", "e2eTestBranchKPI219");
	}

	private Set<String> resolveAndPersistInternal(FieldMapping fieldMapping, ObjectId projectConfigId,
			Function<FieldMapping, List<String>> getter, BiConsumer<FieldMapping, List<String>> setter, String kpiId,
			String fieldName) {
		if (fieldMapping == null) {
			return Set.of();
		}

		List<String> configured = getter.apply(fieldMapping);
		if (configured != null && !configured.isEmpty()) {
			return new LinkedHashSet<>(configured);
		}

		Set<String> discovered = discoverScmBranches(projectConfigId);
		if (!discovered.isEmpty()) {
			setter.accept(fieldMapping, new ArrayList<>(discovered));
			fieldMappingRepository.save(fieldMapping);
			log.info("{}: auto-populated {}={} from SCM connections for project {}", kpiId, fieldName, discovered,
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
