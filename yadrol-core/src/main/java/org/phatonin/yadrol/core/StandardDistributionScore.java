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
