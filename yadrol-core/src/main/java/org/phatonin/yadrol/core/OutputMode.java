/**
   Copyright 2016, Robert Bossy

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

package org.phatonin.yadrol.core;

import org.phatonin.yadrol.core.values.ValueType;

public enum OutputMode {
	ROLL {
		@Override
		public Object record(Expression expression, EvaluationContext ctx, Scope scope, ValueType evaluationType, String name) throws EvaluationException {
			RollRecord record = ctx.startRollRecord(name, expression, evaluationType);
			Object result = expression.evaluate(ctx, scope, evaluationType);
			record.setResult(result);
			ctx.endRollRecord();
			return result;
		}
		
		@Override
		public String toString() {
			return "roll";
		}
	},
	
	SAMPLE {
		@Override
		public Object record(Expression expression, EvaluationContext ctx, Scope scope, ValueType evaluationType, String name) throws EvaluationException {
			SampleRecord record = ctx.startSampleRecord(name, expression, evaluationType);
			Distribution dist = record.getDistribution();
			dist.sample(expression, ctx, scope, evaluationType, ctx.getSampleSize());
			ctx.endSampleRecord();
			dist.compute();
			return dist.mode().getValue();
		}

		@Override
		public String toString() {
			return "sample";
		}
	},
	
	DEFAULT {
		@Override
		public Object record(Expression expression, EvaluationContext ctx, Scope scope, ValueType evaluationType, String name) throws EvaluationException {
			return ctx.getDefaultOutputMode().record(expression, ctx, scope, evaluationType, name);
		}

		@Override
		public String toString() {
			return "output";
		}		
	}
	;
	
	public abstract Object record(Expression expression, EvaluationContext ctx, Scope scope, ValueType evaluationType, String name) throws EvaluationException;

	public static OutputMode fromString(String op) {
		switch (op) {
			case "roll": return ROLL;
			case "sample": return SAMPLE;
			case "output": return DEFAULT;
		}
		throw new IllegalArgumentException("unknown output " + op);
	}
}
