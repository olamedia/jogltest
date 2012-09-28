package ru.olamedia.olacraft.events;

import java.util.HashMap;

public class GameEventRegistry {
	private static HashMap<String, Integer> map = new HashMap<String, Integer>();
	private static int i = 0;

	public static int get(String name) {
		if (map.containsKey(name)) {
			return map.get(name);
		}
		i++;
		map.put(name, i);
		return i;
	}
}
