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

/**
 * A location represents an address in a script file.
 * 
 *
 */
public class Location {
	/**
	 * None location.
	 */
	public static final Location NONE = new Location(null, 0, 0);
	
	private final String source;
	private final int lineno;
	private final int column;
	
	/**
	 * Create a location.
	 * @param source source (e.g. path to the file or URL)
	 * @param lineno line number.
	 * @param column column number.
	 */
	public Location(String source, int lineno, int column) {
		super();
		this.source = source;
		this.lineno = lineno;
		this.column = column;
	}

	/**
	 * Returns the source (e.g. path to the file or URL).
	 * @return
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Returns the line number.
	 * @return
	 */
	public int getLineno() {
		return lineno;
	}

	/**
	 * Returns the column number.
	 * @return
	 */
	public int getColumn() {
		return column;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + lineno;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		Location other = (Location) obj;
		if (column != other.column)
			return false;
		if (lineno != other.lineno)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		}
		else if (!source.equals(other.source))
			return false;
		return true;
	}
}
