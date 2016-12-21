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
import org.phatonin.yadrol.core.values.Roller;
import org.phatonin.yadrol.core.values.ValueType;

public class Die extends AbstractExpression {
	private final Expression type;

	public Die(Location location, Expression type) {
		super(location);
		this.type = type;
	}

	public Expression getType() {
		return type;
	}

	@Override
	public Expression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new Die(getLocation(), type.reduce());
	}
	
	static boolean isPureDiceType(Expression type) throws EvaluationException {
		if (!type.isPureConstant()) {
			return false;
		}
		Object t = type.evaluate(null, null);
		switch (ValueType.get(t)) {
			case BOOLEAN:
			case INTEGER:
			case ANY:
			case LIST: return false;
			default: return true;
		}
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureDiceType(type);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Die(getLocation(), type.substituteVariables(scope));
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Object type = this.type.evaluate(ctx, scope);
			Object result = Roller.roll(this, ctx, type);
			ctx.logDice(type, result);
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
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Die other = (Die) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		}
		else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		String operator = type.requiresSpaceAsDiceType() ? "d " : "d";
		unaryOperator(sb, operator, type, Precedence.UNARY);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.DICE;
	}
}
