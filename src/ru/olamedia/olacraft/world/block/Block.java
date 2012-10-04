package ru.olamedia.olacraft.world.block;

import javax.vecmath.Vector3f;

import ru.olamedia.camera.MatrixCamera;
import ru.olamedia.olacraft.world.blockTypes.BlockType;
import ru.olamedia.olacraft.world.blockTypes.EmptyBlockType;
import ru.olamedia.olacraft.world.chunk.ChunkUnavailableException;
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

	public boolean isEmpty() throws ChunkUnavailableException {
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

	public float getDistance(MatrixCamera cam) {
		String nearest = "";
		float topDistance = cam.intersectsRectangle(getTopLeftBack(), getTopLeftFront(), getTopRightFront(),
				getTopRightBack());
		float bottomDistance = cam.intersectsRectangle(getBottomLeftBack(), getBottomLeftFront(),
				getBottomRightFront(), getBottomRightBack());
		float leftDistance = cam.intersectsRectangle(getTopLeftBack(), getTopLeftFront(), getBottomLeftFront(),
				getBottomLeftBack());
		float rightDistance = cam.intersectsRectangle(getTopRightBack(), getTopRightFront(), getBottomRightFront(),
				getBottomRightBack());
		float frontDistance = cam.intersectsRectangle(getTopLeftFront(), getTopRightFront(), getBottomRightFront(),
				getBottomLeftFront());
		float backDistance = cam.intersectsRectangle(getTopLeftBack(), getTopRightBack(), getBottomRightBack(),
				getBottomLeftBack());
		float topBottom = Math.min(topDistance, bottomDistance);
		float leftRight = Math.min(leftDistance, rightDistance);
		float frontBack = Math.min(frontDistance, backDistance);
		float distance = Math.min(Math.min(topBottom, leftRight), frontBack);
		if (distance == bottomDistance) {
			nearest = "BOTTOM";
		}
		if (distance == topDistance) {
			nearest = "TOP";
		}
		if (distance == leftDistance) {
			nearest = "LEFT";
		}
		if (distance == rightDistance) {
			nearest = "RIGHT";
		}
		if (distance == frontDistance) {
			nearest = "FRONT";
		}
		if (distance == backDistance) {
			nearest = "BACK";
		}
		return distance;
	}

	private Vector3f getBottomRightBack() {
		return new Vector3f(x + 0.5f, y - 0.5f, z - 0.5f);
	}

	private Vector3f getBottomRightFront() {
		return new Vector3f(x + 0.5f, y - 0.5f, z + 0.5f);
	}

	private Vector3f getBottomLeftFront() {
		return new Vector3f(x - 0.5f, y - 0.5f, z + 0.5f);
	}

	private Vector3f getBottomLeftBack() {
		return new Vector3f(x - 0.5f, y - 0.5f, z - 0.5f);
	}

	private Vector3f getTopRightBack() {
		return new Vector3f(x + 0.5f, y + 0.5f, z - 0.5f);
	}

	private Vector3f getTopLeftFront() {
		return new Vector3f(x - 0.5f, y + 0.5f, z + 0.5f);
	}

	private Vector3f getTopRightFront() {
		return new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
	}

	private Vector3f getTopLeftBack() {
		return new Vector3f(x - 0.5f, y + 0.5f, z - 0.5f);
	}
}
