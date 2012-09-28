package ru.olamedia.olacraft.world.dataProvider;

import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.data.RegionData;
import ru.olamedia.olacraft.world.data.SectorData;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.location.RegionLocation;
import ru.olamedia.olacraft.world.location.SectorLocation;

abstract public class AbstractChunkDataProvider {
	/**
	 * is data already available or we should wait
	 * 
	 * @param RegionLocation
	 * @return
	 */
	public boolean isChunkAvailable(ChunkLocation chunkLocation) {
		return this.isRegionAvailable(chunkLocation.getRegionLocation());
	}

	public boolean isSectorAvailable(SectorLocation sectorLocation) {
		return this.isRegionAvailable(sectorLocation.getRegionLocation());
	}

	abstract public boolean isRegionAvailable(RegionLocation regionLocation);

	/**
	 * we need this chunk now, send request to server or preload
	 * 
	 * @param RegionLocation
	 */
	public void loadChunk(ChunkLocation chunkLocation) {
		//System.out.println("loadChunk(" + chunkLocation + ")");
		this.loadRegion(chunkLocation.getRegionLocation());
		//System.out.println("loadChunk(" + chunkLocation + ")--");
	}

	public void loadSector(SectorLocation sectorLocation) {
		this.loadRegion(sectorLocation.getRegionLocation());
	}

	abstract public void loadRegion(RegionLocation regionLocation);

	/**
	 * Get data if already available
	 * 
	 * @param RegionLocation
	 * @return
	 */
	public ChunkData getChunk(ChunkLocation chunkLocation) {
		return this.getRegion(chunkLocation.getRegionLocation()).getChunkData(chunkLocation);
	}

	public SectorData getSector(SectorLocation sectorLocation) {
		return this.getRegion(sectorLocation.getRegionLocation()).getSectorData(sectorLocation);
	}

	abstract public RegionData getRegion(RegionLocation regionLocation);

}
