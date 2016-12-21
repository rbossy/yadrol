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
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.ValueType;

public class ConvertBoolean extends AbstractBooleanExpression {
	private final Expression expression;

	public ConvertBoolean(Location location, Expression expression) {
		super(location);
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}

	@Override
	public AbstractBooleanExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureConstant();
		}
		if (expression.getReturnType() == ValueType.BOOLEAN) {
			return (AbstractBooleanExpression) expression.reduce();
		}
		return new ConvertBoolean(getLocation(), expression.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return expression.isPureConstant();
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new ConvertBoolean(getLocation(), expression.substituteVariables(scope));
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			return expression.evaluateBoolean(ctx, scope);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
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
		ConvertBoolean other = (ConvertBoolean) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		}
		else if (!expression.equals(other.expression))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		unaryOperator(sb, "boolean ", expression, Precedence.SUBSCRIPT);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.UNARY;
	}
}
