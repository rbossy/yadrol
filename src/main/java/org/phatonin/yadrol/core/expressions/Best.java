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
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.ValueComparator;

public class Best extends AbstractExpression {
	public static enum Operator {
		HIGHEST {
			@Override
			public int getBest(List<Object> list) {
				Object best = list.get(0);
				int result = 0;
				for (int i = 1; i < list.size(); ++i) {
					Object v = list.get(i);
					if (ValueComparator.INSTANCE.compare(v, best) > 0) {
						result = i;
						best = v;
					}
				}
				return result;
			}

			@Override
			public <T> List<T> select(List<T> list, int n) {
				final int len = list.size();
				return list.subList(len - n, len);
			}

			@Override
			public String toString() {
				return "highest";
			}
		},
		
		LOWEST {
			@Override
			public int getBest(List<Object> list) {
				Object best = list.get(0);
				int result = 0;
				for (int i = 1; i < list.size(); ++i) {
					Object v = list.get(i);
					if (ValueComparator.INSTANCE.compare(v, best) < 0) {
						best = v;
						result = i;
					}
				}
				return result;
			}

			@Override
			public <T> List<T> select(List<T> list, int n) {
				return list.subList(0, n);
			}

			@Override
			public String toString() {
				return "lowest";
			}
		};
		
		public static Operator fromString(String op) {
			switch (op) {
				case "highest": return HIGHEST;
				case "lowest": return LOWEST;
			}
			throw new RuntimeException();
		}
		
		public abstract int getBest(List<Object> list);
		public abstract <T> List<T> select(List<T> list, int n);
	}
	
	private final Operator operator;
	private final Expression expression;
	
	public Best(Location location, Operator operator, Expression expression) {
		super(location);
		this.operator = operator;
		this.expression = expression;
	}

	public Operator getOperator() {
		return operator;
	}

	public Expression getExpression() {
		return expression;
	}

	@Override
	public Expression reduce() throws EvaluationException {
		if (expression.isPureConstant()) {
			return pureExpression();
		}
		return new Best(getLocation(), operator, expression.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return expression.isPureConstant();
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Best(getLocation(), operator, expression.substituteVariables(scope));
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			List<Object> list = expression.evaluateList(ctx, scope);
			if (list.isEmpty()) {
				return null;
			}
			int best = operator.getBest(list);
			return list.get(best);
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
		Best other = (Best) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		}
		else if (!expression.equals(other.expression))
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		sb.append(operator.toString());
		sb.append(" of ");
		expression.toString(sb, Precedence.DRAW);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.BEST;
	}
}
