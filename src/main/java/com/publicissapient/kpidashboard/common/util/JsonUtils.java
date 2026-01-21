package com.publicissapient.kpidashboard.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

	private JsonUtils() {
		// Utility class - private constructor
	}

	public static boolean isValidJSON(String jsonInString) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			mapper.readTree(jsonInString);
			return true;
		} catch (JsonProcessingException e) {
			return false;
		}
	}
}
