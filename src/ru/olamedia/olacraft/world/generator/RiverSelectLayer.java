package ru.olamedia.olacraft.world.generator;

import libnoiseforjava.NoiseGen.NoiseQuality;
import libnoiseforjava.exception.ExceptionInvalidParam;
import libnoiseforjava.module.Perlin;
import libnoiseforjava.module.RidgedMulti;

public class RiverSelectLayer {

	protected Perlin noise = new Perlin();
	protected Perlin widthNoise = new Perlin();
	protected Perlin deepNoise = new Perlin();

	// protected RidgedMulti noise = new RidgedMulti();

	public RiverSelectLayer() {
		noise.setNoiseQuality(NoiseQuality.QUALITY_BEST);
		widthNoise.setNoiseQuality(NoiseQuality.QUALITY_BEST);
		deepNoise.setNoiseQuality(NoiseQuality.QUALITY_BEST);
		try {
			noise.setOctaveCount(5);
			noise.setFrequency(0.000004);
			widthNoise.setOctaveCount(6);
			widthNoise.setFrequency(0.004);
			deepNoise.setOctaveCount(3);
			deepNoise.setFrequency(0.004);
		} catch (ExceptionInvalidParam e) {
			e.printStackTrace();
		}
	}

	public void setSeed(int seed) {
		noise.setSeed(seed);
		widthNoise.setSeed(seed);
		deepNoise.setSeed(seed);
	}

	static double min = -0.0005;
	static double max = 0;
	private static double deepFactor = 0;

	public static double scale(double value) {
		final double factor = 1 / (max - min);
		final double r01 = (value - min) * factor; // 0..1
		final double r3 = r01 > 0.5 ? -r01 : -r01;
		return -(1 - Math.abs(1 - r01 * 2)) * deepFactor;
	}

	public double getHeight(double x, double z) {
		final double widthFactor = widthNoise.getValue(x, 0, z);
		deepFactor = (deepNoise.getValue(x, 0, z) + 1.2) / 2.2;
		min = -0.000005 * (1 + (1 + widthFactor) * 40);
		final double value = noise.getValue(x, 0, z);
		return value >= min && value <= max ? scale(value) : 0;
	}
}
