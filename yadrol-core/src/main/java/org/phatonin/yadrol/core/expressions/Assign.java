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
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

/**
 * <code>lvalue = rvalue</code>
 * 
 *
 */
public class Assign extends AbstractExpression {
	private final Expression lvalue;
	private final Expression rvalue;
	
	public Assign(Expression lvalue, Expression rvalue) {
		super(lvalue.getLocation());
		this.lvalue = lvalue;
		this.rvalue = rvalue;
	}

	public Expression getLvalue() {
		return lvalue;
	}

	public Expression getRvalue() {
		return rvalue;
	}

	@Override
	public Expression reduce() throws EvaluationException {
		return new Assign(lvalue, rvalue.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return false;
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Assign(lvalue, rvalue.substituteVariables(scope));
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Object value = rvalue.evaluate(ctx, scope);
			lvalue.assign(ctx, scope, value);
			return value;
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lvalue == null) ? 0 : lvalue.hashCode());
		result = prime * result + ((rvalue == null) ? 0 : rvalue.hashCode());
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
		Assign other = (Assign) obj;
		if (lvalue == null) {
			if (other.lvalue != null)
				return false;
		}
		else if (!lvalue.equals(other.lvalue))
			return false;
		if (rvalue == null) {
			if (other.rvalue != null)
				return false;
		}
		else if (!rvalue.equals(other.rvalue))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		binaryOperator(sb, " = ", lvalue, rvalue, Precedence.CONTROL);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.ASSIGN;
	}
}
