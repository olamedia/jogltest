package ru.olamedia.math;

import static org.openmali.FastMath.*;

// 4x4 float matrix, column-major notation
public class Matrix4f {
	protected float[] m;

	public Matrix4f() {
		m = new float[16];
	}

	public Matrix4f(float[] m) {
		this.m = m;
	}

	public Matrix4f(javax.vecmath.Matrix4f m2) {
		m = new float[16];
		m[0] = m2.m00;
		m[1] = m2.m10;
		m[2] = m2.m20;
		m[3] = m2.m30;
		m[4] = m2.m01;
		m[5] = m2.m11;
		m[6] = m2.m21;
		m[7] = m2.m31;
		m[8] = m2.m02;
		m[9] = m2.m12;
		m[10] = m2.m22;
		m[11] = m2.m32;
		m[12] = m2.m03;
		m[13] = m2.m13;
		m[14] = m2.m23;
		m[15] = m2.m33;
	}

	public float[] toFloatArray() {
		return m;
	}

	public void set(int i, float v) {
		m[i] = v;
	}

	public float get(int i) {
		return m[i];
	}

	public void loadIdentity() {
		setIdentity();
	}

	public void setIdentity() {
		m[0] = m[5] = m[10] = m[15] = 1.0f;
		m[1] = m[2] = m[3] = m[4] = 0.0f;
		m[6] = m[7] = m[8] = m[9] = 0.0f;
		m[11] = m[12] = m[13] = m[14] = 0.0f;
	}

	public static Matrix4f translateMatrix(float x, float y, float z) {
		Matrix4f m = new Matrix4f();
		m.setIdentity();
		// Translate slots.
		m.set(12, x);
		m.set(13, y);
		m.set(14, z);
		return m;
	}

	public static Matrix4f scaleMatrix(float sx, float sy, float sz) {
		Matrix4f m = new Matrix4f();
		m.setIdentity();
		// Scale slots.
		m.set(0, sx);
		m.set(5, sy);
		m.set(10, sz);
		return m;
	}

	public static Matrix4f rotateXMatrix(float degrees) {
		float radians = toRad(degrees);
		float c = cos(radians);
		float s = sin(radians);
		Matrix4f m = new Matrix4f();
		m.setIdentity();
		// Rotate X formula.
		m.set(5, c);
		m.set(6, -s);
		m.set(9, s);
		m.set(10, c);
		return m;
	}

	public static Matrix4f rotateYMatrix(float degrees) {
		float radians = toRad(degrees);
		float c = cos(radians);
		float s = sin(radians);
		Matrix4f m = new Matrix4f();
		m.setIdentity();
		// Rotate Y formula.
		m.set(0, c);
		m.set(2, s);
		m.set(8, -s);
		m.set(10, c);
		return m;
	}

	public static Matrix4f rotateZMatrix(float degrees) {
		float radians = toRad(degrees);
		float c = cos(radians);
		float s = sin(radians);
		Matrix4f m = new Matrix4f();
		m.setIdentity();
		// Rotate Z formula.
		m.set(0, c);
		m.set(1, s);
		m.set(4, -s);
		m.set(5, c);
		return m;
	}

	public Vector3f getUpVector() {
		return new Vector3f(m[1], m[5], m[9]);
	}

	public Vector3f getLookVector() { // POSITIVE_Z
		return new Vector3f(m[2], m[6], m[10]);
	}

	public Vector3f getRightVector() {
		return new Vector3f(m[0], m[4], m[8]);
	}

	public Matrix4f multiply(Matrix4f m) {
		return Matrix4fUtil.multiply(this, m);
	}

	public void apply(Matrix4f m) {
		this.m = multiply(m).toFloatArray();
	}

	public float[] getAngles() {
		// TODO check majority
		float ax, ay, az;
		float cy;
		ay = -asin(m[2]); /* Calculate Y-axis angle */
		cy = cos(ay);
		ay = toDeg(ay);
		float trX, trY;

		if (Math.abs(cy) > 0.005) /* Gimball lock? */
		{
			trX = m[10] / cy; /* No, so get X-axis angle */
			trY = -m[6] / cy;

			ax = toDeg(atan2(trY, trX));

			trX = m[0] / cy; /* Get Z-axis angle */
			trY = -m[1] / cy;

			az = toDeg(atan2(trY, trX));
		} else /* Gimball lock has occurred */
		{
			ax = 0; /* Set X-axis angle to zero */

			trX = m[5]; /* And calculate Z-axis angle */
			trY = m[4];

			az = toDeg(atan2(trY, trX));
		}

		ax = clamp(ax, 0, 360); /* Clamp all angles to range */
		ay = clamp(ay, 0, 360);
		az = clamp(az, 0, 360);
		return new float[] { ax, ay, ax };
	}

	private float clamp(float a, float min, float max) {
		a = a % max;
		return a;
	}

	public float c(int column, int row) {
		// COLUMN-BASED
		return m[column * 4 + row];
	}

	public void set(float[] m) {
		this.m = m;
	}
}
