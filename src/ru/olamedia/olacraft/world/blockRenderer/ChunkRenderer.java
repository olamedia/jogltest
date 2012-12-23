package ru.olamedia.olacraft.world.blockRenderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLException;
import javax.media.opengl.GLUniformData;

import com.jogamp.opengl.util.PMVMatrix;
import com.jogamp.opengl.util.glsl.ShaderState;

import ru.olamedia.asset.Shader;
import ru.olamedia.geom.ChunkMesh;
import ru.olamedia.geom.ImmModeMesh;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.render.jogl.ChunkRangeRenderer;
import ru.olamedia.olacraft.world.blockTypes.BlockType;
import ru.olamedia.olacraft.world.calc.LightCalculator;
import ru.olamedia.olacraft.world.calc.LightData;
import ru.olamedia.olacraft.world.chunk.Chunk;
import ru.olamedia.olacraft.world.chunk.ChunkUnavailableException;
import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.data.ChunkDataNeighbors;
import ru.olamedia.olacraft.world.drop.DroppedEntity;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.IntLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class ChunkRenderer {
	protected static WorldProvider provider;
	protected static Chunk chunk;
	protected static ChunkData data;
	protected ChunkData top;
	protected ChunkData bottom;
	protected ChunkData left;
	protected ChunkData right;
	protected ChunkData front;
	protected ChunkData back;

	protected int[] visible;

	public static int visibleCount = 0;
	public static int visibleOpaqueCount = 0;
	public static int vertexCount = 0;
	public static int vertexOpaqueCount = 0;

	public boolean isAvailable = false;
	public boolean isCompiling = false;
	public boolean isCompiled = false;
	public boolean needRecompilation = true;

	protected static ImmModeMesh mesh;
	protected ImmModeMesh prevMesh;
	protected ImmModeMesh[] meshes;

	protected static ImmModeMesh opaqueMesh;
	protected ImmModeMesh prevOpaqueMesh;
	protected ImmModeMesh[] opaqueMeshes;

	protected ImmModeMesh[] droppedMeshes;

	protected ArrayBlockingQueue<ImmModeMesh> disposable = new ArrayBlockingQueue<ImmModeMesh>(4096);
	int droppedEntityVertexCount = 0;

	private static Shader shader = null;
	public static GLUniformData pmvMatrixUniform;
	public static GLUniformData sunColor;

	private static boolean useShader = true;

	public static Shader getShader() {
		if (null == shader) {
			shader = new Shader();
			shader.compile();
			final GL2ES2 gl = GLContext.getCurrentGL().getGL2ES2();
			final ShaderState state = shader.getState();
			final PMVMatrix pmvMatrix = Game.client.getScene().getPmvMatrix();
			state.attachObject("pmvMatrix", pmvMatrix);
			pmvMatrixUniform = new GLUniformData("pmvMatrix", 4, 4, pmvMatrix.glGetPMvMatrixf());
			state.ownUniform(pmvMatrixUniform);
			state.uniform(gl, pmvMatrixUniform);

			state.attachObject("sunColor", ChunkRenderer.sunColor);
			sunColor = new GLUniformData("sunColor", 4, Game.client.getScene().dayTime.sunColor);
			state.ownUniform(ChunkRenderer.sunColor);
			state.uniform(gl, ChunkRenderer.sunColor);

			// if (!state.uniform(gl, new GLUniformData("sunColor", 4,
			// Game.client.getScene().time.sunColor))) {
			// throw new GLException("Error setting sunColor in shader: " +
			// state);
			// }
			if (!state.uniform(gl, new GLUniformData("mesh_ActiveTexture", 0))) {
				throw new GLException("Error setting mesh_ActiveTexture in shader: " + state);
			}
		}
		return shader;
	}

	public ChunkRenderer(Chunk chunk) {
		this.chunk = chunk;
		this.provider = chunk.getProvider();
	}

	public void invalidate() {
		needRecompilation = true;
	}

	protected void disposeMesh(ImmModeMesh mesh) {
		if (null != mesh) {
			disposable.offer(mesh);
		}
	}

	public void disposeMeshes() {
		// while (!disposable.isEmpty()){
		// final ImmModeMesh mesh = disposable.poll();
		// mesh.dispose();
		// }
	}

	private static IntBuffer blockLocation = IntBuffer.allocate(3);
	private static FloatBuffer renderLocation = FloatBuffer.allocate(3);

	private static short currentId;
	private static BlockType currentType;
	private static AbstractBlockRenderer currentRenderer;

	private static void prepareRenderer(WorldProvider provider, Chunk chunk, ChunkData data) {
		currentType = provider.getTypeRegistry().getBlockType(data.types[currentId]);
		if (null == currentType) {
			throw new RuntimeException("invalid type: " + currentType + " " + currentId);
		}
		final int x = currentId / 256;
		final int y = (currentId - x * 256) / 16;
		final int z = currentId - x * 256 - y * 16;
		IntLocation.set(blockLocation, chunk.offset.x + x, chunk.offset.y + y, chunk.offset.z + z);
		renderLocation.put(0, blockLocation.get(0));
		renderLocation.put(1, blockLocation.get(1));
		renderLocation.put(2, blockLocation.get(2));
		// final RenderLocation location = new
		// RenderLocation(blockLocation);
		// System.out.println(chunk.location);
		currentRenderer = currentType.getRenderer();
		if (null == currentRenderer) {
			throw new RuntimeException("invalid renderer: " + currentType.getClass().getName());
		}
		currentRenderer.setScale(1);
		currentRenderer.setNeighbors(neighbors);
		if (currentType.hideTouchedSides() || currentType.isOpaque()) {
			// check each side
			try {
				currentRenderer.renderBottom = !provider.hideTouchedSides(blockLocation.get(0),
						blockLocation.get(1) - 1, blockLocation.get(2), data.types[currentId]);
				currentRenderer.renderTop = !provider.hideTouchedSides(blockLocation.get(0), blockLocation.get(1) + 1,
						blockLocation.get(2), data.types[currentId]);
				currentRenderer.renderLeft = !provider.hideTouchedSides(blockLocation.get(0) - 1, blockLocation.get(1),
						blockLocation.get(2), data.types[currentId]);
				currentRenderer.renderRight = !provider.hideTouchedSides(blockLocation.get(0) + 1,
						blockLocation.get(1), blockLocation.get(2), data.types[currentId]);
				currentRenderer.renderFront = !provider.hideTouchedSides(blockLocation.get(0), blockLocation.get(1),
						blockLocation.get(2) + 1, data.types[currentId]);
				currentRenderer.renderBack = !provider.hideTouchedSides(blockLocation.get(0), blockLocation.get(1),
						blockLocation.get(2) - 1, data.types[currentId]);
			} catch (ChunkUnavailableException e) {
				e.printStackTrace();
			}
		} else {
			currentRenderer.renderAllSides();
		}
	}

	private static ChunkDataNeighbors neighbors = new ChunkDataNeighbors();

	public static void compile(Chunk chunk) {
		final ChunkMesh chunkMesh = chunk.mesh;
		chunkMesh.setValid(true);
		final WorldProvider provider = chunk.getProvider();
		if (chunk.offset.y >= provider.getInfo().maxHeight) {
			chunkMesh.setCompiled(true);
			return;
		}
		if (chunk.offset.y < provider.getInfo().minHeight) {
			chunkMesh.setCompiled(true);
			return;
		}

		if (!chunk.isAvailable()) {
			chunk.request();
			return;
		}
		if (!chunk.isNeighborsAvailable()) {
			chunk.requestNeighbors();
			return;
		}
		final ChunkData data = chunk.getData();
		neighbors.setProvider(provider);
		neighbors.setData(data);
		neighbors.loadNeighbors();
		neighbors.calcLight();
		neighbors.calcVisibility();
		// data.computeVisibility(provider);
		// updateVisibility();

		// droppedEntityVertexCount = 0;
		// if (!data.droppedEntities.isEmpty()) {
		// droppedMeshes = new ImmModeMesh[data.droppedEntities.size()];
		// int i = 0;
		// for (DroppedEntity entity : data.droppedEntities) {
		// droppedMeshes[i] = compileDroppedEntity(entity);
		// droppedEntityVertexCount += droppedMeshes[i].getVertexCount();
		// i++;
		// }
		// }
		//
		// vertexCount = droppedEntityVertexCount;
		vertexCount = 0;
		vertexOpaqueCount = 0;
		for (currentId = 0; currentId < 4096; currentId++) {
			if (neighbors.isVisible(currentId)) {
				prepareRenderer(provider, chunk, data);
				if (null != currentRenderer) {
					if (currentType.isOpaque()) {
						vertexOpaqueCount += currentRenderer.getMeshVertexCount(currentType, renderLocation, useShader);
					} else {
						vertexOpaqueCount += currentRenderer.getMeshVertexCount(currentType, renderLocation, useShader);
						//vertexCount += currentRenderer.getMeshVertexCount(currentType, renderLocation, useShader);
					}
				}
			}
		}
		if (vertexCount > 0) {
			mesh = ImmModeMesh.allocate(vertexCount);
			// mesh.setServer(true);
			mesh.setGLSL(true);
			mesh.enableColor4();
			mesh.enableTexCoord2();
			mesh.enableVertex3();
			// mesh.enableNormal3();
			mesh.beginQuads();
		} else {
			mesh = null;
		}
		if (vertexOpaqueCount > 0) {
			opaqueMesh = ImmModeMesh.allocate(vertexOpaqueCount);
			// opaqueMesh.setServer(true);
			opaqueMesh.setGLSL(true);
			opaqueMesh.enableColor4();
			opaqueMesh.enableTexCoord2();
			opaqueMesh.enableVertex3();
			// opaqueMesh.enableNormal3();
			opaqueMesh.beginQuads();
		} else {
			opaqueMesh = null;
		}
		for (currentId = 0; currentId < 4096; currentId++) {
			if (neighbors.isVisible(currentId)) {
				prepareRenderer(provider, chunk, data);
				//if (currentType.isOpaque()) {
					visibleOpaqueCount++;
					if (vertexOpaqueCount > 0) {
						currentRenderer.putMesh(opaqueMesh, currentType, renderLocation, useShader);
					}
				//} else {
				//	if (vertexCount > 0) {
				//		currentRenderer.putMesh(mesh, currentType, renderLocation, useShader);
				//	}
				//}
			}
		}
		if (vertexCount > 0) {
			mesh.end();
			mesh.compact();
		}
		if (vertexOpaqueCount > 0) {
			opaqueMesh.end();
			opaqueMesh.compact();
		}
		chunkMesh.setMesh(mesh);
		chunkMesh.setOpaqueMesh(opaqueMesh);
	}

	public ImmModeMesh getOpaqueMesh() {
		return opaqueMesh;
	}

	public ImmModeMesh getMesh() {
		return mesh;
	}

	public ImmModeMesh compileDroppedEntity(DroppedEntity entity) {
		final BlockType type = entity.stack.type;
		final RenderLocation location = new RenderLocation(entity.location);
		// int id = location.clampId();
		// System.out.println(chunk.location);
		final AbstractBlockRenderer renderer = type.getRenderer();
		renderer.setScale(0.3f);
		renderer.renderAllSides();
		return renderer.getMesh(type, location, useShader);
	}

	public void render(int pass) {

		if (!isCompiled || isCompiling || !isAvailable) {
			// compile();
			return;
		}
		if (pass == ChunkRangeRenderer.OPAQUE_PASS) {
			if (null != opaqueMesh) {
				opaqueMesh.draw();
			} else if (null != prevOpaqueMesh) {
				prevOpaqueMesh.draw();
			}
		} else {
			if (null != mesh) {
				mesh.draw();
			} else if (null != prevMesh) {
				prevMesh.draw();
			}
		}
		/*
		 * if (!disposable.isEmpty()) {
		 * for (ImmModeMesh mesh : disposable) {
		 * mesh.dispose();
		 * }
		 * disposable.clear();
		 * }
		 */
	}

	public static void disableShader() {
		getShader().disable();
	}

	public static void enableShader() {
		// GL2ES2 gl = GLContext.getCurrentGL().getGL2ES2();
		getShader().enable();
		// Game.client.getScene().time.getClearColor();
		// getShader().getState().uniform(gl, pmvMatrixUniform);
		// getShader().getState().uniform(gl, sunColor);
	}

	public void destroy() {
		isCompiling = true;
		mesh = null;
		prevMesh = null;
		meshes = null;
		needRecompilation = true;
		isCompiled = false;
		isCompiling = false;
	}
}
