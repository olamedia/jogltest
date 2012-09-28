package ru.olamedia.olacraft.world.chunk;

import java.util.HashMap;

import ru.olamedia.olacraft.world.provider.WorldProvider;

public class ChunkSlice {
	private WorldProvider provider;
	private int leftX;
	private int bottomY;
	private int backZ;
	private int width;
	private int height;
	private int depth;

	public ChunkSlice(WorldProvider provider, int width, int height, int depth) {
		this.provider = provider;
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	protected HashMap<String, Chunk> chunks = new HashMap<String, Chunk>();

	public Chunk getChunk(int x, int y, int z) {
		String key = x + ";" + y + ";" + z;
		if (chunks.containsKey(key)) {
			return chunks.get(key);
		} else {
			Chunk chunk = new Chunk(provider);
			chunk.setLocation(x * 16, y * 16, z * 16);
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

	public void setLocation(int x, int y, int z) {
		leftX = x;
		bottomY = y;
		backZ = z;
	}

	public void setCenter(int x, int y, int z) {
		leftX = x - width / 2;
		bottomY = y - height / 2;
		backZ = z - depth / 2;
	}

	/**
	 * @return the leftX
	 */
	public int getX() {
		return leftX;
	}

	/**
	 * @return the bottomY
	 */
	public int getY() {
		return bottomY;
	}

	/**
	 * @return the backZ
	 */
	public int getZ() {
		return backZ;
	}
}
