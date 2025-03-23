package com.team33.modulebatch.config;

import java.util.HashMap;
import java.util.Map;

public class QueryGenerator {

	public static Map<String, Object> getParameterForQuery(String parameter, String value) {

		Map<String, Object> parameters = new HashMap<>();
		parameters.put(parameter, value);

		return parameters;
	}
}
