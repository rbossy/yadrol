package org.phatonin.yadrol.app.cli.display;

import java.io.PrintStream;

public class ConstantColumnSpecification implements ColumnSpecification {
	private final String value;

	public ConstantColumnSpecification(String value) {
		super();
		this.value = value;
	}

	@Override
	public boolean consumesDataCell() {
		return false;
	}

	@Override
	public void printColumn(PrintStream out, int maxDataWidth, String cell) {
		out.print(value);
	}
}
