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

	public ChunkLocation(ChunkLocation loc) {
		this.x = loc.x;
		this.y = loc.y;
		this.z = loc.z;
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

	public BlockLocation getBlockLocation() {
		return new BlockLocation(Chunk.rev(x), Chunk.rev(y) - 128, Chunk.rev(z));
	}

	public ChunkLocation getNeighbor(int dx, int dy, int dz) {
		return new ChunkLocation(x + dx, y + dy, z + dz);
	}

	public ChunkLocation getLeft() {
		return getNeighbor(-1, 0, 0);
	}

	public ChunkLocation getRight() {
		return getNeighbor(1, 0, 0);
	}

	public ChunkLocation getTop() {
		return getNeighbor(0, 1, 0);
	}

	public ChunkLocation getBottom() {
		return getNeighbor(0, -1, 0);
	}

	public ChunkLocation getBack() {
		return getNeighbor(0, 0, -1);
	}

	public ChunkLocation getFront() {
		return getNeighbor(0, 0, 1);
	}
}
