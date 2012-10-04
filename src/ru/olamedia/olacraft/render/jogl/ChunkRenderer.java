package ru.olamedia.olacraft.render.jogl;

import javax.media.opengl.GLAutoDrawable;

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

	public int visibleTop = 0;
	public int visibleBottom = 0;
	public int visibleLeft = 0;
	public int visibleRight = 0;
	public int visibleFront = 0;
	public int visibleBack = 0;

	public int frustumCulledChunks = 0;
	public int frustumIntersectChunks = 0;

	public boolean renderChunk(Chunk chunk, boolean skipnew) {

		if (!chunk.isAvailable()) {
			System.out.println("not available " + chunk);
			chunk.request();
			return skipnew;
		}

		if (!chunk.isNeighborsAvailable()) {
			System.out.println("not available " + chunk);
			chunk.requestNeighbors();
			return skipnew;
		}

		// System.out.println("available");
		Box box = new Box(chunk.getOffset().x, chunk.getOffset().y, chunk.getOffset().z, chunk.getOffset().x
				+ chunk.getWidth(), chunk.getOffset().y + chunk.getHeight(), chunk.getOffset().z + chunk.getDepth());
		if (Game.instance.camera.frustum.quickClassify(box) == Classifier.OUTSIDE) {
			frustumCulledChunks++;
			return skipnew;
		}

		// // boolean inside = true;
		// if (Game.camera.frustum != null) {
		// if (Game.camera.frustum.quickClassify(box) ==
		// Classifier.Classification.OUTSIDE) {
		// frustumCulledChunks++;
		// return;
		// }
		// }
		// } else if (Game.camera.frustum.test(box) == Classifier.INTERSECT) {
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
		if (null == chunk.getMesh()) {
			// System.out.println("mesh is null");
			// skipnew = true;
		} else {
			SimpleQuadMesh mesh = chunk.getMesh();
			// System.out.println("render " + chunk + " " +
			// mesh.getVertexCount());
			mesh.joglRender();
		}
		return skipnew;
	}

	public void render(GLAutoDrawable drawable) {

		if (!ChunkMeshBulder.instance.isAlive() && !ChunkMeshBulder.instance.isInterrupted()) {
			ChunkMeshBulder.instance.start();
		}

		visibleTop = 0;
		visibleBottom = 0;
		visibleLeft = 0;
		visibleRight = 0;
		visibleFront = 0;
		visibleBack = 0;
		frustumCulledChunks = 0;
		boolean skipnew = false;
		ChunkSlice cs = slice.getChunkSlice();
		for (int x = cs.getX(); x < cs.getX() + cs.getWidth(); x++) {
			for (int z = cs.getZ(); z < cs.getZ() + cs.getDepth(); z++) {
				for (int y = cs.getY(); y < cs.getY() + cs.getHeight(); y++) {
					skipnew = renderChunk(cs.getChunk(new ChunkLocation(x, y, z)), skipnew);
				}
			}
		}
		if (true) {
			return;
		}
		// rendering from center
		int x, y, z;
		int half = (Options.renderDistance / 16) / 2 + 1;
		BlockLocation camera = new BlockLocation();
		camera.x = (int) Game.client.getScene().getPlayer().getCameraX();
		camera.y = (int) Game.client.getScene().getPlayer().getCameraY();
		camera.z = (int) Game.client.getScene().getPlayer().getCameraZ();
		ChunkLocation cameraChunk = camera.getChunkLocation();
		int cx = cameraChunk.x;
		int cy = cameraChunk.y;
		int cz = cameraChunk.z;
		ChunkLocation cLoc = new ChunkLocation(cx, cy, cz);
		for (int r = 0; r <= half; r++) {
			// +x
			x = cx + r;
			for (z = cz - r - 1; z <= cz + r; z++) {
				for (y = cy - r - 1; y <= cy + r; y++) {
					cLoc = new ChunkLocation(x, y, z);
					skipnew = renderChunk(cs.getChunk(cLoc), skipnew);
				}
			}
			// -x
			x = cx - r - 1;
			for (z = cz - r - 1; z <= cz + r; z++) {
				for (y = cy - r - 1; y <= cy + r; y++) {
					cLoc = new ChunkLocation(x, y, z);
					skipnew = renderChunk(cs.getChunk(cLoc), skipnew);
				}
			}

			// +z
			z = cz + r;
			for (x = cx - r - 1; x <= cz + r; x++) {
				for (y = cy - r - 1; y <= cy + r; y++) {
					cLoc = new ChunkLocation(x, y, z);
					skipnew = renderChunk(cs.getChunk(cLoc), skipnew);
				}
			}
			// -z
			z = cz - r - 1;
			for (x = cx - r - 1; x <= cz + r; x++) {
				for (y = cy - r - 1; y <= cy + r; y++) {
					cLoc = new ChunkLocation(x, y, z);
					skipnew = renderChunk(cs.getChunk(cLoc), skipnew);
				}
			}
			// +y
			y = cy + r;
			for (x = cx - r - 1; x <= cz + r; x++) {
				for (z = cz - r - 1; z <= cz + r; z++) {
					cLoc = new ChunkLocation(x, y, z);
					skipnew = renderChunk(cs.getChunk(cLoc), skipnew);
				}
			}
			// -y
			y = cy - r - 1;
			for (x = cx - r - 1; x <= cz + r; x++) {
				for (z = cz - r - 1; z <= cz + r; z++) {
					cLoc = new ChunkLocation(x, y, z);
					skipnew = renderChunk(cs.getChunk(cLoc), skipnew);
				}
			}
			if (skipnew) {
				// break;
			}
			// break;
		}

		// int dw = cs.getWidth() / 2;
		// int dd = cs.getDepth() / 2;
		// int dh = cs.getHeight() / 2;
		// for (int dx = 0; dx < dw; dx++) {
		// x = cs.getX() + dw + dx;
		// for (int dz = 0; dz < dd; dz++) {
		// z = cs.getZ() + dd + dz;
		// for (int dy = 0; dy < dh; dy++) {
		// y = cs.getY() + dh + dy;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// y = cs.getY() + dh - dy - 1;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// }
		// z = cs.getZ() + dd - dz - 1;
		// for (int dy = 0; dy < dh; dy++) {
		// y = cs.getY() + dh + dy;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// y = cs.getY() + dh - dy - 1;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// }
		// }
		// x = cs.getX() + dw - dx - 1;
		// for (int dz = 0; dz < dd; dz++) {
		// z = cs.getZ() + dd + dz;
		// for (int dy = 0; dy < dh; dy++) {
		// y = cs.getY() + dh + dy;
		// int dd = cs.getDepth() / 2;
		// int dh = cs.getHeight() / 2;
		// for (int dx = 0; dx < dw; dx++) {
		// x = cs.getX() + dw + dx;
		// for (int dz = 0; dz < dd; dz++) {
		// z = cs.getZ() + dd + dz;
		// for (int dy = 0; dy < dh; dy++) {
		// y = cs.getY() + dh + dy;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// y = cs.getY() + dh - dy - 1;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// }
		// z = cs.getZ() + dd - dz - 1;
		// for (int dy = 0; dy < dh; dy++) {
		// y = cs.getY() + dh + dy;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// y = cs.getY() + dh - dy - 1;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// }
		// }
		// x = cs.getX() + dw - dx - 1;
		// for (int dz = 0; dz < dd; dz++) {
		// z = cs.getZ() + dd + dz;
		// for (int dy = 0; dy < dh; dy++) {
		// y = cs.getY() + dh + dy;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// y = cs.getY() + dh - dy - 1;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// }
		// z = cs.getZ() + dd - dz - 1;
		// for (int dy = 0; dy < dh; dy++) {
		// y = cs.getY() + dh + dy;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// y = cs.getY() + dh - dy - 1;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// }
		// }
		// }
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// y = cs.getY() + dh - dy - 1;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// }
		// z = cs.getZ() + dd - dz - 1;
		// for (int dy = 0; dy < dh; dy++) {
		// y = cs.getY() + dh + dy;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// y = cs.getY() + dh - dy - 1;
		// skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
		// }
		// }
		// }

		// System.out.println("visible top " + visibleTop);
	}
}
