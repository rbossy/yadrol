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

package org.phatonin.yadrol.core.values;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Singleton that compares two values.
 * <p>undef &lt; string &lt; boolean &lt; integer &lt; list &lt; map &lt; function.</p>
 * <p>Two strings are compared by the lexicographical order.</p>
 * <p><code>false</code> &lt; <code>true</code>.</p>
 * <p>Two integers are compared as expected, duh.</p>
 * <p>Two lists are compared element by element.</p>
 * <p>Two maps are compared value by value.</p>
 * <p>Two functions are compared in an undefined way, though the comparison is stable.</p>
 *
 */
public enum ValueComparator implements Comparator<Object> {
	INSTANCE;
	
	@SuppressWarnings("unchecked")
	@Override
	public int compare(Object o1, Object o2) {
		ValueType t1 = ValueType.get(o1);
		ValueType t2 = ValueType.get(o2);
		if (t1 == t2) {
			switch (t1) {
				case UNDEF:
					return 0;
				case BOOLEAN:
					return Boolean.compare((boolean) o1, (boolean) o2); 
				case FUNCTION:
					return Integer.compare(o1.hashCode(), o2.hashCode());
				case INTEGER:
					return Long.compare((long) o1, (long) o2);
				case LIST:
					return LIST_COMPARATOR.compare((List<Object>) o1, (List<Object>) o2);
				case MAP:
					return MAP_COMPARATOR.compare((Map<String,Object>) o1, (Map<String,Object>) o2);
				case STRING:
					return ((String) o1).compareTo((String) o2);
				default:
					throw new RuntimeException();
			}
		}
		return t1.compareTo(t2);
	}
	
	private static int compareIterators(Iterator<Object> it1, Iterator<Object> it2) {
		while (it1.hasNext() && it2.hasNext()) {
			Object v1 = it1.next();
			Object v2 = it2.next();
			int r = INSTANCE.compare(v1, v2);
			if (r != 0) {
				return r;
			}
		}
		if (it1.hasNext()) {
			return 1;
		}
		if (it2.hasNext()) {
			return -1;
		}
		return 0;
	}

	public static Comparator<List<Object>> LIST_COMPARATOR = new Comparator<List<Object>>() {
		@Override
		public int compare(List<Object> o1, List<Object> o2) {
			Iterator<Object> it1 = o1.iterator();
			Iterator<Object> it2 = o2.iterator();
			return compareIterators(it1, it2);
		}
	};
	
	public static Comparator<Map<String,Object>> MAP_COMPARATOR = new Comparator<Map<String,Object>>() {
		@Override
		public int compare(Map<String,Object> o1, Map<String,Object> o2) {
			Iterator<Object> it1 = o1.values().iterator();
			Iterator<Object> it2 = o2.values().iterator();
			return compareIterators(it1, it2);
		}
	};
}
