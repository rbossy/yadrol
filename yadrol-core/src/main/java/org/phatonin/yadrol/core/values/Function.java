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

package org.phatonin.yadrol.core.values;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.Scope;

public class Function {
	private final Scope parentScope;
//	private final Map<String,Object> arguments;
	private final List<String> positionalArgs;
	private final Map<String,Object> namedArgs;
	private final Expression body;
	private final Object owner;

	public Function(Scope parentScope, List<String> positionalArgs, Map<String,Object> namedArgs, Expression body, Object owner) {
		super();
		this.parentScope = parentScope;
		this.positionalArgs = positionalArgs;
		this.namedArgs = namedArgs;
		this.body = body;
		this.owner = owner;
	}

	public Function reassignOwner(Object owner) {
		if (owner == this.owner) {
			return this;
		}
		return new Function(parentScope, positionalArgs, namedArgs, body, owner);
	}

	public Scope getParentScope() {
		return parentScope;
	}

	public List<String> getPositionalArgs() {
		return Collections.unmodifiableList(positionalArgs);
	}

	public Map<String,Object> getNamedArgs() {
		return Collections.unmodifiableMap(namedArgs);
	}

	public Expression getBody() {
		return body;
	}

	public Object getOwner() {
		return owner;
	}
	
	private Map<String,Object> createVariables(Expression call, List<Object> positionalArgs, Map<String,Object> namedArgs) throws EvaluationException {
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		Iterator<Object> positionalIt = positionalArgs.iterator();
		for (String name : this.positionalArgs) {
			if (!positionalIt.hasNext()) {
				break;
			}
			Object value = positionalIt.next();
			result.put(name, value);
		}
		for (Map.Entry<String,Object> e : this.namedArgs.entrySet()) {
			if (!positionalIt.hasNext()) {
				break;
			}
			String name = e.getKey();
			Object value = positionalIt.next();
			result.put(name, value);
		}
		for (Map.Entry<String,Object> e : namedArgs.entrySet()) {
			String name = e.getKey();
			if (result.containsKey(name)) {
				throw new EvaluationException(call, "argument " + name + " already set");
			}
			if (!(this.positionalArgs.contains(name) || this.namedArgs.containsKey(name))) {
				throw new EvaluationException(call, "unknown argument " + name);
			}
			Object value = e.getValue();
			result.put(name, value);
		}
		for (Map.Entry<String,Object> e : this.namedArgs.entrySet()) {
			String name = e.getKey();
			if (!result.containsKey(name)) {
				Object value = e.getValue();
				result.put(name, value);
			}
		}
		return result;
	}

//	private Map<String,Object> createVariables(List<Object> positionalArgs, Map<String,Object> namedArgs) {
//		Map<String,Object> result = new LinkedHashMap<String,Object>();
//		Iterator<String> args = arguments.keySet().iterator();
//		for (Object v : positionalArgs) {
//			if (!args.hasNext()) {
//				break;
//			}
//			String name = args.next();
//			result.put(name, v);
//		}
//		for (Map.Entry<String,Object> e : namedArgs.entrySet()) {
//			String name = e.getKey();
//			if (arguments.containsKey(name)) {
//				result.put(name, e.getValue());
//			}
//		}
//		result.put("this", owner);
//		return result;
//	}
	
	private Scope createScope(Expression call, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Map<String,Object> vars = createVariables(call, positionalArgs, namedArgs);
		Collection<String> missingArgs = new LinkedHashSet<String>(this.positionalArgs);
		missingArgs.removeAll(vars.keySet());
		if (!missingArgs.isEmpty()) {
			throw new EvaluationException(call, "missing arguments " + missingArgs);
		}
		return new Scope(parentScope, vars, depth + 1);
	}
	
	public Object call(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluate(ctx, scope);
	}
	
	public void callUndef(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		body.evaluateUndef(ctx, scope);
	}
	
	public boolean callBoolean(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluateBoolean(ctx, scope);
	}
	
	public String callString(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluateString(ctx, scope);
	}
	
	public long callInteger(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluateInteger(ctx, scope);
	}
	
	public List<Object> callList(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluateList(ctx, scope);
	}
	
	public Map<String,Object> callMap(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluateMap(ctx, scope);
	}
	
	public Function callFunction(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluateFunction(ctx, scope);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((namedArgs == null) ? 0 : namedArgs.hashCode());
		result = prime * result + ((parentScope == null) ? 0 : parentScope.hashCode());
		result = prime * result + ((positionalArgs == null) ? 0 : positionalArgs.hashCode());
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
		Function other = (Function) obj;
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
		if (parentScope == null) {
			if (other.parentScope != null)
				return false;
		}
		else if (!parentScope.equals(other.parentScope))
			return false;
		if (positionalArgs == null) {
			if (other.positionalArgs != null)
				return false;
		}
		else if (!positionalArgs.equals(other.positionalArgs))
			return false;
		return true;
	}
}
