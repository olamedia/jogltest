package ru.olamedia.olacraft.modelAnimator;

import java.util.Random;

public class Randomizer {

	public Randomizer(float minTimeout, float maxTimeout) {
		super();
		this.minTimeout = minTimeout;
		this.maxTimeout = maxTimeout;
	}

	private float minTimeout = 1f;
	private float maxTimeout = 1f;
	private long seed;
	private long prevSeed = 0;
	private float seedTimeout = 0;
	private float seedTime = 0;
	private float delta;
	private boolean isPaused = false;
	private boolean isForcePaused = false;

	/**
	 * @return the isPaused
	 */
	public boolean isPaused() {
		return isPaused;
	}

	/**
	 * @param isPaused
	 *            the isPaused to set
	 */
	public void setPaused(boolean isPaused) {
		this.isForcePaused = isPaused;
	}

	private Random rand = new Random();
	private Random prev = new Random();

	public void tick(float delta) {
		if (!isForcePaused) {
			seedTimeout -= delta;
		}
		if (seedTimeout <= 0) {
			if (!isPaused) {
				prevSeed = seed;
			}
			Random srand = new Random();
			seed = srand.nextLong();
			seedTime = seedTimeout = minTimeout + srand.nextFloat() * (maxTimeout - minTimeout);
		}
		prev.setSeed(prevSeed);
		rand.setSeed(seed);
		prev.nextBoolean();
		isPaused = rand.nextBoolean();
		if (isPaused) {
			this.delta = 0;
		} else {
			this.delta = (seedTime - seedTimeout) / seedTime;
		}
	}

	public float getMinTimeout() {
		return minTimeout;
	}

	public void setMinTimeout(float minTimeout) {
		this.minTimeout = minTimeout;
	}

	public float getMaxTimeout() {
		return maxTimeout;
	}

	public void setMaxTimeout(float maxTimeout) {
		this.maxTimeout = maxTimeout;
	}

	public void randomize(Skeleton skel) {
		skel.randomize(rand, prev, delta);
	}

	public void randomize(Leg leg) {
		leg.randomize(rand, prev, delta);
	}

	public void randomize(ISkeletonNode b) {
		b.randomize(rand, prev, delta);
	}

}
