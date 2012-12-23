package ru.olamedia.game;

import java.util.Random;

import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.game.Timer;
import ru.olamedia.olacraft.scene.GameScene;
import ru.olamedia.olacraft.world.chunk.ChunkUnavailableException;

public class GameLogicThread extends Thread {
	public static GameLogicThread instance = new GameLogicThread("Game logic");
	private Timer fpsTimer = new Timer();

	public GameLogicThread(String name) {
		super(name);

	}

	private Random rand = new Random();

	@Override
	public synchronized void start() {
		super.start();
		fpsTimer.initialize();
		fpsTimer.update();
	}

	private float delta;

	private void tick() {
		fpsTimer.update();
		delta = (float) fpsTimer.getElapsedTime() / 1000f;
		if (rand.nextFloat() > 0.95f) {
			Game.client.getScene().dayTime.tick();
		}
		// bullets.update(Game.instance.getDelta());
		// physics.getWorld().step(Game.instance.getDelta());
		if (Game.client.getScene().isInitialized && null != Game.client.getScene().player) {
			Game.client.getScene().player.camera.setAspect(Game.Display.getAspect());
			try {
				Game.client.getScene().player.updateKeyboard(delta);
			} catch (ChunkUnavailableException e) {
				e.printStackTrace();
			}
			if (Game.client.getScene().player.isOrientationChanged || Game.instance.camera.isOrientationChanged) {
				Game.client.getScene().player.isOrientationChanged = false;
				Game.instance.camera.isOrientationChanged = false;
				updateNearestBlock();
			}
		}
	}

	private void updateNearestBlock() {
		final GameScene scene = Game.client.getScene();
		if (null != scene.player && null != scene.pickSlice) {
			scene.pickSlice.setCenter(scene.player.getCameraX(), scene.player.getCameraY(), scene.player.getCameraZ());
			scene.nearestBlock = scene.pickSlice.getNearest(Game.instance.camera);
			scene.nearestPutBlock = scene.pickSlice.getNearestPutBlock();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				tick();
				Thread.sleep(50);
				// Thread.sleep(10); // or wait/join etc
			} catch (InterruptedException ex) {
				// cleanup here
				Thread.currentThread().interrupt(); // for nested loops
				break;
			}
		}
	}
}
