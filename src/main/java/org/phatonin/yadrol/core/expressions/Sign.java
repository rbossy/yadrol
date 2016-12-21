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

public class Sign extends AbstractIntegerExpression {
	public static enum Operator {
		PLUS {
			@Override
			public long compute(long x) {
				return x;
			}

			@Override
			public String toString() {
				return "+";
			}
		},
		
		MINUS {
			@Override
			public long compute(long x) {
				return -x;
			}

			@Override
			public String toString() {
				return "-";
			}
		};
		
		public static Operator fromString(String op) {
			switch (op) {
				case "+": return PLUS;
				case "-": return MINUS;
			}
			throw new RuntimeException();
		}
		
		public abstract long compute(long x);
	}
	
	private final Operator operator;
	private final Expression operand;
	
	public Sign(Location location, Operator operator, Expression operand) {
		super(location);
		this.operator = operator;
		this.operand = operand;
	}

	public Operator getOperator() {
		return operator;
	}

	public Expression getOperand() {
		return operand;
	}

	@Override
	public AbstractIntegerExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureConstant();
		}
		if (operator == Operator.PLUS) {
			return new ConvertInteger(getLocation(), operand).reduce();
		}
		return new Sign(getLocation(), operator, operand.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return operand.isPureConstant();
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Sign(getLocation(), operator, operand.substituteVariables(scope));
	}

	@Override
	public long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			long x = operand.evaluateInteger(ctx, scope);
			return operator.compute(x);
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
		Sign other = (Sign) obj;
		if (operand == null) {
			if (other.operand != null)
				return false;
		}
		else if (!operand.equals(other.operand))
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		unaryOperator(sb, operator.toString(), operand, Precedence.BEST);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.SIGN;
	}
}
