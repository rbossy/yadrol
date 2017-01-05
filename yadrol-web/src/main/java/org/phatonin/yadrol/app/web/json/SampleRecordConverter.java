package org.phatonin.yadrol.app.web.json;

import org.json.simple.JSONObject;
import org.phatonin.yadrol.app.web.WebOptions;
import org.phatonin.yadrol.core.Distribution;
import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.SampleRecord;
import org.phatonin.yadrol.core.Scope;

public class SampleRecordConverter implements JsonConverter<SampleRecord> {
	private final EvaluationContext ctx;
	
	public SampleRecordConverter(EvaluationContext ctx) {
		super();
		this.ctx = ctx;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(SampleRecord value, WebOptions options) {
		try {
			JSONObject result = new JSONObject();
			result.put("name", value.getName());
			Distribution dist = value.getDistribution();
			result.put("counts", ConverterUtil.convert(dist.getCounts(), CountConverter.INSTANCE, options));
			Scope scope = ctx.getGlobalScope();
			result.put("mean", dist.mean(ctx, scope));
			result.put("stddev", dist.stddev(ctx, scope));
			result.put("mode", CountConverter.INSTANCE.convert(dist.mode(), options));
			result.put("median-sup", CountConverter.INSTANCE.convert(dist.medianSup(), options));
			result.put("median-inf", CountConverter.INSTANCE.convert(dist.medianInf(), options));
			result.put("confidence-interval", ConverterUtil.convert(dist.confidenceInterval(options.getConfidenceIntervalRisk()), CountConverter.INSTANCE, options));
			return result;
		}
		catch (EvaluationException e) {
			throw new RuntimeException(e);
		}
	}
}
