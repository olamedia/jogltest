package ru.olamedia.olacraft.world.location;

import java.io.Serializable;

import ru.olamedia.olacraft.world.chunk.Chunk;

public class BlockLocation implements Serializable {
	private static final long serialVersionUID = -4987461467575474762L;
	public int x;
	public int y;
	public int z;

	public BlockLocation() {
	}

	public BlockLocation(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public ChunkLocation getChunkLocation() {
		return new ChunkLocation(Chunk.v(x), Chunk.v(y + 128), Chunk.v(z));
	}

	public RegionLocation getRegionLocation() {
		return new RegionLocation(Chunk.v(Chunk.v(x)), Chunk.v(Chunk.v(z)));
	}

	public SectorLocation getSectorLocation() {
		return new SectorLocation(Chunk.v(x), Chunk.v(z));
	}

	public String toString() {
		return "blockLocation[" + x + "," + y + "," + z + "]";
	}
}
