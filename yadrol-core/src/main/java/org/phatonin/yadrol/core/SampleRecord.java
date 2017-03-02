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

import org.phatonin.yadrol.core.values.ValueType;

/**
 * Sample records store the distribution of repeated evaluation of an expression.
 * 
 *
 */
public class SampleRecord extends OutputRecord {
	private final Distribution distribution = new Distribution();

	SampleRecord(String name, Expression expression, ValueType evaluationType) {
		super(name, expression, evaluationType);
	}

	/**
	 * Returns the distribution of evaluation results.
	 * @return
	 */
	public Distribution getDistribution() {
		return distribution;
	}
}
