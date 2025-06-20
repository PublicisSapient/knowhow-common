/*******************************************************************************
 * Copyright 2014 CapitalOne, LLC.
 * Further development Copyright 2022 Sapient Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package com.publicissapient.kpidashboard.common.model.application.dto; // NOPMD

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.publicissapient.kpidashboard.common.constant.CommonConstant;
import com.publicissapient.kpidashboard.common.model.application.AdditionalFilterConfig;
import com.publicissapient.kpidashboard.common.model.application.FieldMappingHistory;
import com.publicissapient.kpidashboard.common.model.application.LabelCount;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** The type Field mapping. Represents Jira field mapping values */
@SuppressWarnings("PMD.TooManyFields")
@Getter
@Setter
public class FieldMappingDTO extends FieldMappingHistory {

	private ObjectId projectToolConfigId;
	private ObjectId basicProjectConfigId;
	private String projectId;
	private String atmQueryEndpoint;
	private String sprintName;
	private String epicName;
	private List<String> jiradefecttype;
	private String epicLink;
	private List<String> jiraSubTaskDefectType;

	// defectPriority
	private List<String> defectPriority;
	private List<LabelCount> defectPriorityKPI135;
	private List<String> defectPriorityKPI35;
	private List<String> defectPriorityKPI14;
	private List<String> defectPriorityQAKPI111;
	private List<LabelCount> defectPriorityKPI82;
	private List<String> defectPriorityKPI133;
	private List<String> defectPriorityKPI34;

	private String[] jiraIssueTypeNames;
	private String[] jiraIssueTypeNamesAVR;
	private String storyFirstStatus;
	private String storyFirstStatusKPI148;
	private String[] linkDefectToStoryField;
	private String rootCause;
	private List<String> rootCauseValues;
	private String rootCauseIdentifier;
	private List<String> jiraStatusForDevelopment;
	private List<String> jiraStatusForDevelopmentAVR;
	private List<String> jiraStatusForDevelopmentKPI82;
	private List<String> jiraStatusForDevelopmentKPI135;
	private String jiraAtmProjectId;
	private String jiraAtmProjectKey;
	private List<String> jiraIssueEpicType;
	private List<String> jiraIssueRiskTypeKPI176;
	private List<String> jiraIssueDependencyTypeKPI176;

	private List<String> jiraStatusForQa;
	private List<String> jiraStatusForQaKPI135;
	private List<String> jiraStatusForQaKPI82;
	private List<String> jiraStatusForQaKPI148;
	// type of test cases
	private List<String> jiraDefectInjectionIssueType;
	private List<String> jiraDefectInjectionIssueTypeKPI14;
	private List<String> jiraDod;
	private List<String> jiraDodKPI152;
	private List<String> jiraDodKPI151;
	private List<String> jiraDodKPI14;
	private List<String> jiraDodQAKPI111;
	private List<String> jiraDodKPI37;
	private List<String> jiraDodKPI142;
	private List<String> jiraDodKPI144;
	private List<String> jiraDodKPI143;

	private String jiraDefectCreatedStatus;
	private String jiraDefectCreatedStatusKPI14;
	private List<String> jiraTechDebtIssueType;
	private String jiraTechDebtIdentification;
	private String jiraTechDebtCustomField;
	private List<String> jiraTechDebtValue;

	private String jiraDefectRejectionStatus;
	private String jiraDefectRejectionStatusKPI152;
	private List<String> jiraDefectRejectionStatusKPI151;
	private String jiraDefectRejectionStatusAVR;
	private String jiraDefectRejectionStatusKPI28;
	private String jiraDefectRejectionStatusKPI37;
	private String jiraDefectRejectionStatusKPI35;
	private String jiraDefectRejectionStatusKPI82;
	private String jiraDefectRejectionStatusKPI135;
	private String jiraDefectRejectionStatusKPI133;
	private String jiraDefectRejectionStatusRCAKPI36;
	private String jiraDefectRejectionStatusKPI14;
	private String jiraDefectRejectionStatusQAKPI111;
	private String jiraDefectRejectionStatusKPI34;
	private String jiraDefectRejectionStatusKPI191;
	private List<String> jiraDefectRemovalStatusKPI191;
	private List<String> resolutionTypeForRejectionKPI191;

	private String jiraBugRaisedByIdentification;
	private List<String> jiraBugRaisedByValue;

	private List<String> jiraDefectSeepageIssueType;
	private List<String> jiraIssueTypeKPI35;
	private String jiraBugRaisedByCustomField;
	private List<String> jiraDefectRemovalStatus;
	private List<String> jiraDefectRemovalStatusKPI34;
	private List<String> jiraDefectRemovalIssueType;
	// Added for Defect Reopen Rate KPI.
	private List<String> jiraDefectClosedStatus;
	private List<String> jiraDefectClosedStatusKPI137;

	private String jiraStoryPointsCustomField;
	// parent issue type for the test
	private List<String> jiraTestAutomationIssueType;
	// value of the automated test case Eg. Yes, Cannot Automate, No

	private List<String> jiraSprintVelocityIssueType;
	private List<String> jiraSprintVelocityIssueTypeKPI138;

	private List<String> jiraSprintCapacityIssueType;
	private List<String> jiraSprintCapacityIssueTypeKpi46;

	private List<String> jiraDefectRejectionlIssueType;

	private List<String> jiraDefectCountlIssueType;
	private List<String> jiraDefectCountlIssueTypeKPI28;
	private List<String> jiraDefectCountlIssueTypeKPI36;

	private List<String> jiraIssueDeliverdStatus;
	private List<String> jiraIssueDeliverdStatusKPI138;
	private List<String> jiraIssueDeliverdStatusAVR;
	private List<String> jiraIssueDeliverdStatusKPI126;
	private List<String> jiraIssueDeliverdStatusKPI82;
	private List<String> jiraIterationCompletionStatusKPI176;

	private String readyForDevelopmentStatus;
	private List<String> readyForDevelopmentStatusKPI138;

	private String jiraDor;

	private List<String> jiraIntakeToDorIssueType;
	private List<String> jiraIssueTypeKPI3;

	private List<String> jiraStoryIdentification;
	private List<String> jiraStoryIdentificationKPI129;
	private List<String> jiraStoryIdentificationKpi40;
	private List<String> jiraStoryCategoryKpi40;
	private List<String> jiraStoryIdentificationKPI164;

	private String jiraLiveStatus;
	private String jiraLiveStatusKPI152;
	private String jiraLiveStatusKPI151;
	private List<String> jiraLiveStatusKPI3;
	private String jiraLiveStatusKPI53;
	private String jiraLiveStatusKPI50;
	private String jiraLiveStatusKPI48;
	private String jiraLiveStatusKPI51;
	private String jiraLiveStatusKPI997;

	private List<String> ticketCountIssueType;
	private List<String> ticketCountIssueTypeKPI50;
	private List<String> ticketCountIssueTypeKPI48;
	private List<String> ticketCountIssueTypeKPI997;
	private List<String> ticketCountIssueTypeKPI54;
	private List<String> ticketCountIssueTypeKPI55;

	private List<String> kanbanRCACountIssueType;
	private List<String> kanbanRCACountIssueTypeKPI51;

	private List<String> jiraTicketVelocityIssueTypeKPI49;

	private List<String> ticketDeliveredStatusKPI49;

	private List<String> ticketReopenStatus;

	private List<String> kanbanJiraTechDebtIssueType;

	private List<String> jiraTicketResolvedStatus;
	private List<String> jiraTicketClosedStatus;
	private List<String> jiraTicketClosedStatusKPI48;
	private List<String> jiraTicketClosedStatusKPI50;
	private List<String> jiraTicketClosedStatusKPI51;
	private List<String> jiraTicketClosedStatusKPI53;
	private List<String> jiraTicketClosedStatusKPI54;
	private List<String> jiraTicketClosedStatusKPI55;
	private List<String> jiraTicketClosedStatusKPI997;

	private List<String> kanbanCycleTimeIssueType;
	private List<String> kanbanCycleTimeIssueTypeKPI53;
	private List<String> jiraTicketTriagedStatus;
	private List<String> jiraTicketTriagedStatusKPI53;
	private List<String> jiraTicketWipStatus;
	private List<String> jiraTicketRejectedStatus;
	private List<String> jiraTicketRejectedStatusKPI50;
	private List<String> jiraTicketRejectedStatusKPI151;
	private List<String> jiraTicketRejectedStatusKPI48;
	private List<String> jiraTicketRejectedStatusKPI997;

	private String jiraStatusMappingCustomField;
	private List<String> excludeStatusKpi129;

	private List<String> rootCauseValue;
	private List<String> excludeRCAFromFTPR; // test done
	private List<String> excludeRCAFromKPI163;
	private List<String> includeRCAForKPI82;
	private List<String> includeRCAForKPI135;
	private List<String> includeRCAForKPI14;
	private List<String> includeRCAForQAKPI111;
	private List<String> includeRCAForKPI133;
	private List<String> includeRCAForKPI35;
	private List<String> includeRCAForKPI34;

	private Boolean pickNewATMJIRADetails;

	private List<String> jiraDorToLiveIssueType;
	private List<String> jiraProductiveStatus;

	private List<String> jiraCommitmentReliabilityIssueType;

	private List<String> resolutionTypeForRejection;
	private List<String> resolutionTypeForRejectionAVR;
	private List<String> resolutionTypeForRejectionKPI28;
	private List<String> resolutionTypeForRejectionKPI37;
	private List<String> resolutionTypeForRejectionKPI35;
	private List<String> resolutionTypeForRejectionKPI82;
	private List<String> resolutionTypeForRejectionKPI135;
	private List<String> resolutionTypeForRejectionKPI133;
	private List<String> resolutionTypeForRejectionRCAKPI36;
	private List<String> resolutionTypeForRejectionKPI14;
	private List<String> resolutionTypeForRejectionQAKPI111;
	private List<String> resolutionTypeForRejectionKPI34;

	private List<String> jiraQADefectDensityIssueType;
	private List<String> jiraQAKPI111IssueType;
	private List<String> jiraItrQSIssueTypeKPI133;

	private List<String> jiraDefectDroppedStatus;

	// Epic custom Field mapping
	private String epicCostOfDelay;
	private String epicRiskReduction;
	private String epicUserBusinessValue;
	private String epicWsjf;
	private String epicTimeCriticality;
	private String epicJobSize;
	private String epicPlannedValue;
	private String epicAchievedValue;

	private String atmSubprojectField;

	// Squad Mapping
	private String squadIdentifier;
	private List<String> squadIdentMultiValue;
	private String squadIdentSingleValue;

	// Production Defect Mapping
	private String productionDefectCustomField;
	private String productionDefectIdentifier;
	private List<String> productionDefectValue;
	private String productionDefectComponentValue;

	// issue status to exclude missing worklogs
	private List<String> issueStatusToBeExcludedFromMissingWorklogs;

	// field for In Progress status
	private List<String> jiraStatusForInProgress;
	private List<String> jiraStatusForInProgressKPI148;
	private List<String> jiraStatusForInProgressKPI122;
	private List<String> jiraStatusForInProgressKPI145;
	private List<String> jiraStatusForInProgressKPI125;
	private List<String> jiraStatusForInProgressKPI128;
	private List<String> jiraStatusForInProgressKPI123;
	private List<String> jiraStatusForInProgressKPI119;

	@Builder.Default
	private String estimationCriteria = "Story Point";

	@Builder.Default
	private Double storyPointToHourMapping = 8D;

	@Builder.Default
	private Double workingHoursDayCPT = 6D;

	// additional filter config fields
	private List<AdditionalFilterConfig> additionalFilterConfig;

	// issue status to exclude missing worklogs
	private List<String> issueStatusExcluMissingWork;
	private List<String> issueStatusExcluMissingWorkKPI124;

	// issue On Hold status to exclude Closure possible
	private List<String> jiraOnHoldStatus;

	// field for FTPR
	private List<String> jiraFTPRStoryIdentification;
	private List<String> jiraKPI82StoryIdentification;
	private List<String> jiraKPI135StoryIdentification;

	// field for Wasting - wait status
	private List<String> jiraWaitStatus;
	private List<String> jiraWaitStatusKPI131;

	// field for Wasting - block status
	private List<String> jiraBlockedStatus;
	private List<String> jiraBlockedStatusKPI131;

	// field for Wasting - Include Blocked Status
	private String jiraIncludeBlockedStatus;
	private String jiraIncludeBlockedStatusKPI131;

	// for for JiraDueDate
	@Builder.Default
	private String jiraDueDateField = CommonConstant.DUE_DATE;
	private String jiraDueDateCustomField;
	private String jiraDevDueDateField = CommonConstant.DEV_DUE_DATE;
	private String jiraDevDueDateCustomField;
	private List<String> jiraDevDoneStatus;
	private List<String> jiraDevDoneStatusKPI119;
	private List<String> jiraDevDoneStatusKPI145;
	private List<String> jiraDevDoneStatusKPI128;

	// For DTS_21154 - field for Team refinement status
	private List<String> jiraRejectedInRefinement;
	private List<String> jiraRejectedInRefinementKPI139;

	// For DTS_21154 - field for Stakeholder refinement status
	private List<String> jiraAcceptedInRefinement;
	private List<String> jiraAcceptedInRefinementKPI139;

	// For DTS_21154 - field for Stakeholder refinement status
	private List<String> jiraReadyForRefinement;
	private List<String> jiraReadyForRefinementKPI139;

	private List<String> jiraFtprRejectStatus;
	private List<String> jiraFtprRejectStatusKPI135;
	private List<String> jiraFtprRejectStatusKPI82;

	private List<String> jiraIterationCompletionStatusCustomField;
	private List<String> jiraIterationCompletionStatusKPI135;
	private List<String> jiraIterationCompletionStatusKPI122;
	private List<String> jiraIterationCompletionStatusKPI75;
	private List<String> jiraIterationCompletionStatusKPI145;
	private List<String> jiraIterationCompletionStatusKpi72;
	private List<String> jiraIterationCompletionStatusKpi39;
	private List<String> jiraIterationCompletionStatusKpi5;
	private List<String> jiraIterationCompletionStatusKPI124;
	private List<String> jiraIterationCompletionStatusKPI123;
	private List<String> jiraIterationCompletionStatusKPI125;
	private List<String> jiraIterationCompletionStatusKPI120;
	private List<String> jiraIterationCompletionStatusKPI128;
	private List<String> jiraIterationCompletionStatusKPI134;
	private List<String> jiraIterationCompletionStatusKPI133;
	private List<String> jiraIterationCompletionStatusKPI119;
	private List<String> jiraIterationCompletionStatusKPI131;
	private List<String> jiraIterationCompletionStatusKPI138;

	private List<String> jiraIterationCompletionTypeCustomField;
	private List<String> jiraIterationIssuetypeKPI122;
	private List<String> jiraIterationIssuetypeKPI138;
	private List<String> jiraIterationIssuetypeKPI131;
	private List<String> jiraIterationIssuetypeKPI128;
	private List<String> jiraIterationIssuetypeKPI134;
	private List<String> jiraIterationIssuetypeKPI145;
	private List<String> jiraIterationIssuetypeKpi72;
	private List<String> jiraIterationIssuetypeKPI119;
	private List<String> jiraIterationIssuetypeKpi5;
	private List<String> jiraIterationIssuetypeKPI75;
	private List<String> jiraIterationIssuetypeKPI123;
	private List<String> jiraIterationIssuetypeKPI125;
	private List<String> jiraIterationIssuetypeKPI120;
	private List<String> jiraIterationIssuetypeKPI124;
	private List<String> jiraIterationIssuetypeKPI39;
	private String jiraDefectRejectionStatusKPI155;
	private List<String> jiraDodKPI155;
	private String jiraLiveStatusKPI155;
	private boolean uploadData;
	private boolean uploadDataKPI42;
	private boolean uploadDataKPI16;
	@Builder.Default
	private boolean notificationEnabler = true;
	@Builder.Default
	private boolean excludeUnlinkedDefects = true;
	private List<String> jiraIssueEpicTypeKPI153;

	// DTS-26150 start
	// Testing Phase Defect Mapping
	private String testingPhaseDefectCustomField;
	private String testingPhaseDefectsIdentifier;
	private List<String> testingPhaseDefectValue;
	private String testingPhaseDefectComponentValue;
	private List<String> jiraDodKPI163;
	// DTS-26150 end

	private List<String> jiraIssueTypeNamesKPI161;
	private List<String> jiraIssueTypeNamesKPI146;
	private List<String> jiraIssueTypeNamesKPI148;
	private List<String> jiraIssueTypeNamesKPI151;
	private List<String> jiraIssueTypeNamesKPI152;

	private List<String> jiraDodKPI156;
	private List<String> jiraIssueTypeKPI156;
	private List<String> jiraIssueWaitStateKPI170;
	private List<String> jiraIssueClosedStateKPI170;

	@Builder.Default
	private String leadTimeConfigRepoTool = "Jira";

	@Builder.Default
	private String toBranchForMRKPI156 = "master";
	private Map<String, Integer> startDateCountKPI150;
	private List<String> jiraDevDoneStatusKPI150;
	private boolean populateByDevDoneKPI150;
	private List<String> releaseListKPI150;

	// threshold field
	private String thresholdValueKPI14;
	private String thresholdValueKPI191;
	private String thresholdValueKPI82;
	private String thresholdValueKPI111;
	private String thresholdValueKPI35;
	private String thresholdValueKPI34;
	private String thresholdValueKPI37;
	private String thresholdValueKPI28;
	private String thresholdValueKPI36;
	private String thresholdValueKPI16;
	private String thresholdValueKPI17;
	private String thresholdValueKPI38;
	private String thresholdValueKPI27;
	private String thresholdValueKPI72;
	private String thresholdValueKPI84;
	private String thresholdValueKPI11;
	private String thresholdValueKPI157;
	private String thresholdValueKPI158;
	private String thresholdValueKPI160;
	private String thresholdValueKPI164;
	private String thresholdValueKPI3;
	private String thresholdValueKPI126;
	private String thresholdValueKPI42;
	private String thresholdValueKPI168;
	private String thresholdValueKPI70;
	private String thresholdValueKPI40;
	private String thresholdValueKPI5;
	private String thresholdValueKPI39;
	private String thresholdValueKPI46;
	private String thresholdValueKPI8;
	private String thresholdValueKPI172;
	private String thresholdValueKPI73;
	private String thresholdValueKPI113;
	private String thresholdValueKPI149;
	private String thresholdValueKPI153;
	private String thresholdValueKPI162;
	private String thresholdValueKPI116;
	private String thresholdValueKPI156;
	private String thresholdValueKPI118;
	private String thresholdValueKPI127;
	private String thresholdValueKPI170;
	private String thresholdValueKPI139;
	private String thresholdValueKPI166;
	private String thresholdValueKPI173;
	private String thresholdValueKPI180;
	private String thresholdValueKPI181;
	private String thresholdValueKPI182;
	private String thresholdValueKPI185;
	private String thresholdValueKPI186;

	/** kanban kpis threshold fields starts * */
	private String thresholdValueKPI51;

	private String thresholdValueKPI55;
	private String thresholdValueKPI54;
	private String thresholdValueKPI50;
	private String thresholdValueKPI48;
	private String thresholdValueKPI997;
	private String thresholdValueKPI63;
	private String thresholdValueKPI62;
	private String thresholdValueKPI64;
	private String thresholdValueKPI67;
	private String thresholdValueKPI71;
	private String thresholdValueKPI49;
	private String thresholdValueKPI58;
	private String thresholdValueKPI66;
	private String thresholdValueKPI65;
	private String thresholdValueKPI53;
	private String thresholdValueKPI74;
	private String thresholdValueKPI114;
	private String thresholdValueKPI159;
	private String thresholdValueKPI184;
	private String thresholdValueKPI183;

	/** kanban kpi threshold fields ends * */
	private String jiraProductionIncidentIdentification;

	private String jiraProdIncidentRaisedByCustomField;
	private List<String> jiraProdIncidentRaisedByValue;

	private List<String> jiraStoryIdentificationKPI166;
	private List<String> jiraDodKPI166;

	private List<String> jiraLabelsKPI14;
	private List<String> jiraLabelsKPI82;
	private List<String> jiraLabelsQAKPI111;
	private List<String> jiraLabelsKPI133;
	private List<String> storyFirstStatusKPI154;
	private List<String> jiraStatusForInProgressKPI154;

	private List<String> jiraDevDoneStatusKPI154;
	private List<String> jiraQADoneStatusKPI154;
	private List<String> jiraOnHoldStatusKPI154;
	private List<String> jiraIterationCompletionStatusKPI154;

	private List<String> jiraSubTaskIdentification;
	private List<String> jiraStatusStartDevelopmentKPI154;
	private List<String> jiraLabelsKPI135;

	private List<String> jiraStatusForInProgressKPI161;
	private List<String> jiraStatusForRefinedKPI161;
	private List<String> jiraStatusForNotRefinedKPI161;

	private List<String> jiraIssueTypeKPI171;
	private List<String> jiraDodKPI171;
	private List<String> jiraDorKPI171;
	private List<String> jiraLiveStatusKPI171;
	private String storyFirstStatusKPI171;

	private List<String> jiraIssueTypeExcludeKPI124;
	private List<String> jiraIssueTypeExcludeKPI75;
	// production defect ageing status to consider
	private List<String> jiraStatusToConsiderKPI127;

	private List<String> issueTypesToConsiderKpi113;
	private List<String> closedIssueStatusToConsiderKpi113;

	private boolean includeActiveSprintInBacklogKPI;

	private List<String> defectRejectionLabelsKPI37;

    private List<String> jiraLabelsKPI120;

	private List<String> resolutionTypeForRejectionKPI190;
	private String jiraDefectRejectionStatusKPI190;
	private String defectReopenStatusKPI190;
	private String thresholdValueKPI190;
	/**
	 * Get jira issue type names string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getJiraIssueTypeNames() {
		return jiraIssueTypeNames == null ? null : jiraIssueTypeNames.clone();
	}

	/**
	 * Sets jira issue type names.
	 *
	 * @param jiraIssueTypeNames
	 *          the jira issue type names
	 */
	public void setJiraIssueTypeNames(String[] jiraIssueTypeNames) {
		this.jiraIssueTypeNames = jiraIssueTypeNames == null ? null : jiraIssueTypeNames.clone();
	}

	/**
	 * Get link defect to story field string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getLinkDefectToStoryField() {
		return linkDefectToStoryField == null ? linkDefectToStoryField : linkDefectToStoryField.clone();
	}

	/**
	 * Sets link defect to story field.
	 *
	 * @param linkDefectToStoryField
	 *          the link defect to story field
	 */
	public void setLinkDefectToStoryField(String[] linkDefectToStoryField) {
		this.linkDefectToStoryField = linkDefectToStoryField == null ? null : linkDefectToStoryField.clone();
	}

	public boolean getNotificationEnabler() {
		return notificationEnabler;
	}
}
