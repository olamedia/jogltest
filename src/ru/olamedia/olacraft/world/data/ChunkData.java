package ru.olamedia.olacraft.world.data;

import java.io.Serializable;
import java.util.BitSet;

import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;

public class ChunkData implements Serializable {
	private static final long serialVersionUID = -5704237444737895501L;
	public ChunkLocation location;
	public static transient int SIZE = 4096;
	// private boolean[] notEmpty = new boolean[SIZE];
	private BitSet emptyBlocks = new BitSet(4096);
	public int notEmptyCount = 0;

	// public transient int[] type = new int[SIZE];
	// /public transient ChunkLightData light;

	public ChunkData() {
		// light = new ChunkLightData();
	}

	public void compact() {
		if (notEmptyCount == 0) {
			emptyBlocks = null;
		}
	}

	public static int normalize(int v) {
		int n = v;
		if (n > 15) {
			n = n % 16;
		}
		if (n < 0) {
			n = 16 + n % 16 - 1;
			// v = 15 - v;
		}
		// System.out.println("normalize(" + v + ") = " + n);
		return n;
	}

	public static int getId(int xInsideChunk, int yInsideChunk, int zInsideChunk) {
		xInsideChunk = normalize(xInsideChunk);
		yInsideChunk = normalize(yInsideChunk);
		zInsideChunk = normalize(zInsideChunk);
		int id = xInsideChunk * 16 * 16 + yInsideChunk * 16 + zInsideChunk;
		if (id > SIZE) {
			System.err.println("Exception while getID(" + xInsideChunk + "," + yInsideChunk + "," + zInsideChunk + ")");
			throw new ArrayIndexOutOfBoundsException(id);
		}
		return id;
	}

	public boolean isEmpty(BlockLocation blockLocation) {
		if (notEmptyCount == 0) {
			return true;
		}
		int id = getId(Chunk.in(blockLocation.x), Chunk.in(blockLocation.y), Chunk.in(blockLocation.z));
		return isEmpty(id);
		// return !notEmpty[id];
	}

	public boolean isEmpty(int id) {
		if (notEmptyCount == 0) {
			return true;
		}
		return emptyBlocks.get(id);
	}

	public void setEmpty(int id, boolean isEmpty) {
		if (isEmpty(id) != isEmpty) {
			if (!isEmpty) {
				notEmptyCount++;
			} else {
				notEmptyCount--;
			}
		}
		emptyBlocks.set(id, isEmpty);
	}

	public boolean isEmpty() {
		return notEmptyCount == 0;
	}
}
