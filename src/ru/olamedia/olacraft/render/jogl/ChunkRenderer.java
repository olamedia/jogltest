package ru.olamedia.olacraft.render.jogl;

import ru.olamedia.Options;
import ru.olamedia.geom.SimpleQuadMesh;
import ru.olamedia.math.Box;
import ru.olamedia.math.Classifier;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.world.chunk.BlockSlice;
import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.chunk.ChunkMeshBulder;
import ru.olamedia.olacraft.world.chunk.ChunkSlice;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;

public class ChunkRenderer {
	private BlockSlice slice;

	public ChunkRenderer(BlockSlice slice) {
		this.slice = slice;
	}

	public int testedChunks = 0;

	public int visibleTop = 0;
	public int visibleBottom = 0;
	public int visibleLeft = 0;
	public int visibleRight = 0;
	public int visibleFront = 0;
	public int visibleBack = 0;

	public int frustumCulledChunks = 0;
	public int frustumIntersectChunks = 0;

	public boolean renderChunk(Chunk chunk, boolean skipnew) {
		float d = (float) Math.sqrt(Math.pow(chunk.getOffset().x + 8 - cameraBlock.x, 2)
				+ Math.pow(chunk.getOffset().y + 8 - cameraBlock.y, 2)
				+ Math.pow(chunk.getOffset().z + 8 - cameraBlock.z, 2));
		if (d > Options.renderDistance) {
			return skipnew;
		}
		testedChunks++;
		if (!chunk.inWorldRange()) {
			return skipnew;
		}
		Box box = new Box(chunk.getOffset().x, chunk.getOffset().y, chunk.getOffset().z, chunk.getOffset().x
				+ chunk.getWidth(), chunk.getOffset().y + chunk.getHeight(), chunk.getOffset().z + chunk.getDepth());
		if (Game.instance.camera.frustum.quickClassify(box) == Classifier.OUTSIDE) {
			frustumCulledChunks++;
			return skipnew;
		}

		chunk.render();
		if (!chunk.isMeshCostructed) {
			if (!chunk.isAvailable()) {
				// System.out.println("not available " + chunk);
				chunk.request();
				return skipnew;
			}

			if (!chunk.isNeighborsAvailable()) {
				// System.out.println("not available " + chunk);
				chunk.requestNeighbors();
				return skipnew;
			}
			// compute visibility

			// System.out.println("available");

			// // boolean inside = true;
			// if (Game.camera.frustum != null) {
			// if (Game.camera.frustum.quickClassify(box) ==
			// Classifier.Classification.OUTSIDE) {
			// frustumCulledChunks++;
			// return;
			// }
			// }
			// } else if (Game.camera.frustum.test(box) == Classifier.INTERSECT)
			// {
			// inside = false;
			// frustumIntersectChunks++;
			// } else {
			// frustumCulledChunks++;
			// return;
			// }
			if (!chunk.isMeshCostructed) {
				if (skipnew) {
					return skipnew;
				}
			}
			if (!chunk.isMeshCostructed) {
				ChunkMeshBulder.instance.add(chunk);
				if (ChunkMeshBulder.instance.isFull()) {
					// System.out.println("queue is full, skipping");
					skipnew = true;
				}
				// System.out.println("not constructed");
				return skipnew;
			}

			return skipnew;
		} else {
			return skipnew;
		}
	}

	BlockLocation cameraBlock;
	ChunkLocation cameraChunk;
	int distance;
	public ChunkSlice chunkSlice;

	public void render() {

		if (!ChunkMeshBulder.instance.isAlive() && !ChunkMeshBulder.instance.isInterrupted()) {
			ChunkMeshBulder.instance.start();
		}
		if (null == chunkSlice) {
			chunkSlice = slice.getChunkSlice();
		}
		testedChunks = 0;
		visibleTop = 0;
		visibleBottom = 0;
		visibleLeft = 0;
		visibleRight = 0;
		visibleFront = 0;
		visibleBack = 0;
		frustumCulledChunks = 0;

		cameraBlock = Game.client.getScene().getPlayer().getCameraBlockLocation();
		cameraChunk = cameraBlock.getChunkLocation();
		ChunkLocation renderLoc;

		boolean skipnew = false;

		for (distance = 0; distance <= Options.renderDistance / 16; distance++) {
			if (distance > 0) {
				int shortDistance = distance - 1;
				renderLoc = new ChunkLocation(cameraChunk);
				for (renderLoc.x = cameraChunk.x - distance; renderLoc.x <= cameraChunk.x + distance; renderLoc.x += distance * 2) {
					// render ZY sides
					for (renderLoc.y = cameraChunk.y - distance; renderLoc.y <= cameraChunk.y + distance; renderLoc.y++) {
						for (renderLoc.z = cameraChunk.z - distance; renderLoc.z <= cameraChunk.z + distance; renderLoc.z++) {
							skipnew = renderChunk(chunkSlice.getChunk(renderLoc), skipnew);
						}
					}
				}
				for (renderLoc.z = cameraChunk.z - distance; renderLoc.z <= cameraChunk.z + distance; renderLoc.z += distance * 2) {
					// render XY sides
					for (renderLoc.x = cameraChunk.x - shortDistance; renderLoc.x <= cameraChunk.x + shortDistance; renderLoc.x++) {
						for (renderLoc.y = cameraChunk.y - distance; renderLoc.y <= cameraChunk.y + distance; renderLoc.y++) {
							skipnew = renderChunk(chunkSlice.getChunk(renderLoc), skipnew);
						}
					}
				}
				for (renderLoc.y = cameraChunk.y - distance; renderLoc.y <= cameraChunk.y + distance; renderLoc.y += distance * 2) {
					// render XZ sides
					for (renderLoc.x = cameraChunk.x - shortDistance; renderLoc.x <= cameraChunk.x + shortDistance; renderLoc.x++) {
						for (renderLoc.z = cameraChunk.z - shortDistance; renderLoc.z <= cameraChunk.z + shortDistance; renderLoc.z++) {
							skipnew = renderChunk(chunkSlice.getChunk(renderLoc), skipnew);
						}
					}
				}
			} else {
				renderLoc = new ChunkLocation(cameraChunk);
				skipnew = renderChunk(chunkSlice.getChunk(renderLoc), skipnew);
			}
		}
	}
}
