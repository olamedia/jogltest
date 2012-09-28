package ru.olamedia.camera;

/**
 * Cameraman.
 * 
 * @desc Primary purpose is providing eyes level: getCameraY()
 * 
 * @author olamedia
 * 
 */
public interface Cameraman {
	public float getCameraX();

	public float getCameraY();

	public float getCameraZ();

	public void update(float delta);

	public void captureControls();
}
