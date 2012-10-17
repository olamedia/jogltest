package ru.olamedia.olacraft.world.chunk;

import java.util.Iterator;

import ru.olamedia.camera.MatrixCamera;
import ru.olamedia.olacraft.world.block.Block;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class BlockSlice implements Iterator<Block> {
	protected WorldProvider provider;
	protected BlockLocation offset;
	protected int width;
	protected int height;
	protected int depth;

	private int itX = 0;
	private int itY = 0;
	private int itZ = 0;

	public Block getNearest(MatrixCamera cam) {
		float notEmptyBlockDistance = Float.MAX_VALUE;
		Block nearestBlock = null;
		while (hasNext()) {
			Block b = next();
			try {
				if (!b.isEmpty()) {
					float d = b.getDistance(cam);
					//System.out.print("d: " + d + " ");
					if (d <= notEmptyBlockDistance) {
						notEmptyBlockDistance = d;
						nearestBlock = b;
					}
				}
			} catch (ChunkUnavailableException e) {
				//e.printStackTrace();
				b.request();
			}
		}
		return nearestBlock;
	}

	@Override
	public boolean hasNext() {
		itZ++;
		if (itZ > depth) {
			itY++;
			itZ = 0;
			if (itY > height) {
				itX++;
				itY = 0;
				if (itX > width) {
					itX = 0;
					itY = 0;
					itZ = 0;
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public Block next() {
		return getBlock(offset.x + itX, offset.y + itY, offset.z + itZ);
	}

	private Block getBlock(int x, int y, int z) {
		return new Block(provider, x, y, z);
	}

	@Override
	public void remove() {
		// do nothing >_> why we have to remove block???
	}

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

	public void setCenter(float x, float y, float z) {
		setCenter((int) x, (int) y, (int) z);
	}
}
