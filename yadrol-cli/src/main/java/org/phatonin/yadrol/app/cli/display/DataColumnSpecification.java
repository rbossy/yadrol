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

package org.phatonin.yadrol.app.cli.display;

import java.io.PrintStream;

public enum DataColumnSpecification implements ColumnSpecification {
	RAW {
		@Override
		public void printColumn(PrintStream out, int maxDataWidth, String cell) {
			out.print(cell);
		}
	},
	
	MAX_LEFT {
		@Override
		public void printColumn(PrintStream out, int maxDataWidth, String cell) {
			out.print(cell);
			fillSpaces(out, maxDataWidth - cell.length());
		}
	},
	
	MAX_RIGHT {
		@Override
		public void printColumn(PrintStream out, int maxDataWidth, String cell) {
			fillSpaces(out, maxDataWidth - cell.length());
			out.print(cell);
		}
	},
	
	MAX_CENTER {
		@Override
		public void printColumn(PrintStream out, int maxDataWidth, String cell) {
			int half = (maxDataWidth - cell.length()) / 2;
			fillSpaces(out, half);
			out.print(cell);
			fillSpaces(out, maxDataWidth - half);
		}
	}
	;
	
	private static void fillSpaces(PrintStream out, int n) {
		for (int i = 0; i < n; ++i) {
			out.print(' ');
		}
	}

	@Override
	public boolean consumesDataCell() {
		return true;
	}
}
