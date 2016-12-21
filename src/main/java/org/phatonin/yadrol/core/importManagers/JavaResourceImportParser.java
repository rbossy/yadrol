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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class JavaResourceImportParser extends AbstractImportParser {
	private final ClassLoader classLoader;
	private final List<String> searchPaths = new ArrayList<String>();

	public JavaResourceImportParser(ClassLoader classLoader) {
		super();
		this.classLoader = classLoader;
	}

	public void addSearchPaths(Collection<String> searchPaths) {
		this.searchPaths.addAll(searchPaths);
	}
	
	public void addSearchPaths(String... searchPaths) {
		addSearchPaths(Arrays.asList(searchPaths));
	}

	@Override
	protected Reader resolveStream(String address) throws Exception {
		for (String path : searchPaths) {
			String reasourceName = path + '/' + address;
			InputStream is = classLoader.getResourceAsStream(reasourceName);
			if (is != null) {
				return new InputStreamReader(is);
			}
		}
		return null;
	}
}
