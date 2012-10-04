package ru.olamedia.olacraft.world.chunk;

import ru.olamedia.geom.SimpleQuadMesh;
import ru.olamedia.olacraft.world.blockTypes.GrassBlockType;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class Chunk extends BlockSlice {
	public boolean isMeshCostructed = false;
	public SimpleQuadMesh mesh;

	public int visibleTop = 0;
	public int visibleBottom = 0;
	public int visibleLeft = 0;
	public int visibleRight = 0;
	public int visibleFront = 0;
	public int visibleBack = 0;

	public Chunk(WorldProvider provider) {
		super(provider, 16, 16, 16);
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

	public void setMeshColor(SimpleQuadMesh mesh, int x, int y, int z, boolean isSide) {
		float level = 1f;// ((float) getProvider().getBlockLightLevel(x, y, z) -
							// (isSide ? 2 : 0)) / 15.0f;
		mesh.setColor4f(level, level, level, 1);
		if (y < 0) {
			mesh.setColor4f(0, 0, 1, 1);
		} else if (y > 30) {
			mesh.setColor4f(1, 1, 1, 1);
		} else {
			mesh.setColor4f(1, 1, 0, 1);
		}
	}

	/**
	 * @return the mesh
	 */
	public SimpleQuadMesh getMesh() {
		if (isMeshCostructed) {
			return mesh;
		}
		if (offset.y > provider.getInfo().maxHeight) {
			// isMeshCostructed = true;
			// return null;
		}
		if (offset.y < provider.getInfo().minHeight) {
			// isMeshCostructed = true;
			// return null;
		}
		if (null == mesh) {
			mesh = new SimpleQuadMesh(14739); // unindexed
			// 17x17x17
			// vertices
			mesh.useColor();
			mesh.useTexture();
			// gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_FASTEST);
			// gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
			GrassBlockType grass = new GrassBlockType();
			for (int x = offset.x; x < offset.x + getWidth(); x++) {
				for (int y = offset.y; y < offset.y + getHeight(); y++) {
					for (int z = offset.z; z < offset.z + getDepth(); z++) {
						//
						try {
							if (!isEmptyBlock(x, y, z)) {
								mesh.setTranslation(x, y, z);
								// mesh.setColor4f(0, 1, 0, 1);
								float cbase = (float) (y / 200.0) * (float) (7.0 / 10.0);
								if (cbase > 9 / 10) {
									cbase = (float) (9.0 / 10.0);
								}
								// cbase = (float) (9.0 / 10.0);
								float cred, cgreen, cblue;
								// cbase;
								cred = cgreen = cblue = getLightLevel256(x, y, z);
								if (x == 1) {
									mesh.setColor4f(1, 0, 0, 1);
									// red to the right
								}
								if (x == 0 || z == 0) {
									if (y == 6) {
										mesh.setColor4f(1, 0, 0, 1);
									} else if (y % 2 == 0) {
										mesh.setColor4f(1, 0, 1, 1);
									} else {
										mesh.setColor4f(1, 1, 0, 1);
									}
								}
								if (z == 1) {
									mesh.setColor4f(0, 0, 1, 1);
									// blue to the bottom
								}
								if (renderBottom(x, y, z)) {
									setMeshColor(mesh, x, y - 1, z, false);
									mesh.setTexture(grass.getBottomTexture());
									mesh.addBottomQuad();
									visibleBottom++;
								}
								if (renderTop(x, y, z)) {
									if (x == 15 || z == 15) {
										// debug: show through..
									} else {
										setMeshColor(mesh, x, y + 1, z, false);
										mesh.setTexture(grass.getTopTexture());
										mesh.addTopQuad();
									}
									visibleTop++;
								}
								if (renderLeft(x, y, z)) {
									setMeshColor(mesh, x - 1, y, z, true);
									mesh.setTexture(grass.getLeftTexture());
									mesh.addLeftQuad();
									visibleLeft++;
								}
								if (renderRight(x, y, z)) {
									setMeshColor(mesh, x + 1, y, z, true);
									mesh.setTexture(grass.getRightTexture());
									mesh.addRightQuad();
									visibleRight++;
								}
								if (renderBack(x, y, z)) {
									setMeshColor(mesh, x, y, z - 1, true);
									mesh.setTexture(grass.getBackTexture());
									mesh.addBackQuad();
									visibleBack++;
								}
								if (renderFront(x, y, z)) {
									setMeshColor(mesh, x, y, z + 1, true);
									mesh.setTexture(grass.getFrontTexture());
									mesh.addFrontQuad();
									visibleFront++;
								}
								// System.out.println("mesh not empty");
							} else {
								// System.out.println("mesh empty");
							}
						} catch (ChunkUnavailableException e) {
							e.printStackTrace();
						}
					}
				}
			}
			mesh.endMesh();
			isMeshCostructed = true;
			return null;
		}
		return mesh;
	}

	/**
	 * @param mesh
	 *            the mesh to set
	 */
	public void setMesh(SimpleQuadMesh mesh) {
		this.mesh = mesh;
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
	}
}
