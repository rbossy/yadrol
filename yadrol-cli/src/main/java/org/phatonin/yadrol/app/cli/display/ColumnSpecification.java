package org.phatonin.yadrol.app.cli.display;

import java.io.PrintStream;

public interface ColumnSpecification {
	boolean consumesDataCell();
	void printColumn(PrintStream out, int maxDataWidth, String cell);
}
