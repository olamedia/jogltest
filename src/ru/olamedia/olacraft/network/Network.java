package ru.olamedia.olacraft.network;

import java.util.BitSet;

import ru.olamedia.olacraft.network.packet.ChunkDataPacket;
import ru.olamedia.olacraft.network.packet.ConnectionPacket;
import ru.olamedia.olacraft.network.packet.ConnectionRequestPacket;
import ru.olamedia.olacraft.network.packet.GetChunkDataPacket;
import ru.olamedia.olacraft.network.packet.GetRegionPacket;
import ru.olamedia.olacraft.network.packet.LiveEntityLocationUpdatePacket;
import ru.olamedia.olacraft.network.packet.RegionDataPacket;
import ru.olamedia.olacraft.network.packet.SpawnPacket;
import ru.olamedia.olacraft.network.packet.SpawnRequestPacket;
import ru.olamedia.olacraft.network.packet.WorldInfoPacket;
import ru.olamedia.olacraft.world.WorldInfo;
import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.data.ChunkLightData;
import ru.olamedia.olacraft.world.data.HeightMap;
import ru.olamedia.olacraft.world.data.RegionData;
import ru.olamedia.olacraft.world.data.SectorData;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.location.RegionLocation;
import ru.olamedia.olacraft.world.location.SectorLocation;

import com.esotericsoftware.kryo.Kryo;

public class Network {
	public static void registerPackets(Kryo kryo) {
		// types
		kryo.register(boolean.class);
		kryo.register(boolean[].class);
		kryo.register(byte.class);
		kryo.register(byte[].class);
		kryo.register(byte[][].class);
		kryo.register(int.class);
		kryo.register(int[].class);
		kryo.register(float.class);
		kryo.register(float[].class);
		kryo.register(long.class);
		kryo.register(long[].class);
		kryo.register(BitSet.class);
		kryo.register(HeightMap.class);
		kryo.register(WorldInfo.class);
		kryo.register(WorldInfoPacket.class);
		kryo.register(BlockLocation.class);
		kryo.register(ChunkLocation.class);
		kryo.register(SectorLocation.class);
		kryo.register(RegionLocation.class);
		kryo.register(ChunkData.class);
		kryo.register(ChunkData[].class);
		kryo.register(SectorData.class);
		kryo.register(SectorData[].class);
		kryo.register(SectorData[][].class);
		kryo.register(RegionData.class);
		kryo.register(GetRegionPacket.class);
		kryo.register(RegionDataPacket.class);
		
		
		kryo.register(ChunkLightData.class);
		kryo.register(ChunkData.class);
		// packets
		kryo.register(ConnectionRequestPacket.class);
		kryo.register(ConnectionPacket.class);
		kryo.register(SpawnRequestPacket.class);
		kryo.register(SpawnPacket.class);
		kryo.register(GetChunkDataPacket.class);
		kryo.register(ChunkDataPacket.class);
		kryo.register(LiveEntityLocationUpdatePacket.class);
	}
}
