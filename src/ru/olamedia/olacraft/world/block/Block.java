package ru.olamedia.olacraft.world.block;

import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;
import javax.vecmath.Vector3f;

import ru.olamedia.camera.MatrixCamera;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.world.blockTypes.BlockType;
import ru.olamedia.olacraft.world.blockTypes.EmptyBlockType;
import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.chunk.ChunkUnavailableException;
import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class Block {
	public WorldProvider provider;
	public BlockLocation location = new BlockLocation();

	public void removeFromWorld() {
		ChunkData cdata = provider.getChunk(location.getChunkLocation());
		cdata.setEmpty(location, true);
		cdata.voidLightPrecomputed = false;
		invalidateChunks();
	}

	public void insertToWorld() {
		ChunkData cdata = provider.getChunk(location.getChunkLocation());
		cdata.setEmpty(location, false);
		cdata.types[ChunkData.ClampID(location.x, location.y, location.z)] = (byte) type.getId(provider);
		cdata.voidLightPrecomputed = false;
		invalidateChunks();
	}

	/**
	 * Inventory block
	 */
	public Block() {
		this.provider = null;
		location.x = 0;
		location.y = 0;
		location.z = 0;
	}

	public void setLocation(WorldProvider worldProvider, int x, int y, int z) {
		this.provider = worldProvider;
		location.x = x;
		location.y = y;
		location.z = z;
	}

	public Block(WorldProvider worldProvider, int x, int y, int z) {
		setLocation(worldProvider, x, y, z);
	}

	public Block(WorldProvider worldProvider, BlockLocation location) {
		setLocation(worldProvider, location.x, location.y, location.z);
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return location.x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x) {
		location.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return location.y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y) {
		location.y = y;
	}

	/**
	 * @return the z
	 */
	public int getZ() {
		return location.z;
	}

	/**
	 * @param z
	 *            the z to set
	 */
	public void setZ(int z) {
		location.z = z;
	}

	public boolean isEmpty() throws ChunkUnavailableException {
		return provider.isEmptyBlock(location.x, location.y, location.z);
	}

	public Block getNeighbor(int dx, int dy, int dz) {
		return new Block(provider, location.x + dx, location.y + dy, location.z + dz);
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

	public int nearestX = 0;
	public int nearestY = 0;
	public int nearestZ = 0;

	public float getDistance(MatrixCamera cam) {
		nearestX = 0;
		nearestY = 0;
		nearestZ = 0;
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
			nearestY = -1;
		} else if (distance == topDistance) {
			nearestY = 1;
		} else if (distance == leftDistance) {
			nearestX = -1;
		} else if (distance == rightDistance) {
			nearestX = 1;
		} else if (distance == frontDistance) {
			nearestZ = 1;
		} else if (distance == backDistance) {
			nearestZ = -1;
		}
		return distance;
	}

	private Vector3f getBottomRightBack() {
		return new Vector3f(location.x + 0.5f, location.y - 0.5f, location.z - 0.5f);
	}

	private Vector3f getBottomRightFront() {
		return new Vector3f(location.x + 0.5f, location.y - 0.5f, location.z + 0.5f);
	}

	private Vector3f getBottomLeftFront() {
		return new Vector3f(location.x - 0.5f, location.y - 0.5f, location.z + 0.5f);
	}

	private Vector3f getBottomLeftBack() {
		return new Vector3f(location.x - 0.5f, location.y - 0.5f, location.z - 0.5f);
	}

	private Vector3f getTopRightBack() {
		return new Vector3f(location.x + 0.5f, location.y + 0.5f, location.z - 0.5f);
	}

	private Vector3f getTopLeftFront() {
		return new Vector3f(location.x - 0.5f, location.y + 0.5f, location.z + 0.5f);
	}

	private Vector3f getTopRightFront() {
		return new Vector3f(location.x + 0.5f, location.y + 0.5f, location.z + 0.5f);
	}

	private Vector3f getTopLeftBack() {
		return new Vector3f(location.x - 0.5f, location.y + 0.5f, location.z - 0.5f);
	}

	public void request() {
		provider.loadChunk(location.getChunkLocation());
	}

	public void renderFrame() {
		GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
		// gl.glEnable(GL2.GL_BLEND); // Enable Blending
		// gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		float min = 0.505f;
		float max = 0.495f;
		gl.glBegin(GL2.GL_QUADS);
		{
			gl.glColor4f(0, 0, 0, 0.8f);
			if (nearestY == 1) {
				// top: right
				gl.glVertex3f(location.x + min, location.y + min, location.z + min);
				gl.glVertex3f(location.x + min, location.y + min, location.z - min);
				gl.glVertex3f(location.x + max, location.y + min, location.z - min);
				gl.glVertex3f(location.x + max, location.y + min, location.z + min);
				// top: left
				gl.glVertex3f(location.x - min, location.y + min, location.z + min);
				gl.glVertex3f(location.x - min, location.y + min, location.z - min);
				gl.glVertex3f(location.x - max, location.y + min, location.z - min);
				gl.glVertex3f(location.x - max, location.y + min, location.z + min);
				// top: back
				gl.glVertex3f(location.x + min, location.y + min, location.z - min);
				gl.glVertex3f(location.x - min, location.y + min, location.z - min);
				gl.glVertex3f(location.x - min, location.y + min, location.z - max);
				gl.glVertex3f(location.x + min, location.y + min, location.z - max);
				// top: front
				gl.glVertex3f(location.x + min, location.y + min, location.z + min);
				gl.glVertex3f(location.x - min, location.y + min, location.z + min);
				gl.glVertex3f(location.x - min, location.y + min, location.z + max);
				gl.glVertex3f(location.x + min, location.y + min, location.z + max);
			}
			if (nearestY == -1) {
				// bottom: right
				gl.glVertex3f(location.x + min, location.y - min, location.z + min);
				gl.glVertex3f(location.x + min, location.y - min, location.z - min);
				gl.glVertex3f(location.x + max, location.y - min, location.z - min);
				gl.glVertex3f(location.x + max, location.y - min, location.z + min);
				// bottom: left
				gl.glVertex3f(location.x - min, location.y - min, location.z + min);
				gl.glVertex3f(location.x - min, location.y - min, location.z - min);
				gl.glVertex3f(location.x - max, location.y - min, location.z - min);
				gl.glVertex3f(location.x - max, location.y - min, location.z + min);
				// bottom: back
				gl.glVertex3f(location.x + min, location.y - min, location.z - min);
				gl.glVertex3f(location.x - min, location.y - min, location.z - min);
				gl.glVertex3f(location.x - min, location.y - min, location.z - max);
				gl.glVertex3f(location.x + min, location.y - min, location.z - max);
				// bottom: front
				gl.glVertex3f(location.x + min, location.y - min, location.z + min);
				gl.glVertex3f(location.x - min, location.y - min, location.z + min);
				gl.glVertex3f(location.x - min, location.y - min, location.z + max);
				gl.glVertex3f(location.x + min, location.y - min, location.z + max);
			}
			if (nearestZ == 1) {
				// front: right
				gl.glVertex3f(location.x + min, location.y + min, location.z + min);
				gl.glVertex3f(location.x + min, location.y - min, location.z + min);
				gl.glVertex3f(location.x + max, location.y - min, location.z + min);
				gl.glVertex3f(location.x + max, location.y + min, location.z + min);
				// front: left
				gl.glVertex3f(location.x - min, location.y + min, location.z + min);
				gl.glVertex3f(location.x - min, location.y - min, location.z + min);
				gl.glVertex3f(location.x - max, location.y - min, location.z + min);
				gl.glVertex3f(location.x - max, location.y + min, location.z + min);
				// front: bottom
				gl.glVertex3f(location.x + min, location.y - min, location.z + min);
				gl.glVertex3f(location.x - min, location.y - min, location.z + min);
				gl.glVertex3f(location.x - min, location.y - max, location.z + min);
				gl.glVertex3f(location.x + min, location.y - max, location.z + min);
				// front: top
				gl.glVertex3f(location.x + min, location.y + min, location.z + min);
				gl.glVertex3f(location.x - min, location.y + min, location.z + min);
				gl.glVertex3f(location.x - min, location.y + max, location.z + min);
				gl.glVertex3f(location.x + min, location.y + max, location.z + min);
			}
			if (nearestZ == -1) {
				// back: right
				gl.glVertex3f(location.x + min, location.y + min, location.z - min);
				gl.glVertex3f(location.x + min, location.y - min, location.z - min);
				gl.glVertex3f(location.x + max, location.y - min, location.z - min);
				gl.glVertex3f(location.x + max, location.y + min, location.z - min);
				// back: left
				gl.glVertex3f(location.x - min, location.y + min, location.z - min);
				gl.glVertex3f(location.x - min, location.y - min, location.z - min);
				gl.glVertex3f(location.x - max, location.y - min, location.z - min);
				gl.glVertex3f(location.x - max, location.y + min, location.z - min);
				// back: bottom
				gl.glVertex3f(location.x + min, location.y - min, location.z - min);
				gl.glVertex3f(location.x - min, location.y - min, location.z - min);
				gl.glVertex3f(location.x - min, location.y - max, location.z - min);
				gl.glVertex3f(location.x + min, location.y - max, location.z - min);
				// back: top
				gl.glVertex3f(location.x + min, location.y + min, location.z - min);
				gl.glVertex3f(location.x - min, location.y + min, location.z - min);
				gl.glVertex3f(location.x - min, location.y + max, location.z - min);
				gl.glVertex3f(location.x + min, location.y + max, location.z - min);
			}
			if (nearestX == -1) {
				// left: back
				gl.glVertex3f(location.x - min, location.y + min, location.z - min);
				gl.glVertex3f(location.x - min, location.y - min, location.z - min);
				gl.glVertex3f(location.x - min, location.y - min, location.z - max);
				gl.glVertex3f(location.x - min, location.y + min, location.z - max);
				// left: front
				gl.glVertex3f(location.x - min, location.y + min, location.z + min);
				gl.glVertex3f(location.x - min, location.y - min, location.z + min);
				gl.glVertex3f(location.x - min, location.y - min, location.z + max);
				gl.glVertex3f(location.x - min, location.y + min, location.z + max);
				// left: top
				gl.glVertex3f(location.x - min, location.y + min, location.z + min);
				gl.glVertex3f(location.x - min, location.y + min, location.z - min);
				gl.glVertex3f(location.x - min, location.y + max, location.z - min);
				gl.glVertex3f(location.x - min, location.y + max, location.z + min);
				// left: bottom
				gl.glVertex3f(location.x - min, location.y - min, location.z + min);
				gl.glVertex3f(location.x - min, location.y - min, location.z - min);
				gl.glVertex3f(location.x - min, location.y - max, location.z - min);
				gl.glVertex3f(location.x - min, location.y - max, location.z + min);
			}
			if (nearestX == 1) {
				// right: back
				gl.glVertex3f(location.x + min, location.y + min, location.z - min);
				gl.glVertex3f(location.x + min, location.y - min, location.z - min);
				gl.glVertex3f(location.x + min, location.y - min, location.z - max);
				gl.glVertex3f(location.x + min, location.y + min, location.z - max);
				// right: front
				gl.glVertex3f(location.x + min, location.y + min, location.z + min);
				gl.glVertex3f(location.x + min, location.y - min, location.z + min);
				gl.glVertex3f(location.x + min, location.y - min, location.z + max);
				gl.glVertex3f(location.x + min, location.y + min, location.z + max);
				// right: top
				gl.glVertex3f(location.x + min, location.y + min, location.z + min);
				gl.glVertex3f(location.x + min, location.y + min, location.z - min);
				gl.glVertex3f(location.x + min, location.y + max, location.z - min);
				gl.glVertex3f(location.x + min, location.y + max, location.z + min);
				// right: bottom
				gl.glVertex3f(location.x + min, location.y - min, location.z + min);
				gl.glVertex3f(location.x + min, location.y - min, location.z - min);
				gl.glVertex3f(location.x + min, location.y - max, location.z - min);
				gl.glVertex3f(location.x + min, location.y - max, location.z + min);
			}
		}
		gl.glEnd();
	}

	public void invalidateChunks() {
		// TODO Auto-generated method stub
		Chunk chunk = Game.client.getScene().blockRenderer.chunkSlice.getChunk(location.getChunkLocation());
		chunk.invalidate();
		if (location.isChunkEdge()) {
			if (location.isChunkBackEdge()) {
				Chunk neighbor = Game.client.getScene().blockRenderer.chunkSlice.getChunk(location.getChunkLocation()
						.getBack());
				neighbor.invalidate();
			} else if (location.isChunkFrontEdge()) {
				Chunk neighbor = Game.client.getScene().blockRenderer.chunkSlice.getChunk(location.getChunkLocation()
						.getFront());
				neighbor.invalidate();
			} else if (location.isChunkLeftEdge()) {
				Chunk neighbor = Game.client.getScene().blockRenderer.chunkSlice.getChunk(location.getChunkLocation()
						.getLeft());
				neighbor.invalidate();
			} else if (location.isChunkRightEdge()) {
				Chunk neighbor = Game.client.getScene().blockRenderer.chunkSlice.getChunk(location.getChunkLocation()
						.getRight());
				neighbor.invalidate();
			} else if (location.isChunkTopEdge()) {
				Chunk neighbor = Game.client.getScene().blockRenderer.chunkSlice.getChunk(location.getChunkLocation()
						.getTop());
				neighbor.invalidate();
			} else if (location.isChunkBottomEdge()) {
				Chunk neighbor = Game.client.getScene().blockRenderer.chunkSlice.getChunk(location.getChunkLocation()
						.getBottom());
				neighbor.invalidate();
			}
		}
	}

	public boolean canMoveThrough() throws ChunkUnavailableException {
		return provider.canMoveThrough(location.x, location.y, location.z);
	}
}
