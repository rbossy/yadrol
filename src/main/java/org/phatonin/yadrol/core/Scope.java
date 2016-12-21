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

package org.phatonin.yadrol.core;

import java.util.HashMap;
import java.util.Map;

public class Scope {
	private final Scope parent;
	private final Map<String,Object> variables;
	private final long depth;

	public Scope(Scope parent, Map<String,Object> variables, long depth) {
		super();
		this.parent = parent;
		this.variables = variables;
		this.depth = depth;
	}

	public Scope(Scope parent, Map<String,Object> variables) {
		this(parent, variables, parent == null ? 0 : parent.depth);
	}

	public Scope(Scope parent) {
		this(parent, new HashMap<String,Object>());
	}
	
	public Scope() {
		this(null);
	}
	
	public Scope getParent() {
		return parent;
	}

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

	public boolean hasVariable(String name) {
		for (Scope scope = this; scope != null; scope = scope.parent) {
			if (scope.variables.containsKey(name)) {
				return true;
			}
		}
		return false;
	}

	public Object getVariable(String name) {
		Scope scope = lookupVariable(name);
		return scope.variables.get(name);
	}

	public void setVariable(String name, Object value) {
		Scope scope = lookupVariable(name);
		scope.variables.put(name, value);
	}
	
	public Map<String,Object> getVariables() {
		return variables;
	}
}
