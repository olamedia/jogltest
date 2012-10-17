package ru.olamedia.olacraft.world.data;

import java.io.Serializable;

import ru.olamedia.math.OpenBitSet;
import ru.olamedia.olacraft.world.blockTypes.BlockType;
import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class ChunkData implements Serializable {
	private static final long serialVersionUID = -5704237444737895501L;
	public ChunkLocation location;
	public static transient int SIZE = 4096;
	// private boolean[] notEmpty = new boolean[SIZE];
	public OpenBitSet emptyBlocks = new OpenBitSet(4096);
	public int visibleCount = 0;
	public OpenBitSet visible = null; // fast precomputed
														// visibility (true if
														// any side is open)
	//public OpenBitSet sunlight = new OpenBitSet(65536);
	public byte[] types = new byte[4096];
	public int notEmptyCount = 0;
	public boolean visibilityPrecomputed = false;

	// public transient int[] type = new int[SIZE];

	public ChunkData() {
	}

	private void setVisible(int x, int y, int z) {
		if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) {
			return;
		}
		int id = x * 16 * 16 + y * 16 + z;
		if (!visible.get(id)){
			visibleCount++;
		}
		visible.set(id);
	}

	private void setInvisible(int x, int y, int z) {
		if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15) {
			return;
		}
		int id = x * 16 * 16 + y * 16 + z;
		if (visible.get(id)){
			visibleCount--;
		}
		visible.clear(id);
	}

	public void computeVisibility(WorldProvider provider) {
		visible = new OpenBitSet(4096);
		visibleCount = 0;
		precomputeVisibility();
		computeVisibility(provider.getChunk(location.getLeft()), provider.getChunk(location.getRight()),
				provider.getChunk(location.getTop()), provider.getChunk(location.getBottom()),
				provider.getChunk(location.getFront()), provider.getChunk(location.getBack()));
	}

	public void computeVisibility(ChunkData left, ChunkData right, ChunkData top, ChunkData bottom, ChunkData front,
			ChunkData back) {
		// compute left/right
		int x, y, z, id;
		for (y = 0; y <= 15; y++) {
			for (z = 0; z <= 15; z++) {
				x = 15;
				id = x * 16 * 16 + y * 16 + z;
				if (left.emptyBlocks.get(id)) {
					x = 0;
					setVisible(x, y, z);
				}
				x = 0;
				id = x * 16 * 16 + y * 16 + z;
				if (right.emptyBlocks.get(id)) {
					x = 15;
					setVisible(x, y, z);
				}
			}
		}
		// top/bottom
		for (x = 0; x <= 15; x++) {
			for (z = 0; z <= 15; z++) {
				y = 15;
				id = x * 16 * 16 + y * 16 + z;
				if (bottom.emptyBlocks.get(id)) {
					y = 0;
					setVisible(x, y, z);
				}
				y = 0;
				id = x * 16 * 16 + y * 16 + z;
				if (top.emptyBlocks.get(id)) {
					y = 15;
					setVisible(x, y, z);
				}
			}
		}
		// front/back
		for (x = 0; x <= 15; x++) {
			for (y = 0; y <= 15; y++) {
				z = 15;
				id = x * 16 * 16 + y * 16 + z;
				if (back.emptyBlocks.get(id)) {
					z = 0;
					setVisible(x, y, z);
				}
				z = 0;
				id = x * 16 * 16 + y * 16 + z;
				if (front.emptyBlocks.get(id)) {
					z = 15;
					setVisible(x, y, z);
				}
			}
		}
	}

	/**
	 * Compute visibility ("visible" if any side have non-opaque neighbor)
	 * Leaving side blocks invisible. Use computeVisibility(WorldProvider) to compute visibility of side blocks
	 */
	public void precomputeVisibility() {
		// first pass, make all blocks invisible, except side blocks
		for (int x = 0; x <= 15; x++) {
			for (int y = 0; y <= 15; y++) {
				for (int z = 0; z <= 15; z++) {
					// if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z
					// == 15) {
					// setVisible(x, y, z);
					// } else {
					// setInvisible(x, y, z);
					// }
					setInvisible(x, y, z);
				}
			}
		}
		// second pass, make some blocks visible
		for (int x = 0; x <= 15; x++) {
			for (int y = 0; y <= 15; y++) {
				for (int z = 0; z <= 15; z++) {
					int id = x * 16 * 16 + y * 16 + z;
					if (emptyBlocks.get(id)) {
						setVisible(x - 1, y, z);
						setVisible(x + 1, y, z);
						setVisible(x, y - 1, z);
						setVisible(x, y + 1, z);
						setVisible(x, y, z - 1);
						setVisible(x, y, z + 1);
					}
				}
			}
		}
		visibilityPrecomputed = true;
	}

	public void compact() {
		if (emptyBlocks.cardinality() == 0) {
			// emptyBlocks = null;
		}
	}

	public BlockType getType(BlockLocation blockLocation, WorldProvider provider) {
		if (emptyBlocks == null) {
			return null;
		}
		int id = Chunk.in(blockLocation.x) * 16 * 16 + Chunk.in(blockLocation.y) * 16 + Chunk.in(blockLocation.z);
		if (isEmpty(id)) {
			return null;
		}
		return provider.getBlockTypeById(types[id]);
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
