/*
 * Copyright 2014 CapitalOne, LLC.
 * Further development Copyright 2022 Sapient Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.publicissapient.kpidashboard.common.util;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Update;

import com.publicissapient.kpidashboard.common.model.application.BaseFieldMappingStructure;
import com.publicissapient.kpidashboard.common.model.application.ConfigurationHistoryChangeLog;
import com.publicissapient.kpidashboard.common.model.application.FieldMapping;
import com.publicissapient.kpidashboard.common.model.application.FieldMappingMeta;
import com.publicissapient.kpidashboard.common.model.application.FieldMappingResponse;
import com.publicissapient.kpidashboard.common.model.application.FieldMappingStructure;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class FieldMappingHelper {
	public static final String HISTORY = "history";
	public static final String OBJECT_ID = "org.bson.types.ObjectId";
	public static final String DOUBLE = "java.lang.Double";

	private FieldMappingHelper() {
	}

	public static Object getFieldMappingField(FieldMapping fieldMapping, Class<?> fieldMapping1, String field)
			throws NoSuchFieldException, IllegalAccessException {
		Field declaredField = fieldMapping1.getDeclaredField(field);
		setAccessible(declaredField);
		return declaredField.get(fieldMapping);
	}

	public static void setAccessible(Field field) {
		field.setAccessible(true); // NOSONAR
	}

	/**
	 * @param fieldMapping
	 *          fieldMapping
	 * @param fieldName
	 *          fieldName
	 * @return list of history logs
	 * @throws NoSuchFieldException
	 *           no field exception
	 * @throws IllegalAccessException
	 *           accessibility
	 */
	public static List<ConfigurationHistoryChangeLog> getAccessibleFieldHistory(FieldMapping fieldMapping,
			String fieldName) throws NoSuchFieldException, IllegalAccessException {
		return (List<ConfigurationHistoryChangeLog>) getFieldMappingField(fieldMapping, FieldMapping.class.getSuperclass(),
				HISTORY + fieldName);
	}

	/*
	 * while getting fields get fieldmappinghistory
	 */
	public static List<ConfigurationHistoryChangeLog> getFieldMappingHistory(FieldMapping fieldMapping, String field,
			String nodeId, boolean nodeSpecificField) throws NoSuchFieldException, IllegalAccessException {
		List<ConfigurationHistoryChangeLog> accessibleFieldHistory = getAccessibleFieldHistory(fieldMapping, field);
		if (nodeSpecificField && StringUtils.isNotEmpty(nodeId) && CollectionUtils.isNotEmpty(accessibleFieldHistory)) {
			return accessibleFieldHistory.stream()
					.filter(configurationHistoryChangeLog -> StringUtils.isNotEmpty(configurationHistoryChangeLog
							.getReleaseNodeId()) && configurationHistoryChangeLog.getReleaseNodeId().equalsIgnoreCase(nodeId))
					.toList();
		}
		return accessibleFieldHistory;
	}

	/*
	 * to get the field from fieldMapping
	 */
	public static Object getFieldMappingData(FieldMapping fieldMapping, Class<FieldMapping> fieldMappingClass,
			String field, String nodeId, boolean nodeSpecificField) throws NoSuchFieldException, IllegalAccessException {
		Object fieldMappingField = getFieldMappingField(fieldMapping, fieldMappingClass, field);
		if (nodeSpecificField && StringUtils.isNotEmpty(nodeId)) {
			if (ObjectUtils.isNotEmpty(fieldMappingField)) {
				Map<String, Integer> mappingField = (Map<String, Integer>) fieldMappingField;
				return mappingField.getOrDefault(nodeId, 0);
			} else {
				return 0;
			}
		} else {
			return fieldMappingField;
		}
	}

	/**
	 * compares field values for saved and unsaved data.
	 *
	 * @param value
	 *          unsaved value
	 * @param value1
	 *          existing value
	 * @return is value updated
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static boolean isValueUpdated(Object value, Object value1) {
		if (ObjectUtils.isEmpty(value) && ObjectUtils.isEmpty(value1)) {
			return false;

		} else {
			if (value instanceof List) {
				return !(value1 instanceof List && (((List) value).size() == ((List) value1).size()) && ((List) value)
						.containsAll((List) value1));

			} else if (value instanceof String[]) {
				return !(value1 instanceof String[] && Arrays.equals((String[]) value, (String[]) value1));

			} else {
				if (value1 != null) {
					return !value1.equals(value);
				} else {
					return true;
				}
			}
		}
	}

	public static Object getNestedField(FieldMapping newMapping, Class<FieldMapping> fieldMappingClass, Object newValue,
			FieldMappingStructure mappingStructure) throws NoSuchFieldException, IllegalAccessException {
		if (null != mappingStructure && CollectionUtils.isNotEmpty(mappingStructure.getNestedFields())) {
			StringBuilder originalValue = new StringBuilder(newValue + "-");
			// for nested fields
			for (BaseFieldMappingStructure nestedField : mappingStructure.getNestedFields()) {
				if (nestedField.getFilterGroup().contains(newValue)) {
					Object fieldMappingField = FieldMappingHelper.getFieldMappingField(newMapping, fieldMappingClass,
							nestedField.getFieldName());
					if (fieldMappingField != null) {
						originalValue.append(fieldMappingField).append(":");
					}
				}
			}
			return originalValue.deleteCharAt(originalValue.length() - 1).toString();
		}
		return newValue;
	}

	public static Object generateAdditionalFilters(Object newValue, String fieldName) {
		if (fieldName.equalsIgnoreCase("additionalFilterConfig")) {
			List<LinkedHashMap<String, Object>> additonalValue = (List<LinkedHashMap<String, Object>>) newValue;
			StringBuilder originalValue = new StringBuilder();
			for (Map<String, Object> value : additonalValue) {

				String identificationButton = (String) value.get("identifyFrom");
				String identificationValue;
				if (identificationButton.equalsIgnoreCase("customfield")) {
					identificationValue = (String) value.get("identificationField");
				} else {
					identificationValue = value.get("values").toString();
				}
				originalValue.append(value.get("filterId")).append("-").append(identificationButton).append(":")
						.append(identificationValue).append(" ,");
			}
			return originalValue.toString();
		}
		return null;
	}

	public static void setFieldValue(FieldMapping object, String fieldName, Object value) throws IllegalAccessException {
		try {
			Field field = FieldMapping.class.getDeclaredField(fieldName);
			setAccessible(field);
			Object v = convertToSameType(field, value);
			field.set(object, v);
		} catch (NoSuchFieldException e) {
			log.warn("Field not found");
		}
	}

	private static Object convertToSameType(Field field, Object value) {

		if (value == null)
			return null;
		Class<?> fieldType = field.getType();

		// Convert List to String[]
		if (fieldType == String[].class && value instanceof List) {
			List<?> list = (List<?>) value;
			return list.toArray(new String[0]);
		}

		// Convert String[] to List<String>
		if (List.class.isAssignableFrom(fieldType) && value instanceof String[]) {
			return List.of((String[]) value);
		}

		// Convert String to ObjectId
		if (fieldType.getName().equalsIgnoreCase("org.bson.types.ObjectId") && value instanceof String) {
			return new ObjectId((String) value);
		}

		// Convert Integer to Double
		if (fieldType == Double.class && value instanceof Integer) {
			return ((Integer) value).doubleValue();
		}

		// Convert Integer to Long
		if (fieldType == Long.class && value instanceof Integer) {
			return ((Integer) value).longValue();
		}

		// Convert Long to Double
		if (fieldType == Double.class && value instanceof Long) {
			return ((Long) value).doubleValue();
		}

		// Fallback: if already assignable
		if (fieldType.isAssignableFrom(value.getClass())) {
			return value;
		}
		throw new IllegalArgumentException("Unsupported type conversion for field: " + field.getName());
	}

	public static boolean isFieldPresent(Class<?> clazz, String fieldName) {
		try {
			clazz.getDeclaredField(fieldName);
			return true;
		} catch (NoSuchFieldException e) {
			return false;
		}
	}

	/**
	 * the history of all the nested fields should appear on the first identifier.
	 *
	 * @param fieldMappingResponseList
	 *          fieldMappingResponseList
	 * @param fieldMappingResponse
	 *          fieldMappingResponse
	 * @param mappingStructure
	 *          mappingStructure
	 * @param fieldMapping
	 *          fieldMapping
	 * @throws NoSuchFieldException
	 *           NoSuchFieldException
	 * @throws IllegalAccessException
	 *           IllegalAccessException
	 */
	public static void generateHistoryForNestedFields(List<FieldMappingResponse> fieldMappingResponseList,
			FieldMappingResponse fieldMappingResponse, FieldMappingStructure mappingStructure, FieldMapping fieldMapping)
			throws NoSuchFieldException, IllegalAccessException {
		if (CollectionUtils.isNotEmpty(mappingStructure.getNestedFields())) {

			StringBuilder originalValue = new StringBuilder(fieldMappingResponse.getOriginalValue() + "-");
			// check the fields in nestedfield section of fieldmapping structure and find if
			// those fields are present in the fieldmapping response
			for (BaseFieldMappingStructure nestedField : mappingStructure.getNestedFields()) {
				Optional<FieldMappingResponse> mappingResponse = fieldMappingResponseList.stream()
						.filter(response -> response.getFieldName().equalsIgnoreCase(nestedField.getFieldName()) && nestedField
								.getFilterGroup().contains(fieldMappingResponse.getOriginalValue().toString()))
						.findFirst();
				mappingResponse.ifPresent(response -> originalValue.append(response.getOriginalValue()).append(":"));
			}
			setFieldMappingResponse(fieldMappingResponse, fieldMapping, originalValue);
		}
	}

	public static void setMappingResponseWithGeneratedField(FieldMappingResponse fieldMappingResponse,
			FieldMapping fieldMapping) throws NoSuchFieldException, IllegalAccessException {
		Object additonalFilter = generateAdditionalFilters(fieldMappingResponse.getOriginalValue(),
				fieldMappingResponse.getFieldName());
		if (additonalFilter != null) {
			setFieldMappingResponse(fieldMappingResponse, fieldMapping, new StringBuilder((String) additonalFilter));
		}
	}

	public static void setFieldMappingResponse(FieldMappingResponse fieldMappingResponse, FieldMapping fieldMapping,
			StringBuilder originalValue) throws NoSuchFieldException, IllegalAccessException {
		List<ConfigurationHistoryChangeLog> changeLogs = FieldMappingHelper.getAccessibleFieldHistory(fieldMapping,
				fieldMappingResponse.getFieldName());
		String previousValue = "";
		if (CollectionUtils.isNotEmpty(changeLogs)) {
			ConfigurationHistoryChangeLog configurationHistoryChangeLog = changeLogs.get(changeLogs.size() - 1);
			previousValue = String.valueOf(configurationHistoryChangeLog.getChangedTo());
		}
		if (ObjectUtils.isNotEmpty(originalValue)) {
			fieldMappingResponse.setOriginalValue(originalValue.deleteCharAt(originalValue.length() - 1).toString());
		}
		fieldMappingResponse.setPreviousValue(previousValue);
	}

	/*
	 * create Node Specific FieldData
	 */
	public static void setNodeSpecificFields(FieldMappingStructure mappingStructure,
			FieldMappingResponse fieldMappingResponse, FieldMapping fieldMapping, String nodeId, Update update)
			throws NoSuchFieldException, IllegalAccessException {
		if (mappingStructure.isNodeSpecific() && StringUtils.isNotEmpty(nodeId)) {
			Object fieldMappingField = getFieldMappingField(fieldMapping, FieldMapping.class,
					fieldMappingResponse.getFieldName());
			Object originalValue = fieldMappingResponse.getOriginalValue();
			if (ObjectUtils.isNotEmpty(originalValue)) {
				Map<String, Integer> map = new HashMap<>();
				if (ObjectUtils.isNotEmpty(fieldMappingField)) {
					map = (Map<String, Integer>) fieldMappingField;
				}
				map.put(nodeId, (Integer) originalValue);
				update.set(fieldMappingResponse.getFieldName(), map);
			}

			List<ConfigurationHistoryChangeLog> getNodeSpecificFieldHistory = FieldMappingHelper
					.getAccessibleFieldHistory(fieldMapping, fieldMappingResponse.getFieldName());
			Integer previousValue = 0;
			if (CollectionUtils.isNotEmpty(getNodeSpecificFieldHistory)) {
				List<ConfigurationHistoryChangeLog> changeLogs = getNodeSpecificFieldHistory.stream()
						.filter(configurationHistoryChangeLog -> StringUtils.isNotEmpty(configurationHistoryChangeLog
								.getReleaseNodeId()) && configurationHistoryChangeLog.getReleaseNodeId().equalsIgnoreCase(nodeId))
						.toList();
				if (CollectionUtils.isNotEmpty(changeLogs)) {
					ConfigurationHistoryChangeLog configurationHistoryChangeLog = changeLogs.get(changeLogs.size() - 1);
					previousValue = (Integer) configurationHistoryChangeLog.getChangedTo();
				}
			}
			if (ObjectUtils.isNotEmpty(originalValue)) {
				fieldMappingResponse.setOriginalValue(originalValue);
			}
			fieldMappingResponse.setPreviousValue(previousValue);
		}
	}

	public static void removeDuplicateFieldsFromResponse(List<FieldMappingResponse> originalFieldMappingResponseList,
			Map<String, FieldMappingResponse> responseHashMap) {
		for (FieldMappingResponse response : originalFieldMappingResponseList) {
			responseHashMap.computeIfPresent(response.getFieldName(),
					(k, existingResponse) -> (existingResponse.getPreviousValue() == null && response.getPreviousValue() != null)
							? response
							: existingResponse);
			responseHashMap.putIfAbsent(response.getFieldName(), response);
		}
	}

	public static ConfigurationHistoryChangeLog createHistoryChangeLog(FieldMappingMeta fieldMappingMeta,
			FieldMappingResponse fieldMappingResponse, FieldMappingStructure mappingStructure, String loggedInUser) {
		ConfigurationHistoryChangeLog configurationHistoryChangeLog = new ConfigurationHistoryChangeLog();
		configurationHistoryChangeLog.setChangedTo(fieldMappingResponse.getOriginalValue());
		configurationHistoryChangeLog.setChangedFrom(fieldMappingResponse.getPreviousValue());
		configurationHistoryChangeLog.setChangedBy(loggedInUser);
		configurationHistoryChangeLog.setUpdatedOn(LocalDateTime.now().toString());
		if (mappingStructure.isNodeSpecific()) {
			configurationHistoryChangeLog.setReleaseNodeId(fieldMappingMeta.getReleaseNodeId());
		}
		return configurationHistoryChangeLog;
	}

	/**
	 * used in processors
	 *
	 * @param dbFieldMapping
	 * @param source
	 */
	public static void mergeIntoTarget(FieldMapping dbFieldMapping, FieldMapping source) {
		if (dbFieldMapping == null || source == null)
			return;
		Field[] fields = FieldMapping.class.getDeclaredFields();
		Object sourceValue = null;
		for (Field field : fields) {
			setAccessible(field);
			try {
				sourceValue = field.get(source);
				Object dbValue = field.get(dbFieldMapping);
				if (shouldMerge(sourceValue, dbValue)) {
					setFieldValue(dbFieldMapping, field.getName(), sourceValue);
				} else {
					log.debug("Skipping field '{}': source={}, db={}", field.getName(), sourceValue, dbValue);
				}
			} catch (IllegalAccessException | ClassCastException e) {
				log.error("Error while merging field mapping {} and {}", field.getName(), sourceValue);
			}
		}
	}

	private static boolean shouldMerge(Object sourceValue, Object dbValue) {
		if (sourceValue == null)
			return false;

		// Skip ObjectId (let DB manage IDs)
		if (sourceValue instanceof ObjectId)
			return false;

		// If DB is null, merge
		if (dbValue == null)
			return true;

		// Handle empty Lists
		if (sourceValue instanceof List && dbValue instanceof List) {
			return ((List<?>) dbValue).isEmpty();
		}

		// Handle empty arrays
		if (sourceValue instanceof String[] && dbValue instanceof String[]) {
			return ((String[]) dbValue).length == 0;
		}

		return false;
	}

	/**
	 * Backfills a hand picked set of fields on an already configured mapping.
	 *
	 * <p>
	 * Unlike {@link #mergeIntoTarget(FieldMapping, FieldMapping)}, which walks
	 * every field and is only safe on a mapping that has not been configured yet,
	 * this variant touches nothing beyond {@code fieldNames} and only writes a
	 * field that is still unset in the database. A value a user has already chosen
	 * is therefore never overwritten, which makes the call safe to run against live
	 * project configuration on every collection cycle.
	 *
	 * <p>
	 * Unknown field names are skipped with a warning rather than failing the whole
	 * merge, so a configuration document that references a field belonging to a
	 * newer or older release still applies cleanly.
	 *
	 * @param dbFieldMapping
	 *          the mapping loaded from the database, mutated in place
	 * @param source
	 *          the freshly derived mapping to take values from
	 * @param fieldNames
	 *          the only fields allowed to be written
	 * @return the names of the fields that were actually updated, empty when
	 *         nothing changed
	 */
	public static List<String> mergeUnsetFields(FieldMapping dbFieldMapping, FieldMapping source,
			Collection<String> fieldNames) {
		List<String> updatedFields = new ArrayList<>();
		if (dbFieldMapping == null || source == null || CollectionUtils.isEmpty(fieldNames)) {
			return updatedFields;
		}
		for (String fieldName : fieldNames) {
			if (StringUtils.isBlank(fieldName)) {
				continue;
			}
			try {
				Field field = FieldMapping.class.getDeclaredField(fieldName);
				setAccessible(field);
				Object sourceValue = field.get(source);
				// nothing discovered, so there is nothing worth writing
				if (isUnset(sourceValue) || sourceValue instanceof ObjectId) {
					continue;
				}
				// the project already decided this one, leave it alone
				if (!isUnset(field.get(dbFieldMapping))) {
					log.debug("Keeping the configured value of field '{}'", fieldName);
					continue;
				}
				setFieldValue(dbFieldMapping, fieldName, sourceValue);
				updatedFields.add(fieldName);
			} catch (NoSuchFieldException e) {
				log.warn("Field '{}' is not part of FieldMapping, skipping it", fieldName);
			} catch (IllegalAccessException | IllegalArgumentException | ClassCastException e) {
				log.error("Error while backfilling field mapping '{}'", fieldName, e);
			}
		}
		return updatedFields;
	}

	/**
	 * Resolves configuration keys onto the {@link FieldMapping} properties they
	 * populate.
	 *
	 * <p>
	 * Only {@code String} properties are considered, because the callers of this
	 * method resolve a single identifier such as a Jira custom field id.
	 * Restricting the match by type keeps an unrelated key from ever landing on a
	 * list or a numeric property. An exact match always wins; a case insensitive
	 * match is only attempted when no property carries the exact name, so a key
	 * spelled {@code rootcause} still resolves onto {@code rootCause}.
	 *
	 * @param candidateNames
	 *          keys coming from configuration, typically metadata identifier types
	 * @return the resolvable keys mapped onto the property they should be written
	 *         to, in encounter order
	 */
	public static Map<String, String> resolveStringFieldNames(Collection<String> candidateNames) {
		Map<String, String> resolved = new LinkedHashMap<>();
		if (CollectionUtils.isEmpty(candidateNames)) {
			return resolved;
		}
		Set<String> stringFields = new HashSet<>();
		Map<String, String> stringFieldsByLowerCaseName = new LinkedHashMap<>();
		for (Field field : FieldMapping.class.getDeclaredFields()) {
			if (field.getType() == String.class) {
				stringFields.add(field.getName());
				stringFieldsByLowerCaseName.putIfAbsent(field.getName().toLowerCase(Locale.ROOT), field.getName());
			}
		}
		for (String candidate : candidateNames) {
			if (StringUtils.isBlank(candidate)) {
				continue;
			}
			if (stringFields.contains(candidate)) {
				resolved.put(candidate, candidate);
				continue;
			}
			String caseInsensitiveMatch = stringFieldsByLowerCaseName.get(candidate.toLowerCase(Locale.ROOT));
			if (caseInsensitiveMatch != null) {
				resolved.put(candidate, caseInsensitiveMatch);
			}
		}
		return resolved;
	}

	/**
	 * Tells whether a field still holds nothing meaningful and can safely be filled
	 * in.
	 *
	 * <p>
	 * A blank string counts as unset on purpose: a mapping saved before a field
	 * existed, or one saved from a form where the input was left empty, ends up
	 * holding {@code ""} rather than {@code
	 * null} and would otherwise stay empty forever.
	 *
	 * @param value
	 *          the value to inspect
	 * @return true when the value is null, blank, or an empty collection, map or
	 *         array
	 */
	public static boolean isUnset(Object value) {
		if (value == null) {
			return true;
		}
		if (value instanceof CharSequence charSequence) {
			return StringUtils.isBlank(charSequence);
		}
		if (value instanceof Collection<?> collection) {
			return collection.isEmpty();
		}
		if (value instanceof Map<?, ?> map) {
			return map.isEmpty();
		}
		if (value instanceof Object[] array) {
			return array.length == 0;
		}
		return false;
	}
}
