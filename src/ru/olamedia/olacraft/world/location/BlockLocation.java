package ru.olamedia.olacraft.world.location;

import java.io.Serializable;

public class BlockLocation extends Location3i implements Serializable {
	private static final long serialVersionUID = -4987461467575474762L;
	private static int yChunkShift = 128;

	public BlockLocation() {
	}

	public byte getByteX() {
		return (byte) (x & 15);
	}

	public int getChunkX() {
		return (int) (x >> 4);
	}

	public int getRegionX() {
		return (int) (x >> 8);
	}

	public byte getByteY() {
		return (byte) (y & 15);
	}

	public int getChunkY() {
		return (int) ((y + yChunkShift) >> 4);
	}

	public int getRegionY() {
		return (int) ((y + yChunkShift) >> 8);
	}

	public byte getByteZ() {
		return (byte) (z & 15);
	}

	public int getChunkZ() {
		return (int) (z >> 4);
	}

	public int getRegionZ() {
		return (int) (z >> 8);
	}

	public short getId() {
		return IntLocation.id(x, y, z);
	}

	public boolean isChunkEdge() {
		int cx = getByteX();
		int cy = getByteY();
		int cz = getByteZ();
		return (cx == 0 || cy == 0 || cz == 0 || cx == 15 || cy == 15 || cz == 15);
	}

	public boolean isChunkLeftEdge() {
		return getByteX() == 0;
	}

	public boolean isChunkRightEdge() {
		return getByteX() == 15;
	}

	public boolean isChunkTopEdge() {
		return getByteY() == 15;
	}

	public boolean isChunkBottomEdge() {
		return getByteY() == 0;
	}

	public boolean isChunkFrontEdge() {
		return getByteZ() == 15;
	}

	public boolean isChunkBackEdge() {
		return getByteZ() == 0;
	}

	public BlockLocation(int x, int y, int z) {
		super(x, y, z);
	}

	public BlockLocation(float x, float y, float z) {
		super(x, y, z);
	}

	public BlockLocation(Location3i location) {
		super(location);
	}

	public BlockLocation(Location3f location) {
		super(location);
	}

	public ChunkLocation getChunkLocation() {
		return new ChunkLocation(getChunkX(), getChunkY(), getChunkZ());
	}

	public RegionLocation getRegionLocation() {
		return new RegionLocation(getRegionX(), getRegionZ());
	}

	public SectorLocation getSectorLocation() {
		return new SectorLocation(getChunkX(), getChunkZ());
	}

	public String toString() {
		return "blockLocation[" + x + "," + y + "," + z + "]";
	}
}
