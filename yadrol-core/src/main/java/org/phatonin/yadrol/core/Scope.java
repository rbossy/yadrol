/**
   Copyright 2016-2017, Robert Bossy

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

package org.phatonin.yadrol.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Scope objects represent variable scopes within which expressions are evaluated.
 * 
 *
 */
public class Scope {
	private final Scope parent;
	private final Map<String,Object> variables;
	private final long depth;

	/**
	 * Create a scope.
	 * @param parent parent scope (<code>null</code> if global).
	 * @param variables variables defined in this scope.
	 * @param depth depth of this scope.
	 */
	public Scope(Scope parent, Map<String,Object> variables, long depth) {
		super();
		this.parent = parent;
		this.variables = variables;
		this.depth = depth;
	}

	/**
	 * Create a scope with the same depth as its parent.
	 * @param parent parent scope (<code>null</code> if global).
	 * @param variables variables defined in this scope.
	 */
	public Scope(Scope parent, Map<String,Object> variables) {
		this(parent, variables, parent == null ? 0 : parent.depth);
	}

	/**
	 * Create a scope with the same depth as its parent and no defined variables.
	 * @param parent parent scope (<code>null</code> if global).
	 */
	public Scope(Scope parent) {
		this(parent, new HashMap<String,Object>());
	}
	
	/**
	 * Create a global scope.
	 */
	public Scope() {
		this(null);
	}
	
	/**
	 * Returns the parent scope of this scope.
	 * @return the parent scope of this scope.
	 */
	public Scope getParent() {
		return parent;
	}

	/**
	 * Returns the depth of this scope.
	 * @return the depth of this scope.
	 */
	public long getDepth() {
		return depth;
	}

	private Scope lookupVariable(String name) {
		for (Scope scope = this; scope != null; scope = scope.parent) {
			if (scope.variables.containsKey(name)) {
				return scope;
			}
		}
		return this;
	}

	/**
	 * Checks if a variable with the specified name is defined in this scope, or an ancestor scope.
	 * @param name
	 * @return
	 */
	public boolean hasVariable(String name) {
		for (Scope scope = this; scope != null; scope = scope.parent) {
			if (scope.variables.containsKey(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the value of the variable with the specified name in this scope or an ancestor scope.
	 * @param name
	 * @return the variable value or <code>null</code> if it is not defined.
	 */
	public Object getVariable(String name) {
		Scope scope = lookupVariable(name);
		return scope.variables.get(name);
	}

	/**
	 * Set the value of the specified variable.
	 * If the variable is already defined in an ancestor scope, then its value is overwritten.
	 * Otherwise this method creates a new variable in this scope.
	 * @param name
	 * @param value
	 */
	public void setVariable(String name, Object value) {
		Scope scope = lookupVariable(name);
		scope.variables.put(name, value);
	}
	
	/**
	 * Returns all variables defined in this scope.
	 * @return all variables defined in this scope. The returned map is modifiable and changes are reflected in the scope.
	 */
	public Map<String,Object> getVariables() {
		return variables;
	}
	
	public void clear() {
		variables.clear();
	}
}
