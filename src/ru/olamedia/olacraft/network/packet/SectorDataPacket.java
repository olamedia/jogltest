package ru.olamedia.olacraft.network.packet;

import ru.olamedia.olacraft.world.data.SectorData;

public class SectorDataPacket implements IPacket {
	public SectorDataPacket() {
	}

	public SectorDataPacket(SectorData data) {
		this.data = data;
	}

	public SectorData data;
}
