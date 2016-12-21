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

/**
 * Function objects represent yadrol function values.
 *
 */
public class Function {
	private final Scope parentScope;
	private final List<String> positionalArgs;
	private final Map<String,Object> namedArgs;
	private final Expression body;
	private final Object owner;

	/**
	 * Create a function.
	 * @param parentScope the scope in which this function is declared.
	 * @param positionalArgs positional arguments.
	 * @param namedArgs named arguments.
	 * @param body this function body.
	 * @param owner object value to which this function belongs, must be either a list or a map.
	 */
	public Function(Scope parentScope, List<String> positionalArgs, Map<String,Object> namedArgs, Expression body, Object owner) {
		super();
		this.parentScope = parentScope;
		this.positionalArgs = positionalArgs;
		this.namedArgs = namedArgs;
		this.body = body;
		this.owner = owner;
	}

	/**
	 * Create a function with the same signature and body that belongs to the specified object.
	 * If the specified owner object is the same as this function owner, then this function is returned.
	 * @param owner owner of the returned function, must be either a list or a map.
	 * @return the function owned by the specified object.
	 */
	public Function reassignOwner(Object owner) {
		if (owner == this.owner) {
			return this;
		}
		return new Function(parentScope, positionalArgs, namedArgs, body, owner);
	}

	/**
	 * Returns the scope in which this function was declared.
	 * @return the scope in which this function was declared.
	 */
	public Scope getParentScope() {
		return parentScope;
	}

	/**
	 * Returns the positional arguments of this function.
	 * @return the positional arguments of this function, the returned list is unmodifiable.
	 */
	public List<String> getPositionalArgs() {
		return Collections.unmodifiableList(positionalArgs);
	}

	/**
	 * Returns the named arguments of this function.
	 * @return the named arguments of this function, the returned map is unmodifiable.
	 */
	public Map<String,Object> getNamedArgs() {
		return Collections.unmodifiableMap(namedArgs);
	}

	/**
	 * Returns the body of this function.
	 * @return the body of this function.
	 */
	public Expression getBody() {
		return body;
	}

	/**
	 * Returns the object to which this function belongs.
	 * @return the object to which this function belongs.
	 */
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
	
	/**
	 * Call this function with the specified arguments.
	 * @param call call expression that requires this function.
	 * @param ctx evaluation context.
	 * @param positionalArgs positional arguments.
	 * @param namedArgs named arguments.
	 * @param depth call depth.
	 * @return the result of the call.
	 * @throws EvaluationException if the call has raised an exception, or if an argument is specified twice, or if a named argument does not match any of this function arguments.
	 */
	public Object call(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluate(ctx, scope);
	}

	/**
	 * Call this function as undef with the specified arguments.
	 * @param call call expression that requires this function.
	 * @param ctx evaluation context.
	 * @param positionalArgs positional arguments.
	 * @param namedArgs named arguments.
	 * @param depth call depth.
	 * @throws EvaluationException if the call has raised an exception, or if an argument is specified twice, or if a named argument does not match any of this function arguments.
	 */
	public void callUndef(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		body.evaluateUndef(ctx, scope);
	}
	
	/**
	 * Call this function as a boolean with the specified arguments.
	 * @param call call expression that requires this function.
	 * @param ctx evaluation context.
	 * @param positionalArgs positional arguments.
	 * @param namedArgs named arguments.
	 * @param depth call depth.
	 * @return the result of the call.
	 * @throws EvaluationException if the call has raised an exception, or if an argument is specified twice, or if a named argument does not match any of this function arguments.
	 */
	public boolean callBoolean(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluateBoolean(ctx, scope);
	}
	
	/**
	 * Call this function as a string with the specified arguments.
	 * @param call call expression that requires this function.
	 * @param ctx evaluation context.
	 * @param positionalArgs positional arguments.
	 * @param namedArgs named arguments.
	 * @param depth call depth.
	 * @return the result of the call.
	 * @throws EvaluationException if the call has raised an exception, or if an argument is specified twice, or if a named argument does not match any of this function arguments.
	 */
	public String callString(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluateString(ctx, scope);
	}
	
	/**
	 * Call this function as an integer with the specified arguments.
	 * @param call call expression that requires this function.
	 * @param ctx evaluation context.
	 * @param positionalArgs positional arguments.
	 * @param namedArgs named arguments.
	 * @param depth call depth.
	 * @return the result of the call.
	 * @throws EvaluationException if the call has raised an exception, or if an argument is specified twice, or if a named argument does not match any of this function arguments.
	 */
	public long callInteger(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluateInteger(ctx, scope);
	}
	
	/**
	 * Call this function as a alist with the specified arguments.
	 * @param call call expression that requires this function.
	 * @param ctx evaluation context.
	 * @param positionalArgs positional arguments.
	 * @param namedArgs named arguments.
	 * @param depth call depth.
	 * @return the result of the call.
	 * @throws EvaluationException if the call has raised an exception, or if an argument is specified twice, or if a named argument does not match any of this function arguments.
	 */
	public List<Object> callList(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluateList(ctx, scope);
	}
	
	/**
	 * Call this function as a map with the specified arguments.
	 * @param call call expression that requires this function.
	 * @param ctx evaluation context.
	 * @param positionalArgs positional arguments.
	 * @param namedArgs named arguments.
	 * @param depth call depth.
	 * @return the result of the call.
	 * @throws EvaluationException if the call has raised an exception, or if an argument is specified twice, or if a named argument does not match any of this function arguments.
	 */
	public Map<String,Object> callMap(Expression call, EvaluationContext ctx, List<Object> positionalArgs, Map<String,Object> namedArgs, long depth) throws EvaluationException {
		Scope scope = createScope(call, positionalArgs, namedArgs, depth);
		return body.evaluateMap(ctx, scope);
	}
	
	/**
	 * Call this function as a function with the specified arguments.
	 * @param call call expression that requires this function.
	 * @param ctx evaluation context.
	 * @param positionalArgs positional arguments.
	 * @param namedArgs named arguments.
	 * @param depth call depth.
	 * @return the result of the call.
	 * @throws EvaluationException if the call has raised an exception, or if an argument is specified twice, or if a named argument does not match any of this function arguments.
	 */
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
