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

package org.phatonin.yadrol.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.phatonin.yadrol.core.CountSelector;
import org.phatonin.yadrol.core.Location;
import org.phatonin.yadrol.core.OutputMode;
import org.phatonin.yadrol.core.expressions.Import;
import org.phatonin.yadrol.core.importManagers.FileSystemImportManager;
import org.phatonin.yadrol.core.importManagers.JavaResourceImportParser;
import org.phatonin.yadrol.core.importManagers.StreamImportManager;
import org.phatonin.yadrol.core.importManagers.URLImportManager;
import org.phatonin.yadrol.core.values.ValueType;

/**
 * Yadrol application options.
 * 
 *
 */
public class YadrolOptions {
	public static final long DEFAULT_SAMPLE_SIZE = 30000;

	private String source = null;
	private boolean forceOutput = true;
	private String expressionString = null;
	private OutputMode outputMode = OutputMode.SAMPLE;
	private ValueType defaultEvaluationType = ValueType.INTEGER;
	private long sampleSize = DEFAULT_SAMPLE_SIZE;
	private boolean reduce = false;
	private long seed = new Random().nextLong();
	private CountSelector countSelector = CountSelector.AT_LEAST;
//	private ImportManager importManager = NoImport.INSTANCE;
	private StreamImportManager streamImportManager = null;
	private FileSystemImportManager fileSystemImportManager = null;
	private JavaResourceImportParser javaResourceImportParser = null;
	private URLImportManager urlImportManager = null;
	private final List<Import> imports = new ArrayList<Import>();

	/**
	 * Create an option set with default values.
	 * The specified source is used to show errors in options.
	 * @param source
	 */
	public YadrolOptions(String source) {
		super();
		this.source = source;
	}

	/**
	 * Default output mode ({@link OutputMode#SAMPLE}).
	 * @return
	 */
	public OutputMode getOutputMode() {
		return outputMode;
	}

	/**
	 * Sample size, number of repeated evaluations to build a distribution (<code>30 000</code>).
	 * @return
	 */
	public long getSampleSize() {
		return sampleSize;
	}

	/**
	 * Either to reduce the expression (<code>false</code>).
	 * @return
	 */
	public boolean isReduce() {
		return reduce;
	}

	/**
	 * Expression string (<code>null</code>).
	 * @return
	 */
	public String getExpressionString() {
		return expressionString;
	}

	/**
	 * Either the expression string is different from <code>null</code>.
	 * @return
	 */
	public boolean hasExpressionString() {
		return expressionString != null;
	}

	/**
	 * Initial RNG seed (unspecified).
	 * @return
	 */
	public long getSeed() {
		return seed;
	}

	/**
	 * Default evaluation type ({@link ValueType#INTEGER}).
	 * @return
	 */
	public ValueType getDefaultEvaluationType() {
		return defaultEvaluationType;
	}

	/**
	 * Count selector for multi-counts ({@link CountSelector#AT_LEAST).
	 * @return
	 */
	public CountSelector getCountSelector() {
		return countSelector;
	}

	/**
	 * Either to force an output if there are none ({@literal <code>true</code>}).
	 * @return
	 */
	public boolean isForceOutput() {
		return forceOutput;
	}

	/**
	 * Returns the source name of this option set.
	 * @return
	 */
	public String getSource() {
		return source;
	}

	/**
	 * Required imports (none).
	 * @return
	 */
	public List<Import> getImports() {
		return Collections.unmodifiableList(imports);
	}
	
	public StreamImportManager getStreamImportManager() {
		return streamImportManager;
	}

	public FileSystemImportManager getFileSystemImportManager() {
		return fileSystemImportManager;
	}

	public JavaResourceImportParser getJavaResourceImportParser() {
		return javaResourceImportParser;
	}

	public URLImportManager getUrlImportManager() {
		return urlImportManager;
	}

	public void setStreamImportManager(StreamImportManager streamImportManager) {
		this.streamImportManager = streamImportManager;
	}

	public void setFileSystemImportManager(FileSystemImportManager fileSystemImportManager) {
		this.fileSystemImportManager = fileSystemImportManager;
	}

	public void setJavaResourceImportParser(JavaResourceImportParser javaResourceImportParser) {
		this.javaResourceImportParser = javaResourceImportParser;
	}

	public void setUrlImportManager(URLImportManager urlImportManager) {
		this.urlImportManager = urlImportManager;
	}

	public void addImport(Import imp) {
		imports.add(imp);
	}

	public void addImport(Location location, String address) {
		List<String> emptyNames = Collections.emptyList();
		addImport(new Import(location, address, emptyNames, null));
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setForceOutput(boolean forceOutput) {
		this.forceOutput = forceOutput;
	}

	public void setReduce(boolean reduce) {
		this.reduce = reduce;
	}

	public void setCountSelector(CountSelector countSelector) {
		this.countSelector = countSelector;
	}

	public void setDefaultEvaluationType(ValueType defaultEvaluationType) {
		this.defaultEvaluationType = defaultEvaluationType;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public void setExpressionString(String expression) {
		this.expressionString = expression;
	}

	public void setSampleSize(long sampleSize) {
		this.sampleSize = sampleSize;
	}

	public void setOutputMode(OutputMode outputMode) {
		this.outputMode = outputMode;
	}
}
