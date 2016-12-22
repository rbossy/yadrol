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

/**
 * A count object contains the occurrence statistics for a single value.
 * 
 *
 */
public class Count {
	private final Object value;
	private long frequency;
	private long atLeastFrequency;
	private long atMostFrequency;
	private double relativeFrequency;
	private double relativeAtLeastFrequency;
	private double relativeAtMostFrequency;

	/**
	 * Create a count for the specified value.
	 * @param value
	 */
	public Count(Object value) {
		super();
		this.value = value;
	}

	/**
	 * Returns the value for which this count stores occurrences.
	 * @return
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns the number of occurrences of this value.
	 * @return
	 */
	public long getFrequency() {
		return frequency;
	}
	
	/**
	 * Returns the total number of occurrences for the values above or equal the value of this count.
	 * @return
	 */
	public long getAtLeastFrequency() {
		return atLeastFrequency;
	}
	
	/**
	 * Returns the total number of occurrences for the values below or equal the value of this count.
	 * @return
	 */
	public long getAtMostFrequency() {
		return atMostFrequency;
	}
	
	/**
	 * Returns the relative frequency of this value.
	 * @return
	 */
	public double getRelativeFrequency() {
		return relativeFrequency;
	}
	
	/**
	 * Returns the relative frequency for the values above or equal the value of this count.
	 * @return
	 */
	public double getRelativeAtLeastFrequency() {
		return relativeAtLeastFrequency;
	}
	
	/**
	 * Returns the relative frequency for the values below or equal the value of this count.
	 * @return
	 */
	public double getRelativeAtMostFrequency() {
		return relativeAtMostFrequency;
	}
	
	void incr() {
		frequency++;
	}

	void compute(Count prev, long total) {
		if (prev == null) {
			atLeastFrequency = total;
			atMostFrequency = frequency;
		}
		else {
			atLeastFrequency = prev.atLeastFrequency - prev.frequency;
			atMostFrequency = prev.atMostFrequency + frequency;
		}
		relativeFrequency = relative(frequency, total);
		relativeAtLeastFrequency = relative(atLeastFrequency, total);
		relativeAtMostFrequency = relative(atMostFrequency, total);
	}
	
	private static double relative(long f, long t) {
		return f / (double) t;
	}
}
