package ru.olamedia.olacraft.world.blockRenderer;

import java.nio.FloatBuffer;

import ru.olamedia.geom.ImmModeMesh;
import ru.olamedia.olacraft.world.blockTypes.BlockType;
import ru.olamedia.olacraft.world.data.ChunkDataNeighbors;
import ru.olamedia.olacraft.world.location.Location3f;
import ru.olamedia.olacraft.world.location.Location3i;

public abstract class AbstractBlockRenderer implements IBlockRenderer {

	public boolean renderBottom = true;
	public boolean renderTop = true;
	public boolean renderLeft = true;
	public boolean renderRight = true;
	public boolean renderFront = true;
	public boolean renderBack = true;
	public float scale = 1f;
	private ChunkDataNeighbors neighbors;

	protected ImmModeMesh mesh = null;

	public void renderAllSides() {
		renderBottom = true;
		renderTop = true;
		renderLeft = true;
		renderRight = true;
		renderFront = true;
		renderBack = true;
	}

	abstract public void putMesh(ImmModeMesh mesh, BlockType type, FloatBuffer renderLocation, boolean useShader);

	public ImmModeMesh getMesh(BlockType type, FloatBuffer renderLocation, boolean useShader) {
		return mesh;
	}

	public ImmModeMesh getMesh(BlockType type, Location3f location, boolean glsl) {
		return mesh;
	}

	@Override
	public void render(BlockType type, Location3f location, boolean glsl) {
		getMesh(type, location, glsl).draw();
	}

	@Override
	public void render(BlockType type, Location3i location, boolean glsl) {
		getMesh(type, new Location3f(location), glsl).draw();
	}

	public void setScale(float f) {
		scale = f;
	}

	abstract public int getMeshVertexCount(BlockType currentType, FloatBuffer renderLocation,
			boolean useShader);

	public void setNeighbors(ChunkDataNeighbors neighbors) {
		this.neighbors = neighbors;
	}

}
