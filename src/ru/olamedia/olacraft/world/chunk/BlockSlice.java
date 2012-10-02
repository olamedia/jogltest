package ru.olamedia.olacraft.world.chunk;

import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class BlockSlice {
	protected WorldProvider provider;
	protected BlockLocation offset;
	protected int width;
	protected int height;
	protected int depth;

	protected ChunkSlice chunkSlice;

	public void invalidateCache() {
	}

	/**
	 * 
	 * @param provider
	 * @param width
	 *            (blocks)
	 * @param height
	 *            (blocks)
	 * @param depth
	 *            (blocks)
	 */
	public BlockSlice(WorldProvider provider, int width, int height, int depth) {
		offset = new BlockLocation();
		this.provider = provider;
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	public ChunkSlice getChunkSlice() {
		if (null == chunkSlice) {
			chunkSlice = new ChunkSlice(provider, width / 16, height / 16, depth / 16);
		}
		ChunkLocation chunkOffset = offset.getChunkLocation();
		chunkSlice.setLocation(chunkOffset);
		return chunkSlice;
	}

	public int getTotalBlocks() {
		return getWidth() * getHeight() * getDepth();
	}

	/**
	 * @return the width (blocks)
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            (blocks)
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height (blocks)
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            (blocks)
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the depth (blocks)
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @param depth
	 *            (blocks)
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/**
	 * 
	 * @param x
	 *            (blocks)
	 * @param y
	 *            (blocks)
	 * @param z
	 *            (blocks)
	 */
	public void setLocation(int x, int y, int z) {
		if (x != offset.x || y != offset.y || z != offset.z) {
			invalidateCache();
		}
		offset.x = x;
		offset.y = y;
		offset.z = z;
	}

	/**
	 * 
	 * @param x
	 *            (blocks)
	 * @param y
	 *            (blocks)
	 * @param z
	 *            (blocks)
	 */
	public void setCenter(int x, int y, int z) {
		setLocation(x - width / 2, y - height / 2, z - depth / 2);
	}

	/**
	 * @return offset
	 */
	public BlockLocation getOffset() {
		return offset;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + offset + ";" + width + "x" + height + "x" + depth + "]";
	}
}
