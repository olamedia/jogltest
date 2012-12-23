package ru.olamedia.olacraft.world.generator;

import libnoiseforjava.NoiseGen.NoiseQuality;
import libnoiseforjava.exception.ExceptionInvalidParam;
import libnoiseforjava.module.Perlin;
import libnoiseforjava.module.RidgedMulti;

public class TemperatureSelectLayer {
	protected long seed;

	protected Perlin noise = new Perlin();
	//protected RidgedMulti noise = new RidgedMulti();

	public TemperatureSelectLayer() {
		noise.setNoiseQuality(NoiseQuality.QUALITY_BEST);
		try {
			noise.setOctaveCount(5);
			noise.setFrequency(0.004);
		} catch (ExceptionInvalidParam e) {
			e.printStackTrace();
		}
	}

	public void setSeed(int seed) {
		noise.setSeed(seed);
	}

	public double getTemperature(double x, double z) {
		return noise.getValue(x, 0, z);
	}
}
