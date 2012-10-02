package ru.olamedia.olacraft.network.packet;

import ru.olamedia.olacraft.world.data.RegionData;

public class RegionDataPacket implements IPacket {
	public RegionDataPacket() {
	}

	public RegionDataPacket(RegionData data) {
		this.data = data;
	}

	public RegionData data;
}
