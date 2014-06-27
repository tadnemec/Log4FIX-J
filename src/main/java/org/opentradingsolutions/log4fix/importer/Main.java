/*
 * The Log4FIX Software License
 * Copyright (c) 2006 - 2011 Brian M. Coyner  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Neither the name of the product (Log4FIX), nor Brian M. Coyner,
 *    nor the names of its contributors may be used to endorse or promote
 *    products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL BRIAN M. COYNER OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package org.opentradingsolutions.log4fix.importer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.opentradingsolutions.log4fix.Log4FIX;
import org.opentradingsolutions.log4fix.ui.fields.FieldHighlighter;
import org.opentradingsolutions.log4fix.ui.importer.ImporterController;
import org.opentradingsolutions.log4fix.ui.messages.RawMessageTableCellRenderer;

/**
 * @author Brian M. Coyner
 */

public class Main {

	/**
	 * This is the main entry point when starting Log4FIX in "standalone" mode.
	 * Use this starting point to import log files.
	 * 
	 * @param args
	 *            may contain a single absolute path to a log file that
	 *            automatically imports.
	 * @throws Exception
	 *             if the application fails to start.
	 */
	public static void main(String[] args) throws Exception {

		// Use config file
		boolean liveTab = true;
		boolean liveSearch = true;

		File file = new File(".\\CONFIG");
		if (file.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					if (line.matches(".*:[ ]*(true|false).*")
							&& !line.startsWith("//")) {
						if (line.startsWith("LIVE_TABS")) {
							liveTab = (line.contains("true")) ? true : false;
						} else if (line.startsWith("LIVE_SEARCH")) {
							liveSearch = (line.contains("true")) ? true : false;
						}
					}
					if (line.matches(".*:[ ]*[0-9]{1,3}[ ]*,[ ]*[0-9]{1,3}[ ]*,[ ]*[0-9]{1,3}.*")
							&& !line.startsWith("//")) {
						String[] values = line.substring(line.indexOf(":") + 1)
								.split(",");
						int r = Integer.parseInt(values[0].replaceAll("[^0-9]",
								""));
						int g = Integer.parseInt(values[1].replaceAll("[^0-9]",
								""));
						int b = Integer.parseInt(values[2].replaceAll("[^0-9]",
								""));
						if (line.startsWith("IN_COLOR")) {
							RawMessageTableCellRenderer
									.setIC(new Color(r, g, b));
						} else if (line.startsWith("OUT_COLOR")) {
							RawMessageTableCellRenderer
									.setOC(new Color(r, g, b));
						} else if (line.startsWith("IN_TEXT")) {
							RawMessageTableCellRenderer
									.setIT(new Color(r, g, b));
						} else if (line.startsWith("OUT_TEXT")) {
							RawMessageTableCellRenderer
									.setOT(new Color(r, g, b));
						} else if (line.startsWith("IN_SELECT_COLOR")) {
							RawMessageTableCellRenderer
									.setIS(new Color(r, g, b));
						} else if (line.startsWith("OUT_SELECT_COLOR")) {
							RawMessageTableCellRenderer
									.setOS(new Color(r, g, b));
						} else if (line.startsWith("IN_SELECT_TEXT")) {
							RawMessageTableCellRenderer.setIST(new Color(r, g,
									b));
						} else if (line.startsWith("OUT_SELECT_TEXT")) {
							RawMessageTableCellRenderer.setOST(new Color(r, g,
									b));
						} else if (line.startsWith("DATA")) {
							FieldHighlighter.setDFC(new Color(r, g, b));
						} else if (line.startsWith("HEADER")) {
							FieldHighlighter.setHFC(new Color(r, g, b));
						} else if (line.startsWith("TRAILER")) {
							FieldHighlighter.setTFC(new Color(r, g, b));
						}
					}
				}
				br.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		ImporterController controller = new ImporterController();
		Log4FIX forImport = Log4FIX.createForMultiple(controller, liveTab,
				liveSearch);
		forImport.show();
	}
}
