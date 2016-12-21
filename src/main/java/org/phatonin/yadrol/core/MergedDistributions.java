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

import java.util.Collection;
import java.util.TreeSet;

import org.phatonin.yadrol.core.values.ValueComparator;

public class MergedDistributions {
	public static void mergeDistributions(Collection<SampleRecord> samples) {
		Collection<Object> values = getValues(samples);
		ensureAllValues(samples, values);
		computeAll(samples);
	}

	private static Collection<Object> getValues(Collection<SampleRecord> samples) {
		Collection<Object> result = new TreeSet<Object>(ValueComparator.INSTANCE);
		for (SampleRecord rec : samples) {
			Distribution dist = rec.getDistribution();
			result.addAll(dist.getValues());
		}
		return result;
	}

	private static void ensureAllValues(Collection<SampleRecord> samples, Collection<Object> values) {
		for (SampleRecord rec : samples) {
			Distribution dist = rec.getDistribution();
			for (Object v : values) {
				dist.ensureCount(v);
			}
		}
	}

	private static void computeAll(Collection<SampleRecord> samples) {
		for (SampleRecord rec : samples) {
			Distribution dist = rec.getDistribution();
			dist.compute();
		}
	}
}
