package ru.olamedia.olacraft.world.generator;

import libnoiseforjava.NoiseGen.NoiseQuality;
import libnoiseforjava.exception.ExceptionInvalidParam;
import libnoiseforjava.module.Perlin;
import libnoiseforjava.module.RidgedMulti;

public class MountainSelectLayer {
	protected long seed;

	protected Perlin mountainNoise = new Perlin();
	protected Perlin hillNoise = new Perlin();

	public MountainSelectLayer() {
		hillNoise.setNoiseQuality(NoiseQuality.QUALITY_BEST);
		mountainNoise.setNoiseQuality(NoiseQuality.QUALITY_BEST);
		try {
			hillNoise.setOctaveCount(5);
			hillNoise.setFrequency(0.004);
			mountainNoise.setOctaveCount(7);
			mountainNoise.setFrequency(0.004);
		} catch (ExceptionInvalidParam e) {
			e.printStackTrace();
		}
	}

	public void setSeed(int seed) {
		hillNoise.setSeed(seed);
		mountainNoise.setSeed(seed);
	}

	private double val;

	public double getHeight(double x, double z) {
		val = mountainNoise.getValue(x, 0, z);
		return (val > 0) ? 1 - val : 1 + val;
	}
}
