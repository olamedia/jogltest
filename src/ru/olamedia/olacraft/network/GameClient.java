package ru.olamedia.olacraft.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import ru.olamedia.game.GameManager;
import ru.olamedia.liveEntity.LiveEntity;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.network.packet.ConnectionPacket;
import ru.olamedia.olacraft.network.packet.ConnectionRequestPacket;
import ru.olamedia.olacraft.network.packet.IPacket;
import ru.olamedia.olacraft.network.packet.IPacketListener;
import ru.olamedia.olacraft.network.packet.LiveEntityLocationUpdatePacket;
import ru.olamedia.olacraft.network.packet.SpawnPacket;
import ru.olamedia.olacraft.network.packet.SpawnRequestPacket;
import ru.olamedia.olacraft.network.packet.WorldInfoPacket;
import ru.olamedia.olacraft.scene.GameScene;
import ru.olamedia.olacraft.world.WorldInfo;
import ru.olamedia.olacraft.world.dataProvider.CachedChunkDataProvider;
import ru.olamedia.olacraft.world.dataProvider.LocalChunkDataProvider;
import ru.olamedia.olacraft.world.dataProvider.RemoteChunkDataProvider;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class GameClient extends ConnectionStateListener implements IPacketListener {
	private WorldProvider worldProvider;
	private GameScene scene;

	private Client client = new Client(10 * 1024 * 1024, 30 * 1024 * 1024);
	private String hostname = "127.0.0.1";

	@Override
	public void onChangeState(ConnectionState state) {
		if (state.isConnected()) {
			GameManager.instance.hideMainMenu();
			client.sendTCP(new ConnectionRequestPacket());
			//
			// provider.load(0, 0, 0);
			// provider.load(1, 2, 3);
		}
	}

	/*
	 * public AbstractChunkDataProvider getChunkDataProvider() {
	 * return worldProvider.getChunkDataProvider();
	 * }
	 */

	public WorldProvider getWorldProvider() {
		return worldProvider;
	}

	private List<ConnectionStateListener> stateListeners = new ArrayList<ConnectionStateListener>();
	private List<IPacketListener> packetListeners = new ArrayList<IPacketListener>();
	private ExecutorService threadPool = Executors.newFixedThreadPool(1);

	public GameClient() {
		// INIT WORLD
		worldProvider = new WorldProvider();
		// worldProvider.setChunkDataProvider(new CachedChunkDataProvider(new
		// RemoteChunkDataProvider(this)));
		// worldProvider.setChunkDataProvider(new
		// LocalChunkDataProvider(worldProvider.getInfo().name));
		worldProvider.setChunkDataProvider(new CachedChunkDataProvider(new LocalChunkDataProvider(worldProvider
				.getInfo().name)));
		// CREATE SCENE
		scene = new GameScene(worldProvider);
		Kryo kryo = client.getKryo();
		Network.registerPackets(kryo);
		addStateListener(this);
		addPacketListener(this);
		client.addListener(new Listener.ThreadedListener(new Listener() {

			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof IPacket) {
					dispatchPacket(connection, (IPacket) object);
				}
			}

			@Override
			public void disconnected(Connection connection) {
				super.disconnected(connection);
				dispatchState(ConnectionState.STATE_DISCONNECTED);
			}
		}, threadPool));
		client.start();
	}

	public void addStateListener(ConnectionStateListener listener) {
		stateListeners.add(listener);
	}

	private void dispatchState(ConnectionState state) {
		for (ConnectionStateListener l : stateListeners) {
			l.onChangeState(state);
		}
	}

	private void dispatchPacket(Connection connection, IPacket p) {
		for (IPacketListener l : packetListeners) {
			l.onPacket(connection, p);
		}
	}

	public void connect() {
		new Thread("Connect") {
			public void run() {
				try {
					dispatchState(ConnectionState.STATE_CONNECTING);
					client.connect(5000, hostname, Game.port);
					dispatchState(ConnectionState.STATE_CONNECTED);
				} catch (IOException ex) {
					dispatchState(ConnectionState.STATE_DISCONNECTED);
				}
			}
		}.start();
	}

	public void close() {
		client.close();
	}

	public void send(IPacket p) {
		client.sendTCP(p);
	}

	public void addPacketListener(IPacketListener listener) {
		packetListeners.add(listener);
	}

	public boolean isConnected() {
		return client.isConnected();
	}

	private static boolean DEBUG = true;

	private void debug(String s) {
		if (DEBUG) {
			System.out.println("[GameClient] " + s);
		}
	}

	@Override
	public void onPacket(Connection connection, IPacket p) {
		debug("received " + p.getClass().getName());
		// 1. receive ConnectionPacket, send SpawnRequestPacket
		if (p instanceof ConnectionPacket) {
			Game.instance.player.setConnectionId(((ConnectionPacket) p).connectionId);
			send(new SpawnRequestPacket());
		}
		if (p instanceof WorldInfoPacket) {
			worldProvider.setInfo(((WorldInfoPacket) p).info);
		}
		// 2. Receive SpawnPacket
		if (p instanceof SpawnPacket) {
			LiveEntity entity;
			if (((SpawnPacket) p).connectionId == client.getID()) {
				// me
				entity = Game.instance.player;
			} else {
				// another player
				entity = new LiveEntity();
			}
			entity.setX(((SpawnPacket) p).x);
			entity.setY(((SpawnPacket) p).y);
			entity.setZ(((SpawnPacket) p).z);
			entity.setConnectionId(((SpawnPacket) p).connectionId);
			scene.registerLiveEntity(entity);
			if (((SpawnPacket) p).connectionId == client.getID()) {
				// me
				scene.registerPlayer(entity);
			}
		}
		if (p instanceof LiveEntityLocationUpdatePacket) {
			LiveEntity entity = scene.getLiveEntity(((LiveEntityLocationUpdatePacket) p).connectionId);
			if (null != entity) {
				entity.setX(((LiveEntityLocationUpdatePacket) p).x);
				entity.setY(((LiveEntityLocationUpdatePacket) p).y);
				entity.setZ(((LiveEntityLocationUpdatePacket) p).z);
			}
		}
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void dispose() {
		client.close();
		client.stop();
		// threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
		threadPool.shutdownNow();
	}

	public GameScene getScene() {
		return scene;
	}

	public WorldInfo getWorldInfo() {
		return worldProvider.getInfo();
	}

}
