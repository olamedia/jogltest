package ru.olamedia.olacraft.network.packet;

import ru.olamedia.olacraft.world.WorldInfo;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class WorldInfoPacket implements IPacket {
	public WorldInfoPacket(){
		
	}
	public WorldInfoPacket(WorldProvider worldProvider) {
		info = worldProvider.getInfo();
	}

	public WorldInfo info;
}
