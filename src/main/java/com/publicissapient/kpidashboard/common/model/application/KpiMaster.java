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

package com.publicissapient.kpidashboard.common.model.application;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.publicissapient.kpidashboard.common.model.generic.BasicModel;
import com.publicissapient.kpidashboard.common.model.kpivideolink.KPIVideoLink;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Model class to represent kpi_master collection. */
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "kpi_master")
@Schema(description = "KPI Master Model representing the structure of KPI master data")
public class KpiMaster extends BasicModel {
	@Schema(description = "Unique identifier of the Kpi Master", example = "60d5ec49f8d2e30f8c8b4567")
	private String kpiId;

	@Schema(description = "Name of the KPI", example = "Code Coverage")
	private String kpiName;

	@Schema(description = "Is the KPI deleted", example = "false")
	private String isDeleted;

	@Schema(description = "Default order of the KPI", example = "1")
	private Integer defaultOrder;

	@Schema(description = "Category of the KPI", example = "Quality")
	private String kpiCategory;

	@Schema(description = "Sub-category of the KPI", example = "Code Quality")
	private String kpiSubCategory;

	@Schema(description = "Indicates if the KPI is part of aggregated feed", example = "Yes")
	private String kpiInAggregatedFeed;

	@Schema(description = "List of dashboards where the KPI is displayed", example = "[\"Release Dashboard\",\"Sprint Dashboard\"]")
	private List<String> kpiOnDashboard;

	@Schema(description = "KPI base line", example = "85%")
	private String kpiBaseLine;

	@Schema(description = "KPI unit", example = "Percentage")
	private String kpiUnit;

	@Schema(description = "KPI calculation logic", example = "Custom Script")
	private String chartType;

	@Schema(description = "KPI visualization type", example = "Line Chart")
	private String upperThresholdBG;

	@Schema(description = "KPI lower threshold", example = "60%")
	private String lowerThresholdBG;

	@JsonProperty("xaxisLabel")
	@Schema(description = "Label for the X-axis in KPI visualization", example = "Sprints")
	private String xAxisLabel;

	@JsonProperty("yaxisLabel")
	@Schema(description = "Label for the Y-axis in KPI visualization", example = "Code Coverage Percentage")
	private String yAxisLabel;

	@Schema(description = "Indicates whether to show trend for the KPI", example = "true")
	private boolean showTrend;

	@Schema(description = "Indicates whether to show target for the KPI", example = "true")
	private Boolean isPositiveTrend;

	@Schema(description = "KPI target value", example = "90%")
	private String lineLegend;

	@Schema(description = "KPI bar value", example = "75%")
	private String barLegend;

	@Schema(description = "Type of box for KPI visualization", example = "Standard")
	private String boxType;

	@Schema(description = "Indicates whether to calculate maturity for the KPI", example = "false")
	private boolean calculateMaturity;

	@Schema(description = "Indicates whether to hide overall filter for the KPI", example = "false")
	private boolean hideOverallFilter;

	@JsonProperty("videoLink")
	@Schema(description = "Video link related to the KPI", example = "http://example.com/kpi-video")
	private KPIVideoLink videoLink;

	@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName") // because it will
	// result in
	// changing ui and
	// db
	@Schema(description = "Indicates if trend is up on value increase", example = "true")
	private Boolean isTrendUpOnValIncrease;

	@Schema(description = "Source of the KPI data", example = "JIRA")
	private String kpiSource;

	@Schema(description = "Combined source of the KPI data", example = "JIRA, SonarQube")
	private String combinedKpiSource;

	@Schema(description = "Minimum value for the KPI", example = "0")
	private Object maxValue;

	@Schema(description = "Maximum value for the KPI", example = "100")
	private Double thresholdValue;

	@Schema(description = "Indicates if the KPI is Kanban type", example = "false")
	private Boolean kanban;

	@Schema(description = "Group ID of the KPI", example = "1")
	private Integer groupId;

	@Schema(description = "KPI information details")
	private KpiInfo kpiInfo;

	@Schema(description = "KPI filter criteria", example = "status:open")
	private String kpiFilter;

	@Schema(description = "Aggregation criteria for the KPI", example = "average")
	private String aggregationCriteria;

	@Schema(description = "Aggregation circle criteria for the KPI", example = "monthly")
	private String aggregationCircleCriteria;

	@Schema(description = "Forecast model for the KPI", example = "linear_regression")
	private String forecastModel;

	@Schema(description = "Indicates if the KPI is trend calculative", example = "true")
	private boolean isTrendCalculative;

	@Schema(description = "Trend calculation formulas for the KPI", implementation = KpiFormula.class)
	private List<KpiFormula> trendCalculation;

	@JsonProperty("isAdditionalFilterSupport")
	@Schema(description = "Indicates if additional filter support is available for the KPI", example = "true")
	private boolean isAdditionalFilterSupport;

	@Schema(description = "Maturity range for the KPI", example = "[\"Low\",\"Medium\",\"High\"]")
	private List<String> maturityRange;

	@Schema(description = "KPI display type", example = "bar")
	private Integer kpiWidth;

	@Schema(description = "Maturity levels for the KPI", implementation = MaturityLevel.class)
	private List<MaturityLevel> maturityLevel;

	@Schema(description = "Indicates if the KPI is a repository tool KPI", example = "false")
	private Boolean isRepoToolKpi;

	@Schema(description = "Order of Y-axis in KPI visualization", example = "{\"1\":\"Low\",\"2\":\"Medium\",\"3\":\"High\"}")
	private Map<Integer, String> yaxisOrder;

	@Schema(description = "Indicates if aggregation stacks is enabled for the KPI", example = "true")
	private Boolean isAggregationStacks;

	// to show x axis as ranges and not sprint number
	@Schema(description = "Indicates if X-axis grouping is enabled for the KPI", example = "true")
	private Boolean isXaxisGroup;

	// to implement bar graph by modifying bar-line graph
	@Schema(description = "Indicates if line chart is enabled for the KPI", example = "false")
	private Boolean lineChart;

	@Schema(description = "Height of the KPI visualization", example = "400")
	private Integer kpiHeight;

	@Schema(description = "Indicates if the KPI represents raw data", example = "true")
	private Boolean isRawData;
}
