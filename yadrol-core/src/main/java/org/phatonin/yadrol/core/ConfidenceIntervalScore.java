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

public class ConfidenceIntervalScore implements DistributionScore {
	private final double risk;

	public ConfidenceIntervalScore(double risk) {
		super();
		this.risk = risk;
	}

	@Override
	public Object compute(EvaluationContext ctx, Scope scope, Distribution dist) throws EvaluationException {
		return dist.confidenceInterval(risk);
	}

	@Override
	public String computeAsString(EvaluationContext ctx, Scope scope, Distribution dist, String doubleFormat) throws EvaluationException {
		Count[] interval = dist.confidenceInterval(risk);
		String loString = EvaluationContext.valueString(interval[0].getValue());
		String hiString = EvaluationContext.valueString(interval[1].getValue());
		return String.format("%s - %s", loString, hiString);
	}

	@Override
	public String getName() {
		return String.format("risk=%.2f", risk);
	}
}
