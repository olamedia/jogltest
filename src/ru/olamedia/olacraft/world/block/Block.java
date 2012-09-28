package ru.olamedia.olacraft.world.block;

import ru.olamedia.olacraft.world.blockTypes.BlockType;
import ru.olamedia.olacraft.world.blockTypes.EmptyBlockType;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class Block {
	private WorldProvider provider;
	private int x;
	private int y;
	private int z;

	/**
	 * Inventory block
	 */
	public Block() {
		this.provider = null;
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public void putIntoWorld(WorldProvider worldProvider, int x, int y, int z) {
		this.provider = worldProvider;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Block(WorldProvider worldProvider, int x, int y, int z) {
		putIntoWorld(worldProvider, x, y, z);
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the z
	 */
	public int getZ() {
		return z;
	}

	/**
	 * @param z
	 *            the z to set
	 */
	public void setZ(int z) {
		this.z = z;
	}

	public boolean isEmpty() {
		return provider.isEmptyBlock(x, y, z);
	}

	public Block getNeighbor(int dx, int dy, int dz) {
		return new Block(provider, x + dx, y + dy, z + dz);
	}

	public Block[] getNeighbors() {
		return new Block[] {
				//
				getNeighbor(1, 0, 0),//
				getNeighbor(0, 1, 0),//
				getNeighbor(0, 0, 1),//
				getNeighbor(-1, 0, 0),//
				getNeighbor(0, -1, 0),//
				getNeighbor(0, 0, -1),//
		};
	}

	private BlockType type;

	public void setType(BlockType type) {
		this.type = type;
	}

	public BlockType getType() {
		if (null == type) {
			type = new EmptyBlockType();
		}
		return type;
	}
}
