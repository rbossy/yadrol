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

import java.util.Map;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

public class ScopeVariables extends AbstractMapExpression {
	public static enum Operator {
		LOCAL {
			@Override
			public Scope getScope(EvaluationContext ctx, Scope scope) {
				return scope;
			}

			@Override
			public String toString() {
				return "local";
			}
		},
		
		OUTER {
			@Override
			public Scope getScope(EvaluationContext ctx, Scope scope) {
				Scope result = scope.getParent();
				if (result == null) {
					return new Scope();
				}
				return result;
			}

			@Override
			public String toString() {
				return "outer";
			}
		},

		GLOBAL {
			@Override
			public Scope getScope(EvaluationContext ctx, Scope scope) {
				Scope result = scope;
				while (result.getParent() != null) {
					result = result.getParent();
				}
				return result;
			}

			@Override
			public String toString() {
				return "global";
			}
		};
		
		public abstract Scope getScope(EvaluationContext ctx, Scope scope);
		
		public static Operator fromString(String operator) {
			switch (operator) {
				case "local": return LOCAL;
				case "outer": return OUTER;
				case "global": return GLOBAL;
			}
			throw new RuntimeException();
		}
	}

	private final Operator operator;
	
	public ScopeVariables(Location location, Operator operator) {
		super(location);
		this.operator = operator;
	}

	public Operator getOperator() {
		return operator;
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return false;
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return this;
	}

	@Override
	public Map<String,Object> evaluateMap(EvaluationContext ctx, Scope scope) {
		Scope resultScope = operator.getScope(ctx, scope);
		return resultScope.getVariables();
	}

	@Override
	public AbstractMapExpression reduce() throws EvaluationException {
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		ScopeVariables other = (ScopeVariables) obj;
		if (operator != other.operator)
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		sb.append(operator.toString());
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.ATOM;
	}
}
