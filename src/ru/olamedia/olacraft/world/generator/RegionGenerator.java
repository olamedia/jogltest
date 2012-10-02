package ru.olamedia.olacraft.world.generator;

import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.data.HeightMap;
import ru.olamedia.olacraft.world.data.RegionData;
import ru.olamedia.olacraft.world.data.SectorData;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.location.SectorLocation;

public class RegionGenerator {
	private int[] seed;

	public void setSeed(int[] seed) {
		this.seed = seed;
	}

	public void debug(String s) {
		System.out.println("[RegionGenerator] " + s);
	}

	public void generate(RegionData data) {
		HeightMapGenerator.minValue = -5;
		HeightMapGenerator.maxValue = 60;
		HeightMapGenerator.init();
		HeightMapGenerator.seed = seed[0];
		// BlockLocation blockOffset = data.location.getBlockLocation();
		SectorLocation sectorOffset = data.location.getSectorLocation();
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
				SectorData sector = new SectorData();
				sector.location = new SectorLocation(sectorOffset.x + x, sectorOffset.z + z);
				sector.heightMap = new HeightMap(16, 16);
				sector.chunkData = new ChunkData[16];
				for (int y = 0; y < 16; y++) {
					// CREATE CHUNK
					ChunkData chunk = new ChunkData();
					chunk.location = new ChunkLocation(sector.location.x, y, sector.location.z);
					BlockLocation chunkOffset = chunk.location.getBlockLocation();
					for (int inChunkX = 0; inChunkX < 16; inChunkX++) {
						for (int inChunkZ = 0; inChunkZ < 16; inChunkZ++) {
							int height = data.heightMap.getHeight(x * 16 + inChunkX, z * 16 + inChunkZ);
							// System.out.println("height: " + height);
							sector.heightMap.setHeight(inChunkX, inChunkZ, height);
							BlockLocation blockOffset = new BlockLocation();
							blockOffset.x = chunkOffset.x + inChunkX;
							blockOffset.z = chunkOffset.z + inChunkZ;
							for (int inChunkY = 0; inChunkY < 16; inChunkY++) {
								blockOffset.y = chunkOffset.y + inChunkY;
								// height = sector.heightMap.getHeight(inChunkX,
								// inChunkZ);
								if (blockOffset.y > height) {
									// System.out.println("--- height: " +
									// height + " block:" + blockOffset);
									chunk.setEmpty(blockOffset, true);
								} else {
									if (blockOffset.y > 0) {
										// System.out.println("+++ height: " +
										// height + " block:" + blockOffset);
										// System.out.println("not empty, height: "
										// + height);
									}
									chunk.setEmpty(blockOffset, false);
								}
							}
						}
					}
					chunk.compact();
					sector.chunkData[y] = chunk;
				}
				data.sectorData[x][z] = sector;
			}
		}
	}
}
