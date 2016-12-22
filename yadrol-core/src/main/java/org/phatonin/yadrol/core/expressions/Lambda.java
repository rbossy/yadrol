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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionListUtils;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.values.Function;

public class Lambda extends AbstractFunctionExpression {
	private final String[] positionalArgs;
	private final Map<String,Expression> namedArgs;
	private final Expression body;
	
	private Lambda(Location location, String[] positionalArgs, Map<String,Expression> namedArgs, Expression body) {
		super(location);
		this.positionalArgs = positionalArgs;
		this.namedArgs = namedArgs;
		this.body = body;
	}

	public Lambda(Location location, List<String> positionalArgs, Map<String,Expression> namedArgs, Expression body) {
		this(location, positionalArgs.toArray(new String[positionalArgs.size()]), namedArgs, body);
	}
	
	public String[] getPositionalArgs() {
		return positionalArgs;
	}

	public Map<String,Expression> getNamedArgs() {
		return namedArgs;
	}

	public Expression getBody() {
		return body;
	}

	@Override
	public AbstractFunctionExpression reduce() throws EvaluationException {
		return new Lambda(getLocation(), positionalArgs, reduce(namedArgs), body.reduce());
	}

	@Override
	public boolean isPureConstant() throws EvaluationException {
		return false;
	}

	@Override
	public Expression substituteVariables(Scope scope) {
		return new Lambda(getLocation(), positionalArgs, substituteVariables(namedArgs, scope), body.substituteVariables(scope));
	}

	@Override
	public Function evaluateFunction(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Map<String,Object> namedArgs = ExpressionListUtils.evaluate(this.namedArgs, ctx, scope);
			return new Function(scope, Arrays.asList(positionalArgs), namedArgs, body, null);
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((namedArgs == null) ? 0 : namedArgs.hashCode());
		result = prime * result + Arrays.hashCode(positionalArgs);
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
		Lambda other = (Lambda) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		}
		else if (!body.equals(other.body))
			return false;
		if (namedArgs == null) {
			if (other.namedArgs != null)
				return false;
		}
		else if (!namedArgs.equals(other.namedArgs))
			return false;
		if (!Arrays.equals(positionalArgs, other.positionalArgs))
			return false;
		return true;
	}

	@Override
	protected void toStringWithoutParen(StringBuilder sb) {
		sb.append("fun (");
		boolean notFirst = false;
		for (String arg : positionalArgs) {
			if (notFirst) {
				sb.append(", ");
			}
			else {
				notFirst = true;
			}
			identifierToString(sb, arg);
		}
		for (Map.Entry<String,Expression> e : namedArgs.entrySet()) {
			if (notFirst) {
				sb.append(", ");
			}
			else {
				notFirst = true;
			}
			identifierToString(sb, e.getKey());
			sb.append(": ");
			e.getValue().toString(sb, Precedence.SEQUENCE);
		}
		sb.append(") { ");
		body.toString(sb, Precedence.SEQUENCE);
		sb.append(" }");
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.ATOM;
	}
}
