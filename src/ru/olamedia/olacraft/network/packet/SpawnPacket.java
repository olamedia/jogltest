package ru.olamedia.olacraft.network.packet;

/**
 * Client sends SpawnRequestPacket on connect
 * Server fills location, connectionId and sends SpawnPacket to every connection
 * 
 * @author olamedia
 * 
 */
public class SpawnPacket implements IPacket {
	public float x;
	public float y;
	public float z;
	public int connectionId; // filled by server only
}
