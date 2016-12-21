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

public class Count {
	private final Object value;
	private long frequency;
	private long atLeastFrequency;
	private long atMostFrequency;
	private double relativeFrequency;
	private double relativeAtLeastFrequency;
	private double relativeAtMostFrequency;

	public Count(Object value) {
		super();
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public long getFrequency() {
		return frequency;
	}
	
	public long getAtLeastFrequency() {
		return atLeastFrequency;
	}
	
	public long getAtMostFrequency() {
		return atMostFrequency;
	}
	
	public double getRelativeFrequency() {
		return relativeFrequency;
	}
	
	public double getRelativeAtLeastFrequency() {
		return relativeAtLeastFrequency;
	}
	
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
