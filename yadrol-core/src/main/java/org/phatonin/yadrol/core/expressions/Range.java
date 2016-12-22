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

package org.phatonin.yadrol.core.expressions;

import java.util.ArrayList;
import java.util.List;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

/**
 * <code>start .. end</code>
 * 
 *
 */
public class Range extends AbstractListExpression {
	private final Expression start;
	private final Expression end;
	
	public Range(Expression start, Expression end) {
		super(start.getLocation());
		this.start = start;
		this.end = end;
	}

	public Expression getStart() {
		return start;
	}

	public Expression getEnd() {
		return end;
	}

	@Override
	public AbstractListExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new Range(start.reduce(), end.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(start, end);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Range(start.substituteVariables(scope), end.substituteVariables(scope));
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			long start = this.start.evaluateInteger(ctx, scope);
			long end = this.end.evaluateInteger(ctx, scope);
			int step = start <= end ? 1 : -1;
			List<Object> result = new ArrayList<Object>((int) (end - start));
			for (long i = start; i != end; i += step) {
				result.add(i);
			}
			result.add(end);
			return result;
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Range other = (Range) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		}
		else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		}
		else if (!start.equals(other.start))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		binaryOperator(sb, "..", start, end, Precedence.PLUS);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.RANGE;
	}
}
