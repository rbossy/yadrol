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

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

/**
 * <code>not operand</code>
 * 
 *
 */
public class BooleanNot extends AbstractBooleanExpression {
	private final Expression operand;

	public BooleanNot(Location location, Expression operand) {
		super(location);
		this.operand = operand;
	}

	public Expression getOperand() {
		return operand;
	}

	@Override
	public AbstractBooleanExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new BooleanNot(getLocation(), operand.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return operand.isPureConstant();
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new BooleanNot(getLocation(), operand.substituteVariables(scope));
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			return !operand.evaluateBoolean(ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operand == null) ? 0 : operand.hashCode());
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
		BooleanNot other = (BooleanNot) obj;
		if (operand == null) {
			if (other.operand != null)
				return false;
		}
		else if (!operand.equals(other.operand))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		unaryOperator(sb, "not ", operand, Precedence.COMPARISON);
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.unaryOperator("not ", operand, Precedence.COMPARISON);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.NOT;
	}
}
