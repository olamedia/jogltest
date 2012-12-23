package ru.olamedia.geom;

import java.nio.FloatBuffer;

import org.openmali.vecmath2.Matrix4f;

import com.jogamp.opengl.util.PMVMatrix;

//import org.openmali.spatial.bodies.Frustum;

public class Frustum extends org.openmali.spatial.bodies.Frustum {
	protected PMVMatrix pmvMatrix;
	protected int mvOffset;

	public Frustum() {
		super();
	}

	public Frustum(PMVMatrix matrix) {
		super();
		this.pmvMatrix = matrix;
	}

	public float[] getMatrixFloat(FloatBuffer b) {
		if (pmvMatrix.usesBackingArray()) {
			return b.array();
		} else {
			int p = b.position();
			float[] pm = new float[16];
			b.get(pm, p, 16);
			b.position(p);
			return pm;
		}
	}

	public void compute() {
		Matrix4f proj = new Matrix4f(getMatrixFloat(pmvMatrix.glGetPMatrixf()));
		// proj.transpose();
		Matrix4f modl = new Matrix4f(getMatrixFloat(pmvMatrix.glGetMviMatrixf()));
		// modl.transpose();
		compute(proj, modl);
	}
}
