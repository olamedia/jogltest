package ru.olamedia.geom;

import java.util.HashMap;

import ru.olamedia.olacraft.game.Game;

public class ChunkMeshOctree {
	private HashMap<Integer, HashMap<Integer, HashMap<Integer, ChunkMeshNode>>> nodes = new HashMap<Integer, HashMap<Integer, HashMap<Integer, ChunkMeshNode>>>();
	private static int level = 2;
	private static int nodeAxisChunks = 4;
	private static int nodeChunks = 64;
	private static int levelp = 4;

	private int getRenderDistance() {
		return Game.instance.player.settings.renderDistance;
	}

	private int getCameraX() {
		return (int) Game.instance.player.camera.getCameraX();
	}

	private int getCameraY() {
		return (int) Game.instance.player.camera.getCameraY();
	}

	private int getCameraZ() {
		return (int) Game.instance.player.camera.getCameraZ();
	}

	public void render(int pass) {
		final int startX = getCameraX() - getRenderDistance() / 2;
		final int startY = getCameraY() - getRenderDistance() / 2;
		final int startZ = getCameraZ() - getRenderDistance() / 2;
		final int deltaChunks = getRenderDistance() / 16;
		// renderDistance = 256, deltaChunks = 16
		// renderDistance = 128, deltaChunks = 8
		// renderDistance = 64, deltaChunks = 4
		// renderDistance = 32, deltaChunks = 2
		// level = 1: 1 parent quadtree node (32x32x32), each root node = 8
		// chunks
		// level = 2: 2 parent quadtree nodes (64x64x64), each root node = 64
		// chunks
		final int chunkStartX = (startX / 16) / nodeAxisChunks;
		final int chunkStartY = (startY / 16) / nodeAxisChunks;
		final int chunkStartZ = (startZ / 16) / nodeAxisChunks;
		for (int ix = chunkStartX; ix < nodeAxisChunks; ix++) {
			for (int iy = chunkStartY; iy < nodeAxisChunks; iy++) {
				for (int iz = chunkStartZ; iz < nodeAxisChunks; iz++) {
					renderNode(ix, iy, iz, pass);
				}
			}
		}
	}

	private void renderNode(int ix, int iy, int iz, int pass) {
		if (!nodes.containsKey(ix)) {
			return;
		}
		if (!nodes.get(ix).containsKey(iy)) {
			return;
		}
		if (!nodes.get(ix).get(iy).containsKey(iz)) {
			return;
		}
		nodes.get(ix).get(iy).get(iz).render(pass);
	}

	public void render(int x, int y, int z, int pass) {
		final int ix = x >> 2;
		final int iy = y >> 2;
		final int iz = z >> 2;
		if (!nodes.containsKey(ix)) {
			nodes.put(ix, new HashMap<Integer, HashMap<Integer, ChunkMeshNode>>());
			nodes.get(ix).put(iy, new HashMap<Integer, ChunkMeshNode>());
			// nodes.get(ix).get(iy).put(iz, new ChunkMeshNode());
		}
	}

	public void put(int chunkX, int chunkY, int chunkZ, ChunkMesh mesh) {
		final int ix = chunkX / nodeAxisChunks;
		final int iy = chunkY / nodeAxisChunks;
		final int iz = chunkZ / nodeAxisChunks;
		final int id = (chunkX & 64) * +(chunkY & 64) * +(chunkZ & 64);
		if (!nodes.containsKey(ix)) {
			nodes.put(ix, new HashMap<Integer, HashMap<Integer, ChunkMeshNode>>());
		}
		if (!nodes.get(ix).containsKey(iy)) {
			nodes.get(ix).put(iy, new HashMap<Integer, ChunkMeshNode>());
		}
		if (!nodes.get(ix).get(iy).containsKey(iz)) {
			nodes.get(ix).get(iy).put(iz, new ChunkMeshNode(level));
		}
		nodes.get(ix).get(iy).get(iz).combine();
	}
}
