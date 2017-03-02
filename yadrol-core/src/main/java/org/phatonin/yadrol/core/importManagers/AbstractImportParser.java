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

package org.phatonin.yadrol.core.importManagers;

import java.io.Reader;
import java.util.Map;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ExpressionListUtils;
import org.phatonin.yadrol.core.ImportManager;
import org.phatonin.yadrol.core.Scope;
import org.phatonin.yadrol.core.parser.YadrolParser;

/**
 * Abstract class of import managers that parses a stream containing a Yadrol script.
 * 
 *
 */
public abstract class AbstractImportParser implements ImportManager {
	protected AbstractImportParser() {
		super();
	}
	
	/**
	 * Resolves the specified address as a stream.
	 * @param address the address.
	 * @return the stream containing a Yadrol script.
	 * @throws Exception
	 */
	protected abstract Reader resolveStream(String address) throws Exception;

	@Override
	public Map<String,Object> resolveImport(Expression expression, EvaluationContext ctx, String address) throws EvaluationException {
		try (Reader stream = resolveStream(address)) {
			if (stream == null) {
				return null;
			}
			YadrolParser parser = new YadrolParser(stream);
			Expression[] expressions = parser.parse(address, 0);
			Scope scope = new Scope();
			ExpressionListUtils.evaluate(expressions, ctx, scope);
			Map<String,Object> result = scope.getVariables();
			return result;
		}
		catch (Exception e) {
			throw new EvaluationException(expression, e);
		}
	}
}
