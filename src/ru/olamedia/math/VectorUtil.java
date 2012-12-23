package ru.olamedia.math;

public class VectorUtil {
	private static float[] tmpVec = new float[3];

	public static float dot(float[] vec1, float[] vec2) {
		return (vec1[0] * vec2[0] + vec1[1] * vec2[1] + vec1[2] * vec2[2]);
	}

	public static void translate(float[] result, float[] vec, float distance) {
		for (int i = 0; i < 3; i++) {
			result[i] += vec[i] * distance;
		}
	}

	public static void set(float[] result, float[] vec) {
		for (int i = 0; i < 3; i++) {
			result[i] = vec[i];
		}
	}

	public static void cross(float[] vec1, float[] vec2) {
		set(tmpVec, vec1);

		vec1[0] = vec2[2] * tmpVec[1] - vec2[1] * tmpVec[2];
		vec1[1] = vec2[0] * tmpVec[2] - vec2[2] * tmpVec[0];
		vec1[2] = vec2[1] * tmpVec[0] - vec2[0] * tmpVec[1];
	}
}
