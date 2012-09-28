package ru.olamedia.olacraft.world.data;

import java.io.Serializable;

import ru.olamedia.olacraft.world.location.SectorLocation;

/**
 * Sector: set of all chunks by one x,z vertical
 * 
 * @author olamedia
 * 
 */
public class SectorData implements Serializable{
	private static final long serialVersionUID = 5304471397211814748L;
	public HeightMap heightMap; // locations of highest nonempty blocks
	public ChunkData[] chunkData; // 256/16 = 16
	public SectorLocation location;

	public static int yIndex(int y) {
		return (y + 128) / 16;
		//  1: (-128 + 128) / 16 = 0
		// ......
		// 15: (-114 + 128) / 16 = 14/16 = 0
		// 16: (-113 + 128) / 16 = 15/16 = 0
		// 17: (-112 + 128) / 16 = 16/16 = 1
	}
	public static SectorData generate(){
		SectorData data = new SectorData();
		data.heightMap = new HeightMap(16, 16);
		data.chunkData = new ChunkData[16];
		return data;
	}
}
