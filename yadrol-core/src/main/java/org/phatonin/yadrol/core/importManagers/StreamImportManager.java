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

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * An import manager that resolves a single address.
 * If the address is required, this returns the script contained in the stream specified to the constructor.
 * This manager can resolve only once.
 *
 */
public class StreamImportManager extends AbstractImportParser {
	private final String address;
	private final Reader reader;

	/**
	 * Create a stream import manager.
	 * @param address the address that this manager resolves.
	 * @param reader the Yadrol script.
	 */
	public StreamImportManager(String address, Reader reader) {
		super();
		this.address = address;
		this.reader = reader;
	}
	
	/**
	 * Create a stream import manager that resolves to a script read from standard input.
	 * @param address the address that this manager resolves.
	 */
	public StreamImportManager(String address) {
		this(address, new InputStreamReader(System.in));
	}
	
	/**
	 * Create a stream import manager that resolves the <code>null</code> address to a script read from standard input.
	 * @param address the address that this manager resolves.
	 */	
	public StreamImportManager() {
		this(null);
	}

	@Override
	protected Reader resolveStream(String address) throws Exception {
		if ((address == null && this.address == null) || address.equals(address)) {
			return reader;
		}
		return null;
	}
}
