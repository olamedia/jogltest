package ru.olamedia.olacraft.world.location;

import java.io.Serializable;

import ru.olamedia.olacraft.world.chunk.Chunk;

public class RegionLocation implements Serializable {
	private static final long serialVersionUID = -141619138379029773L;
	public int x;
	public int z;

	public RegionLocation() {
	}

	public RegionLocation(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public String toString() {
		return "regionLocation[" + x + "," + z + "]";
	}

	public String getFilename() {
		return "" + x + "_" + z + ".region";
	}

	public SectorLocation getSectorLocation() {
		return new SectorLocation(Chunk.rev(x), Chunk.rev(z));
	}

	public ChunkLocation getChunkLocation() {
		return new ChunkLocation(Chunk.rev(x), 0, Chunk.rev(z));
	}

	public BlockLocation getBlockLocation() {
		return new BlockLocation(Chunk.rev(Chunk.rev(x)), 0, Chunk.rev(Chunk.rev(z)));
	}
}
