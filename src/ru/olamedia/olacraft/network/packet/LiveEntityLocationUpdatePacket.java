package ru.olamedia.olacraft.network.packet;

/**
 * Client sends this packet every time location changed
 * Server fills connectionId and sends back to every connection
 * 
 * @author olamedia
 * 
 */
public class LiveEntityLocationUpdatePacket implements IPacket {
	public float x;
	public float y;
	public float z;
	public int connectionId; // filled by server only
}
