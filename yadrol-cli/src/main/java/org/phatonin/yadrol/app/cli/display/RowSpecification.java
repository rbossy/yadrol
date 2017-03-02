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
import java.util.ArrayList;
import java.util.List;

public class RowSpecification {
	private final List<ColumnSpecification> columnSpecifications = new ArrayList<ColumnSpecification>();
	private boolean printNewline;
	
	public RowSpecification(boolean printNewline) {
		super();
		this.printNewline = printNewline;
	}
	
	public RowSpecification() {
		this(true);
	}

	public boolean isPrintNewline() {
		return printNewline;
	}

	public void setPrintNewline(boolean printNewline) {
		this.printNewline = printNewline;
	}

	public void appendColumnSpecification(ColumnSpecification spec) {
		columnSpecifications.add(spec);
	}
	
	public void insertSeparator(ColumnSpecification spec) {
		for (int i = columnSpecifications.size() - 1; i > 0; --i) {
			columnSpecifications.add(i, spec);
		}
	}
	
	public void insertSeparator(String sep) {
		insertSeparator(new ConstantColumnSpecification(sep));
	}
	
	public int countExpectedDataCells() {
		int result = 0;
		for (ColumnSpecification spec : columnSpecifications) {
			if (spec.consumesDataCell()) {
				result++;
			}
		}
		return result;
	}

	public void printRow(PrintStream out, int[] maxDataWidth, String[] row) {
		int currentCol = 0;
		for (ColumnSpecification spec : columnSpecifications) {
			spec.printColumn(out, maxDataWidth[currentCol], row[currentCol]);
			if (spec.consumesDataCell() && currentCol < (row.length - 1)) {
				currentCol++;
			}
		}
		if (printNewline) {
			out.println();
		}
	}
}
