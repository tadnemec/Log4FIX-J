package org.opentradingsolutions.log4fix.core;

import javax.swing.JComponent;

public class Tab {
	private String title;
	private JComponent component;
	private int count;
	private MemoryLogModel mlm;

	public Tab(String title, int count, JComponent component, MemoryLogModel mlm) {
		this.title = title;
		this.count = count;
		this.component = component;
		this.mlm = mlm;
	}

	public String getTitle() {
		return title;
	}

	public JComponent getComponent() {
		return component;
	}

	public int getCount() {
		return count;
	}

	public MemoryLogModel getModel() {
		return mlm;
	}

	public void setComponent(JComponent j) {
		component = j;
	}
}
