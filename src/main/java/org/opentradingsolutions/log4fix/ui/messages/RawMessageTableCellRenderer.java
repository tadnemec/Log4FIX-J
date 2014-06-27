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

import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.opentradingsolutions.log4fix.core.LogMessage;
import org.opentradingsolutions.log4fix.core.ValidationError;

import ca.odell.glazedlists.swing.EventTableModel;

/**
 * @author Brian M. Coyner
 */
public class RawMessageTableCellRenderer extends DefaultTableCellRenderer {

	public static final String RECEIVING = "Receiving";
	public static final String SENDING = "Sending";

	private static Color incomingColor = new Color(131, 218, 102);
	private static Color outgoingColor = new Color(233, 173, 89);
	private static Color incomingSelected = incomingColor.darker().darker();
	private static Color outgoingSelected = outgoingColor.darker().darker();
	private static Color incomingText = new Color(0, 0, 0);
	private static Color outgoingText = new Color(0, 0, 0);
	private static Color incomingSelectedText = new Color(255, 255, 255);
	private static Color outgoingSelectedText = new Color(255, 255, 255);
	private SimpleDateFormat formatter = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss");

	// TODO: Adjustable colors
	public static void setIC(Color c) {
		incomingColor = c;
	}

	public static void setOC(Color c) {
		outgoingColor = c;
	}

	public static void setIS(Color c) {
		incomingSelected = c;
	}

	public static void setOS(Color c) {
		outgoingSelected = c;
	}

	public static void setIT(Color c) {
		incomingText = c;
	}

	public static void setOT(Color c) {
		outgoingText = c;
	}

	public static void setIST(Color c) {
		incomingSelectedText = c;
	}

	public static void setOST(Color c) {
		outgoingSelectedText = c;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component comp = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		// noinspection unchecked
		EventTableModel<LogMessage> tableModel = (EventTableModel<LogMessage>) table
				.getModel();
		LogMessage message = tableModel.getElementAt(row);

		if (table.convertColumnIndexToModel(column) == 2) {
			if (value != null) {
				setValue(formatter.format(value));
			} else {
				setValue("Not Found");
			}
		}

		if (message.isIncoming()) {
			if (table.convertColumnIndexToModel(column) == 0) {
				setValue(RECEIVING);
			}
			setBackground(isSelected ? incomingSelected : incomingColor);
			setForeground(isSelected ? incomingSelectedText : incomingText);
		} else {
			if (table.convertColumnIndexToModel(column) == 0) {
				setValue(SENDING);
			}
			setBackground(isSelected ? outgoingSelected : outgoingColor);
			setForeground(isSelected ? outgoingSelectedText : outgoingText);
		}

		if (message.getValidationErrorMessages() != null) {
			setBorder(BorderFactory.createLineBorder(Color.RED));

			String tooltip = "<html>Message Errors:";
			java.util.List<ValidationError> errors = message
					.getValidationErrorMessages();
			for (ValidationError error : errors) {
				tooltip += "<div>" + error.getMessage() + "</div>";
			}

			tooltip += "</html>";
			setToolTipText(tooltip);
		} else {
			setToolTipText(message.getRawMessage());
		}

		return comp;
	}
}
