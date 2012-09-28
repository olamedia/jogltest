package ru.olamedia.math;

public class QuaternionUtil {
	// QUATERNION INVERSE
	public static Quaternion inverse(Quaternion q) {
		Quaternion newQ = new Quaternion();
		newQ.w = q.w;
		newQ.x = -q.x;
		newQ.y = -q.y;
		newQ.z = -q.z;
		// normalize here
		return newQ;
	}

	// QUATERNION MULTIPLICATION
	public static Quaternion multiply(Quaternion q1, Quaternion q2) {
		Quaternion newQ = new Quaternion();
		newQ.w = q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z;
		newQ.x = q1.w * q2.x + q1.x * q2.w + q1.y * q2.z - q1.z * q2.y;
		newQ.y = q1.w * q2.y - q1.x * q2.z + q1.y * q2.w + q1.z * q2.x;
		newQ.z = q1.w * q2.z + q1.x * q2.y - q1.y * q2.x + q1.z * q2.w;
		return newQ;
	}

	// QUATERNION-TO-MATRIX, COLUMN-MAJOR NOTATION
	public static float[] toMatrixArray(Quaternion q) {
		float[] matrix = new float[16];
		// First Column
		matrix[0] = 1 - 2 * (q.y * q.y + q.z * q.z);
		matrix[1] = 2 * (q.x * q.y + q.z * q.w);
		matrix[2] = 2 * (q.x * q.z - q.y * q.w);
		matrix[3] = 0;
		// Second Column
		matrix[4] = 2 * (q.x * q.y - q.z * q.w);
		matrix[5] = 1 - 2 * (q.x * q.x + q.z * q.z);
		matrix[6] = 2 * (q.z * q.y + q.x * q.w);
		matrix[7] = 0;
		// Third Column
		matrix[8] = 2 * (q.x * q.z + q.y * q.w);
		matrix[9] = 2 * (q.y * q.z - q.x * q.w);
		matrix[10] = 1 - 2 * (q.x * q.x + q.y * q.y);
		matrix[11] = 0;
		// Fourth Column
		matrix[12] = 0;
		matrix[13] = 0;
		matrix[14] = 0;
		matrix[15] = 1;
		return matrix;
	}

	public static float magnitude(Quaternion qa) {
		return (float) (Math.sqrt((double) (qa.w * qa.w + qa.x * qa.x + qa.y * qa.y + qa.z * qa.z)));
	}
}
