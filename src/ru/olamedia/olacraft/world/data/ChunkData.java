package ru.olamedia.olacraft.world.data;

import java.io.Serializable;

import ru.olamedia.math.OpenBitSet;
import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;

public class ChunkData implements Serializable {
	private static final long serialVersionUID = -5704237444737895501L;
	public ChunkLocation location;
	public static transient int SIZE = 4096;
	// private boolean[] notEmpty = new boolean[SIZE];
	public OpenBitSet emptyBlocks = new OpenBitSet(4096);
	public int notEmptyCount = 0;

	// public transient int[] type = new int[SIZE];

	public ChunkData() {
	}

	public void compact() {
		if (emptyBlocks.cardinality() == 0) {
			emptyBlocks = null;
		}
	}

	public boolean isEmpty(BlockLocation blockLocation) {
		if (emptyBlocks == null) {
			return true;
		}
		int id = Chunk.in(blockLocation.x) * 16 * 16 + Chunk.in(blockLocation.y) * 16 + Chunk.in(blockLocation.z);
		return isEmpty(id);
	}

	public boolean isEmpty(int id) {
		if (emptyBlocks == null) {
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
		if (isEmpty) {
			emptyBlocks.set(id);
		} else {
			emptyBlocks.clear(id);
		}
	}

	public boolean isEmpty() {
		return emptyBlocks == null || emptyBlocks.cardinality() == 0;
	}

	public void setEmpty(int inChunkX, int inChunkY, int inChunkZ, boolean isEmpty) {
		int id = inChunkX * 16 * 16 + inChunkY * 16 + inChunkZ;
		setEmpty(id, isEmpty);
	}

	public void setEmpty(BlockLocation blockLocation, boolean isEmpty) {
		int id = Chunk.in(blockLocation.x) * 16 * 16 + Chunk.in(blockLocation.y) * 16 + Chunk.in(blockLocation.z);
		setEmpty(id, isEmpty);
	}

}
