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

package org.phatonin.yadrol.app.cli;

import java.io.File;

import org.phatonin.yadrol.app.YadrolOptions;
import org.phatonin.yadrol.core.importManagers.FileSystemImportManager;
import org.phatonin.yadrol.core.importManagers.ImportManagers;
import org.phatonin.yadrol.core.importManagers.StreamImportManager;

public class CLIOptions extends YadrolOptions {
	public static final String YADROL_IMPORT_PATH = "YADROL_IMPORT_PATH";

	private boolean help = false;
	private DisplayManager displayManager = new DefaultDisplay();
	private File seedFile = null;
	private File rollRecordsFile = null;
	private File sampleRecordsFile = null;
	private boolean writeDiceRecords = false;
	private final FileSystemImportManager fsImportManager = new FileSystemImportManager(true);
	
	public CLIOptions(String source) {
		super(source);
		ImportManagers importManagers = new ImportManagers();
		importManagers.addImportManager(new StreamImportManager());
		importManagers.addImportManager(fsImportManager);
		String paths = System.getenv(YADROL_IMPORT_PATH);
		if (paths != null) {
			fsImportManager.addSearchPaths(paths);
		}
		setReduce(true);
	}

	public CLIOptions() {
		this("command line");
	}

	public boolean isHelp() {
		return help;
	}
	
	public DisplayManager getDisplayManager() {
		return displayManager;
	}
	
	public File getSeedFile() {
		return seedFile;
	}

	public File getRollRecordsFile() {
		return rollRecordsFile;
	}

	public File getSampleRecordsFile() {
		return sampleRecordsFile;
	}

	public boolean isWriteDiceRecords() {
		return writeDiceRecords;
	}

	public FileSystemImportManager getFsImportManager() {
		return fsImportManager;
	}

	public void setWriteDiceRecords(boolean writeDiceRecords) {
		this.writeDiceRecords = writeDiceRecords;
	}

	public void setRollRecordsFile(File rollRecordsFile) {
		this.rollRecordsFile = rollRecordsFile;
	}

	public void setSampleRecordsFile(File sampleRecordsFile) {
		this.sampleRecordsFile = sampleRecordsFile;
	}

	public void setSeedFile(File seedFile) {
		this.seedFile = seedFile;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}
	
	public void setDisplayManager(DisplayManager displayManager) {
		this.displayManager = displayManager;
	}
}
