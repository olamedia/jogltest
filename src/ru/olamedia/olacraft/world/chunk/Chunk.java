package ru.olamedia.olacraft.world.chunk;

import ru.olamedia.geom.ChunkMesh;
import ru.olamedia.geom.SimpleQuadMesh;
import ru.olamedia.olacraft.world.blockRenderer.ChunkRenderer;
import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class Chunk extends BlockSlice {
	public boolean isMeshCostructed = false;
	public boolean usePrevMesh = false;

	public int visibleTop = 0;
	public int visibleBottom = 0;
	public int visibleLeft = 0;
	public int visibleRight = 0;
	public int visibleFront = 0;
	public int visibleBack = 0;
	public ChunkLocation location;

	public ChunkMesh mesh = new ChunkMesh();

	public void render(int pass) {
		mesh.render(pass);
	}

	public void invalidate() {
		mesh.setValid(false);
	}

	public Chunk(WorldProvider provider, ChunkLocation location) {
		super(provider, 16, 16, 16);
		setLocation(location);
	}

	/**
	 * Convert block coordinate into chunk coordinate
	 * 
	 * @param v
	 *            block coordinate along one axis
	 * @return
	 */
	public static int v(int v) {
		if (v >= 0) {
			return v / 16;
		} else {
			return (v + 1) / 16 - 1;
		}
	}

	/**
	 * Convert chunk coordinate into block coordinate (back left bottom)
	 * 
	 * @param v
	 *            block coordinate along one axis
	 * @return
	 */
	public static int rev(int v) {
		return v * 16; // -32..-17 -16..-1 0..15 16..31 32..
		/*
		 * if (v >= 0) {
		 * return v * 16;
		 * } else {
		 * return (v + 1) * 16 - 1;
		 * }
		 */
	}

	/**
	 * Convert block coordinate into block position inside of chunk
	 * 
	 * @param v
	 *            block coordinate along one axis
	 * @return
	 */
	public static int in(int v) {
		int tmp = v - v(v) * 16;
		return tmp >= 0 ? tmp : 16 + tmp; // block location minus chunk base
											// location
											// (lower-left-back corner)
		// if (v >= 0) {
		// return v % 16;
		// } else {
		// int in = v + 1; // shift up so -1 will be 0
		// in %= 16; // get remaining -15..0
		// in += 15; // revert 0..15
		// return in;
		// }
	}

	public void setMeshBrightness(ChunkData origin, ChunkData data, short id, boolean side) {
		// data.calculateVoidLight(provider);
		// float[] sunlight = Game.client.getScene().time.getClearColor();
		// byte lv = data.getVoidLight(id);
		// light = lightValues[lv];
		// if (side && lv > 0) {
		// light = lightValues[lv - 1];
		// }
		// lightR = light;// * sunlight[0];
		// lightG = light;// * sunlight[1];
		// lightB = light;// * sunlight[2];
	}

	public void setMeshBrightness(SimpleQuadMesh mesh, int x, int y, int z, ChunkData data, boolean side) {
		if (x < 0) {
			setMeshBrightness(data, provider.getChunk(data.location.getLeft()), ChunkData.ClampID(x, y, z), side);
		} else if (x > 15) {
			setMeshBrightness(data, provider.getChunk(data.location.getRight()), ChunkData.ClampID(x, y, z), side);
		} else if (y < 0) {
			setMeshBrightness(data, provider.getChunk(data.location.getBottom()), ChunkData.ClampID(x, y, z), side);
		} else if (y > 15) {
			setMeshBrightness(data, provider.getChunk(data.location.getTop()), ChunkData.ClampID(x, y, z), side);
		} else if (z < 0) {
			setMeshBrightness(data, provider.getChunk(data.location.getBack()), ChunkData.ClampID(x, y, z), side);
		} else if (z > 15) {
			setMeshBrightness(data, provider.getChunk(data.location.getFront()), ChunkData.ClampID(x, y, z), side);
		} else {
			setMeshBrightness(data, data, ChunkData.ID(x, y, z), side);
		}
	}

	private float light = 0f;
	private float lightR = 0f;
	private float lightG = 0f;
	private float lightB = 0f;
	private static float[] lightValues = new float[] {//
	0.035f, // 0.035184372f, 0
			0.043980465f, // 1
			0.054975581f, // 2
			0.068719477f, // 3
			0.085899346f, // 4
			0.107374182f, // 5
			0.134217728f, // 6
			0.16777216f, // 7
			0.2097152f, // 8
			0.262144f, // 9
			0.32768f, // 10
			0.4096f, // 11
			0.512f, // 12
			0.64f, // 13
			0.8f, // 14
			1f // 15
	};

	public void setMeshColor(SimpleQuadMesh mesh, int x, int y, int z, boolean isSide) {
		mesh.setColor4f(lightR, lightG, lightB, 1);
		if (y < 0) {
			// mesh.setColor4f(0, 0, 1, 1);
		} else if (y > 30) {
			// mesh.setColor4f(1, 1, 1, 1);
		} else {
			// mesh.setColor4f(1, 1, 0, 1);
		}
	}

	/**
	 * @return the mesh
	 */
	public void getMesh() {
		if (!mesh.isCompiled()) {
			ChunkRenderer.compile(this);
		}
	}

	public boolean isEmpty() {
		// MUST BE LOADED
		return provider.getChunk(getBlockLocation().getChunkLocation()).isEmpty();
	}

	private BlockLocation getBlockLocation() {
		return offset;
	}

	public boolean isAvailable() {
		return provider.isChunkAvailable(getBlockLocation().getChunkLocation());
	}

	public boolean isNeighborsAvailable() {
		int x = offset.getChunkLocation().x;
		int y = offset.getChunkLocation().y;
		int z = offset.getChunkLocation().z;
		return provider.isChunkAvailable(new ChunkLocation(x - 1, y, z))
				&& provider.isChunkAvailable(new ChunkLocation(x + 1, y, z))
				&& provider.isChunkAvailable(new ChunkLocation(x, y - 1, z))
				&& provider.isChunkAvailable(new ChunkLocation(x, y + 1, z))
				&& provider.isChunkAvailable(new ChunkLocation(x, y, z - 1))
				&& provider.isChunkAvailable(new ChunkLocation(x, y, z + 1));
	}

	public void requestNeighbors() {
		int x = offset.getChunkLocation().x;
		int y = offset.getChunkLocation().y;
		int z = offset.getChunkLocation().z;
		if (!provider.isChunkAvailable(new ChunkLocation(x - 1, y, z))) {
			provider.loadChunk(new ChunkLocation(x - 1, y, z));
		}
		if (!provider.isChunkAvailable(new ChunkLocation(x + 1, y, z))) {
			provider.loadChunk(new ChunkLocation(x + 1, y, z));
		}
		if (!provider.isChunkAvailable(new ChunkLocation(x, y - 1, z))) {
			provider.loadChunk(new ChunkLocation(x, y - 1, z));
		}
		if (!provider.isChunkAvailable(new ChunkLocation(x, y + 1, z))) {
			provider.loadChunk(new ChunkLocation(x, y + 1, z));
		}
		if (!provider.isChunkAvailable(new ChunkLocation(x, y, z - 1))) {
			provider.loadChunk(new ChunkLocation(x, y, z - 1));
		}
		if (!provider.isChunkAvailable(new ChunkLocation(x, y, z + 1))) {
			provider.loadChunk(new ChunkLocation(x, y, z + 1));
		}
	}

	public void request() {
		provider.loadChunk(offset.getChunkLocation());
	}

	// public BlockType getBlockType(int x, int y, int z) {
	// return provider.getBlockType(int x, int y, int z);
	// }

	public boolean isEmptyBlock(int x, int y, int z) throws ChunkUnavailableException {
		return provider.isEmptyBlock(x, y, z);
	}

	public float getLightLevel256(int x, int y, int z) {
		return ((float) getLightLevel(x, y, z)) / 15.0f;// * 255.0f
	}

	public int getLightLevel(int x, int y, int z) {
		return 15;
	}

	public boolean renderBottom(int x, int y, int z) throws ChunkUnavailableException {
		return provider.renderBottom(x, y, z);
	}

	public boolean renderTop(int x, int y, int z) throws ChunkUnavailableException {
		return provider.renderTop(x, y, z);
	}

	public boolean renderLeft(int x, int y, int z) throws ChunkUnavailableException {
		return provider.renderLeft(x, y, z);
	}

	public boolean renderRight(int x, int y, int z) throws ChunkUnavailableException {
		return provider.renderRight(x, y, z);
	}

	public boolean renderFront(int x, int y, int z) throws ChunkUnavailableException {
		return provider.renderFront(x, y, z);
	}

	public boolean renderBack(int x, int y, int z) throws ChunkUnavailableException {
		return provider.renderBack(x, y, z);
	}

	public WorldProvider getProvider() {
		return provider;
	}

	public void setLocation(ChunkLocation location) {
		setLocation(location.getBlockLocation().x, location.getBlockLocation().y, location.getBlockLocation().z);
		this.location = new ChunkLocation(location);
	}

	public boolean inWorldRange() {
		return (offset.y + 16 < provider.getInfo().maxHeight) && (offset.y > provider.getInfo().minHeight);
	}

	public boolean meshInvalid = false;

	public void markMeshInvalid() {
		if (isMeshCostructed) {
			meshInvalid = true;
		}
	}

	public ChunkData getData() {
		return provider.getChunk(location);
	}
}
