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
			//result.put("confidence-interval", ConverterUtil.convert(dist.confidenceInterval(options.getConfidenceIntervalRisk()), CountConverter.INSTANCE, options));
			return result;
		}
		catch (EvaluationException e) {
			throw new RuntimeException(e);
		}
	}
}
