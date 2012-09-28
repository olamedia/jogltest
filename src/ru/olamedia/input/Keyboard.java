package ru.olamedia.input;

import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

public class Keyboard implements com.jogamp.newt.event.KeyListener {
	public static Keyboard instance = new Keyboard();
	private static boolean[] downState = new boolean[256];
	private static BidiMap names = new DualHashBidiMap();

	public static void setName(String name, int keyCode) {
		names.put(name, keyCode);
	}

	public static boolean isKeyDown(int keyCode) {
		return downState[keyCode];
	}

	public static boolean isKeyDown(String name) {
		if (names.containsKey(name)) {
			return downState[((Integer) names.get(name)).intValue()];
		}
		return false;
	}

	private static List<ru.olamedia.input.KeyListener> listeners = new ArrayList<ru.olamedia.input.KeyListener>();

	public static void attach(ru.olamedia.input.KeyListener l) {
		listeners.add(l);
	}

	@Override
	public void keyPressed(com.jogamp.newt.event.KeyEvent e) {
		downState[e.getKeyCode()] = true;
		if (names.containsValue(e.getKeyCode())) {
			String name = (String) names.getKey(e.getKeyCode());
			for (ru.olamedia.input.KeyListener l : listeners) {
				l.onKeyPressed(name, e);
			}
		}
	}

	@Override
	public void keyReleased(com.jogamp.newt.event.KeyEvent e) {
		downState[e.getKeyCode()] = false;
		if (names.containsValue(e.getKeyCode())) {
			String name = (String) names.getKey(e.getKeyCode());
			for (ru.olamedia.input.KeyListener l : listeners) {
				l.onKeyReleased(name, e);
			}
		}
	}

	@Override
	public void keyTyped(com.jogamp.newt.event.KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
