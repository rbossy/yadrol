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
import java.util.Arrays;
import java.util.List;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

/**
 * <code>expr1 and expr2 and ...</code>
 * 
 *
 */
public class BooleanAnd extends AbstractBooleanExpression {
	private final Expression[] operands;

	public BooleanAnd(List<Expression> operands) {
		super(operands.get(0).getLocation());
		this.operands = operands.toArray(new Expression[operands.size()]);
	}

	public Expression[] getOperands() {
		return operands;
	}

	@Override
	public AbstractBooleanExpression reduce() throws EvaluationException {
		List<Expression> operands = new ArrayList<Expression>(this.operands.length);
		for (Expression e : this.operands) {
			if (e.isPureConstant() && !e.evaluateBoolean(null, null)) {
				operands.add(new BooleanConstant(e.getLocation(), false));
				break;
			}
			operands.add(e.reduce());
		}
		if (operands.isEmpty()) {
			return new BooleanConstant(getLocation(), true);
		}
		if (operands.size() == 1) {
			return new ConvertBoolean(getLocation(), operands.get(0)).reduce();
		}
		return new BooleanAnd(operands);
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		for (Expression e : operands) {
			if (!e.isPureConstant()) {
				return false;
			}
			if (!e.evaluateBoolean(null, null)) {
				break;
			}
		}
		return true;
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new BooleanAnd(substituteVariables(operands, scope));
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			for (Expression e : operands) {
				if (!e.evaluateBoolean(ctx, scope)) {
					return false;
				}
			}
			return true;
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(operands);
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
		BooleanAnd other = (BooleanAnd) obj;
		if (!Arrays.equals(operands, other.operands))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		nAryOperator(sb, " and ", operands, Precedence.NOT);
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.nAryOperator(" and ", operands, Precedence.NOT);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.AND;
	}
}
