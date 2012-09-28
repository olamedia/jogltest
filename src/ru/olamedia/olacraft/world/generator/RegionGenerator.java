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
		HeightMapGenerator.maxValue = 100;
		HeightMapGenerator.init();
		HeightMapGenerator.seed = seed[0];
		BlockLocation offset = data.location.getBlockLocation();
		// int[][] heightMap =
		// HeightMapGenerator.getHeightMap(data.location.getBlockLocation().x,
		// data.location.getBlockLocation().z, 256, 256);
		debug(data.location.toString());
		data.heightMap = HeightMapGenerator.getHeightMap(data.location);
		//debug(data.heightMap.toString());
		data.sectorData = new SectorData[16][16];
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				// CREATE SECTOR
				SectorData sector = new SectorData();
				sector.location = new SectorLocation(offset.x + x * 16, offset.z + z * 16);
				sector.heightMap = new HeightMap(16, 16);
				sector.chunkData = new ChunkData[16];
				for (int y = 0; y < 16; y++) {
					// CREATE CHUNK
					ChunkData chunk = new ChunkData();
					chunk.location = new ChunkLocation(sector.location.x, y, sector.location.z);
					int chunkOffsetY = y * 16 - 128;
					for (int inChunkX = 0; inChunkX < 16; inChunkX++) {
						for (int inChunkZ = 0; inChunkZ < 16; inChunkZ++) {
							int height = data.heightMap.getHeight(x * 16 + inChunkX, z * 16 + inChunkZ);
							//System.out.println("height: " + height);
							sector.heightMap.setHeight(inChunkX, inChunkZ, height);
							for (int inChunkY = 0; inChunkY < 16; inChunkY++) {
								//height = sector.heightMap.getHeight(inChunkX, inChunkZ);
								int id = ChunkData.getId(inChunkX, inChunkY, inChunkZ);
								if (chunkOffsetY + inChunkY > height) {
									chunk.setEmpty(id, true);
								} else {
									//System.out.println("not empty, height: " + height);
									chunk.setEmpty(id, false);
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
