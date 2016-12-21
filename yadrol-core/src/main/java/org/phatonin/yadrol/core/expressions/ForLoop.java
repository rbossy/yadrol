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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.ContainerVisitor;

public class ForLoop extends AbstractExpression {
	public static final String DEFAULT_ITEM_VARIABLE = "_";
	
	private final String indexVariable;
	private final String itemVariable;
	private final Expression out;
	private final Expression container;
	private final Expression condition;

	public ForLoop(String indexVariable, String itemVariable, Expression out, Expression container, Expression condition) {
		super(out.getLocation());
		this.indexVariable = indexVariable;
		this.itemVariable = itemVariable;
		this.out = out;
		this.container = container;
		this.condition = condition;
	}

	public String getIndexVariable() {
		return indexVariable;
	}

	public String getItemVariable() {
		return itemVariable;
	}

	public Expression getOut() {
		return out;
	}

	public Expression getContainer() {
		return container;
	}

	public Expression getCondition() {
		return condition;
	}

	@Override
	public Expression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureExpression();
		}
		return new ForLoop(indexVariable, itemVariable, out.reduce(), container.reduce(), condition.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		if (!container.isPureConstant()) {
			return false;
		}
		List<Object> l = container.evaluateList(null, null);
		if (l.isEmpty()) {
			return true;
		}
		if (!condition.isPureConstant()) {
			return false;
		}
		if (!condition.evaluateBoolean(null, null)) {
			return true;
		}
		return out.isPureConstant();
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new ForLoop(indexVariable, itemVariable, out.substituteVariables(scope), container.substituteVariables(scope), condition.substituteVariables(scope));
	}

	@Override
	public Object evaluate(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Object container = this.container.evaluate(ctx, scope);
			ForLoopIterator iterator = new ContainerIterator().visit(container, null);
			Scope loopScope = new Scope(scope);
			while (iterator.next()) {
				if (indexVariable != null) {
					loopScope.setVariable(indexVariable, iterator.getIndex());
				}
				loopScope.setVariable(itemVariable, iterator.getItem());
				if (condition.evaluateBoolean(ctx, loopScope)) {
					Object out = this.out.evaluate(ctx, loopScope);
					iterator.handleOutput(out);
				}
			}
			return iterator.getResult();
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}
	
	private static interface ForLoopIterator {
		boolean next();
		Object getIndex();
		Object getItem();
		void handleOutput(Object out);
		Object getResult();
	}
	
	private static class ListLoopIterator implements ForLoopIterator {
		private final List<Object> result;
		private final ListIterator<Object> iterator;
		private Object item;
		
		private ListLoopIterator(List<Object> list) {
			result = new ArrayList<Object>(list.size());
			iterator = list.listIterator();
		}
		
		@Override
		public boolean next() {
			if (iterator.hasNext()) {
				item = iterator.next();
				return true;
			}
			return false;
		}

		@Override
		public Object getIndex() {
			return (long) iterator.previousIndex() + 1;
		}

		@Override
		public Object getItem() {
			return item;
		}

		@Override
		public void handleOutput(Object out) {
			result.add(out);
		}

		@Override
		public Object getResult() {
			return result;
		}
	}
	
	private static class MapLoopIterator implements ForLoopIterator {
		private final Map<String,Object> result = new LinkedHashMap<String,Object>();
		private final Iterator<Map.Entry<String,Object>> iterator;
		private Map.Entry<String,Object> item;

		private MapLoopIterator(Map<String,Object> map) {
			iterator = map.entrySet().iterator();
		}
		
		@Override
		public boolean next() {
			if (iterator.hasNext()) {
				item = iterator.next();
				return true;
			}
			return false;
		}
		
		@Override
		public Object getIndex() {
			return item.getKey();
		}
		
		@Override
		public Object getItem() {
			return item.getValue();
		}
		
		@Override
		public void handleOutput(Object out) {
			result.put(item.getKey(), out);
		}
		
		@Override
		public Object getResult() {
			return result;
		}
	}
	
	private class ContainerIterator extends ContainerVisitor<ForLoopIterator,Void,EvaluationException> {
		@Override
		public ForLoopIterator visitUndef(Void param) throws EvaluationException {
			throw new EvaluationException(ForLoop.this, "invalid loop: undef");
		}

		@Override
		protected ForLoopIterator visitScalar(Object value, Void param) throws EvaluationException {
			throw new EvaluationException(ForLoop.this, "invalid loop: " + value);
		}

		@Override
		public ForLoopIterator visit(List<Object> value, Void param) {
			return new ListLoopIterator(value);
		}

		@Override
		public ForLoopIterator visit(Map<String,Object> value, Void param) {
			return new MapLoopIterator(value);
		}
	};

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + ((indexVariable == null) ? 0 : indexVariable.hashCode());
		result = prime * result + ((itemVariable == null) ? 0 : itemVariable.hashCode());
		result = prime * result + ((container == null) ? 0 : container.hashCode());
		result = prime * result + ((out == null) ? 0 : out.hashCode());
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
		ForLoop other = (ForLoop) obj;
		if (condition == null) {
			if (other.condition != null)
				return false;
		}
		else if (!condition.equals(other.condition))
			return false;
		if (indexVariable == null) {
			if (other.indexVariable != null)
				return false;
		}
		else if (!indexVariable.equals(other.indexVariable))
			return false;
		if (itemVariable == null) {
			if (other.itemVariable != null)
				return false;
		}
		else if (!itemVariable.equals(other.itemVariable))
			return false;
		if (container == null) {
			if (other.container != null)
				return false;
		}
		else if (!container.equals(other.container))
			return false;
		if (out == null) {
			if (other.out != null)
				return false;
		}
		else if (!out.equals(other.out))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		if (new Variable(Location.NONE, itemVariable).equals(out)) {
			sb.append("for ");
		}
		else {
			out.toString(sb, Precedence.OR);
			sb.append(" for ");
		}
		if (indexVariable != null) {
			identifierToString(sb, indexVariable);
			sb.append(", ");
		}
		identifierToString(sb, itemVariable);
		sb.append(" in ");
		container.toString(sb, Precedence.OR);
		if (!new BooleanConstant(Location.NONE, true).equals(condition)) {
			sb.append(" if ");
			condition.toString(sb, Precedence.OR);
		}
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.CONTROL;
	}
}
