package ru.olamedia.olacraft.game;

import ru.olamedia.camera.MatrixCamera;
import ru.olamedia.game.GameFrame;
import ru.olamedia.olacraft.events.GameEvent;
import ru.olamedia.olacraft.network.GameClient;
import ru.olamedia.olacraft.network.GameServer;
import ru.olamedia.player.Player;

public class Game {
	public static Game instance = null;
	public static int port = 26002;
	public static boolean isServerRunning = false;
	public static GameServer server = new GameServer();
	public static GameClient client = new GameClient();
	public static Timer fpsTimer = new Timer();

	public MatrixCamera camera;

	public static int MODE_SINGLEPLAYER = 1;
	public static int MODE_MULTIPLAYER = 2;
	public static int MODE_SERVER = 4;
	@SuppressWarnings("unused")
	private int mode = 1;
	private boolean isRunning = false;
	// player
	public Player player;

	// block world
	// private blockWorld;
	// live entities (including player and npcs)
	// private liveEntities;
	public Game() {
		this(MODE_SINGLEPLAYER);
	}

	public Game(int mode) {
		this.mode = mode;
		if ((MODE_MULTIPLAYER & mode) > 0) {
			if ((MODE_SERVER & mode) > 0) {
				// init server
			} else {
				// init client
			}
		}
		player = new Player();
		camera = new MatrixCamera();
		camera.attachTo(player, true);
		camera.setFov(90);
		camera.pack();
		// scene.registerLiveEntity(player);
	}

	public void start() {
		isRunning = true;
		GameEvent e = new GameEvent(null);
		e.setType(GameEvent.GAME_START);
		e.dispatch();
	}

	// Pause game in single mode
	public void pause() {

	}

	public void stop() {

	}

	public boolean isRunning() {
		return isRunning;
	}

	public void spawnMe(int x, int y, int z) {
		player.setLocation(x, y, z);
	}

	public void tick() {

	}

	public static class Display {
		public static int getWidth() {
			return (int) GameFrame.getWidth();
		}

		public static int getHeight() {
			return (int) GameFrame.getHeight();
		}

		public static float getAspect() {
			return ((float) getWidth()) / ((float) getHeight());
		}
	}

	public float getFrameDelta() {
		return (float) fpsTimer.getElapsedTime() / 1000f;
	}

}
