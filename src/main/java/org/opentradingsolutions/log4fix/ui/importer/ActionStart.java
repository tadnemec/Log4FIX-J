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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Executor;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.opentradingsolutions.log4fix.importer.Importer;
import org.opentradingsolutions.log4fix.importer.ImporterCallback;
import org.opentradingsolutions.log4fix.importer.ImporterModel;

/**
 * @author Brian M. Coyner
 */
public class ActionStart extends AbstractAction {

	private static final long serialVersionUID = 1L;
	private final Importer importer;
	private final ImporterModel model;
	private final ImporterCallback callback;
	private final Executor executor;

	private JFileChooser fileChooser;

	private File currentFile = null;
	private long timeStamp;

	public File getCurrentFile() {
		return currentFile;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long l) {
		timeStamp = l;
	}

	public void setCurrentFile(File file) {
		currentFile = file;
		if (file != null) {
			setTimeStamp(file.lastModified());
		} else {
			setTimeStamp(0);
		}
	}

	public ActionStart(Importer importer, ImporterModel model,
			ImporterCallback callback) {
		super("Import");

		this.model = model;
		this.importer = importer;
		this.callback = callback;
		executor = new ThreadPerTaskExecutor();
	}

	public void actionPerformed(ActionEvent e) {
		maybeCreateFileChooser();

		if (openLogFile(fileChooser)) {
			final File selectedFile = fileChooser.getSelectedFile();
			importFile(selectedFile);
		}
	}

	public void importFile(final File selectedFile) {
		currentFile = selectedFile;
		if (currentFile != null) {
			setTimeStamp(currentFile.lastModified());
		} else {
			setTimeStamp(0);
		}
		model.setLastAccessedFilePath(selectedFile.getPath());

		Runnable task = new Runnable() {
			public void run() {
				try {
					importer.start(model, new FileInputStream(selectedFile),
							callback);
				} catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}
		};
		executor.execute(task);
	}

	private void maybeCreateFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser(model.getLastAccessedFilePath());
			fileChooser.setFileFilter(new LogFileFilter());
		}
	}

	private boolean openLogFile(JFileChooser fileChooser) {
		return fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION;
	}
}