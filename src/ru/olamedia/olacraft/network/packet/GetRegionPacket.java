package ru.olamedia.olacraft.network.packet;

import ru.olamedia.olacraft.world.location.RegionLocation;

public class GetRegionPacket implements IPacket {
	public RegionLocation location;

	public GetRegionPacket() {

	}

	public GetRegionPacket(RegionLocation location) {
		this.location = location;
	}
}
