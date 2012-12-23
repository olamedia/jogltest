package ru.olamedia.olacraft.world.chunk;

import java.util.HashMap;

import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class ChunkSlice {
	private WorldProvider provider;
	private ChunkLocation offset;
	private int width;
	private int height;
	private int depth;

	public static ChunkSlice rendererInstance;

	public ChunkSlice(WorldProvider provider, int width, int height, int depth) {
		this.provider = provider;
		this.width = width;
		this.height = height;
		this.depth = depth;
		offset = new ChunkLocation();
	}

	public HashMap<Integer, HashMap<Integer, HashMap<Integer, Chunk>>> iChunks = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Chunk>>>();

	public Chunk getChunk(ChunkLocation location) {
		// int x = location.x;
		// int y = location.y;
		// int z = location.z;
		// String key = x + ";" + y + ";" + z;
		if (iChunks.containsKey(location.x)) {
			if (iChunks.get(location.x).containsKey(location.y)) {
				if (iChunks.get(location.x).get(location.y).containsKey(location.z)) {
					return iChunks.get(location.x).get(location.y).get(location.z);
				} else {

				}
			} else {
				iChunks.get(location.x).put(location.y, new HashMap<Integer, Chunk>());
			}
		} else {
			iChunks.put(location.x, new HashMap<Integer, HashMap<Integer, Chunk>>());
			iChunks.get(location.x).put(location.y, new HashMap<Integer, Chunk>());
		}

		Chunk chunk = new Chunk(provider, location);
		// chunks.put(key, chunk);
		iChunks.get(location.x).get(location.y).put(location.z, chunk);
		return chunk;
		// if (chunks.containsKey(key)) {
		// return chunks.get(key);
		// } else {
		// Chunk chunk = new Chunk(provider);
		// chunk.setLocation(location);
		// chunks.put(key, chunk);
		// return chunk;
		// }
	}

	public void removeChunk(ChunkLocation location) {
		if (iChunks.containsKey(location.x)) {
			if (iChunks.get(location.x).containsKey(location.y)) {
				if (iChunks.get(location.x).get(location.y).containsKey(location.z)) {
					iChunks.get(location.x).get(location.y).remove(location.z);
				}
			}
		}
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @param depth
	 *            the depth to set
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void setCenter(int x, int y, int z) {
		offset.x = x - width / 2;
		offset.y = y - height / 2;
		offset.z = z - depth / 2;
	}

	/**
	 * @return the leftX
	 */
	public int getX() {
		return offset.x;
	}

	/**
	 * @return the bottomY
	 */
	public int getY() {
		return offset.y;
	}

	/**
	 * @return the backZ
	 */
	public int getZ() {
		return offset.z;
	}

	public void setLocation(ChunkLocation chunkOffset) {
		offset = chunkOffset;
	}

}
