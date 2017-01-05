package org.phatonin.yadrol.app.web.json;

import org.json.simple.JSONObject;
import org.phatonin.yadrol.app.web.WebOptions;
import org.phatonin.yadrol.core.DiceRecord;

public enum DiceRecordConverter implements JsonConverter<DiceRecord> {
	INSTANCE;

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(DiceRecord value, WebOptions options) {
		JSONObject result = new JSONObject();
		result.put("type", ValueConverter.INSTANCE.convert(value.getType(), options));
		result.put("result", ConverterUtil.convert(value.getResult(), ValueConverter.INSTANCE, options));
		return result;
	}
}
