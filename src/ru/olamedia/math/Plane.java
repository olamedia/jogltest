package ru.olamedia.math;

import static java.lang.Math.sqrt;

public class Plane {
	// normal
	public Vector3f n = new Vector3f();
	// distance
	public float d;

	public void set(Plane p) {
		n.x = p.n.x;
		n.y = p.n.y;
		n.z = p.n.z;
		d = p.d;
	}

	public float magnitude() {
		return (float) sqrt(n.x * n.x + n.y * n.y + n.z * n.z);
	}

	public void normalize() {
		float mag = magnitude();
		n.x /= mag;
		n.y /= mag;
		n.z /= mag;
		d /= mag;
	}

	public float distance(float x, float y, float z) {
		return n.x * x + n.y * y + n.z * z + d;
	}

	public float distance(Vector3f point) {
		return n.x * point.x + n.y * point.y + n.z * point.z + d;
	}

	private float dec(float a) {
		return (float) Math.floor(a * 100) / 100;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Plane[nx=" + dec(n.x) + ";ny=" + dec(n.y) + ";nz=" + dec(n.z) + ";d=" + dec(d) + "]";
	}

	public Vector3f cross(Plane a, Plane b) {
		return n.cross(a.n.cross(b.n));
	}

	public void set3Points(Vector3f va, Vector3f vb, Vector3f vc) {
		// A = y1 (z2 - z3) + y2 (z3 - z1) + y3 (z1 - z2)
		// B = z1 (x2 - x3) + z2 (x3 - x1) + z3 (x1 - x2)
		// C = x1 (y2 - y3) + x2 (y3 - y1) + x3 (y1 - y2)
		// - D = x1 (y2 z3 - y3 z2) + x2 (y3 z1 - y1 z3) + x3 (y1 z2 - y2 z1)
		Vector3f ab = vb.sub(va);
		Vector3f ac = vc.sub(va);
		Vector3f vn = ab.cross(ac);
		n.set(vn);
		n.x = va.y * (vb.z - vc.z) + vb.y * (vc.z - va.z) + vc.y * (va.z - vb.z);
		n.y = va.z * (vb.x - vc.x) + vb.z * (vc.x - va.x) + vc.z * (va.x - vb.x);
		n.z = va.x * (vb.y - vc.y) + vb.x * (vc.y - va.y) + vc.x * (va.y - vb.y);
		d = -(vn.x * va.x + vn.y * va.y + vn.z * va.z);
		normalize();
	}
}
