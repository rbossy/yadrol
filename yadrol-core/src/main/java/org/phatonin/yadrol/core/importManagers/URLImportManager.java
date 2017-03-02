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

package org.phatonin.yadrol.core.importManagers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * URL import managers resolves Internet addresses and read the Yadrol script at the given location.
 * 
 *
 */
public class URLImportManager extends AbstractImportParser {
	public URLImportManager() {
		super();
	}

	@Override
	protected Reader resolveStream(String address) throws Exception {
		URL url = new URL(address);
		if (!"http".equals(url.getProtocol())) {
			return null;
		}
		HttpURLConnection connect = (HttpURLConnection) url.openConnection();
		int status = connect.getResponseCode();
		if (status != 200) {
			return null;
		}
		InputStream is = url.openStream();
		return new InputStreamReader(is);
	}
}
