package ru.olamedia.olacraft.world.location;

import java.io.Serializable;

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

	public BlockLocation getBlockLocation() {
		return new BlockLocation(x * 256, 0, z * 256);
	}
}
