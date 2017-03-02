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

import java.util.Map;

/**
 * A multi-count object stores the statistics for a specified value in several distributions.
 * 
 *
 */
public class MultiCount {
	private final Object value;
	private final Map<String,Number> counts;
	
	MultiCount(Object value, Map<String,Number> counts) {
		super();
		this.value = value;
		this.counts = counts;
	}

	/**
	 * Returns the value for which the statistics are stored.
	 * @return
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns the statistics for each distribution.
	 * The keys of the returned map are the names of the corresponding SampleRecord instances.
	 * @return
	 */
	public Map<String,Number> getCounts() {
		return counts;
	}
}
