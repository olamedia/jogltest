package ru.olamedia.math;

import com.jogamp.opengl.math.geom.AABBox;

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

	private static final boolean quickClassify(Plane p, AABBox box) {
		final float[] low = box.getLow();
		final float[] high = box.getHigh();
		if (p.distance(low[0], low[1], low[2]) > 0.0f)
			return (true);
		if (p.distance(high[0], low[1], low[2]) > 0.0f)
			return (true);
		if (p.distance(low[0], high[1], low[2]) > 0.0f)
			return (true);
		if (p.distance(high[0], high[1], low[2]) > 0.0f)
			return (true);
		if (p.distance(low[0], low[1], high[2]) > 0.0f)
			return (true);
		if (p.distance(high[0], low[1], high[2]) > 0.0f)
			return (true);
		if (p.distance(low[0], high[1], high[2]) > 0.0f)
			return (true);
		if (p.distance(high[0], high[1], high[2]) > 0.0f)
			return (true);

		return (false);
	}

	/**
	 * Quick check to see if an orthogonal bounding box is inside the frustum
	 */
	public final boolean isOutside(AABBox box) {
		if (!quickClassify(leftPlane, box))
			return true;
//		if (!quickClassify(rightPlane, box))
//			return true;
//		if (!quickClassify(topPlane, box))
//			return true;
//		if (!quickClassify(bottomPlane, box))
//			return true;
//		if (!quickClassify(nearPlane, box))
//			return true;
//		if (!quickClassify(farPlane, box))
//			return true;

		// We make no attempt to determine whether it's fully inside or not.
		return false;
	}

	private static final boolean isPointInside(Plane plane, Vector3f p) {
		return (plane.distance(p) <= 0.0f);
	}

	@SuppressWarnings("unused")
	private final boolean isPointInside(Vector3f p) {
		return isPointInside(topPlane, p) && isPointInside(bottomPlane, p) && isPointInside(leftPlane, p)
				&& isPointInside(rightPlane, p) && isPointInside(nearPlane, p);
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
