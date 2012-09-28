package ru.olamedia.math;

public class Quaternion {
	// identity:
	public float x = 0;
	public float y = 0;
	public float z = 0;
	public float w = 1;

	public static Quaternion identity() {
		return new Quaternion();
	}

	public Quaternion inverse() {
		return QuaternionUtil.inverse(this);
	}

	public Quaternion mul(Quaternion q) {
		return QuaternionUtil.multiply(this, q);
	}
	
	public float[] toMatrixArray(){
		return QuaternionUtil.toMatrixArray(this);
	}
}
