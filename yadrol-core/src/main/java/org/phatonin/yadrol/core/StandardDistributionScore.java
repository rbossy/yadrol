package org.phatonin.yadrol.core;

public enum StandardDistributionScore implements DistributionScore {
	MEAN {
		@Override
		public Double compute(EvaluationContext ctx, Scope scope, Distribution dist) throws EvaluationException {
			return dist.mean(ctx, scope);
		}

		@Override
		public String computeAsString(EvaluationContext ctx, Scope scope, Distribution dist, String doubleFormat) throws EvaluationException {
			return String.format(doubleFormat, dist.mean(ctx, scope));
		}

		@Override
		public String getName() {
			return "mean";
		}
	},
	
	STDDEV {
		@Override
		public Double compute(EvaluationContext ctx, Scope scope, Distribution dist) throws EvaluationException {
			return dist.stddev(ctx, scope);
		}

		@Override
		public String computeAsString(EvaluationContext ctx, Scope scope, Distribution dist, String doubleFormat) throws EvaluationException {
			return String.format(doubleFormat, dist.stddev(ctx, scope));
		}

		@Override
		public String getName() {
			return "stddev";
		}
	},
	
	MODE {
		@Override
		public Count compute(EvaluationContext ctx, Scope scope, Distribution dist) throws EvaluationException {
			return dist.mode();
		}

		@Override
		public String computeAsString(EvaluationContext ctx, Scope scope, Distribution dist, String doubleFormat) throws EvaluationException {
			Count count = compute(ctx, scope, dist);
			return countValueToString(count);
		}

		@Override
		public String getName() {
			return "mode";
		}
	},
	
	MEDIAN_SUP {
		@Override
		public Count compute(EvaluationContext ctx, Scope scope, Distribution dist) throws EvaluationException {
			dist.compute();
			for (Count count : dist.getCounts()) {
				if (count.getRelativeAtMostFrequency() >= 0.5) {
					return count;
				}
			}
			return null;
		}

		@Override
		public String computeAsString(EvaluationContext ctx, Scope scope, Distribution dist, String doubleFormat) throws EvaluationException {
			Count count = compute(ctx, scope, dist);
			return countValueToString(count);
		}

		@Override
		public String getName() {
			return "median";
		}
	},
	
	MEDIAN_INF {
		@Override
		public Count compute(EvaluationContext ctx, Scope scope, Distribution dist) throws EvaluationException {
			dist.compute();
			for (Count count : dist.getCounts()) {
				if (count.getRelativeAtLeastFrequency() <= 0.5) {
					return count;
				}
			}
			return null;
		}

		@Override
		public String computeAsString(EvaluationContext ctx, Scope scope, Distribution dist, String doubleFormat) throws EvaluationException {
			Count count = compute(ctx, scope, dist);
			return countValueToString(count);
		}

		@Override
		public String getName() {
			return "low mean";
		}
	};
	
	private static String countValueToString(Count count) {
		Object value = count.getValue();
		Expression valueExpr = EvaluationContext.valueToExpression(value);
		return valueExpr.toString();
	}
}
