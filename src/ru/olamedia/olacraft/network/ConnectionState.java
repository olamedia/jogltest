package ru.olamedia.olacraft.network;

public class ConnectionState {
	private int state;

	public ConnectionState(int state) {
		this.state = state;
	}

	public static ConnectionState STATE_DISCONNECTED = new ConnectionState(0);
	public static ConnectionState STATE_CONNECTED = new ConnectionState(1);
	public static ConnectionState STATE_CONNECTING = new ConnectionState(2);

	public boolean isConnected() {
		return state == 1;
	}
}
