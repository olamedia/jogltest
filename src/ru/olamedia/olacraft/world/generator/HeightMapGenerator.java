package ru.olamedia.olacraft.world.generator;

import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.data.HeightMap;
import ru.olamedia.olacraft.world.location.RegionLocation;
import libnoiseforjava.NoiseGen.NoiseQuality;
import libnoiseforjava.exception.ExceptionInvalidParam;
import libnoiseforjava.module.Billow;
import libnoiseforjava.module.Blend;
import libnoiseforjava.module.Max;
import libnoiseforjava.module.Perlin;
import libnoiseforjava.module.RidgedMulti;
import libnoiseforjava.module.ScaleBias;
import libnoiseforjava.module.Select;
import libnoiseforjava.module.Turbulence;
import libnoiseforjava.util.NoiseMap;
import libnoiseforjava.util.NoiseMapBuilderPlane;

public class HeightMapGenerator {
	public static int minValue;
	public static int maxValue;

	public static int seed = (int) (Integer.MAX_VALUE * Math.random());

	private static Billow plainsNoise;
	private static ScaleBias plains;
	private static RidgedMulti hillsNoise;
	private static ScaleBias hills;
	private static RidgedMulti mountainsNoise;
	private static ScaleBias mountains;
	private static Perlin terrainType;
	private static Blend blendedTerrain;
	private static Select selectedTerrain;
	private static Max maxTerrain;
	private static Turbulence turbulence;
	private static ScaleBias finalTerrain;

	private static boolean isInitialized = false;

	public static void init() {
		if (isInitialized) {
			return;
		}
		isInitialized = true;
		try {
			// PLAINS
			plainsNoise = new Billow();
			plainsNoise.setFrequency(0.01);
			plains = new ScaleBias(plainsNoise);
			plains.setScale(0.05);
			plains.setBias(-0.75);
			// HILLS
			hillsNoise = new RidgedMulti();
			hillsNoise.setFrequency(0.01);
			hills = new ScaleBias(hillsNoise);
			hills.setScale(0.5);
			hills.setBias(-0.75);
			// MOUNTAINS
			mountainsNoise = new RidgedMulti();
			mountainsNoise.setFrequency(0.04);
			mountainsNoise.setOctaveCount(6);
			turbulence = new Turbulence(mountainsNoise);
			turbulence.setFrequency(0.2);
			turbulence.setPower(1);
			mountains = new ScaleBias(turbulence);
			mountains.setScale(1.0);
			mountains.setBias(-1.25);
			terrainType = new Perlin();
			terrainType.setOctaveCount(6);
			terrainType.setFrequency(0.06);
			terrainType.setPersistence(0.25);
			terrainType.setNoiseQuality(NoiseQuality.QUALITY_BEST);
			selectedTerrain = new Select(plains, mountains, terrainType);
			selectedTerrain.setBounds(0, 1);
			selectedTerrain.setEdgeFalloff(0.125);
			blendedTerrain = new Blend(plains, mountains, terrainType);
			maxTerrain = new Max(plains, turbulence);
			finalTerrain = new ScaleBias(maxTerrain);
			finalTerrain.setBias(2);
			setSeed(seed);
		} catch (ExceptionInvalidParam e) {
			e.printStackTrace();
		}
	}

	public static void setSeed(int newseed) {
		seed = newseed;
		plainsNoise.setSeed(seed);
		hillsNoise.setSeed(seed);
		mountainsNoise.setSeed(seed);
		terrainType.setSeed(seed);
	}

	public static int[][] getChunkHeightMap(int chunkX, int chunkZ) {
		init();
		try {
			NoiseMapBuilderPlane builder = new NoiseMapBuilderPlane(16, 16);
			// builder.enableSeamless(true);
			// Perlin plains = new Perlin();

			// Select finalTerrain = new Select(plains, mountains, null);
			// finalTerrain.setControlModule(terrainType);
			// finalTerrain.setBounds(0.0, 1000);
			// finalTerrain.setEdgeFalloff(1.25);

			NoiseMap heightMap = new NoiseMap(16, 16);
			builder.setSourceModule(maxTerrain);
			builder.setDestNoiseMap(heightMap);
			double bx = chunkX;
			double bz = chunkZ;
			builder.setDestSize(16, 16);
			builder.setBounds(bx, bx + 1, bz, bz + 1);
			builder.build();
			double[][] heights = heightMap.getNoiseMap();
			int[][] ints = new int[16][16];
			// System.out.print("heightmap:");
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					// System.out.print(((float) heights[x][z]) + ";");
					ints[x][z] = (int) (minValue + (maxValue - minValue) * (heights[x][z] + 1) / 2);
				}
			}
			// System.out.println("");
			return ints;
		} catch (ExceptionInvalidParam e) {
			e.printStackTrace();
		}
		return null;
	}

	public static HeightMap getHeightMap(RegionLocation location) {
		init();
		HeightMap map = new HeightMap(256, 256);
		try {
			NoiseMapBuilderPlane builder = new NoiseMapBuilderPlane(256, 256);
			NoiseMap heightMap = new NoiseMap(256, 256);
			builder.setSourceModule(finalTerrain);
			builder.setDestNoiseMap(heightMap);
			builder.setDestSize(256, 256);
			float bx = location.x;
			float bz = location.z;
			builder.setBounds(bx, bx + 1, bz, bz + 1);
			builder.build();
			double[][] heights = heightMap.getNoiseMap();
			for (int x = 0; x < 256; x++) {
				for (int z = 0; z < 256; z++) {
					map.setHeight(x, z, 0);
					//(int) (minValue + (maxValue - minValue) * (heights[x][z] + 1) / 2)
				}
			}
			return map;
		} catch (ExceptionInvalidParam e) {
			e.printStackTrace();
		}
		return null;
	}
}
