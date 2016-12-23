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

import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.Function;
import org.phatonin.yadrol.core.values.ValueType;

/**
 * <code>native "address"</code>
 * 
 *
 */
public class Native extends AbstractExpression {
	private final String address;
	private Expression expression;

	public Native(Location location, String address) {
		super(location);
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		ensureExpression();
		return expression.evaluate(ctx, scope);
	}
	
	@Override
	public void evaluateUndef(EvaluationContext ctx, Scope scope) throws EvaluationException {
		ensureExpression();
		expression.evaluateUndef(ctx, scope);
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		ensureExpression();
		return expression.evaluateBoolean(ctx, scope);
	}

	@Override
	public String evaluateString(EvaluationContext ctx, Scope scope) throws EvaluationException {
		ensureExpression();
		return expression.evaluateString(ctx, scope);
	}

	@Override
	public long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException {
		ensureExpression();
		return expression.evaluateInteger(ctx, scope);
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		ensureExpression();
		return expression.evaluateList(ctx, scope);
	}

	@Override
	public Map<String,Object> evaluateMap(EvaluationContext ctx, Scope scope) throws EvaluationException {
		ensureExpression();
		return expression.evaluateMap(ctx, scope);
	}

	@Override
	public Function evaluateFunction(EvaluationContext ctx, Scope scope) throws EvaluationException {
		ensureExpression();
		return expression.evaluateFunction(ctx, scope);
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope, ValueType type) throws EvaluationException {
		ensureExpression();
		return expression.evaluate(ctx, scope, type);
	}

	private void ensureExpression() throws EvaluationException {
		if (expression != null) {
			return;
		}
		try {
			Class<?> klass = Class.forName(address);
			if (!Expression.class.isAssignableFrom(klass)) {
				throw new EvaluationException(this, "native class is not an expression");
			}
			expression = (Expression) klass.newInstance();
		}
		catch (ClassNotFoundException|InstantiationException|IllegalAccessException e) {
			throw new EvaluationException(expression, e);
		}
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		ensureExpression();
		return expression.isPureConstant();
	}

	@Override
	public Expression reduce() throws EvaluationException {
		ensureExpression();
		expression = expression.reduce();
		return this;
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
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
		Native other = (Native) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		}
		else if (!address.equals(other.address))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		sb.append("native ");
		stringConstant(sb, address);
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.keyword("native ")
		.string(address);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.ATOM;
	}
}
