package ru.olamedia.olacraft.world.location;

import java.io.Serializable;

import ru.olamedia.olacraft.world.chunk.Chunk;

public class ChunkLocation implements Serializable {
	private static final long serialVersionUID = -3620722885522274470L;

	public ChunkLocation() {

	}

	public ChunkLocation(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int x;
	public int y;
	public int z;

	public SectorLocation getSectorLocation() {
		return new SectorLocation(x, z);
	}

	public RegionLocation getRegionLocation() {
		return new RegionLocation(Chunk.v(x), Chunk.v(z));
	}

	public String toString() {
		return "chunkLocation[" + x + "," + y + "," + z + "]";
	}
	/*
	 * public BlockSlice getSlice(){
	 * 
	 * }
	 */
}
