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
 * Abstract visitor class for values.
 * 
 * @param <R> return type.
 * @param <P> parameter type.
 * @param <E> exception type.
 */
public abstract class ValueVisitor<R,P,E extends Throwable> {
	public abstract R visitUndef(P param) throws E;
	public abstract R visit(String value, P param) throws E;
	public abstract R visit(boolean value, P param) throws E;
	public abstract R visit(long value, P param) throws E;
	public abstract R visit(List<Object> value, P param) throws E;
	public abstract R visit(Map<String,Object> value, P param) throws E;
	public abstract R visit(Function value, P param) throws E;
	
	@SuppressWarnings("unchecked")
	public R visit(Object value, P param) throws E {
		if (value == null) {
			return visitUndef(param);
		}
		if (value instanceof Boolean) {
			return visit((boolean) value, param);
		}
		if (value instanceof Integer) {
			return visit((long) (int) value, param);
		}
		if (value instanceof Long) {
			return visit((long) value, param);
		}
		if (value instanceof String) {
			return visit((String) value, param);
		}
		if (value instanceof List) {
			return visit((List<Object>) value, param);
		}
		if (value instanceof Map) {
			return visit((Map<String,Object>) value, param);
		}
		if (value instanceof Function) {
			return visit((Function) value, param);
		}
		throw new RuntimeException("unhandled type " + value.toString());
	}
}
