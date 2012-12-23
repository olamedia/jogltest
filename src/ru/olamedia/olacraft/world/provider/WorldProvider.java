package ru.olamedia.olacraft.world.provider;

import ru.olamedia.olacraft.game.SpawnLocation;
import ru.olamedia.olacraft.world.WorldInfo;
import ru.olamedia.olacraft.world.block.Block;
import ru.olamedia.olacraft.world.block.BlockRegistry;
import ru.olamedia.olacraft.world.blockTypes.AbstractBlockType;
import ru.olamedia.olacraft.world.blockTypes.BlockType;
import ru.olamedia.olacraft.world.blockTypes.DirtBlockType;
import ru.olamedia.olacraft.world.blockTypes.GrassBlockType;
import ru.olamedia.olacraft.world.blockTypes.GravelBlockType;
import ru.olamedia.olacraft.world.blockTypes.IceBlockType;
import ru.olamedia.olacraft.world.blockTypes.SnowBlockType;
import ru.olamedia.olacraft.world.blockTypes.TallGrassBlockType;
import ru.olamedia.olacraft.world.blockTypes.WaterBlockType;
import ru.olamedia.olacraft.world.blockTypes.WheatBlockType;
import ru.olamedia.olacraft.world.blockTypes.stone.BrecciaStoneBlockType;
import ru.olamedia.olacraft.world.blockTypes.stone.ChertStoneBlockType;
import ru.olamedia.olacraft.world.blockTypes.stone.CoalStoneBlockType;
import ru.olamedia.olacraft.world.blockTypes.stone.ConglomerateStoneBlockType;
import ru.olamedia.olacraft.world.blockTypes.stone.LimestoneStoneBlockType;
import ru.olamedia.olacraft.world.blockTypes.stone.SandstoneStoneBlockType;
import ru.olamedia.olacraft.world.blockTypes.stone.ShaleStoneBlockType;
import ru.olamedia.olacraft.world.blockTypes.stone.SiltstoneStoneBlockType;
import ru.olamedia.olacraft.world.chunk.ChunkUnavailableException;
import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.data.RegionData;
import ru.olamedia.olacraft.world.dataProvider.AbstractChunkDataProvider;
import ru.olamedia.olacraft.world.drop.DroppedEntity;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.location.RegionLocation;

/**
 * Provides ALL information about world (world height, block types, chunks, mobs, spawn locations etc)
 *
 */
public class WorldProvider {
	private WorldInfo info = new WorldInfo();
	private BlockRegistry types = new BlockRegistry();
	private AbstractChunkDataProvider dataProvider;
	
	public WorldProvider() {
		registerBlockTypes(false);
	}

	public void registerTextures(){
		registerBlockTypes(true);
	}
	
	protected void registerBlockType(AbstractBlockType t, boolean registerTextures){
		if (registerTextures) {
			t.getBackTexture();
			t.getBottomTexture();
			t.getFrontTexture();
			t.getLeftTexture();
			t.getRightTexture();
			t.getTopTexture();
		} else {
			types.registerBlockType(t);
		}
	}
	
	protected void registerBlockTypes(boolean registerTextures) {
		registerBlockType(new GrassBlockType(), registerTextures);
		registerBlockType(new ConglomerateStoneBlockType(), registerTextures);
		registerBlockType(new LimestoneStoneBlockType(), registerTextures);
		registerBlockType(new ChertStoneBlockType(), registerTextures);
		registerBlockType(new SiltstoneStoneBlockType(), registerTextures);
		registerBlockType(new SandstoneStoneBlockType(), registerTextures);
		registerBlockType(new ShaleStoneBlockType(), registerTextures);
		registerBlockType(new CoalStoneBlockType(), registerTextures);
		registerBlockType(new BrecciaStoneBlockType(), registerTextures);
		registerBlockType(new DirtBlockType(), registerTextures);
		registerBlockType(new GravelBlockType(), registerTextures);
		registerBlockType(new WheatBlockType(), registerTextures);
		registerBlockType(new WaterBlockType(), registerTextures);
		registerBlockType(new IceBlockType(), registerTextures);
		registerBlockType(new SnowBlockType(), registerTextures);
		registerBlockType(new TallGrassBlockType(), registerTextures);
	}

	public WorldInfo getInfo() {
		return info;
	}

	public BlockRegistry getTypeRegistry() {
		return types;
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
		dataProvider.setTypeRegistry(this.types);
	}

	public SpawnLocation getSpawnLocation(int connectionId) {
		dataProvider.setTypeRegistry(this.types);
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
			if (!chunk.isEmpty(spawnLocation)) {
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

	public boolean renderBottom(int x, int y, int z) throws ChunkUnavailableException {
		return (!isEmptyBlock(x, y, z)) && (isEmptyBlock(x, y - 1, z));
	}

	public boolean renderTop(int x, int y, int z) throws ChunkUnavailableException {
		// System.out.println("Check render top " + y + "[" + x + " " + y + " "
		// + z + "]" + !isEmptyBlock(x, y, z)
		// + " && " + isEmptyBlock(x, y + 1, z));
		return (!isEmptyBlock(x, y, z)) && (isEmptyBlock(x, y + 1, z));
	}

	public boolean renderLeft(int x, int y, int z) throws ChunkUnavailableException {
		return (!isEmptyBlock(x, y, z)) && (isEmptyBlock(x - 1, y, z));
	}

	public boolean renderRight(int x, int y, int z) throws ChunkUnavailableException {
		return (!isEmptyBlock(x, y, z)) && (isEmptyBlock(x + 1, y, z));
	}

	public boolean renderBack(int x, int y, int z) throws ChunkUnavailableException {
		return (!isEmptyBlock(x, y, z)) && (isEmptyBlock(x, y, z - 1));
	}

	public boolean renderFront(int x, int y, int z) throws ChunkUnavailableException {
		return (!isEmptyBlock(x, y, z)) && (isEmptyBlock(x, y, z + 1));
	}

	public boolean isEmptyBlock(int x, int y, int z) throws ChunkUnavailableException {
		BlockLocation blockLocation = new BlockLocation(x, y, z);

		if (isChunkAvailable(blockLocation.getChunkLocation())) {
			ChunkData data = dataProvider.getChunk(blockLocation.getChunkLocation());
			if (null != data) {
				return data.isEmpty(blockLocation);
			} else {
				System.out.println("chunk null " + x + " " + y + " " + z);
			}
		} else {
			throw new ChunkUnavailableException();
		}
		return true;
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
		return new Block(this, x, y, z);
	}

	public ChunkData getChunk(ChunkLocation chunkLocation) {
		return dataProvider.getChunk(chunkLocation);
	}

	public AbstractBlockType getBlockTypeById(int id) {
		return types.getBlockType(id);
	}

	public boolean isOpaque(int x, int y, int z) throws ChunkUnavailableException {
		BlockLocation blockLocation = new BlockLocation(x, y, z);
		if (isChunkAvailable(blockLocation.getChunkLocation())) {
			ChunkData data = dataProvider.getChunk(blockLocation.getChunkLocation());
			if (null != data) {
				int id = ChunkData.ClampID(x, y, z);
				if (data.isEmpty(id)) {
					return false;
				}
				return types.isOpaque(data.types[id]);
			} else {
				System.out.println("chunk null " + x + " " + y + " " + z);
			}
		} else {
			throw new ChunkUnavailableException();
		}
		return false;
	}

	public boolean hideTouchedSides(int x, int y, int z, int typeid) throws ChunkUnavailableException {
		BlockLocation blockLocation = new BlockLocation(x, y, z);
		if (isChunkAvailable(blockLocation.getChunkLocation())) {
			ChunkData data = dataProvider.getChunk(blockLocation.getChunkLocation());
			if (null != data) {
				int id = ChunkData.ClampID(x, y, z);
				if (data.isEmpty(id)) {
					return false;
				}
				if (data.types[id] == typeid) {
					return types.getBlockType(data.types[id]).hideTouchedSides();
				}
				return types.isOpaque(data.types[id]);
			} else {
				System.out.println("chunk null " + x + " " + y + " " + z);
			}
		} else {
			throw new ChunkUnavailableException();
		}
		return false;
	}

	public boolean canMoveThrough(int x, int y, int z) throws ChunkUnavailableException {
		BlockLocation blockLocation = new BlockLocation(x, y, z);
		if (isChunkAvailable(blockLocation.getChunkLocation())) {
			ChunkData data = dataProvider.getChunk(blockLocation.getChunkLocation());
			if (null != data) {
				int id = ChunkData.ClampID(x, y, z);
				if (data.isEmpty(id)) {
					return true;
				}
				return types.canMoveThrough(data.types[id]);
			} else {
				System.out.println("chunk null " + x + " " + y + " " + z);
			}
		} else {
			throw new ChunkUnavailableException();
		}
		return false;
	}

	public void dropBlock(BlockLocation location, AbstractBlockType type) throws ChunkUnavailableException {
		if (!isChunkAvailable(location.getChunkLocation())) {
			throw new ChunkUnavailableException();
		}
		ChunkData data = dataProvider.getChunk(location.getChunkLocation());
		data.addDroppedEntity(new DroppedEntity(location, type, 1));
	}

}
