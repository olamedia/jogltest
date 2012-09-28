package ru.olamedia.olacraft.network;

import java.io.IOException;
import java.net.BindException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.olamedia.liveEntity.LiveEntity;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.game.SpawnLocation;
import ru.olamedia.olacraft.network.discovery.DiscoveryThread;
import ru.olamedia.olacraft.network.packet.ConnectionPacket;
import ru.olamedia.olacraft.network.packet.ConnectionRequestPacket;
import ru.olamedia.olacraft.network.packet.GetRegionPacket;
import ru.olamedia.olacraft.network.packet.IPacket;
import ru.olamedia.olacraft.network.packet.LiveEntityLocationUpdatePacket;
import ru.olamedia.olacraft.network.packet.RegionDataPacket;
import ru.olamedia.olacraft.network.packet.SpawnPacket;
import ru.olamedia.olacraft.network.packet.SpawnRequestPacket;
import ru.olamedia.olacraft.network.packet.WorldInfoPacket;
import ru.olamedia.olacraft.scene.GameScene;
import ru.olamedia.olacraft.world.data.RegionData;
import ru.olamedia.olacraft.world.dataProvider.CachedChunkDataProvider;
import ru.olamedia.olacraft.world.dataProvider.LocalChunkDataProvider;
import ru.olamedia.olacraft.world.provider.WorldProvider;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class GameServer {
	private WorldProvider worldProvider;
	
	private ExecutorService threadPool = Executors.newFixedThreadPool(1);
	public static Server server = new Server(70 * 1024 * 1024, 1024 * 1024) {
		@Override
		protected PlayerConnection newConnection() {
			// By providing our own connection implementation, we can store per
			// connection state without a connection ID to state look up.
			return new PlayerConnection();
		}
	};
	private boolean isServerRunning = false;

	private GameScene scene;

	private static boolean DEBUG = true;

	private void debug(String s) {
		if (DEBUG) {
			System.out.println("[GameServer] " + s);
		}
	}

	public GameServer() {
		// INIT WORLD
		worldProvider = new WorldProvider();
		worldProvider.setChunkDataProvider(new CachedChunkDataProvider(new LocalChunkDataProvider(worldProvider.getInfo().name)));
		// CREATE SCENE
		scene = new GameScene(worldProvider);
		// worldProvider.getInfo().name = "world";
		Kryo kryo = server.getKryo();
		Network.registerPackets(kryo);
		server.addListener(new Listener.ThreadedListener(new Listener() {
			@Override
			public void disconnected(Connection connection) {
			}

			@Override
			public void received(Connection connection, Object object) {
				debug("received " + object.getClass());
				if (object instanceof ConnectionRequestPacket) {
					ConnectionPacket p = new ConnectionPacket();
					p.connectionId = connection.getID();
					server.sendToTCP(connection.getID(), p);
				}
				if (object instanceof GetRegionPacket) {
					GetRegionPacket p = (GetRegionPacket) object;
					RegionData data = worldProvider.getRegion(p.location);
					RegionDataPacket response = new RegionDataPacket();
					response.data = data;
					server.sendToTCP(connection.getID(), response);
				}
				if (object instanceof SpawnRequestPacket) {
					SpawnLocation loc = worldProvider.getSpawnLocation(connection.getID());
					if (null != loc) {
						server.sendToTCP(connection.getID(), new WorldInfoPacket(worldProvider));
						LiveEntity entity = new LiveEntity();
						entity.setX(loc.x);
						entity.setY(loc.y);
						entity.setZ(loc.z);
						entity.setConnectionId(connection.getID());
						scene.registerLiveEntity(entity);
						// send all entity locations
						for (LiveEntity nextEntity : scene.getLiveEntities().values()) {
							SpawnPacket p = new SpawnPacket();
							p.x = nextEntity.getX();
							p.y = nextEntity.getY();
							p.z = nextEntity.getZ();
							p.connectionId = nextEntity.getConnectionId();
							if (p.connectionId == connection.getID()) {
								server.sendToAllTCP(p);
							} else {
								server.sendToTCP(connection.getID(), p);
							}
						}
					}
				}
				if (object instanceof LiveEntityLocationUpdatePacket) {
					LiveEntityLocationUpdatePacket p = ((LiveEntityLocationUpdatePacket) object);
					p.connectionId = connection.getID();
					LiveEntity entity = scene.getLiveEntity(connection.getID());
					if (null != entity) {
						entity.setLocation(p.x, p.y, p.z);
						server.sendToAllTCP(object);
					}
				}

				// super.received(connection, object);
			}
		}, threadPool));
	}

	private DiscoveryThread discovery;

	public void start() {
		try {
			server.start();
			server.bind(Game.port);
			discovery = new DiscoveryThread("SERVER DISCOVERY");
			discovery.start();
			isServerRunning = true;
			// server.addListener(new Listener());
		} catch (BindException ex) {
			server.stop();
			if (null != discovery && discovery.isAlive()) {
				discovery.interrupt();
			}
			isServerRunning = false;
		} catch (IOException e1) {
			server.stop();
			if (null != discovery && discovery.isAlive()) {
				discovery.interrupt();
			}
			e1.printStackTrace();
			isServerRunning = false;
		}
	}

	public void stop() {
		server.close();
		server.stop();
		isServerRunning = false;
	}

	public boolean isRunning() {
		return isServerRunning;
	}

	public void send(IPacket p) {
	}

	public void dispose() {
		stop();
		// threadPool.awaitTermination(1000, TimeUnit.MILLISECONDS);
		threadPool.shutdownNow();
	}
}
