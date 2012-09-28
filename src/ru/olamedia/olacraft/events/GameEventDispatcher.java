package ru.olamedia.olacraft.events;

import java.util.ArrayList;
import java.util.List;

public class GameEventDispatcher {
	private static List<GameEventListener> listeners = new ArrayList<GameEventListener>();

	public static void attach(GameEventListener listener) {
		listeners.add(listener);
	}

	public static void detach(GameEventListener listener) {
		listeners.remove(listener);
	}

	public static void dispatch(GameEvent e) {
		for (GameEventListener l : listeners) {
			l.on(e);
		}
	}
}
