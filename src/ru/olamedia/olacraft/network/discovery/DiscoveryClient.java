package ru.olamedia.olacraft.network.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import ru.olamedia.tasks.Task;

public class DiscoveryClient extends Task {
	private static DatagramSocket c;
	public static List<InetAddress> list = new ArrayList<InetAddress>();
	private static List<DiscoveryListener> listeners = new ArrayList<DiscoveryListener>();

	public void addHostListener(DiscoveryListener listener) {
		listeners.add(listener);
	}

	public static void discovery() {
		list.clear();
		try {
			list.add(InetAddress.getByName("127.0.0.1"));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		// Find the server using UDP broadcast
		try {
			// Open a random port to send the package
			c = new DatagramSocket();
			c.setBroadcast(true);
			c.setSoTimeout(3000);

			byte[] sendData = "DISCOVER_OLACRAFTSERVER_REQUEST".getBytes();

			// Try the 255.255.255.255 first
			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
						InetAddress.getByName("255.255.255.255"), DiscoveryThread.port);
				c.send(sendPacket);
				System.out.println(DiscoveryClient.class.getName()
						+ ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
			} catch (Exception e) {
			}

			// Broadcast the message over all the network interfaces
			@SuppressWarnings("rawtypes")
			Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue; // Don't want to broadcast to the loopback
								// interface
				}

				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null) {
						continue;
					}

					// Send the broadcast package!
					try {
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
						c.send(sendPacket);
					} catch (Exception e) {
					}

					System.out.println(DiscoveryClient.class.getName() + ">>> Request packet sent to: "
							+ broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
				}
			}

			System.out.println(DiscoveryClient.class.getName()
					+ ">>> Done looping over all network interfaces. Now waiting for a reply!");
			while (true) {
				// Wait for a response
				byte[] recvBuf = new byte[15000];
				DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
				c.receive(receivePacket);

				// We have a response
				System.out.println(DiscoveryClient.class.getName() + ">>> Broadcast response from server: "
						+ receivePacket.getAddress().getHostAddress());

				// Check if the message is correct
				String message = new String(receivePacket.getData()).trim();
				if (message.equals("DISCOVER_OLACRAFTSERVER_RESPONSE")) {
					list.add(receivePacket.getAddress());
					for (DiscoveryListener l : listeners) {
						l.onHost(receivePacket.getAddress());
					}
					// DO SOMETHING WITH THE SERVER'S IP (for example, store it
					// in
					// your controller)
					// Controller_Base.setServerIp(receivePacket.getAddress());
				}
			}
		} catch (SocketTimeoutException ex) {
			// no hosts were discovered
		} catch (IOException ex) {
			// no hosts were discovered
		} finally {
			// Close the port!
			if (null != c) {
				c.close();
			}
			for (DiscoveryListener l : listeners) {
				l.onHost(null); // end of list marker
			}
		}
	}

	@Override
	public void run() {
//		while (!shouldStop()) {
			discovery();
		// try {
		// wait(3000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
	}

	public void refresh() {
	}

	public static DiscoveryClient getInstance() {
		return DiscoveryThreadHolder.INSTANCE;
	}

	private static class DiscoveryThreadHolder {
		private static final DiscoveryClient INSTANCE = new DiscoveryClient();
	}
}
