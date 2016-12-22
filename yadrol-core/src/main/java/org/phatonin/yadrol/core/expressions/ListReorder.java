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
import java.util.List;
import java.util.Random;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.ValueComparator;

/**
 * <code>sort list</code>
 * <code>shuffle list</code>
 * <code>revert list</code>
 * 
 *
 */
public class ListReorder extends AbstractListExpression {
	public static enum Operator {
		SORT {
			@Override
			public void reorder(EvaluationContext ctx, List<Object> list) {
				Collections.sort(list, ValueComparator.INSTANCE);
			}

			@Override
			public String toString() {
				return "sorted";
			}

			@Override
			public boolean canBePureConstant() {
				return true;
			}
		},
		
		SHUFFLE {
			@Override
			public void reorder(EvaluationContext ctx, List<Object> list) {
				Random rnd = ctx.getRandom();
				Collections.shuffle(list, rnd);				
			}

			@Override
			public String toString() {
				return "shuffled";
			}

			@Override
			public boolean canBePureConstant() {
				return false;
			}
		},
		
		REVERT {
			@Override
			public void reorder(EvaluationContext ctx, List<Object> list) {
				Collections.reverse(list);
			}

			@Override
			public String toString() {
				return "reversed";
			}

			@Override
			public boolean canBePureConstant() {
				return true;
			}
		};
		
		public abstract void reorder(EvaluationContext ctx, List<Object> list);
		
		public abstract boolean canBePureConstant();
		
		public static Operator fromString(String op) {
			switch (op) {
				case "sorted": return SORT;
				case "reversed": return REVERT;
				case "shuffled": return SHUFFLE;
			}
			throw new IllegalArgumentException("unknown list reorder operator: " + op);
		}
	}
	
	private final Operator operator;
	private final Expression list;

	public ListReorder(Location location, Operator operator, Expression list) {
		super(location);
		this.operator = operator;
		this.list = list;
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return operator.canBePureConstant() && list.isPureConstant();
	}

	@Override
	public List<Object> evaluateList(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			List<Object> result = new ArrayList<Object>(list.evaluateList(ctx, scope));
			operator.reorder(ctx, result);
			return result;
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public AbstractListExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new ListReorder(getLocation(), operator, list.reduce());
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new ListReorder(getLocation(), operator, list.substituteVariables(scope));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
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
		ListReorder other = (ListReorder) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		}
		else if (!list.equals(other.list))
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		unaryOperator(sb, operator.toString(), list, Precedence.SUBSCRIPT);
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.UNARY;
	}
}
