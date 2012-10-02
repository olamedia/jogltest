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

	public ChunkSlice(WorldProvider provider, int width, int height, int depth) {
		this.provider = provider;
		this.width = width;
		this.height = height;
		this.depth = depth;
		offset = new ChunkLocation();
	}

	protected HashMap<String, Chunk> chunks = new HashMap<String, Chunk>();

	public Chunk getChunk(ChunkLocation location) {
		int x = location.x;
		int y = location.y;
		int z = location.z;
		String key = x + ";" + y + ";" + z;
		if (chunks.containsKey(key)) {
			return chunks.get(key);
		} else {
			Chunk chunk = new Chunk(provider);
			chunk.setLocation(location);
			chunks.put(key, chunk);
			return chunk;
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
