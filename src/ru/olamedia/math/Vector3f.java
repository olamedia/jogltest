package ru.olamedia.math;

public class Vector3f {
	public float x;
	public float y;
	public float z;

	public Vector3f() {
		this(0, 0, 0);
	}

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f(Vector3f vector) {
		this(vector.x, vector.y, vector.z);
	}

	public float dot(Vector3f v) {
		return (x * v.x + y * v.y + z * v.z);
	}

	public Vector3f cross(Vector3f n) {
		Vector3f r = new Vector3f(y * n.z - z * n.y, z * n.x - x * n.z, x * n.y - y * n.x);
		return r;
	}

	public Vector3f add(Vector3f n) {
		Vector3f r = new Vector3f(x + n.x, y + n.y, z + n.z);
		return r;
	}

	public Vector3f sub(Vector3f n) {
		return add(n.negate());
	}

	public Vector3f negate() {
		Vector3f n = new Vector3f(this);
		n.x = -n.x;
		n.y = -n.y;
		n.z = -n.z;
		return n;
	}

	public Vector3f translate(Vector3f look, float f) {
		Vector3f v = new Vector3f(this);
		v.x += look.x * f;
		v.y += look.y * f;
		v.z += look.z * f;
		return v;
	}

	public Vector3f translate(javax.vecmath.Vector3f look, float f) {
		Vector3f v = new Vector3f(this);
		v.x += look.x * f;
		v.y += look.y * f;
		v.z += look.z * f;
		return v;
	}

	public void set(Vector3f vn) {
		x = vn.x;
		y = vn.y;
		z = vn.z;
	}

}
