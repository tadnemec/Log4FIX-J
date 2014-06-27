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

package org.opentradingsolutions.log4fix.ui.messages;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.opentradingsolutions.log4fix.core.GlazedListsMemoryLogModel;
import org.opentradingsolutions.log4fix.core.LogMessage;
import org.opentradingsolutions.log4fix.core.MemoryLogModel;
import org.opentradingsolutions.log4fix.core.SessionKey;
import org.opentradingsolutions.log4fix.ui.fields.FieldHighlighter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

/**
 * @author Brian M. Coyner
 */
public class ViewBuilder {

	public static final String NO_SESSION_ID = "No Session Id";

	/**
	 * Creates a tab pane containing one or more tabs. The behavior is to show
	 * each configured <tt>quickfix.Session</tt> in its own tab.
	 * 
	 * @param memoryLogModels
	 *            an iterator containing <tt>MemoryLogModel</tt>s for each
	 *            configured <tt>Session</tt>.
	 * @return a component ready to show QuickFIX logs.
	 */
	public JComponent createView(Iterator<MemoryLogModel> memoryLogModels) {

		JTabbedPane tabPane = new JTabbedPane();

		while (memoryLogModels.hasNext()) {
			MemoryLogModel memoryLogModel = memoryLogModels.next();
			tabPane.addTab(getTabTitle(memoryLogModel),
					createTabForSession(memoryLogModel));
		}

		return tabPane;
	}

	public JComponent createView(MemoryLogModel memoryLogModel) {
		final JTabbedPane tabPane = new JTabbedPane();

		tabPane.addTab(getTabTitle(memoryLogModel),
				createTabForSession(memoryLogModel));

		memoryLogModel.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {

				String title;
				if (evt.getNewValue() == null) {
					title = NO_SESSION_ID;
				} else {
					title = evt.getNewValue().toString();
				}
				tabPane.setTitleAt(0, title);
			}
		});

		return tabPane;
	}

	public static String getTabTitle(String s, int n) {
		return s + " (" + n + ")";
	}

	public static String getTabTitle(MemoryLogModel mlm) {
		return (mlm != null && mlm.getSessionId() != null) ? mlm.getSessionId()
				.toString() : NO_SESSION_ID;
	}

	public static String getTabTitle(SessionKey sk, LogMessage lm) {
		if (lm.isIncoming()) {
			return sk.getSender();
		}
		return sk.getTarget();
	}

	public static String getTabTitle(SessionKey sk, LogMessage lm, int n) {
		if (lm.isIncoming()) {
			return sk.getSender() + " (" + n + ")";
		}
		return sk.getTarget() + " (" + n + ")";
	}

	public static JComponent createTabForSession(MemoryLogModel memoryLogModel) {

		ViewModel viewModel = new ViewModel(memoryLogModel);

		JSplitPane messageAndEventView = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT);
		messageAndEventView.setResizeWeight(.90);
		JTextArea rawView = new JTextArea("", 1, 1);
		rawView.setEditable(false);

		JComponent rawTable = createRawMessageTable(viewModel, rawView);
		JComponent crackedTable = createCrackedMessageTable(viewModel);

		JSplitPane messageView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		messageView.setResizeWeight(.5);
		messageView.setBorder(null);

		messageView.setLeftComponent(new JScrollPane(rawTable));
		messageView.setRightComponent(new JScrollPane(crackedTable));

		messageAndEventView.setLeftComponent(messageView);
		messageAndEventView.setRightComponent(rawView);
		messageAndEventView.setBorder(null);

		JPanel mainView = new JPanel(new BorderLayout());
		mainView.add(messageAndEventView, BorderLayout.CENTER);

		// @todo - wrap the filter component in a "view builder". This will
		// eliminate
		// the cast and provide the ability to change the layout without
		// junking up
		// builder.
		JTextField filterField = new MessageSearchField();

		FilterList<LogMessage> messages = ((GlazedListsMemoryLogModel) memoryLogModel)
				.getMessages();

		EventList matchers = new BasicEventList();

		TextComponentMatcherEditor<LogMessage> liveSearchMatcherEditor = new TextComponentMatcherEditor<LogMessage>(
				filterField, new MessageFilterator());
		matchers.add(liveSearchMatcherEditor);
		MatcherEditor matcherEditor = new CompositeMatcherEditor(matchers);
		messages.setMatcherEditor(matcherEditor);

		JPanel sortPanel = new JPanel();
		((FlowLayout) sortPanel.getLayout()).setAlignment(FlowLayout.LEFT);
		JCheckBox sortCheckbox = new JCheckBox("Keep New Messages At The Top");
		sortPanel.add(sortCheckbox);
		sortCheckbox.addActionListener(viewModel
				.getSortByMessageIndexActionListener());

		JCheckBox hideHeartbeats = new JCheckBox("Hide Heartbeats");
		sortPanel.add(hideHeartbeats);
		hideHeartbeats.addActionListener(viewModel
				.getHideHeartbeatsActionListener());

		JPanel filterPanel = new JPanel();
		((FlowLayout) filterPanel.getLayout()).setAlignment(FlowLayout.RIGHT);
		filterPanel.add(new JLabel("Search:"));
		filterPanel.add(filterField);

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(sortPanel, BorderLayout.WEST);
		northPanel.add(filterPanel, BorderLayout.EAST);
		mainView.add(northPanel, BorderLayout.NORTH);

		return mainView;
	}

	private static JComponent createEventMessageTable(ViewModel viewModel) {
		return new JList(viewModel.getEventsListModel());
	}

	private static JComponent createRawMessageTable(ViewModel viewModel,
			final JTextArea ta) {
		JTable table = new JTable(viewModel.getRawMessagesTableModel());
		table.setSelectionModel(viewModel.getRawMessagesSelectionModel());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn tableColumn = columns.nextElement();
			tableColumn.setCellRenderer(viewModel
					.getRawMessagesTableCellRenderer());
		}
		for (int column = 0; column < table.getColumnCount() - 1; column++) {
			TableColumn tableColumn = table.getColumnModel().getColumn(column);
			int preferredWidth = tableColumn.getMinWidth();
			int maxWidth = tableColumn.getMaxWidth();

			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer cellRenderer = table.getCellRenderer(row,
						column);
				Component c = table.prepareRenderer(cellRenderer, row, column);
				int width = c.getPreferredSize().width
						+ table.getIntercellSpacing().width;
				preferredWidth = Math.max(preferredWidth, width);

				// We've exceeded the maximum width, no need to check other
				// rows

				if (preferredWidth >= maxWidth) {
					preferredWidth = maxWidth;
					break;
				}
			}

			tableColumn.setMaxWidth(preferredWidth + 10);
			tableColumn.setMinWidth(preferredWidth + 10);
		}
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JTable target = (JTable) e.getSource();
				int row = target.getSelectedRow();
				ta.setText(target.getValueAt(row, 3).toString());
			}
		});

		return table;
	}

	private static JComponent createCrackedMessageTable(ViewModel viewModel) {

		TreeTableModel model = viewModel.getAdminMessagesCrackedModel();
		JXTreeTable table = new JXTreeTable(model);

		// stupid JXTreeTable uses different row values...
		table.setRowHeight(16);
		table.setRowMargin(1);

		table.setColumnMargin(1);

		table.setShowGrid(true);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);
		table.setRootVisible(false);
		table.setShowsRootHandles(true);
		table.setClosedIcon(null);
		table.setOpenIcon(null);
		table.setLeafIcon(null);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		HighlighterPipeline pipeline = new HighlighterPipeline();
		pipeline.addHighlighter(new FieldHighlighter());
		table.setHighlighters(pipeline);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		return table;
	}
}
