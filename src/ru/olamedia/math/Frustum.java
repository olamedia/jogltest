package ru.olamedia.math;

public class Frustum {
	public Plane topPlane = new Plane();
	public Plane bottomPlane = new Plane();
	public Plane leftPlane = new Plane();
	public Plane rightPlane = new Plane();
	public Plane nearPlane = new Plane();
	public Plane farPlane = new Plane();
	private Plane[] planes = getPlanes();

	private Plane[] getPlanes() {
		return new Plane[] { leftPlane, rightPlane, topPlane, bottomPlane };// ,
																			// topPlane,,
																			// topPlane
		// bottomPlane,
		// leftPlane,
		// rightPlane, farPlane };
	}

	public Vector3f[] getVertices() {
		Vector3f[] v = new Vector3f[8];
		v[0] = topPlane.cross(leftPlane, nearPlane);
		v[1] = topPlane.cross(leftPlane, farPlane);
		v[2] = topPlane.cross(rightPlane, nearPlane);
		v[3] = topPlane.cross(rightPlane, farPlane);
		v[4] = bottomPlane.cross(leftPlane, nearPlane);
		v[5] = bottomPlane.cross(leftPlane, farPlane);
		v[6] = bottomPlane.cross(rightPlane, nearPlane);
		v[7] = bottomPlane.cross(rightPlane, farPlane);
		return v;
	}

	public int test(Box b) {
		return quickClassify(b);
		// int out, in = 0, result;
		// result = Classifier.INSIDE;
		// int pc = 5;
		// for (int i = 0; i < 5; i++) {
		// Plane plane = planes[i];
		// out = 0;
		// in = 0;
		// for (int k = 0; k < 8 && (in == 0 || out == 0); k++) {
		// if (plane.distance(b.getVertex(k)) > 0) {
		// out++;
		// } else {
		// in++;
		// }
		// }
		// if (out == 8) {
		// System.out.println(i);
		// return Classifier.OUTSIDE;
		// }
		// }
		// if (in < pc) {
		// result = Classifier.INTERSECT;
		// }
		// // for (int i = 0; i < 6; i++) {
		// // for (int k = 0; k < 8 && (in == 0 || out == 0); k++) {
		// // if (planes[i].distance(b.getVertex(k)) < 0) {
		// // out++;
		// // } else {
		// // in++;
		// // }
		// // }
		// // }
		// // if (in < 1) {
		// // return Classifier.OUTSIDE;
		// // } else if (out > 0) {
		// // result = Classifier.INTERSECT;
		// // }
		// return result;
	}

	private static final boolean isPointInside(Plane plane, Vector3f p) {
		return (plane.distance(p) <= 0.0f);
	}

	@SuppressWarnings("unused")
	private final boolean isPointInside(Vector3f p) {
		return isPointInside(topPlane, p) && isPointInside(bottomPlane, p) && isPointInside(leftPlane, p)
				&& isPointInside(rightPlane, p) && isPointInside(nearPlane, p);
	}

	/**
	 * Quick check to see if an orthogonal bounding box is inside the frustum
	 */
	public final int quickClassify(Box box) {
		// If all vertices is outside of at least one of planes
		for (Plane p : planes) {
			int in = 0;
			@SuppressWarnings("unused")
			int out = 0;
			for (int i = 0; i < 8; i++) {
				Vector3f v = box.getVertex(i);
				if (p.distance(v) > 0.0f) {
					out++;
				} else {
					in++;
				}
			}
			if (in < 1) {
				return (Classifier.OUTSIDE);
			}
		}

		return (Classifier.INTERSECT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Frustum[\r\n" + //
				"Top " + topPlane + "\r\n" + //
				"Bottom " + bottomPlane + "\r\n" + //
				"Left " + leftPlane + "\r\n" + //
				"Right " + rightPlane + "\r\n" + //
				"Near " + nearPlane + "\r\n" + //
				"Far " + farPlane + "\r\n" + //
				"]";
	}

}
