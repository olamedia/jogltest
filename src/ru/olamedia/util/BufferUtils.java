package ru.olamedia.util;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class BufferUtils {

	public static ShortBuffer createShortBuffer(int capacity) {
		return ShortBuffer.allocate(capacity);
	}

	public static FloatBuffer createFloatBuffer(int capacity) {
		return FloatBuffer.allocate(capacity);
	}

}
