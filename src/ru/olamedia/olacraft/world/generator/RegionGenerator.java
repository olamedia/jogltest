package ru.olamedia.olacraft.world.generator;

import java.nio.IntBuffer;
import java.util.Random;

import libnoiseforjava.exception.ExceptionInvalidParam;
import libnoiseforjava.module.Perlin;
import ru.olamedia.olacraft.world.biome.Biome;
import ru.olamedia.olacraft.world.block.BlockRegistry;
import ru.olamedia.olacraft.world.blockTypes.GrassBlockType;
import ru.olamedia.olacraft.world.blockTypes.IceBlockType;
import ru.olamedia.olacraft.world.blockTypes.SnowBlockType;
import ru.olamedia.olacraft.world.blockTypes.TallGrassBlockType;
import ru.olamedia.olacraft.world.blockTypes.WaterBlockType;
import ru.olamedia.olacraft.world.blockTypes.stone.SandstoneStoneBlockType;
import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.data.HeightMap;
import ru.olamedia.olacraft.world.data.RegionData;
import ru.olamedia.olacraft.world.data.SectorData;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.location.IntLocation;
import ru.olamedia.olacraft.world.location.SectorLocation;

public class RegionGenerator {
	private int[] seed;
	private Random rand;

	private OceanSelectLayer oceanSelect = new OceanSelectLayer();
	private IslandSelectLayer islandSelect = new IslandSelectLayer();
	private TemperatureSelectLayer tempSelect = new TemperatureSelectLayer();
	private HumiditySelectLayer humSelect = new HumiditySelectLayer();
	private RiverSelectLayer riverSelect = new RiverSelectLayer();
	private MountainSelectLayer mountainSelect = new MountainSelectLayer();
	private BlockRegistry types;

	private byte waterId;
	private byte iceId;

	public void setSeed(int[] seed) {
		this.seed = seed;
	}

	public void debug(String s) {
		System.out.println("[RegionGenerator] " + s);
	}

	private int[] heightCache;
	private double[] temps;
	private double[] hums;
	private int[] biome;
	private boolean[] initialized;
	private boolean[] water;

	protected Perlin erosionNoise = new Perlin();

	public double avg(double a, double b) {
		return (a + b) / 2;
	}

	public double blend(double... args) {
		double x = 0;
		for (int i = 0; i < args.length; i++) {
			x += args[i];
		}
		return x / args.length;
	}

	public int getHeight(int vid, int x, int z) { // vid - vertical id
		if (!initialized[vid]) {
			double height = 0;
			double oceanHeight = Math.min(0, oceanSelect.getHeight(x, z) * 100);
			double isleHeight = islandSelect.getHeight(x, z) * 4;
			double riverHeight = riverSelect.getHeight(x, z) * 15;
			double temp = 10;// tempSelect.getTemperature(x, z) * 40; // -40..40
			double humidity = humSelect.getHumidity(x, z) * 50 + 50; // 0..100
			double mountain = mountainSelect.getHeight(x, z) * 20;
			temps[vid] = temp;
			hums[vid] = humidity;
			height = mountain;// blend(oceanHeight, isleHeight);
			if (oceanHeight < 0) {
				water[vid] = true;
				height = (int) Math.max(-120, oceanHeight);
			} else {
				water[vid] = false;
				// height = (int) Math.max(0, avg(oceanHeight, avg(isleHeight,
				// avg(oceanHeight, mountain))));
			}

			biome[vid] = Biome.land;
			//
			// if (oceanHeight > 0) {
			// // materic
			// biome[vid] = Biome.land;
			// height = isleHeight;
			// if (mountain > 1) {
			// height = isleHeight;
			// }
			// if (riverHeight < 0) {
			// // RIVERS
			// water[vid] = true;
			// height = riverHeight;
			// } else {
			// if (temp > 30) {
			// biome[vid] = Biome.desert;
			// }
			// }
			// } else {
			// if (isleHeight > 0) {
			// // island core
			// height = 2;
			// biome[vid] = Biome.island;
			// } else if (oceanHeight > -3 && isleHeight > -10) {
			// // half-island merged
			// height = 1;
			// biome[vid] = Biome.island;
			// } else {
			// water[vid] = true;
			// height = -2;
			// }
			// }

			// return (int) height;
			heightCache[vid] = (int) height;
			initialized[vid] = true;
		}
		return heightCache[vid];
	}

	public static int clampFloat(float x, float min, float max) { // x in -1..1
		return (int) (min + (max - min) * ((1 + x) / 2));
	}

	// BlockLocation blockOffset = new BlockLocation();

	private IntBuffer blockLocation = IntLocation.allocate();
	private IntBuffer blockOffset = IntLocation.allocate();
	private IntBuffer chunkLocation = IntLocation.allocate();
	private IntBuffer chunkOffset = IntLocation.allocate();

	public void generateSector(SectorData sector) {
		heightCache = new int[256];
		temps = new double[256];
		hums = new double[256];
		biome = new int[256];
		initialized = new boolean[256];
		water = new boolean[256];
		for (int i = 0; i < 256; i++) {
			initialized[i] = false;
			water[i] = false;
		}
		sector.heightMap = new HeightMap(16, 16);
		for (int y = 0; y < 16; y++) {
			// CREATE CHUNK
			final ChunkData chunk = new ChunkData();
			chunk.location = new ChunkLocation(sector.location.x, y, sector.location.z);
			IntLocation.set(chunkLocation, sector.location.x, y, sector.location.z);
			IntLocation.chunk2block(chunkLocation, chunkOffset);
			// final BlockLocation chunkOffset =
			// chunk.location.getBlockLocation();
			for (int inChunkX = 0; inChunkX < 16; inChunkX++) {
				for (int inChunkZ = 0; inChunkZ < 16; inChunkZ++) {
					final int vid = inChunkX * 16 + inChunkZ;
					final int height = getHeight(vid, sector.location.x * 16 + inChunkX, sector.location.z * 16
							+ inChunkZ);
					// int height = 60;
					// System.out.println("height: " + height);
					sector.heightMap.setHeight(inChunkX, inChunkZ, height);
					IntLocation.setXZ(blockOffset, inChunkX, inChunkZ);
					for (int inChunkY = 0; inChunkY < 16; inChunkY++) {
						IntLocation.setY(blockOffset, inChunkY);
						IntLocation.setSum(blockLocation, chunkOffset, blockOffset);
						blockID = IntLocation.id(blockLocation);
						// height = sector.heightMap.getHeight(inChunkX,
						// inChunkZ);
						makeBlock(chunk, vid);

					}
				}
			}
			// debug(sector.heightMap.toString());
			chunk.compact();
			sector.set(y, chunk);
		}
	}

	private int blockHeight;
	private int blockY;
	private int blockID;

	private void makeBlock(ChunkData chunk, int vid) {
		if (null == types) {
			throw new RuntimeException("types is null");
		}
		blockHeight = getHeight(vid, 0, 0); // coordinates makes no difference
											// as
											// cached already
		blockY = IntLocation.getY(blockLocation);
		blockID = IntLocation.id(blockLocation);
		/*
		 * double erosion = erosionNoise.getValue(blockOffset.x, blockOffset.y,
		 * blockOffset.z);
		 * if (erosion > 0.99) {
		 * chunk.setEmpty(blockOffset, true);
		 * return;
		 * }
		 */
		// WATER:
		if (blockY <= 0) {
			if (water[vid]) {
				if (blockY > blockHeight) {
					chunk.setEmpty(blockID, false);
					chunk.types[blockID] = waterId;
					if (temps[vid] < -10) {
						chunk.types[blockID] = iceId;
					}
					return;
				}
			}
		}
		if (blockY > blockHeight) {
			// AIR
			chunk.setEmpty(blockID, true);
			if (blockY - 1 == blockHeight) {
				// floor level
				if (temps[vid] > 15 && temps[vid] < 25 && hums[vid] > 30 && hums[vid] < 70) {
					if (rand.nextInt(15) > 2) {
						chunk.setEmpty(blockID, false);
						chunk.types[blockID] = (byte) (types.getBlockIdByClassName(TallGrassBlockType.class.getName())); // FIXME,
					}
				}
			}
		} else {
			// LAND
			chunk.setEmpty(blockID, false);
			if (temps[vid] > 30) {
				chunk.types[blockID] = (byte) types.getBlockIdByClassName(SandstoneStoneBlockType.class.getName());
			} else if (temps[vid] < -10 && hums[vid] > 30) {
				chunk.types[blockID] = (byte) (types.getBlockIdByClassName(SnowBlockType.class.getName())); // FIXME,
				// SNOW
			} else {
				chunk.types[blockID] = (byte) (types.getBlockIdByClassName(GrassBlockType.class.getName()));// (1
				// +
				// Math.random()
				// *
				// 11);
			}
		}
	}

	public void generate(RegionData data) {
		final SectorLocation sectorOffset = data.location.getSectorLocation();
		HeightMapGenerator.minValue = -5;
		HeightMapGenerator.maxValue = 60;
		HeightMapGenerator.init();
		HeightMapGenerator.seed = seed[0];
		oceanSelect.setSeed(seed[0]);
		islandSelect.setSeed(seed[1]);
		tempSelect.setSeed(seed[2]);
		humSelect.setSeed(seed[3]);
		riverSelect.setSeed(seed[4]);
		rand = new Random(seed[6]);
		erosionNoise.setFrequency(0.01);
		try {
			erosionNoise.setOctaveCount(5);
		} catch (ExceptionInvalidParam e) {
			e.printStackTrace();
		}
		erosionNoise.setSeed(seed[5]);
		// BlockLocation blockOffset = data.location.getBlockLocation();
		// int[][] heightMap =
		// HeightMapGenerator.getHeightMap(data.location.getBlockLocation().x,
		// data.location.getBlockLocation().z, 256, 256);
		debug(data.location.toString());
		data.heightMap = HeightMapGenerator.getHeightMap(data.location);
		// debug(data.heightMap.toString());
		data.sectorData = new SectorData[16][16];
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				// CREATE SECTOR
				final SectorData sector = new SectorData();
				sector.location = new SectorLocation(sectorOffset.x + x, sectorOffset.z + z);
				generateSector(sector);
				data.sectorData[x][z] = sector;
			}
		}
	}

	public void setTypes(BlockRegistry types) {
		this.types = types;
		waterId = (byte) types.getBlockIdByClassName(WaterBlockType.class.getName());
		iceId = (byte) types.getBlockIdByClassName(IceBlockType.class.getName());
	}

	public static float lerp(float x, float x1, float x2, float q00, float q01) {
		return ((x2 - x) / (x2 - x1)) * q00 + ((x - x1) / (x2 - x1)) * q01;
	}

	public static float biLerp(float x, float y, float q11, float q12, float q21, float q22, float x1, float x2,
			float y1, float y2) {
		float r1 = lerp(x, x1, x2, q11, q21);
		float r2 = lerp(x, x1, x2, q12, q22);

		return lerp(y, y1, y2, r1, r2);
	}

	public static float triLerp(float x, float y, float z, float q000, float q001, float q010, float q011, float q100,
			float q101, float q110, float q111, float x1, float x2, float y1, float y2, float z1, float z2) {
		float x00 = lerp(x, x1, x2, q000, q100);
		float x10 = lerp(x, x1, x2, q010, q110);
		float x01 = lerp(x, x1, x2, q001, q101);
		float x11 = lerp(x, x1, x2, q011, q111);
		float r0 = lerp(y, y1, y2, x00, x01);
		float r1 = lerp(y, y1, y2, x10, x11);

		return lerp(z, z1, z2, r0, r1);
	}
}
