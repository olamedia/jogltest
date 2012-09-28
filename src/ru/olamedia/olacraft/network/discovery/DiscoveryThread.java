package ru.olamedia.olacraft.network.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class DiscoveryThread extends Thread {
	public DiscoveryThread() {
		super();
		try {
			socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			socket.setSoTimeout(1000);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public DiscoveryThread(String name) {
		this();
		setName(name);
	}

	private DatagramSocket socket;
	public static int port = 26003;

	@Override
	public void run() {
		try {
			System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");
			while (true) {

				// Receive a packet
				byte[] recvBuf = new byte[15000];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				try {
					socket.receive(packet);

					// Packet received
					System.out.println(getClass().getName() + ">>>Discovery packet received from: "
							+ packet.getAddress().getHostAddress());
					System.out.println(getClass().getName() + ">>>Packet received; data: "
							+ new String(packet.getData()));

					// See if the packet holds the right command (message)
					String message = new String(packet.getData()).trim();
					if (message.equals("DISCOVER_OLACRAFTSERVER_REQUEST")) {
						byte[] sendData = "DISCOVER_OLACRAFTSERVER_RESPONSE".getBytes();

						// Send a response
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(),
								packet.getPort());
						socket.send(sendPacket);

						System.out.println(getClass().getName() + ">>>Sent packet to: "
								+ sendPacket.getAddress().getHostAddress());
					}
				} catch (SocketTimeoutException ex) {

				}
				try {
					Thread.sleep(100);
				} catch (IllegalMonitorStateException ex) {
					ex.printStackTrace();
				} catch (InterruptedException ex) {
					socket.close();
					Thread.currentThread().interrupt(); // very important
					break;
				}
			}
		} catch (IOException ex) {
			socket.close();
		}
	}

	public static DiscoveryThread getInstance() {
		return DiscoveryThreadHolder.INSTANCE;
	}

	private static class DiscoveryThreadHolder {

		private static final DiscoveryThread INSTANCE = new DiscoveryThread();
	}

}
