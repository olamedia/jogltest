package ru.olamedia.olacraft.world.location;

import java.io.Serializable;

import ru.olamedia.olacraft.world.chunk.Chunk;

public class SectorLocation implements Serializable {
	private static final long serialVersionUID = 4500216114186249375L;

	public SectorLocation() {

	}

	public SectorLocation(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public int x;
	public int z;

	public RegionLocation getRegionLocation() {
		return new RegionLocation(Chunk.v(x), Chunk.v(z));
	}

	public ChunkLocation getChunkLocation() {
		return new ChunkLocation(Chunk.rev(x), 0, Chunk.rev(z));
	}

	public BlockLocation getBlockLocation() {
		return new BlockLocation(Chunk.rev(Chunk.rev(x)), 0, Chunk.rev(Chunk.rev(z)));
	}

	public String toString() {
		return "sectorLocation[" + x + "," + z + "]";
	}
}
