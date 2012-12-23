package ru.olamedia.geom;

import ru.olamedia.olacraft.render.jogl.ChunkRangeRenderer;

public class ChunkMesh {
	private ImmModeMesh opaqueMesh;
	private ImmModeMesh mesh;
	private boolean isCompiled = false;
	private boolean isValid = false;
	private int vertexCount = 0;

	private void updateVertexCount() {
		vertexCount = 0;
		if (null != mesh) {
			vertexCount += mesh.getVertexCount();
		}
		if (null != opaqueMesh) {
			vertexCount += opaqueMesh.getVertexCount();
		}
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void setMesh(ImmModeMesh m) {
		mesh = m;
		updateVertexCount();
	}

	public void setOpaqueMesh(ImmModeMesh m) {
		opaqueMesh = m;
		updateVertexCount();
	}

	public void render(int pass) {
		if (pass == ChunkRangeRenderer.OPAQUE_PASS) {
			if (null != opaqueMesh) {
				opaqueMesh.draw();
			}
		} else {
			if (null != mesh) {
				mesh.draw();
			}
		}
	}

	public boolean isCompiled() {
		return isCompiled;
	}

	public void setCompiled(boolean isCompiled) {
		this.isCompiled = isCompiled;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public boolean isEmpty() {
		return isValid() && (null == mesh) && (null == opaqueMesh);
	}

	public ImmModeMesh getTransparentMesh() {
		return mesh;
	}

	public ImmModeMesh getOpaqueMesh() {
		return opaqueMesh;
	}

	public int getOpaqueVertexCount() {
		return null == opaqueMesh ? 0 : opaqueMesh.getVertexCount();
	}

	public int getTransparentVertexCount() {
		return null == mesh ? 0 : mesh.getVertexCount();
	}
}
