package ru.olamedia.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.network.ConnectionState;
import ru.olamedia.olacraft.network.ConnectionStateListener;
import ru.olamedia.olacraft.network.discovery.DiscoveryClient;
import ru.olamedia.olacraft.network.discovery.DiscoveryListener;

public class MainMenu extends JPanel implements ActionListener {
	private JButton startButton;
	private JButton startServerButton;
	private JButton discoveryButton;
	private DynamicJList hosts;
	private DiscoveryClient discoveryClient = DiscoveryClient.getInstance();
	private Thread discoveryClientThread;
	private static final long serialVersionUID = -271797500986576805L;

	private void stylizeButton(JButton b) {
		Border line = new LineBorder(Color.BLACK);
		Border margin = new EmptyBorder(5, 15, 5, 15);
		Border compound = new CompoundBorder(line, margin);
		b.setBackground(new Color(1f, 1f, 1f, 0.8f));
		b.setBorder(compound);
	}

	private static boolean DEBUG = true;

	@SuppressWarnings("unused")
	private void debug(String s) {
		if (DEBUG) {
			System.out.println("[MainMenu] " + s);
		}
	}

	public MainMenu() {
		setSize(GameFrame.getWidth(), GameFrame.getHeight());
		setOpaque(false);
		// setBackground(new Color(0f, 0f, 0f, 0.8f));
		startButton = new JButton();
		startButton.setSize(500, 40);
		startButton.setLocation((GameFrame.getWidth() - 500) / 2, 200);
		startButton.setText("JOIN GAME");
		startButton.setEnabled(false);
		startButton.setActionCommand("connect");
		startButton.addActionListener(this);
		stylizeButton(startButton);
		startServerButton = new JButton();
		startServerButton.setSize(500, 40);
		startServerButton.setLocation((GameFrame.getWidth() - 500) / 2, 260);
		startServerButton.setText("START SERVER");
		startServerButton.setActionCommand("start server");
		startServerButton.addActionListener(this);
		stylizeButton(startServerButton);
		discoveryButton = new JButton();
		discoveryButton.setSize(500, 40);
		discoveryButton.setLocation((GameFrame.getWidth() - 500) / 2, 320);
		discoveryButton.setText("REFRESH");
		discoveryButton.setActionCommand("discovery lan");
		discoveryButton.addActionListener(this);
		stylizeButton(discoveryButton);
		hosts = new DynamicJList();
		hosts.setSize(500, 150);
		hosts.setLocation((GameFrame.getWidth() - 500) / 2, 10);
		hosts.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (hosts.getContents().isEmpty()) {
					Game.client.setHostname("127.0.0.1");
					System.out.println("Selected: none");
					startButton.setEnabled(false);
				} else {
					String host = (String) hosts.getContents().get(e.getFirstIndex());
					Game.client.setHostname(host);
					System.out.println("Selected: " + host);
					startButton.setEnabled(true);
				}
			}
		});
		add(hosts);
		add(startButton);
		add(startServerButton);
		add(discoveryButton);
		// LAN discover
		// InetAddress address = Game.client.discoverHost(54777, 5000);
		// System.out.println(address);
		setLayout(new BorderLayout());
		validate();
		Game.client.addStateListener(new ConnectionStateListener() {
			@Override
			public void onChangeState(ConnectionState state) {
				// debug("Client ConnectionState changed");
				if (state.isConnected()) {
					GameFrame.instance.setGLMode();
					Game.instance.player.captureControls();
					startButton.setText("LEAVE GAME");
				} else {
					GameFrame.instance.setUIMode();
					startButton.setText("JOIN GAME");
				}
			}
		});
		discoveryClient.addHostListener(new DiscoveryListener() {
			@Override
			public void onHost(InetAddress address) {
				if (null == address) {
					discoveryButton.setEnabled(true);
				} else {
					hosts.getContents().addElement(address.getHostAddress());
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("connect")) {
			if (null != Game.instance) {
				Game.instance = null;
			}
			Game.instance = new Game(Game.MODE_MULTIPLAYER);
			if (Game.client.isConnected()) {
				// LEAVE GAME
				Game.client.close();
			} else {
				Game.client.connect();
			}
			Game.instance.start();
		}
		if (cmd.equals("start server")) {
			startServerButton.setEnabled(false);
			if (Game.server.isRunning()) {
				Game.server.stop();
				startServerButton.setText("START SERVER");
				startServerButton.setEnabled(true);
			} else {
				Game.server.start();
				if (Game.server.isRunning()) {
					startServerButton.setText("STOP SERVER");
					startServerButton.setEnabled(true);
				} else {
					startServerButton.setEnabled(true);
				}
			}
		}
		if (cmd.equals("discovery lan")) {
			discoveryButton.setEnabled(false);
			discoveryClientThread = new Thread(DiscoveryClient.getInstance(), "DISCOVERY CLIENT");
			discoveryClientThread.start();
			hosts.getContents().clear();
		}
	}
}
