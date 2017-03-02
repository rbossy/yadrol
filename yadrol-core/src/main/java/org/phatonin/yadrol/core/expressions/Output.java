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

package org.phatonin.yadrol.core.expressions;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.OutputMode;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.ValueType;

/**
 * <code>roll expression [evaluationType] [name]</code>
 * <code>sample expression [evaluationType] [name]</code>
 * 
 *
 */
public class Output extends AbstractExpression {
	private final StringInterpolation name;
	private final Expression expression;
	private final ValueType evaluationType;
	private final OutputMode operator;
	
	public Output(Location location, StringInterpolation name, Expression expression, ValueType evaluationType, OutputMode operator) {
		super(location);
		this.name = name;
		this.expression = expression;
		this.evaluationType = evaluationType;
		this.operator = operator;
	}

	public Expression getExpression() {
		return expression;
	}

	public ValueType getEvaluationType() {
		return evaluationType;
	}

	public OutputMode getOperator() {
		return operator;
	}

	public String getName(EvaluationContext ctx, Scope scope) throws EvaluationException {
		if (name == null) {
			Expression expr = expression.substituteVariables(scope).reduce();
			return expr.toString();
		}
		return name.evaluateString(ctx, scope);
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			String name = getName(ctx, scope);
			return operator.record(expression, ctx, scope, evaluationType, name);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Output(getLocation(), name, expression.substituteVariables(scope), evaluationType, operator);
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return false;
	}

	@Override
	public Expression reduce() throws EvaluationException {
		return new Output(getLocation(), name, expression.reduce(), evaluationType, operator);
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.keyword(operator.toString()).space()
		.expression(expression, Precedence.ASSIGN);
		if (evaluationType != ValueType.DEFAULT) {
			stringer.space()
			.keyword(evaluationType.toString());
		}
		if (name != null) {
			stringer.space()
			.expression(name, Precedence.ATOM);
		}
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.OUTPUT;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((evaluationType == null) ? 0 : evaluationType.hashCode());
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
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
		Output other = (Output) obj;
		if (evaluationType != other.evaluationType)
			return false;
		if (expression == null) {
			if (other.expression != null)
				return false;
		}
		else if (!expression.equals(other.expression))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}
}
