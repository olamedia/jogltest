package ru.olamedia.olacraft.network.packet;

import ru.olamedia.olacraft.world.data.ChunkData;

public class ChunkDataPacket implements IPacket {
	public int chunkX;
	public int chunkY;
	public int chunkZ;
	public ChunkData data;
}
