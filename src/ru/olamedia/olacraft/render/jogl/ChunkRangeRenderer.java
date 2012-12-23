package ru.olamedia.olacraft.render.jogl;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

import com.jogamp.opengl.math.geom.AABBox;

import ru.olamedia.geom.ImmModeMesh;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.world.blockRenderer.ChunkRenderer;
import ru.olamedia.olacraft.world.blockTypes.BlockType;
import ru.olamedia.olacraft.world.blockTypes.GrassBlockType;
import ru.olamedia.olacraft.world.chunk.BlockSlice;
import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.chunk.ChunkMeshBulder;
import ru.olamedia.olacraft.world.chunk.ChunkSlice;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.player.Player;

public class ChunkRangeRenderer {

	private class Point {
		public int x;
		public int y;
		public int z;
	}

	private ArrayList<ArrayList<Point>> ranges = new ArrayList<ArrayList<Point>>();
	private ArrayList<ArrayList<Point>> visibleRanges = new ArrayList<ArrayList<Point>>();

	public ImmModeMesh big;
	private boolean makeBig = false;
	private boolean isBigInvalid = true;

	private void precalcRange() {
		for (int d = 0; d < 32; d++) {
			ranges.add(new ArrayList<ChunkRangeRenderer.Point>());
		}
		for (int d = 0; d < 32; d++) {
			visibleRanges.add(new ArrayList<ChunkRangeRenderer.Point>());
		}
		for (int dx = -15; dx <= 15; dx++) {
			for (int dy = -15; dy <= 15; dy++) {
				for (int dz = -15; dz <= 15; dz++) {
					final Point p = new Point();
					p.x = dx;
					p.y = dy;
					p.z = dz;
					final int d = (int) Math.floor(Math.sqrt(dx * dx + dy * dy + dz * dz));
					ranges.get(d).add(p);
				}
			}
		}
	}

	private int vertexTmpCount;
	private int components = 0;

	public void updateFrustumCulling() {
		if (null == chunkSlice) {
			return;
		}
		final Player player = Game.client.getScene().getPlayer();
		cameraBlock = player.getCameraBlockLocation();
		cameraChunk = cameraBlock.getChunkLocation();
		final float x = player.camera.getX();
		final float y = player.camera.getY();
		final float z = player.camera.getZ();
		renderDistance = player.settings.renderDistance;
		vertexTmpCount = 0;
		for (int d = 0; d < renderDistance / 16; d++) {
			visibleRanges.get(d).clear();
			for (Point delta : ranges.get(d)) {
				renderLoc.x = cameraChunk.x + delta.x;
				renderLoc.y = cameraChunk.y + delta.y;
				renderLoc.z = cameraChunk.z + delta.z;
				final Chunk chunk = chunkSlice.getChunk(renderLoc);
				center[0] = (float) (chunk.getOffset().x + 8);
				center[1] = (float) (chunk.getOffset().y + 8);
				center[2] = (float) (chunk.getOffset().z + 8);
				if (!Game.instance.camera.frustum.isSphereOutside(center, radius)) {
					if (chunk.mesh.isValid() && chunk.mesh.isEmpty()) {

					} else {
						visibleRanges.get(d).add(delta);
						vertexTmpCount += chunk.mesh.getVertexCount();
						if ((0 == components) && (null != chunk.mesh.getOpaqueMesh())) {
							components = chunk.mesh.getOpaqueMesh().getComponents();
						}
					}
				}
			}
		}
		if (null != big) {
			big.destroy();
			big = null;
		}
		if (makeBig) {
			big = ImmModeMesh.allocate(vertexTmpCount * components);
			big.setGLSL(true);
			big.enableVertex3();
			big.enableColor4();
			big.enableTexCoord2();
			big.setServer(true);
			big.beginQuads();
			for (distance = 0; distance <= renderDistance / 16; distance++) {
				for (Point offset : visibleRanges.get(distance)) {
					renderLoc.x = cameraChunk.x + offset.x;
					renderLoc.y = cameraChunk.y + offset.y;
					renderLoc.z = cameraChunk.z + offset.z;
					final Chunk chunk = chunkSlice.getChunk(renderLoc);
					if (chunk.inWorldRange()) {
						if (chunk.mesh.isValid() && chunk.mesh.isEmpty()) {

						} else {
							if (null != chunk.mesh.getOpaqueMesh()) {
								big.put(chunk.mesh.getOpaqueMesh());
							}
							if (!chunk.mesh.isValid()) {
								if (!ChunkMeshBulder.instance.isFull()) {
									ChunkMeshBulder.instance.add(chunk);
								}
							}
						}
					}
				}
			}
			big.end();
		}
	}

	private boolean useShaders = true;

	public ChunkRangeRenderer(BlockSlice slice) {
		precalcRange();
	}

	private BlockType blockType = new GrassBlockType();

	public int testedChunks = 0;
	public int testedChunksVertices = 0;

	public int visibleTop = 0;
	public int visibleBottom = 0;
	public int visibleLeft = 0;
	public int visibleRight = 0;
	public int visibleFront = 0;
	public int visibleBack = 0;

	public int frustumCulledChunks = 0;
	public int frustumIntersectChunks = 0;

	public int visibleBlocks = 0;
	public int vertexCount = 0;

	public static int lightTick = 0;

	public static int OPAQUE_PASS = 0;
	public static int ALPHA_PASS = 1;
	private AABBox box = new AABBox();
	private float d;
	final float radius = (float) Math.sqrt(8 * 8 + 8 * 8 + 8 * 8);
	final float radius2 = (float) Math.sqrt(0.5 * 0.5 + 0.5 * 0.5 + 0.5 * 0.5);
	private float[] center = new float[3];

	public boolean renderChunk(Chunk chunk, boolean skipnew, int pass) {
		testedChunks++;
		if (!chunk.inWorldRange()) {
			return skipnew;
		}
		// center[0] = (float) (chunk.getOffset().x + 8);
		// center[1] = (float) (chunk.getOffset().y + 8);
		// center[2] = (float) (chunk.getOffset().z + 8);
		chunk.render(pass);
		
		vertexCount += chunk.mesh.getVertexCount();
		if (!chunk.mesh.isValid()) {
			if (!skipnew) {
				ChunkMeshBulder.instance.add(chunk);
				if (ChunkMeshBulder.instance.isFull()) {
					skipnew = true;
				}
			}
		}
		return skipnew;
	}

	Chunk lastInvalidChunk = null;
	BlockLocation cameraBlock;
	ChunkLocation cameraChunk;
	int distance;
	private int renderDistance;
	public ChunkSlice chunkSlice;

	private boolean allChunksRendered = false;
	private boolean allChunksValid = false;
	private ChunkLocation renderLoc = new ChunkLocation();

	public void render(int pass) {
		final GL2 gl = GLContext.getCurrentGL().getGL2();
		visibleBlocks = 0;
		vertexCount = 0;
		allChunksRendered = true;
		allChunksValid = true;
		renderDistance = Game.client.getScene().getPlayer().settings.renderDistance;
		if (!ChunkMeshBulder.instance.isAlive() && !ChunkMeshBulder.instance.isInterrupted()) {
			ChunkMeshBulder.instance.start();
		}
		if (null == chunkSlice) {
			ChunkSlice.rendererInstance = chunkSlice = new ChunkSlice(Game.client.getWorldProvider(), 1, 1, 1);// slice.getChunkSlice();
		}
		/*
		 * if (!ChunkMeshGarbageCollector.instance.isAlive() &&
		 * !ChunkMeshGarbageCollector.instance.isInterrupted()) {
		 * ChunkMeshGarbageCollector.instance.start();
		 * }
		 */
		// chunkSlice = new ChunkSlice(Game.client.getWorldProvider(), 1, 1, 1);
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

		boolean skipnew = false;
		if (null != big) {
			big.draw();
		} else {
			for (distance = 0; distance <= renderDistance / 16; distance++) {
				if (distance > 0) {
					for (Point offset : visibleRanges.get(distance)) {
						renderLoc.x = cameraChunk.x + offset.x;
						renderLoc.y = cameraChunk.y + offset.y;
						renderLoc.z = cameraChunk.z + offset.z;
						skipnew = renderChunk(chunkSlice.getChunk(renderLoc), skipnew, pass);
					}
				} else {
					renderLoc.set(cameraChunk);
					skipnew = renderChunk(chunkSlice.getChunk(renderLoc), skipnew, pass);
				}
			}
		}
	}
}
