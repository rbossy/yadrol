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

import java.util.LinkedHashMap;
import java.util.Map;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

public class MapConstructor extends AbstractMapExpression {
	private final Map<String,Expression> entries;

	public MapConstructor(Location location, Map<String,Expression> entries) {
		super(location);
		this.entries = entries;
	}

	public Map<String,Expression> getEntries() {
		return entries;
	}

	public void addEntry(String key, Expression value) {
		entries.put(key, value);
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return isPureConstant(entries);
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new MapConstructor(getLocation(), substituteVariables(entries, scope));
	}

	@Override
	public AbstractMapExpression reduce() throws EvaluationException {
		if (isPureConstant()) {
			return pureConstructor();
		}
		for (Map.Entry<String,Expression> e : entries.entrySet()) {
			Expression expr = e.getValue().reduce();
			e.setValue(expr);
		}
		return this;
	}

	@Override
	public Map<String,Object> evaluateMap(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Map<String,Object> result = new LinkedHashMap<String,Object>();
			for (Map.Entry<String,Expression> e : entries.entrySet()) {
				String key = e.getKey();
				Expression expr = e.getValue();
				Object v = expr.evaluate(ctx, scope);
				result.put(key, v);
			}
			return result;
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public boolean isAssignable() {
		for (Expression e : entries.values()) {
			if (!e.isAssignable()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void assign(EvaluationContext ctx, Scope scope, Object value) throws EvaluationException {
		try {
			Map<String,Object> map = ctx.valueToMap(scope, value);
			for (Map.Entry<String,Expression> e : entries.entrySet()) {
				Expression expr = e.getValue();
				Object v = map.get(e.getKey());
				expr.assign(ctx, scope, v);
			}
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
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
		MapConstructor other = (MapConstructor) obj;
		if (entries == null) {
			if (other.entries != null)
				return false;
		}
		else if (!entries.equals(other.entries))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		sb.append("{ ");
		expressionMapToString(sb, entries, false);
		sb.append(" }");
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.ATOM;
	}
}
