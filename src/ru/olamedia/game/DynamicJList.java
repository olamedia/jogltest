package ru.olamedia.game;

import javax.swing.DefaultListModel;
import javax.swing.JList;

public class DynamicJList extends JList {
	private static final long serialVersionUID = 8188447632893130182L;

	public DynamicJList() {
		super(new DefaultListModel());
	}

	public DefaultListModel getContents() {
		return (DefaultListModel) getModel();
	}
}
