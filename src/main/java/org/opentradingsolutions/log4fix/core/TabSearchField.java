package org.opentradingsolutions.log4fix.core;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class TabSearchField extends JTextField {

	/**
	 * @param n
	 *            integer representing the size of the field
	 * @param sensitive
	 *            boolean indicating whether the field should update as you type
	 **/
	public TabSearchField(int n, boolean sensitive) {
		super(n);
		if (sensitive) {
			initSensitiveKeyListener();
		} else {
			initKeyListener();
		}
	}

	private void initKeyListener() {
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					setText("");
					postActionEvent();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					postActionEvent();
				}
			}
		});
	}

	private void initSensitiveKeyListener() {
		addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					setText("");
					postActionEvent();
				} else {
					postActionEvent();
				}
			}
		});
	}
}
