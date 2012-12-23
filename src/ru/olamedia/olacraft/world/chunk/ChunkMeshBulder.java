package ru.olamedia.olacraft.world.chunk;

import java.util.concurrent.ArrayBlockingQueue;

import ru.olamedia.olacraft.world.blockRenderer.ChunkRenderer;

public class ChunkMeshBulder extends Thread {
	public static ChunkMeshBulder instance = new ChunkMeshBulder("Mesh builder");
	private ArrayBlockingQueue<Chunk> chunks = new ArrayBlockingQueue<Chunk>(16);

	public ChunkMeshBulder(String name) {
		super(name);
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
			ChunkRenderer.compile(chunks.take());
		}
	}

	@Override
	public void run() {
		while (true) {
			// main loop
			try {
				tick();
				if (chunks.isEmpty()) {
					Thread.sleep(50);
				} else {
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
