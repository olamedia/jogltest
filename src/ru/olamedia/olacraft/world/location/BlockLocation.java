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

	public boolean isChunkEdge() {
		int cx = Chunk.in(x);
		int cy = Chunk.in(y);
		int cz = Chunk.in(z);
		return (cx == 0 || cy == 0 || cz == 0 || cx == 15 || cy == 15 || cz == 15);
	}

	public boolean isChunkLeftEdge() {
		return Chunk.in(x) == 0;
	}

	public boolean isChunkRightEdge() {
		return Chunk.in(x) == 15;
	}

	public boolean isChunkTopEdge() {
		return Chunk.in(y) == 15;
	}

	public boolean isChunkBottomEdge() {
		return Chunk.in(y) == 0;
	}

	public boolean isChunkFrontEdge() {
		return Chunk.in(z) == 15;
	}

	public boolean isChunkBackEdge() {
		return Chunk.in(z) == 0;
	}

	public BlockLocation(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockLocation(float x, float y, float z) {
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
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
