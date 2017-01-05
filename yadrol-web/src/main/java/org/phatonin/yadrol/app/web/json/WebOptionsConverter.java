package org.phatonin.yadrol.app.web.json;

import org.json.simple.JSONObject;
import org.phatonin.yadrol.app.web.WebOptions;
import org.phatonin.yadrol.app.web.WebParamDispatcher;

public enum WebOptionsConverter implements JsonConverter<WebOptions> {
	INSTANCE;

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(WebOptions options, WebOptions duh) {
		JSONObject result = new JSONObject();
		for (WebParamDispatcher wpd : WebParamDispatcher.values()) {
			String value = wpd.getParam(options);
			result.put(wpd.name, value);
		}
		return result;
	}
}
