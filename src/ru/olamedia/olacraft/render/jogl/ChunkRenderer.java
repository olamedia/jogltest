package ru.olamedia.olacraft.render.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;

import ru.olamedia.math.Box;
import ru.olamedia.math.Classifier;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.world.chunk.BlockSlice;
import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.chunk.ChunkMeshBulder;
import ru.olamedia.olacraft.world.chunk.ChunkSlice;

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
		GL gl = GLContext.getCurrentGL();
		if (!chunk.isAvailable()) {
			// System.out.println("not available");
			chunk.request();
			return skipnew;
		}
		/*
		 * if (!chunk.isNeighborsAvailable()) {
		 * System.out.println("no neighbors");
		 * chunk.requestNeighbors();
		 * return;
		 * }
		 */
		// System.out.println("available");
		Box box = new Box(chunk.getX(), chunk.getY(), chunk.getZ(), chunk.getX() + chunk.getWidth(), chunk.getY()
				+ chunk.getHeight(), chunk.getZ() + chunk.getDepth());
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
				skipnew = true;
			}
			return skipnew;
		}
		if (null == chunk.getMesh()) {
		} else {
			chunk.getMesh().joglRender(gl);
		}
		return skipnew;
	}

	public void render(GLAutoDrawable drawable) {

		if (!ChunkMeshBulder.instance.isAlive()) {
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
		// rendering from center
		int x, y, z;
		int dw = cs.getWidth() / 2;
		int dd = cs.getDepth() / 2;
		int dh = cs.getHeight() / 2;
		for (int dx = 0; dx < dw; dx++) {
			x = cs.getX() + dw + dx;
			for (int dz = 0; dz < dd; dz++) {
				z = cs.getZ() + dd + dz;
				for (int dy = 0; dy < dh; dy++) {
					y = cs.getY() + dh + dy;
					skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
					y = cs.getY() + dh - dy - 1;
					skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
				}
				z = cs.getZ() + dd - dz - 1;
				for (int dy = 0; dy < dh; dy++) {
					y = cs.getY() + dh + dy;
					skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
					y = cs.getY() + dh - dy - 1;
					skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
				}
			}
			x = cs.getX() + dw - dx - 1;
			for (int dz = 0; dz < dd; dz++) {
				z = cs.getZ() + dd + dz;
				for (int dy = 0; dy < dh; dy++) {
					y = cs.getY() + dh + dy;
					skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
					y = cs.getY() + dh - dy - 1;
					skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
				}
				z = cs.getZ() + dd - dz - 1;
				for (int dy = 0; dy < dh; dy++) {
					y = cs.getY() + dh + dy;
					skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
					y = cs.getY() + dh - dy - 1;
					skipnew = renderChunk(cs.getChunk(x, y, z), skipnew);
				}
			}
		}
		// System.out.println("visible top " + visibleTop);
	}
}
