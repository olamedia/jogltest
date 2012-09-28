package ru.olamedia.olacraft.world.provider;

import ru.olamedia.olacraft.game.SpawnLocation;
import ru.olamedia.olacraft.world.WorldInfo;
import ru.olamedia.olacraft.world.block.Block;
import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.data.RegionData;
import ru.olamedia.olacraft.world.dataProvider.AbstractChunkDataProvider;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.location.RegionLocation;

public class WorldProvider {
	private WorldInfo info = new WorldInfo();
	private AbstractChunkDataProvider dataProvider;

	public WorldInfo getInfo() {
		return info;
	}

	public void setInfo(WorldInfo worldInfo) {
		info = worldInfo;
	}

	/*
	 * public AbstractChunkDataProvider getChunkDataProvider() {
	 * return dataProvider;
	 * }
	 */

	public void setChunkDataProvider(AbstractChunkDataProvider provider) {
		dataProvider = provider;
	}

	public SpawnLocation getSpawnLocation(int connectionId) {
		SpawnLocation l = new SpawnLocation();
		int maxShift = 10;
		l.x = (int) (maxShift - Math.random() * 2 * maxShift);
		l.z = (int) (maxShift - Math.random() * 2 * maxShift);
		System.out.print("Searching spawn Y");
		BlockLocation spawnLocation = new BlockLocation(l.x, 0, l.z);
		for (int y = info.maxHeight; y > info.minHeight; y--) {
			// search for floor block
			spawnLocation.y = y;
			System.out.print(y + ". ");
			ChunkData chunk = dataProvider.getChunk(spawnLocation.getChunkLocation());
			boolean notEmpty = !chunk.isEmpty(ChunkData.getId(Chunk.in(spawnLocation.x), Chunk.in(spawnLocation.y),
					Chunk.in(spawnLocation.z)));
			if (notEmpty) {
				// found
				l.y = y + 1;
				System.out.println("found: " + y);
				return l;
			}
		}
		System.out.println("not found ");
		// not found
		l.y = info.maxHeight;
		return l;
	}

	public boolean renderBottom(int x, int y, int z) {
		return (!isEmptyBlock(x, y, z)) && (isEmptyBlock(x, y - 1, z));
	}

	public boolean renderTop(int x, int y, int z) {
		return (!isEmptyBlock(x, y, z)) && (isEmptyBlock(x, y + 1, z));
	}

	public boolean renderLeft(int x, int y, int z) {
		return (!isEmptyBlock(x, y, z)) && (isEmptyBlock(x - 1, y, z));
	}

	public boolean renderRight(int x, int y, int z) {
		return (!isEmptyBlock(x, y, z)) && (isEmptyBlock(x + 1, y, z));
	}

	public boolean renderBack(int x, int y, int z) {
		return (!isEmptyBlock(x, y, z)) && (isEmptyBlock(x, y, z - 1));
	}

	public boolean renderFront(int x, int y, int z) {
		return (!isEmptyBlock(x, y, z)) && (isEmptyBlock(x, y, z + 1));
	}

	public boolean isEmptyBlock(int x, int y, int z) {
		BlockLocation blockLocation = new BlockLocation(x, y, z);

		if (isChunkAvailable(blockLocation.getChunkLocation())) {
			ChunkData data = dataProvider.getChunk(blockLocation.getChunkLocation());
			if (null != data) {
				return data.isEmpty(blockLocation);
			}
		}
		return false;
	}

	public void requestChunk(int chunkX, int chunkY, int chunkZ) {
		ChunkLocation chunkLocation = new ChunkLocation(chunkX, chunkY, chunkZ);
		// getChunkDataProvider().loadRegion(chunkLocation.getRegionLocation());
		loadChunk(chunkLocation);
	}

	public boolean isChunkAvailable(ChunkLocation chunkLocation) {
		return dataProvider.isChunkAvailable(chunkLocation);
	}

	public boolean isAvailableBlock(int x, int y, int z) {
		BlockLocation blockLocation = new BlockLocation(x, y, z);
		return dataProvider.isChunkAvailable(blockLocation.getChunkLocation());
	}

	public void loadChunk(ChunkLocation chunkLocation) {
		dataProvider.loadChunk(chunkLocation);
	}

	public RegionData getRegion(RegionLocation regionLocation) {
		return dataProvider.getRegion(regionLocation);
	}

	public Block getBlock(int x, int y, int z) {
		BlockLocation blockLocation = new BlockLocation(x, y, z);
		return new Block(this, x, y, z);
	}

	public ChunkData getChunk(ChunkLocation chunkLocation) {
		return dataProvider.getChunk(chunkLocation);
	}
}
