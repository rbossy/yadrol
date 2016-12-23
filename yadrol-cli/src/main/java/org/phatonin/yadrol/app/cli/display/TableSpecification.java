package org.phatonin.yadrol.app.cli.display;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class TableSpecification {
	private RowSpecification defaultRowSpecification = null;
	private final Map<Integer,RowSpecification> rowSpecifications = new HashMap<Integer,RowSpecification>();
	
	public RowSpecification getDefaultRowSpecification() {
		return defaultRowSpecification;
	}

	public void setDefaultRowSpecification(RowSpecification spec) {
		this.defaultRowSpecification = spec;
	}
	
	public void setRowSpecification(int rowNum, RowSpecification spec) {
		rowSpecifications.put(rowNum, spec);
	}
	
	public void print(PrintStream out, String[][] data) {
		sanityCheck(data);
		int[] maxDataWidth = getMaxDataWidth(data);
		int nRows = data.length;
		for (int i = 0; i < nRows; ++i) {
			RowSpecification spec = getRowSpecification(i, nRows);
			spec.printRow(out, maxDataWidth, data[i]);
		}
	}
	
	private RowSpecification getRowSpecification(int rowNum, int nRows) {
		if (rowSpecifications.containsKey(rowNum)) {
			return rowSpecifications.get(rowNum);
		}
		int fromLast = rowNum - nRows;
		if (rowSpecifications.containsKey(fromLast)) {
			return rowSpecifications.get(fromLast);
		}
		if (defaultRowSpecification != null) {
			return defaultRowSpecification;
		}
		throw new IllegalArgumentException("no row specification for row " + rowNum + " (" + fromLast + ")");
	}

	private void sanityCheck(String[][] data) {
		int expectedDataCells = -1;
		int nRows = data.length;
		if (nRows == 0) {
			throw new IllegalArgumentException("empty data");
		}
		for (int i = 0; i < nRows; ++i) {
			RowSpecification spec = getRowSpecification(i, nRows);
			int ex = spec.countExpectedDataCells();
			if (expectedDataCells == -1) {
				expectedDataCells = ex;
			}
			else if (ex != expectedDataCells) {
				throw new IllegalArgumentException("data cell expectations mismatch: " + ex + "/" + expectedDataCells);
			}
		}
		for (int i = 0; i < nRows; ++i) {
			String[] row = data[i];
			if (row.length != expectedDataCells) {
				throw new IllegalArgumentException("row " + i + " has " + row.length + " columns, expected " + expectedDataCells);
			}
			for (int j = 0; j < expectedDataCells; ++j) {
				String cell = row[j];
				if (cell == null) {
					throw new IllegalArgumentException("null cell row " + i + ", column " + j);
				}
			}
		}
	}
	
	private static int[] getMaxDataWidth(String[][] data) {
		int[] result = new int[data[0].length];
		for (String[] row : data) {
			for (int i = 0; i < row.length; ++i) {
				result[i] = Math.max(result[i], row[i].length());
			}
		}
		return result;
	}
}
