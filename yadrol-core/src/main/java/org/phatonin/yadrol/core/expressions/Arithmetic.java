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
 * <code>left + right</code>
 * <code>left - right</code>
 * <code>left * right</code
 * <code>left / right</code>
 * <code>left % right</code>
 * 
 *
 */
public class Arithmetic extends AbstractIntegerExpression {
	public static enum Operator {
		PLUS {
			@Override
			public long compute(long a, long b) {
				return a + b;
			}

			@Override
			public String toString() {
				return "+";
			}
		},
		
		MINUS {
			@Override
			public long compute(long a, long b) {
				return a - b;
			}

			@Override
			public String toString() {
				return "-";
			}
		},

		MULT {
			@Override
			public long compute(long a, long b) {
				return a * b;
			}

			@Override
			public String toString() {
				return "*";
			}
		},

		DIV {
			@Override
			public long compute(long a, long b) {
				return a / b;
			}

			@Override
			public String toString() {
				return "/";
			}
		},

		MOD {
			@Override
			public long compute(long a, long b) {
				return a % b;
			}

			@Override
			public String toString() {
				return "%";
			}
		};
		
		public static Operator fromString(String op) {
			switch (op) {
				case "+": return PLUS;
				case "-": return MINUS;
				case "*": return MULT;
				case "/": return DIV;
				case "%": return MOD;
			}
			throw new RuntimeException();
		}

		public abstract long compute(long a, long b);
	}
	
	private final Operator operator;
	private final Expression left;
	private final Expression right;

	public Arithmetic(Operator operator, Expression left, Expression right) {
		super(left.getLocation());
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	public Operator getOperator() {
		return operator;
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	@Override
	public AbstractIntegerExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		if (left.isPureConstant()) {
			long lvalue = left.evaluateInteger(null, null);
			if (lvalue == 1 && operator == Operator.MULT) {
				return new ConvertInteger(getLocation(), right).reduce();
			}
			return new Arithmetic(operator, new IntegerConstant(getLocation(), lvalue), right.reduce());
		}
		if (right.isPureConstant()) {
			long rvalue = right.evaluateInteger(null, null);
			if (rvalue == 1) {
				switch (operator) {
					case MULT:
					case DIV:
					case MOD: return new ConvertInteger(getLocation(), left).reduce();
					case PLUS:
					case MINUS: return new Arithmetic(operator, left.reduce(), new IntegerConstant(getLocation(), 1));
				}
			}
			return new Arithmetic(operator, left.reduce(), new IntegerConstant(getLocation(), rvalue));
		}
		return new Arithmetic(operator, left.reduce(), right.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(left, right);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Arithmetic(operator, left.substituteVariables(scope), right.substituteVariables(scope));
	}

	@Override
	public long evaluateInteger(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			long left = this.left.evaluateInteger(ctx, scope);
			long right = this.right.evaluateInteger(ctx, scope);
			return operator.compute(left, right);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		Arithmetic other = (Arithmetic) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		}
		else if (!left.equals(other.left))
			return false;
		if (operator != other.operator)
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		}
		else if (!right.equals(other.right))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		binaryOperator(sb, operator.toString(), left, right, getPrecedence());
	}
	
	@Override
	protected Precedence getPrecedence() {
		switch (operator) {
			case DIV:
			case MOD:
			case MULT: return Precedence.MULT;
			case MINUS:
			case PLUS: return Precedence.PLUS;
		}
		throw new RuntimeException();
	}
}
