package ru.olamedia.math;

public class FrustumUtil {
	public static Plane[] extractPlanes(javax.vecmath.Matrix4f m, boolean normalize) {

		Plane[] planes = new Plane[6];
		for (int j = 0; j < 6; j++) {
			planes[j] = new Plane();
		}
		// Left: [30+00, 31+01, 32+02, 33+03]

		planes[0].n.x = m.m30 + m.m00;
		planes[0].n.y = m.m31 + m.m01;
		planes[0].n.z = m.m32 + m.m02;
		planes[0].d = m.m33 + m.m03;

		// Right: [30-00, 31-01, 32-02, 33-03]

		planes[1].n.x = m.m30 - m.m00;
		planes[1].n.y = m.m31 - m.m01;
		planes[1].n.z = m.m32 - m.m02;
		planes[1].d = m.m33 - m.m03;

		// Bottom: [30+10, 31+11, 32+12, 33+13]

		planes[2].n.x = m.m30 + m.m10;
		planes[2].n.y = m.m31 + m.m11;
		planes[2].n.z = m.m32 + m.m12;
		planes[2].d = m.m33 + m.m13;

		// Top: [30-10, 31-11, 32-12, 33-13]

		planes[3].n.x = m.m30 - m.m10;
		planes[3].n.y = m.m31 - m.m11;
		planes[3].n.z = m.m32 - m.m12;
		planes[3].d = m.m33 - m.m13;

		// Near: [30+20, 31+21, 32+22, 33+23]

		planes[4].n.x = m.m30 + m.m20;
		planes[4].n.y = m.m31 + m.m21;
		planes[4].n.z = m.m32 + m.m22;
		planes[4].d = m.m33 + m.m23;

		// Far: [30-20, 31-21, 32-22, 33-23]

		planes[5].n.x = m.m30 - m.m20;
		planes[5].n.y = m.m31 - m.m21;
		planes[5].n.z = m.m32 - m.m22;
		planes[5].d = m.m33 - m.m23;

		// Normalize
		if (normalize) {
			for (int i = 0; i < 6; ++i) {
				planes[i].normalize();
			}
		}
		return planes;
	}

	public static Frustum extractFrustum(javax.vecmath.Matrix4f m) {
		Frustum f = new Frustum();
		Plane[] planes = extractPlanes(m, true);
		f.leftPlane.set(planes[0]);
		f.rightPlane.set(planes[1]);
		f.bottomPlane.set(planes[2]);
		f.topPlane.set(planes[3]);
		f.nearPlane.set(planes[4]);
		f.farPlane.set(planes[5]);
		return f;
	}
}
