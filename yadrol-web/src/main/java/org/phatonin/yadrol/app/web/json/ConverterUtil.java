/**
   Copyright 2016-2017, Robert Bossy

   This file is part of Yadrol.

   Yadrol is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Yadrol is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Yadrol.  If not, see <http://www.gnu.org/licenses/>.
**/

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
