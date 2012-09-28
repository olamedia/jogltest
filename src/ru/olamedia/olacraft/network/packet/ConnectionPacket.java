package ru.olamedia.olacraft.network.packet;

/**
 * Server sends connection packet with assigned connection ID on client connect 
 * 
 * @author olamedia
 *
 */
public class ConnectionPacket implements IPacket{
	public int connectionId;
}
