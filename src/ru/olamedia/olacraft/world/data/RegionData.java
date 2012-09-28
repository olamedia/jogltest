package ru.olamedia.olacraft.world.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.location.RegionLocation;
import ru.olamedia.olacraft.world.location.SectorLocation;

/**
 * Region is a 16x16 range of sectors. (256x256x256 blocks)
 * 
 * @author olamedia
 * 
 */
public class RegionData implements Serializable {
	private static final long serialVersionUID = 7449677895073874520L;
	public RegionLocation location;
	public HeightMap heightMap = new HeightMap(256, 256);
	public SectorData[][] sectorData = new SectorData[16][16];

	public void writeTo(OutputStream stream) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(stream);
		out.writeObject(this);
		out.close();
	}

	public static RegionData loadFrom(InputStream stream) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(stream);
		RegionData data = (RegionData) in.readObject();
		in.close();
		return data;
	}

	public static RegionData createEmpty(RegionLocation location) {
		RegionData data = new RegionData();
		data.location = location;
		return data;
	}

	public ChunkData getChunkData(ChunkLocation chunkLocation) {
		SectorData sector = getSectorData(chunkLocation.getSectorLocation());
		int y = Chunk.in(chunkLocation.y + 128); // minHeight = -128
		return sector.chunkData[y];
	}

	public SectorData getSectorData(SectorLocation sectorLocation) {
		int x = Chunk.in(sectorLocation.x);
		int z = Chunk.in(sectorLocation.z);
		return sectorData[x][z];
	}
}
