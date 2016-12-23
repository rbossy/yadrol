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
