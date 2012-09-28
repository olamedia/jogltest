package ru.olamedia.olacraft.network.packet;

import com.esotericsoftware.kryonet.Connection;

public interface IPacketListener {
	public void onPacket(Connection connection, IPacket p);
}
