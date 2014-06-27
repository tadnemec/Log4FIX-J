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

package org.opentradingsolutions.log4fix.ui.importer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.opentradingsolutions.log4fix.core.GlazedListsMemoryLogModel;
import org.opentradingsolutions.log4fix.core.LogMessage;
import org.opentradingsolutions.log4fix.core.MemoryLogModel;
import org.opentradingsolutions.log4fix.core.SessionKey;
import org.opentradingsolutions.log4fix.core.Tab;
import org.opentradingsolutions.log4fix.importer.Importer;
import org.opentradingsolutions.log4fix.importer.ImporterCallback;
import org.opentradingsolutions.log4fix.importer.ImporterMemoryLog;
import org.opentradingsolutions.log4fix.importer.ImporterModel;
import org.opentradingsolutions.log4fix.ui.messages.ViewBuilder;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.impl.ThreadSafeList;

/**
 * @author Brian M. Coyner
 */
public class ImporterController {

	private final ActionStart start;
	private final Action stop;
	private final JLabel busyText;
	private JTabbedPane tabPane;
	private List<Tab> masterTabList = new LinkedList<Tab>();
	private ImporterModel model;

	private JFrame frame;

	public ImporterController() {
		this(new Importer(), new ImporterModel());
	}

	public ImporterController(Importer service, ImporterModel model) {
		start = new ActionStart(service, model, new DefaultImporterController());
		stop = new ActionStop(service);

		busyText = new JLabel();
		busyText.setVisible(false);
		this.model = model;
	}

	public void setTabPane(JTabbedPane pane) {
		tabPane = pane;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public void combinedSearch(String tab, String fix) {
		tabPane.removeAll();
		for (Tab t : masterTabList) {
			if (t.getTitle().contains(tab)) {
				if (fix.length() != 0) {
					boolean keep = false;
					ThreadSafeList<LogMessage> underlying = new ThreadSafeList<LogMessage>(
							new BasicEventList<LogMessage>());
					FilterList<LogMessage> lms = new FilterList<LogMessage>(
							underlying);
					for (LogMessage lm : t.getModel().getMessages()) {
						if (lm.getRawMessage().contains(fix)) {
							keep = true;
							lms.add(lm);
						}
					}
					if (keep) {
						tabPane.addTab(
								t.getTitle() + " (" + lms.size() + ")",
								ViewBuilder
										.createTabForSession(new GlazedListsMemoryLogModel(
												null, lms)));
					}
				} else {
					tabPane.addTab(t.getTitle() + " (" + t.getCount() + ")",
							t.getComponent());
				}
			}
		}
	}

	public void resetTabSearch() {
		tabPane.removeAll();
		for (Tab t : masterTabList) {
			tabPane.addTab(t.getTitle() + " (" + t.getCount() + ")",
					t.getComponent());
		}
	}

	public Action getStart() {
		return start;
	}

	public Action getStop() {
		return stop;
	}

	public JComponent getBusyIcon() {
		return busyText;
	}

	public void importWithFile(File file) {
		start.importFile(file);
	}

	public void clear() {
		model.clear();
		if (masterTabList != null && !masterTabList.isEmpty()) {
			masterTabList.clear();
		}
		tabPane.removeAll();
	}

	private class DefaultImporterController implements ImporterCallback {
		public void starting() {
			clear();
			if (start.getCurrentFile() != null) {
				frame.setTitle("Log4FIX - "
						+ start.getCurrentFile().getAbsolutePath());
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					busyText.setText("Importing...");
					busyText.setVisible(true);
					start.setEnabled(false);
					stop.setEnabled(true);
				}
			});
		}

		public void canceling() {
			busyText.setText("Cancelling...");
			start.setTimeStamp(0);
			frame.setTitle("Log4FIX");
		}

		public void done() {

			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					busyText.setVisible(false);
					start.setEnabled(true);
					stop.setEnabled(false);
					for (Entry<SessionKey, ImporterMemoryLog> entry : model
							.getMemoryLogMap().entrySet()) {
						ImporterMemoryLog iml = entry.getValue();
						MemoryLogModel mlm = iml.getMemoryLogModel();
						List<LogMessage> messages = mlm.getMessages();
						if (!messages.isEmpty()) {
							SessionKey key = entry.getKey();
							tabPane.addTab(
									ViewBuilder.getTabTitle(key,
											messages.get(0), messages.size()),
									ViewBuilder.createTabForSession(mlm));
							masterTabList.add(new Tab(ViewBuilder.getTabTitle(
									key, messages.get(0)), messages.size(),
									ViewBuilder.createTabForSession(mlm), mlm));

						}
					}
				}
			});
		}
	}
}
