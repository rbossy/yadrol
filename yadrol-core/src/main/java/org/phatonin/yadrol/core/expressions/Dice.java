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

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.Roller;

/**
 * <code>NdX</code>
 * 
 *
 */
public class Dice extends AbstractListExpression {
	private final Expression n;
	private final Expression type;
	
	public Dice(Expression n, Expression type) {
		super(n.getLocation());
		this.n = n;
		this.type = type;
	}

	public Expression getN() {
		return n;
	}

	public Expression getType() {
		return type;
	}

	@Override
	public AbstractListExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new Dice(n.reduce(), type.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return n.isPureConstant() && Die.isPureDiceType(type);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Dice(n.substituteVariables(scope), type.substituteVariables(scope));
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			long n = this.n.evaluateInteger(ctx, scope);
			Object type = this.type.evaluate(ctx, scope);
			List<Object> result = Roller.roll(this, ctx, n, type);
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
		result = prime * result + ((n == null) ? 0 : n.hashCode());
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
		Dice other = (Dice) obj;
		if (n == null) {
			if (other.n != null)
				return false;
		}
		else if (!n.equals(other.n))
			return false;
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
		StringBuilder operator = new StringBuilder(3);
		if (n.requiresSpaceAsDiceNumber()) {
			operator.append(' ');
		}
		operator.append('d');
		if (type.requiresSpaceAsDiceType()) {
			operator.append(' ');
		}
		binaryOperator(sb, operator, n, type, Precedence.UNARY);
	}
	
	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.expression(n, Precedence.UNARY);
		if (n.requiresSpaceAsDiceNumber()) {
			stringer.space();
		}
		stringer.operator("d");
		if (type.requiresSpaceAsDiceNumber()) {
			stringer.space();
		}
		stringer.expression(type, Precedence.UNARY);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.DICE;
	}
}
