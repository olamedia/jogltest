package ru.olamedia.input;

import com.jogamp.newt.event.KeyEvent;

public interface KeyListener {
	public void onKeyPressed(String name, KeyEvent e);

	public void onKeyReleased(String name, KeyEvent e);

}
