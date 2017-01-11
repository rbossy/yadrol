package org.phatonin.yadrol.app.web.json;

import org.json.simple.JSONObject;
import org.phatonin.yadrol.app.web.WebOptions;
import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.RollRecord;

public enum RollRecordConverter implements JsonConverter<RollRecord> {
	INSTANCE;

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(RollRecord value, WebOptions options) {
		JSONObject result = new JSONObject();
		result.put("name", value.getName());
		result.put("dice-records", ConverterUtil.convert(value.getDiceRecords(), DiceRecordConverter.INSTANCE, options));
		result.put("result", ValueConverter.INSTANCE.convert(value.getResult(), options));
		result.put("string-result", EvaluationContext.valueToExpression(value.getResult()).toString());
		return result;
	}
}
