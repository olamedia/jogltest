package ru.olamedia.olacraft.game;

/**
 * For frame-rate independent movement
 * 
 * @author Oskar Veerhoek
 */
public class Timer {
	private long lastTime; // nanoseconds
	private double elapsedTime;
	private float fps;
	private int fpsCounter = 0;
	private long lastFPS;
	private float avgSeconds = 3;

	/**
	 * @return the fps
	 */
	public float getFps() {
		return fps;
	}

	/**
	 * @return the avgFps
	 */
	public float getAvgFps() {
		return avgFps;
	}

	private float avgFps;

	/**
	 * Creates a timer.
	 */
	public Timer() {
		fps = 0;
	}

	/**
	 * Initializes the timer. Call this just before entering the game loop.
	 */
	public void initialize() {
		lastTime = System.nanoTime();
	}

	/**
	 * @return the elapsed time since the the next to last update call
	 */
	public double getElapsedTime() {
		return elapsedTime;
	}

	/**
	 * Updates the timer. Call this once every iteration of the game loop.
	 * 
	 * @return the elapsed time in milliseconds
	 */
	public double update() {
		if (lastTime == 0) {
			lastTime = System.nanoTime();
			return 0;
		} else {
			long elapsedTime = System.nanoTime() - lastTime;
			updateFps(elapsedTime);
			lastTime = System.nanoTime();
			this.elapsedTime = elapsedTime / (double) 1000000;
			return this.elapsedTime;
		}
	}

	public void updateFps(long elapsedTime) {
		if (elapsedTime > 0) {
			float ms = (float) (elapsedTime / 1000000);
			if (ms > 0) {
				fps = (float) (1000 / ms);
			}
		}
		fpsCounter++;
		if (lastFPS == 0) {
			lastFPS = System.nanoTime();
		} else {
			double elapsedFPS = (System.nanoTime() - lastFPS) / (double) 1000000;
			if (elapsedFPS > 1000 * avgSeconds) {
				avgFps = fpsCounter / avgSeconds;
				fpsCounter = 0;
				lastFPS = System.nanoTime();
			}
		}

		// if (elapsedTime > 0) {
		// fps = (float) (1000 / (elapsedTime / 1000000));
		// if (avgFps == 0) {
		// avgFps = fps;
		// } else {
		// avgFps = avgFps + (fps - avgFps) / 1000;
		// }
		// }
	}
}