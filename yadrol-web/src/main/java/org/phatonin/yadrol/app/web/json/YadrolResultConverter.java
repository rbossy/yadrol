package org.phatonin.yadrol.app.web.json;

import org.json.simple.JSONObject;
import org.phatonin.yadrol.app.YadrolResult;
import org.phatonin.yadrol.app.web.WebOptions;
import org.phatonin.yadrol.core.EvaluationContext;

public enum YadrolResultConverter implements JsonConverter<YadrolResult> {
	INSTANCE;

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(YadrolResult value, WebOptions options) {
		JSONObject result = new JSONObject();
		result.put("expressions", ConverterUtil.convert(value.getExpressions(), ExpressionConverter.INSTANCE, options));
		EvaluationContext ctx = value.getEvaluationContext();
		result.put("dice-records", ConverterUtil.convert(ctx.getDiceRecords(), DiceRecordConverter.INSTANCE, options));
		result.put("global-scope", ConverterUtil.convert(ctx.getGlobalScope().getVariables(), ValueConverter.INSTANCE, options));
		result.put("roll-records", ConverterUtil.convert(ctx.getRollRecords(), RollRecordConverter.INSTANCE, options));
		result.put("sample-records", ConverterUtil.convert(ctx.getSampleRecords(), new SampleRecordConverter(ctx), options));
//		result.put("multi-counts", ConverterUtil.convert(ctx.getMultiCounts(options.getCountSelector().getRelative()), new MultiCountConverter(ctx), options));
		return result;
	}
}
