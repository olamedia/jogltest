package ru.olamedia.geom;

public class VoxelRaypicker {
	public float[] center = new float[3];
	public float[] dir = new float[3];
	public float radius;
	public float[] picker = new float[3];
	public float[] minDelta = new float[3];
	public float[] delta = new float[3];
	public float[] absDelta = new float[3];

	private void calcDelta(byte i) {
		if (dir[i] > 0) {
			delta[i] = ((float) ((int) (center[i] + 1)) - center[i]) / dir[i];
			absDelta[i] = delta[i];
		} else if (dir[0] < 0) {
			delta[i] = -((float) ((int) (center[i] - 1)) - center[i]) / dir[i];
			absDelta[i] = delta[i];
		} else {
			delta[i] = 0;
			absDelta[i] = 0;
		}
		minDelta[i] = delta[i];
	}

	public void reset() {
		picker[0] = center[0];
		picker[1] = center[1];
		picker[2] = center[2];
		calcDelta((byte) 0);
		calcDelta((byte) 1);
		calcDelta((byte) 2);
	}

	public void next() {
	}
}
