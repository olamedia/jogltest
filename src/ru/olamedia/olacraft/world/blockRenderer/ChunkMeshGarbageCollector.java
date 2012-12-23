package ru.olamedia.olacraft.world.blockRenderer;

import java.util.concurrent.ArrayBlockingQueue;

import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.chunk.ChunkSlice;
import ru.olamedia.olacraft.world.location.BlockLocation;

public class ChunkMeshGarbageCollector extends Thread {
	public static ChunkMeshGarbageCollector instance = new ChunkMeshGarbageCollector("Mesh GC");
	private ArrayBlockingQueue<Chunk> chunks = new ArrayBlockingQueue<Chunk>(32);

	public ChunkMeshGarbageCollector(String name) {
		super(name);
	}

	BlockLocation cameraBlock;
	private int renderDistance;

	public void lookup() {
		renderDistance = Game.client.getScene().getPlayer().settings.renderDistance;
		cameraBlock = Game.client.getScene().getPlayer().getCameraBlockLocation();
		// FIXME: cuncurrent modification
		for (Integer x : ChunkSlice.rendererInstance.iChunks.keySet()) {
			for (Integer y : ChunkSlice.rendererInstance.iChunks.get(x).keySet()) {
				for (Integer z : ChunkSlice.rendererInstance.iChunks.get(x).get(y).keySet()) {
					if (isFull()) {
						return;
					}
					Chunk chunk = ChunkSlice.rendererInstance.iChunks.get(x).get(y).get(z);
					float d = (float) Math.sqrt(Math.pow(chunk.getOffset().x + 8 - cameraBlock.x, 2)
							+ Math.pow(chunk.getOffset().y + 8 - cameraBlock.y, 2)
							+ Math.pow(chunk.getOffset().z + 8 - cameraBlock.z, 2));
					if (d > renderDistance + renderDistance / 4) {
						add(chunk);
					}
				}
			}
		}
	}

	public boolean isFull() {
		return chunks.remainingCapacity() == 0;
	}

	public void add(Chunk chunk) {
		chunks.offer(chunk);
	}

	public void clear() {
		chunks.clear();
	}

	public void tick() throws InterruptedException {
		if (!chunks.isEmpty()) {
			Chunk chunk = chunks.take();
			ChunkSlice.rendererInstance.removeChunk(chunk.location);
		} else {
			lookup();
		}
	}

	@Override
	public void run() {
		// glc.makeCurrent();
		while (true) {
			// main loop
			try {
				tick();
				if (chunks.isEmpty()) {
					Thread.sleep(50);
				}
				// Thread.sleep(10); // or wait/join etc
			} catch (InterruptedException ex) {
				// cleanup here
				Thread.currentThread().interrupt(); // for nested loops
				break;
			}
		}
	}
}
