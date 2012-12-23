package ru.olamedia.olacraft.world.location;

import java.nio.IntBuffer;

public class IntLocation {
	private static int yShift = 128;

	public static IntBuffer allocate() {
		return IntBuffer.allocate(3);
	}

	public static boolean inRange(int x, int y, int z) {
		return inRange(x) && inRange(y) && inRange(z);
	}

	public static boolean inRange(int val) {
		return (val >= 0) && (val <= 15);
	}

	public static byte in(int val) {
		return (byte) (val & 15);
	}

	public static int chunk(int val) {
		return val >> 4;
	}

	public static int region(int val) {
		return val >> 8;
	}

	public static int rev(int val) {
		return val * 16;
	}

	public static int rev2(int val) {
		return val * 256;
	}

	public static short id(int x, int y, int z) {
		return (short) ((x & 15) * 256 + (y & 15) * 16 + (z & 15));
	}

	public static short leftId(int x, int y, int z) {
		return id(x - 1, y, z);
	}

	public static short rightId(int x, int y, int z) {
		return id(x + 1, y, z);
	}

	public static short topId(int x, int y, int z) {
		return id(x, y + 1, z);
	}

	public static short bottomId(int x, int y, int z) {
		return id(x, y - 1, z);
	}

	public static short backId(int x, int y, int z) {
		return id(x, y, z + 1);
	}

	public static short frontId(int x, int y, int z) {
		return id(x, y, z - 1);
	}

	public static int[] block2chunk(int[] b) {
		return new int[] { chunk(b[0]), chunk(b[1] + yShift), chunk(b[2]) };
	}

	public static void block2chunk(int[] b, int[] c) {
		c[0] = chunk(b[0]);
		c[1] = chunk(b[1] + yShift);
		c[2] = chunk(b[2]);
	}

	public static void block2chunk(IntBuffer b, IntBuffer c) {
		c.put(0, chunk(b.get(0)));
		c.put(1, chunk(b.get(1) + yShift));
		c.put(2, chunk(b.get(2)));
	}

	public static int[] chunk2block(int[] c) {
		return new int[] { rev(c[0]), rev(c[1]) - yShift, rev(c[2]) };
	}

	public static void chunk2block(int[] c, int[] b) {
		b[0] = rev(c[0]);
		b[1] = rev(c[1]) - yShift;
		b[2] = rev(c[2]);
	}

	public static void chunk2block(IntBuffer c, IntBuffer b) {
		b.put(0, rev(c.get(0)));
		b.put(1, rev(c.get(1)) - yShift);
		b.put(2, rev(c.get(2)));
	}

	public static int[] region2chunk(int[] r) {
		return new int[] { rev(r[0]), rev(r[1]), rev(r[2]) };
	}

	public static void region2chunk(int[] r, int[] c) {
		c[0] = rev(r[0]);
		c[1] = rev(r[1]);
		c[2] = rev(r[2]);
	}

	public static void region2chunk(IntBuffer r, IntBuffer c) {
		c.put(0, rev(r.get(0)));
		c.put(1, rev(r.get(1)));
		c.put(2, rev(r.get(2)));
	}

	public static int[] region2block(int[] r) {
		return new int[] { rev2(r[0]), rev2(r[1]) - yShift, rev2(r[2]) };
	}

	public static void region2block(int[] r, int[] b) {
		b[0] = rev2(r[0]);
		b[1] = rev2(r[1]) - yShift;
		b[2] = rev2(r[2]);
	}

	public static void region2block(IntBuffer r, IntBuffer b) {
		b.put(0, rev2(r.get(0)));
		b.put(1, rev2(r.get(1)) - yShift);
		b.put(2, rev2(r.get(2)));
	}

	public static int[] block2region(int[] b) {
		return new int[] { chunk(chunk(b[0])), chunk(chunk(b[1] + yShift)), chunk(chunk(b[2])) };
	}

	public static void block2region(int[] b, int[] r) {
		r[0] = region(b[0]);
		r[1] = region(b[1] + yShift);
		r[2] = region(b[2]);
	}

	public static void block2region(IntBuffer b, IntBuffer r) {
		r.put(0, region(b.get(0)));
		r.put(1, region(b.get(1) + yShift));
		r.put(2, region(b.get(2)));
	}

	public static int[] chunk2region(int[] c) {
		return new int[] { chunk(c[0]), chunk(c[1]), chunk(c[2]) };
	}

	public static void chunk2region(int[] c, int[] r) {
		r[0] = chunk(c[0]);
		r[1] = chunk(c[1]);
		r[2] = chunk(c[2]);
	}

	public static void chunk2region(IntBuffer c, IntBuffer r) {
		r.put(0, chunk(c.get(0)));
		r.put(1, chunk(c.get(1)));
		r.put(2, chunk(c.get(2)));
	}

	public static void set(IntBuffer b, int x, int y, int z) {
		b.put(0, x);
		b.put(1, y);
		b.put(2, z);
	}

	public static void setXZ(IntBuffer b, int x, int z) {
		b.put(0, x);
		b.put(2, z);
	}

	public static void setY(IntBuffer b, int y) {
		b.put(1, y);
	}

	public static int getX(IntBuffer b) {
		return b.get(0);
	}

	public static int getY(IntBuffer b) {
		return b.get(1);
	}

	public static int getZ(IntBuffer b) {
		return b.get(2);
	}

	public static int id(IntBuffer b) {
		return id(b.get(0), b.get(1), b.get(2));
	}

	public static void setSum(IntBuffer target, IntBuffer b1, IntBuffer b2) {
		target.put(0, b1.get(0) + b2.get(0));
		target.put(1, b1.get(1) + b2.get(1));
		target.put(2, b1.get(2) + b2.get(2));
	}
}
