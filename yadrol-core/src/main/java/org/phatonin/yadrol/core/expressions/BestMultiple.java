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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.expressions.Best.Operator;
import org.phatonin.yadrol.core.values.ValueComparator;

/**
 * <code>highest n of expression</code>
 * <code>lowest n of expression</code>
 * 
 *
 */
public class BestMultiple extends AbstractListExpression {
	private final Best.Operator operator;
	private final Expression n;
	private final Expression expression;

	public BestMultiple(Location location, Operator operator, Expression n, Expression expression) {
		super(location);
		this.operator = operator;
		this.n = n;
		this.expression = expression;
	}

	public Best.Operator getOperator() {
		return operator;
	}

	public Expression getN() {
		return n;
	}

	public Expression getExpression() {
		return expression;
	}

	@Override
	public AbstractListExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new BestMultiple(getLocation(), operator, n.reduce(), expression.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(n, expression);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new BestMultiple(getLocation(), operator, n.substituteVariables(scope), expression.substituteVariables(scope));
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			int n = (int) this.n.evaluateInteger(ctx, scope);
			List<Object> list = expression.evaluateList(ctx, scope);
			if (n <= 0) {
				return new ArrayList<Object>();
			}
			if (list.size() <= n) {
				return new ArrayList<Object>(list);
			}
			List<Integer> indexes = getSortedIndexes(list);
			indexes = operator.select(indexes, n);
			Collections.sort(indexes);
			List<Object> result = new ArrayList<Object>(n);
			for (int i : indexes) {
				Object v = list.get(i);
				result.add(v);
			}
			return result;
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	private static List<Integer> getSortedIndexes(List<Object> list) {
		List<Integer> result = new ArrayList<Integer>(list.size());
		for (int i = 0; i < list.size(); ++i) {
			result.add(i);
		}
		Comparator<Integer> comp = new IndexComparator(list);
		Collections.sort(result, comp);
		return result;
	}
	
	private static class IndexComparator implements Comparator<Integer> {
		private final List<Object> list;
		
		private IndexComparator(List<Object> list) {
			super();
			this.list = list;
		}

		@Override
		public int compare(Integer o1, Integer o2) {
			Object v1 = list.get(o1);
			Object v2 = list.get(o2);
			int r = ValueComparator.INSTANCE.compare(v1, v2);
			if (r != 0) {
				return r;
			}
			return o1.compareTo(o2);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((n == null) ? 0 : n.hashCode());
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
		BestMultiple other = (BestMultiple) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		}
		else if (!expression.equals(other.expression))
			return false;
		if (n == null) {
			if (other.n != null)
				return false;
		}
		else if (!n.equals(other.n))
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.keyword(operator.toString())
		.space()
		.expression(n, Precedence.ASSIGN)
		.keyword(" of ")
		.expression(expression, Precedence.DRAW);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.BEST;
	};
}
