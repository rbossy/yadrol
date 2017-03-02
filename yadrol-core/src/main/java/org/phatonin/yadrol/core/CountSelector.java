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
 * A count selector retrieves a score from a count.
 * 
 *
 */
public enum CountSelector {
	FREQUENCY {
		@Override
		public Number get(Count count) {
			return count.getFrequency();
		}

		@Override
		public String toString() {
			return "frequency";
		}

		@Override
		public CountSelector getRelative() {
			return RELATIVE_FREQUENCY;
		}

		@Override
		public CountSelector getAbsolute() {
			return this;
		}
	},

	AT_LEAST {
		@Override
		public Number get(Count count) {
			return count.getAtLeastFrequency();
		}

		@Override
		public String toString() {
			return "atleast";
		}

		@Override
		public CountSelector getRelative() {
			return RELATIVE_AT_LEAST;
		}

		@Override
		public CountSelector getAbsolute() {
			return this;
		}
	},
	
	AT_MOST {
		@Override
		public Number get(Count count) {
			return count.getAtMostFrequency();
		}

		@Override
		public String toString() {
			return "atmost";
		}

		@Override
		public CountSelector getRelative() {
			return RELATIVE_AT_MOST;
		}

		@Override
		public CountSelector getAbsolute() {
			return this;
		}
	},
	
	RELATIVE_FREQUENCY {
		@Override
		public Number get(Count count) {
			return count.getRelativeFrequency();
		}

		@Override
		public String toString() {
			return "relativefrequency";
		}

		@Override
		public CountSelector getRelative() {
			return this;
		}

		@Override
		public CountSelector getAbsolute() {
			return FREQUENCY;
		}
	},
	
	RELATIVE_AT_LEAST {
		@Override
		public Number get(Count count) {
			return count.getRelativeAtLeastFrequency();
		}

		@Override
		public String toString() {
			return "relativeatleast";
		}

		@Override
		public CountSelector getRelative() {
			return this;
		}

		@Override
		public CountSelector getAbsolute() {
			return AT_LEAST;
		}
	},
	
	RELATIVE_AT_MOST {
		@Override
		public Number get(Count count) {
			return count.getRelativeAtMostFrequency();
		}

		@Override
		public String toString() {
			return "relativeatmost";
		}

		@Override
		public CountSelector getRelative() {
			return this;
		}

		@Override
		public CountSelector getAbsolute() {
			return AT_MOST;
		}
	};
	
	/**
	 * Retrieves the score in the specified count.
	 * @param count
	 * @return
	 */
	public abstract Number get(Count count);
	
	/**
	 * Returns the relative selector of this selector.
	 * @return
	 */
	public abstract CountSelector getRelative();
	
	/**
	 * Returns the absolute selector of this selector.
	 * @return
	 */
	public abstract CountSelector getAbsolute();
	
	/**
	 * Converts the specified string into a count selector.
	 * @param s
	 * @return
	 */
	public static CountSelector fromString(String s) {
		switch (s) {
			case "frequency": return FREQUENCY;
			case "atleast": return AT_LEAST;
			case "atmost": return AT_MOST;
			case "relativefrequency": return RELATIVE_FREQUENCY;
			case "relativeatleast": return RELATIVE_AT_LEAST;
			case "relativeatmost": return RELATIVE_AT_MOST;
		}
		throw new IllegalArgumentException();
	}
}
