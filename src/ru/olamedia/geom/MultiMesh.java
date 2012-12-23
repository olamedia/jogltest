package ru.olamedia.geom;

import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL3;
import javax.media.opengl.GLContext;

public class MultiMesh {
	private IntBuffer vboIndices;
	private IntBuffer counts;
	private int mode = GL2.GL_TRIANGLES;

	public void clear() {
		vboIndices.clear();
	}

	public void add(ImmModeMesh mesh) {
		vboIndices.put(mesh.getVBOName());
	}

	public void draw() {
		final GL3 gl = GLContext.getCurrentGL().getGL3();
	}

	public MultiMesh(int meshCount) {
		vboIndices = IntBuffer.allocate(meshCount);
	}

	public static MultiMesh allocate(int meshCount) {
		return new MultiMesh(meshCount);
	}
}
