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

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Import manager for local file system addresses.
 * The file system import manager will try relative addresses in each of its search paths until it finds a file.
 * By default the search paths are empty, not even the working directory.
 *
 */
public class FileSystemImportManager extends AbstractImportParser {
	private final List<File> searchPaths = new ArrayList<File>();
	private final boolean allowAbsolute;

	/**
	 * Create a file system import manager.
	 * @param allowAbsolute either this import manager accepts absolute paths as addresses.
	 */
	public FileSystemImportManager(boolean allowAbsolute) {
		super();
		this.allowAbsolute = allowAbsolute;
	}

	@Override
	protected Reader resolveStream(String address) throws Exception {
		File file = resolveFile(address);
		if (file == null) {
			return null;
		}
		return new FileReader(file);
	}

	private File resolveFile(String address) {
		File result = new File(address);
		if (result.isAbsolute()) {
			if (allowAbsolute && hasAccess(result)) {
				return result;
			}
			return null;
		}
		for (File path : searchPaths) {
			result = new File(path, address);
			if (hasAccess(result)) {
				return result;
			}
		}
		return null;
	}
	
	private static boolean hasAccess(File file) {
		return file.exists() && file.canRead();
	}
	
	/**
	 * Add the specified path to the search paths.
	 * @param path the path.
	 */
	public void addSearchPath(File path) {
		searchPaths.add(path);
	}
	
	/**
	 * Add the specified path to the search paths.
	 * @param path the path.
	 */
	public void addSearchPath(String path) {
		searchPaths.add(new File(path));
	}
	
	/**
	 * Add the specified paths to the search paths.
	 * @param path the paths, separated by File.pathSeparator.
	 */
	public void addSearchPaths(String paths) {
		for (String path : paths.split(File.pathSeparator)) {
			addSearchPath(path);
		}
	}
}
