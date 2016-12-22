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

/**
 * <code>left === right</code>
 * <code>left !== right</code>
 * <code>left == right</code>
 * <code>left != right</code>
 * <code>left &lt; right</code>
 * <code>left &gt; right</code>
 * <code>left &lt;= right</code>
 * <code>left &gt;= right</code>
 * 
 *
 */
public class Comparison extends AbstractBooleanExpression {
	public static interface Operator {
		boolean test(EvaluationContext ctx, Scope scope, Expression left, Expression right) throws EvaluationException;

		public static enum General implements Operator {
			EQ {
				@Override
				public boolean test(Object left, Object right) {
					if (left == null) {
						return right == null;
					}
					return left.equals(right);
				}

				@Override
				public String toString() {
					return "===";
				}
			},
			
			NE {
				@Override
				public boolean test(Object left, Object right) {
					if (left == null) {
						return right != null;
					}
					return !left.equals(right);
				}

				@Override
				public String toString() {
					return "!==";
				}
			};
			
			public static General get(String op) {
				switch (op) {
					case "===": return EQ;
					case "!==": return NE;
				}
				throw new RuntimeException();
			}

			@Override
			public boolean test(EvaluationContext ctx, Scope scope, Expression left, Expression right) throws EvaluationException {
				Object lv = left.evaluate(ctx, scope);
				Object rv = right.evaluate(ctx, scope);
				return test(lv, rv);
			}
			
			public abstract boolean test(Object left, Object right);
		}
		
		public static enum Numeric implements Operator {
			EQ {
				@Override
				public boolean test(long left, long right) {
					return left == right;
				}

				@Override
				public String toString() {
					return "==";
				}
			},
			
			NE {
				@Override
				public boolean test(long left, long right) {
					return left != right;
				}

				@Override
				public String toString() {
					return "!=";
				}
			},
			
			LT {
				@Override
				public boolean test(long left, long right) {
					return left < right;
				}

				@Override
				public String toString() {
					return "<";
				}
			},
			
			GT {
				@Override
				public boolean test(long left, long right) {
					return left > right;
				}

				@Override
				public String toString() {
					return ">";
				}
			},
			
			LE {
				@Override
				public boolean test(long left, long right) {
					return left <= right;
				}

				@Override
				public String toString() {
					return "<=";
				}
			},
			
			GE {
				@Override
				public boolean test(long left, long right) {
					return left >= right;
				}

				@Override
				public String toString() {
					return ">=";
				}
			};
			
			public static Numeric get(String op) {
				switch (op) {
					case "==": return EQ;
					case "!=": return NE;
					case "<" : return LT;
					case ">" : return GT;
					case "<=": return LE;
					case ">=": return GE;
				}
				throw new RuntimeException();
			}

			@Override
			public boolean test(EvaluationContext ctx, Scope scope, Expression left, Expression right) throws EvaluationException {
				long lv = left.evaluateInteger(ctx, scope);
				long rv = right.evaluateInteger(ctx, scope);
				return test(lv, rv);
			}
			
			public abstract boolean test(long left, long right);
		}
	}
	
	private final Operator operator;
	private final Expression left;
	private final Expression right;
	
	public Comparison(Location location, Operator operator, Expression left, Expression right) {
		super(location);
		this.operator = operator;
		this.left = left;
		this.right = right;
	}
	
	public static Operator operatorFromString(String op) {
		switch (op) {
			case "===": return Operator.General.EQ;
			case "!==": return Operator.General.NE;
			case "==": return Operator.Numeric.EQ;
			case "!=": return Operator.Numeric.NE;
			case "<" : return Operator.Numeric.LT;
			case ">" : return Operator.Numeric.GT;
			case "<=": return Operator.Numeric.LE;
			case ">=": return Operator.Numeric.GE;				
		}
		throw new RuntimeException();
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
	public AbstractBooleanExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new Comparison(getLocation(), operator, left.reduce(), right.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(left, right);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Comparison(getLocation(), operator, left.substituteVariables(scope), right.substituteVariables(scope));
	}

	@Override
	public boolean evaluateBoolean(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			return operator.test(ctx, scope, left, right);
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
		Comparison other = (Comparison) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		}
		else if (!left.equals(other.left))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		}
		else if (!operator.equals(other.operator))
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
		binaryOperator(sb, operator.toString(), left, right, Precedence.INDEXOF);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.COMPARISON;
	}
}
