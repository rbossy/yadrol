package org.phatonin.yadrol.app.web.json;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.phatonin.yadrol.app.web.WebOptions;

public enum ConverterUtil {
	;
	
	@SuppressWarnings("unchecked")
	public static <T> JSONArray convert(Collection<T> list, JsonConverter<T> converter, WebOptions options) {
		JSONArray result = new JSONArray();
		for (T item : list) {
			Object o = converter.convert(item, options);
			result.add(o);
		}
		return result;
	}
	
	public static <T> JSONArray convert(T[] array, JsonConverter<T> converter, WebOptions options) {
		return convert(Arrays.asList(array), converter, options);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> JSONObject convert(Map<String,T> map, JsonConverter<T> converter, WebOptions options) {
		JSONObject result = new JSONObject();
		for (Map.Entry<String,T> e : map.entrySet()) {
			String key = e.getKey();
			T value = e.getValue();
			Object o = converter.convert(value, options);
			result.put(key, o);
		}
		return result;
	}
}
