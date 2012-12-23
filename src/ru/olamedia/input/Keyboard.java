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
		/*
		 * Key pressed: 87
		 * Key pressed: 65
		 * Key pressed: 83
		 * Key pressed: 68
		 * Key pressed: 65034
		 * Key pressed: 1731
		 * Key pressed: 1734
		 * Key pressed: 1753
		 * Key pressed: 1751
		 * 
		 * keycode 24 = q Q Cyrillic_shorti Cyrillic_SHORTI
		 * keycode 25 = w W Cyrillic_tse Cyrillic_TSE
		 * keycode 26 = e E Cyrillic_u Cyrillic_U
		 * keycode 27 = r R Cyrillic_ka Cyrillic_KA
		 * keycode 28 = t T Cyrillic_ie Cyrillic_IE
		 * keycode 29 = y Y Cyrillic_en Cyrillic_EN
		 * keycode 30 = u U Cyrillic_ghe Cyrillic_GHE
		 * keycode 31 = i I Cyrillic_sha Cyrillic_SHA
		 * keycode 32 = o O Cyrillic_shcha Cyrillic_SHCHA
		 * keycode 33 = p P Cyrillic_ze Cyrillic_ZE
		 * 
		 * KeyPress event, serial 36, synthetic NO, window 0x4600001,
		 * root 0xb7, subw 0x0, time 326572351, (602,679), root:(669,730),
		 * state 0x10, keycode 25 (keysym 0x77, w), same_screen YES,
		 * XLookupString gives 1 bytes: (77) "w"
		 * XmbLookupString gives 1 bytes: (77) "w"
		 * XFilterEvent returns: False
		 * 
		 * KeyRelease event, serial 36, synthetic NO, window 0x4600001,
		 * root 0xb7, subw 0x0, time 326572447, (602,679), root:(669,730),
		 * state 0x10, keycode 25 (keysym 0x77, w), same_screen YES,
		 * XLookupString gives 1 bytes: (77) "w"
		 * XFilterEvent returns: False
		 * 
		 * KeyPress event, serial 36, synthetic NO, window 0x4600001,
		 * root 0xb7, subw 0x0, time 326588119, (118,-7), root:(185,44),
		 * state 0x2010, keycode 25 (keysym 0x6c3, Cyrillic_tse), same_screen
		 * YES,
		 * XLookupString gives 2 bytes: (d1 86) "ц"
		 * XmbLookupString gives 2 bytes: (d1 86) "ц"
		 * XFilterEvent returns: False
		 * 
		 * KeyRelease event, serial 36, synthetic NO, window 0x4600001,
		 * root 0xb7, subw 0x0, time 326588200, (118,-7), root:(185,44),
		 * state 0x2010, keycode 25 (keysym 0x6c3, Cyrillic_tse), same_screen
		 * YES,
		 * XLookupString gives 2 bytes: (d1 86) "ц"
		 * XFilterEvent returns: False
		 */
		if (e.getKeyCode() < 256) {
			downState[e.getKeyCode()] = true;
			if (names.containsValue(e.getKeyCode())) {
				String name = (String) names.getKey(e.getKeyCode());
				for (ru.olamedia.input.KeyListener l : listeners) {
					l.onKeyPressed(name, e);
				}
			}
			System.out.println("Key pressed: " + e.getKeyCode());
		} else {
			System.err.println("Key pressed: " + e.getKeyCode());
		}
	}

	@Override
	public void keyReleased(com.jogamp.newt.event.KeyEvent e) {
		if (e.getKeyCode() < 256) {
			downState[e.getKeyCode()] = false;
			if (names.containsValue(e.getKeyCode())) {
				String name = (String) names.getKey(e.getKeyCode());
				for (ru.olamedia.input.KeyListener l : listeners) {
					l.onKeyReleased(name, e);
				}
			}
		}
	}

	@Override
	public void keyTyped(com.jogamp.newt.event.KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
