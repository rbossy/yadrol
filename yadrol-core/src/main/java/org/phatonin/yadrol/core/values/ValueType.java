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

package org.phatonin.yadrol.core.values;

import java.util.List;
import java.util.Map;

/**
 * Types of values and of evaluation.
 * 
 *
 */
public enum ValueType {
	/**
	 * Default evaluation type.
	 */
	DEFAULT {
		@Override
		public String toString() {
			return "default";
		}
	},
	
	/**
	 * Unspecified type.
	 */
	ANY {
		@Override
		public String toString() {
			return "any";
		}
	},
	
	/**
	 * Undef type.
	 */
	UNDEF {
		@Override
		public String toString() {
			return "undef";
		}
	},
	
	STRING {
		@Override
		public String toString() {
			return "string";
		}
	},
	
	BOOLEAN {
		@Override
		public String toString() {
			return "boolean";
		}
	},
	
	INTEGER {
		@Override
		public String toString() {
			return "integer";
		}
	},
	
	LIST {
		@Override
		public String toString() {
			return "list";
		}
	},
	
	MAP {
		@Override
		public String toString() {
			return "map";
		}
	},
	
	FUNCTION {
		@Override
		public String toString() {
			return "function";
		}
	};
	
	/**
	 * Returns the type of the specified value.
	 * @param value the value.
	 * @return the type of the specified value, will not return <code>DEFAULT</code> or <code>ANY</code>.
	 */
	public static ValueType get(Object value) {
		return VISITOR.visit(value, null);
	}

	/**
	 * Converts the specified string into a type.
	 * @param s the type name.
	 * @return the type.
	 */
	public static ValueType fromString(String s) {
		switch (s) {
			case "default": return DEFAULT;
			case "any": return ANY;
			case "undef": return UNDEF;
			case "string": return STRING;
			case "boolean": return BOOLEAN;
			case "integer": return INTEGER;
			case "list": return LIST;
			case "map": return MAP;
			case "function": return FUNCTION;
		}
		throw new IllegalArgumentException("unknown value type: " + s);
	}
	
	private static final ValueVisitor<ValueType,Void,RuntimeException> VISITOR = new ValueVisitor<ValueType,Void,RuntimeException>() {
		@Override
		public ValueType visitUndef(Void param) {
			return UNDEF;
		}

		@Override
		public ValueType visit(String value, Void param) {
			return STRING;
		}

		@Override
		public ValueType visit(boolean value, Void param) {
			return BOOLEAN;
		}

		@Override
		public ValueType visit(long value, Void param) {
			return INTEGER;
		}

		@Override
		public ValueType visit(List<Object> value, Void param) {
			return LIST;
		}

		@Override
		public ValueType visit(Map<String,Object> value, Void param) {
			return MAP;
		}

		@Override
		public ValueType visit(Function value, Void param) {
			return FUNCTION;
		}
	};
}
