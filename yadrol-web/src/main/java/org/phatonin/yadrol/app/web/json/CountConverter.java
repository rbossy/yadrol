package org.phatonin.yadrol.app.web.json;

import org.json.simple.JSONObject;
import org.phatonin.yadrol.app.web.WebOptions;
import org.phatonin.yadrol.core.Count;

public enum CountConverter implements JsonConverter<Count> {
	INSTANCE;

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Count value, WebOptions options) {
		JSONObject result = new JSONObject();
		Object o = value.getValue();
		result.put("value", ValueConverter.INSTANCE.convert(o, options));
		result.put("frequency", value.getFrequency());
		result.put("at-least-frequency", value.getAtLeastFrequency());
		result.put("at-most-frequency", value.getAtMostFrequency());
		result.put("relative-frequency", value.getRelativeFrequency());
		result.put("relative-at-least-frequency", value.getRelativeAtLeastFrequency());
		result.put("relative-at-most-frequency", value.getRelativeAtMostFrequency());
		return result;
	}
}
