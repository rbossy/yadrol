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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionStringer;
import org.phatonin.yadrol.core.ImportManager;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.Precedence;
import org.phatonin.yadrol.core.Scope;

/**
 * <code>import [alias =] "address" [: name1, name2, ...]</code>
 * 
 *
 */
public class Import extends AbstractUndefExpression {
	private final String address;
	private final String[] names;
	private final String alias;

	public Import(Location location, String address, List<String> names, String alias) {
		super(location);
		this.address = address;
		this.names = names.toArray(new String[names.size()]);
		this.alias = alias;
	}

	public String getAddress() {
		return address;
	}

	public String[] getNames() {
		return names;
	}

	public String getAlias() {
		return alias;
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
	public void evaluateUndef(EvaluationContext ctx, Scope scope) throws EvaluationException {
		try {
			Map<String,Object> namespace = getNamespace(scope);
			ImportManager importManager = ctx.getImportManager();
			Map<String,Object> defined = importManager.resolveImport(this, ctx, address);
			Set<String> names = new HashSet<String>(Arrays.asList(this.names));
			for (Map.Entry<String,Object> e : defined.entrySet()) {
				String name = e.getKey();
				if (names.isEmpty() || names.contains(name)) {
					if (namespace.containsKey(name)) {
						throw new EvaluationException(this, "import name clash: " + name);
					}
					namespace.put(name, e.getValue());
				}
			}
		}
		catch (EvaluationException e) {
			throw completeStack(e);
		}
	}

	private Map<String,Object> getNamespace(Scope scope) throws EvaluationException {
		Map<String,Object> vars = scope.getVariables();
		if (alias == null) {
			return vars;
		}
		if (vars.containsKey(alias)) {
			Object value = vars.get(alias);
			Map<String,Object> result = EvaluationContext.asMap(value);
			if (result == null) {
				throw new EvaluationException(this, "import alias " + alias + " is not a map");
			}
			return result;
		}
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		scope.setVariable(alias, result);
		return result;
	}

	@Override
	public AbstractUndefExpression reduce() throws EvaluationException {
		return this;
	}

	@Override
	protected void toStringWithoutParen(ExpressionStringer stringer) {
		stringer.keyword("import ")
		.string(address);
		//XXX
	}

	@Override
	protected Precedence getPrecedence() {
		return Precedence.ATOM;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
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
		Import other = (Import) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		}
		else if (!address.equals(other.address))
			return false;
		return true;
	}
}
