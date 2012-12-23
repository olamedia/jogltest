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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + z;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegionLocation other = (RegionLocation) obj;
		if (x != other.x)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

}
