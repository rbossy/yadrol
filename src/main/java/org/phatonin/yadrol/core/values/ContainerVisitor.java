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

public abstract class ContainerVisitor<R,P,E extends Throwable> extends ValueVisitor<R,P,E> {
	@Override
	public R visit(String value, P param) throws E {
		return visitScalar(value, param);
	}

	@Override
	public R visit(boolean value, P param) throws E {
		return visitScalar(value, param);
	}

	@Override
	public R visit(long value, P param) throws E {
		return visitScalar(value, param);
	}

	@Override
	public R visit(Function value, P param) throws E {
		return visitScalar(value, param);
	}

	protected abstract R visitScalar(Object value, P param) throws E;
}
