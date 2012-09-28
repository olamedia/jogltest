package ru.olamedia.olacraft.world.chunk;

import ru.olamedia.olacraft.world.provider.WorldProvider;

public class BlockSlice {
	protected WorldProvider provider;
	protected int leftX;
	protected int bottomY;
	protected int backZ;
	protected int width;
	protected int height;
	protected int depth;

	protected ChunkSlice chunkSlice;

	// Memory leak:
	//protected int[][] highest = new int[256][256];
	
	public void invalidateCache(){
		//highest = new int[256][256];
	}
	
/*	public int getHighest(int blockX, int blockZ) {
		if (highest[blockX - leftX][blockZ - backZ] > 0){
			return highest[blockX - leftX][blockZ - backZ];
		}
		for (int y = 0; y < 128; y++) {
			if (provider.isEmptyBlock(blockX, y, blockZ)){
				highest[blockX - leftX][blockZ - backZ] = y; 
				return y;
			}
		}
		return 0;
	}*/

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
		this.provider = provider;
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	public ChunkSlice getChunkSlice() {
		if (null == chunkSlice) {
			chunkSlice = new ChunkSlice(provider, width / 16, height / 16, depth / 16);
		}
		int x = Chunk.v(leftX);
		int y = Chunk.v(bottomY);
		int z = Chunk.v(backZ);
		chunkSlice.setLocation(x, y, z);
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
		if (x != leftX || y != bottomY || z != backZ){
			invalidateCache();
		}
		leftX = x;
		bottomY = y;
		backZ = z;
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
	 * @return the left x (blocks)
	 */
	public int getX() {
		return leftX;
	}

	/**
	 * @return the bottom y (blocks)
	 */
	public int getY() {
		return bottomY;
	}

	/**
	 * @return the back z (blocks)
	 */
	public int getZ() {
		return backZ;
	}
}
