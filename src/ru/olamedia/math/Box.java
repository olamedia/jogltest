package ru.olamedia.math;

public class Box {
	private Vector3f low = new Vector3f();
	private Vector3f high = new Vector3f();

	/**
	 * @param x
	 *            left
	 * @param y
	 *            lower
	 * @param z
	 *            back
	 * @param x2
	 *            right
	 * @param y2
	 *            higher
	 * @param z2
	 *            front
	 */
	public Box(float x, float y, float z, float x2, float y2, float z2) {
		low.x = x;
		low.y = y;
		low.z = z;
		high.x = x2;
		high.y = y2;
		high.z = z2;
	}

	public Vector3f getVertex(int i) {
		Vector3f vertex = new Vector3f(low);
		if (i > 3) {
			// 4, 5, 6, 7
			vertex.z = high.z;
		}
		if (i % 4 > 1) {
			// 0 1 2 3 4 5 6 7
			// 0 1 2 3 0 1 2 3
			// - - + + - - + +
			vertex.y = high.y;
		}
		if (i % 2 > 0) {
			// 0 1 2 3 4 5 6 7
			// 0 1 0 1 0 1 0 1
			// - + - + - + - +
			vertex.x = high.x;
		}
		return vertex;
	}

	public float getLowerX() {
		return low.x;
	}

	public float getLowerY() {
		return low.y;
	}

	public float getLowerZ() {
		return low.z;
	}

	public float getUpperX() {
		return high.x;
	}

	public float getUpperY() {
		return high.y;
	}

	public float getUpperZ() {
		return high.z;
	}
}
