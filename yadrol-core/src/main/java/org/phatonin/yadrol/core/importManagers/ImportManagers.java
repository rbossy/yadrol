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

package org.phatonin.yadrol.core.importManagers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.phatonin.yadrol.core.EvaluationContext;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.Expression;
import org.phatonin.yadrol.core.ImportManager;

/**
 * Composite import manager.
 * The resolution tries each sub-manager until one resolves the address, in the order they have been added.
 *
 */
public class ImportManagers implements ImportManager {
	private final List<ImportManager> importManagers = new ArrayList<ImportManager>();

	@Override
	public Map<String,Object> resolveImport(Expression expression, EvaluationContext ctx, String address) throws EvaluationException {
		for (ImportManager mgr : importManagers) {
			Map<String,Object> result = mgr.resolveImport(expression, ctx, address);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Add the specified sub-manager.
	 * @param mgr the import manager.
	 */
	public void addImportManager(ImportManager mgr) {
		if (mgr != null) {
			importManagers.add(mgr);
		}
	}
	
	public ImportManager simplify() {
		if (importManagers.isEmpty()) {
			return NoImport.INSTANCE;
		}
		if (importManagers.size() == 1) {
			return importManagers.get(0);
		}
		return this;
	}
}
