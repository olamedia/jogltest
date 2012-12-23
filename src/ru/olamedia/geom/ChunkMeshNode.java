package ru.olamedia.geom;

public class ChunkMeshNode {
	private ChunkMesh mesh = new ChunkMesh();
	private ChunkMesh[] leafs = new ChunkMesh[8];
	private int vertexCount = 0;
	private int opaqueVertexCount = 0;
	private int transparentVertexCount = 0;
	private int level = 1;
	private int axisChunks = 2;
	private int axisShift = 1;
	private int volumeChunks = 8;

	public ChunkMeshNode(int level) {
		this.level = level;
		this.axisChunks = (int) Math.pow(2, level);
		this.axisShift = level;
		this.volumeChunks = (int) Math.pow(8, level);
	}

	public void putChunkMesh(int chunkX, int chunkY, int chunkZ, ChunkMesh mesh) {
		// id = 0..7
		// final int id = (chunkX & )*4 + ()*2+(chunkZ & );
	}

	private void updateVertexCount() {
		if (!mesh.isValid()) {
			vertexCount = 0;
			opaqueVertexCount = 0;
			transparentVertexCount = 0;
			for (ChunkMesh m : leafs) {
				if (null != m) {
					vertexCount += m.getVertexCount();
					opaqueVertexCount += m.getOpaqueVertexCount();
					transparentVertexCount += m.getTransparentVertexCount();
				}
			}
		}
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void render(int pass) {
		if (mesh.isValid()) {
			mesh.render(pass);
		} else {
			for (ChunkMesh m : leafs) {
				if (null != m) {
					m.render(pass);
				}
			}
		}
	}

	public void combine() {
		updateVertexCount();
		if (!mesh.isValid()) {
			if (transparentVertexCount > 0) {
				final ImmModeMesh tMesh = ImmModeMesh.allocate(transparentVertexCount);
				tMesh.setGLSL(true);
				tMesh.enableColor4();
				tMesh.enableTexCoord2();
				tMesh.enableVertex3();
				tMesh.beginQuads();
				for (ChunkMesh m : leafs) {
					if (null != m) {
						tMesh.put(m.getTransparentMesh());
					}
				}
				tMesh.end();
				mesh.setMesh(tMesh);
			} else {
				mesh.setMesh(null);
			}
			if (opaqueVertexCount > 0) {
				final ImmModeMesh oMesh = ImmModeMesh.allocate(opaqueVertexCount);
				oMesh.setGLSL(true);
				oMesh.enableColor4();
				oMesh.enableTexCoord2();
				oMesh.enableVertex3();
				oMesh.beginQuads();
				for (ChunkMesh m : leafs) {
					if (null != m) {
						oMesh.put(m.getOpaqueMesh());
					}
				}
				oMesh.end();
				mesh.setOpaqueMesh(oMesh);
			} else {
				mesh.setOpaqueMesh(null);
			}
			mesh.setValid(true);
		}
	}
}
