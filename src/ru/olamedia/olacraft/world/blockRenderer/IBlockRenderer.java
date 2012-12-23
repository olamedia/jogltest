package ru.olamedia.olacraft.world.blockRenderer;

import ru.olamedia.geom.ImmModeMesh;
import ru.olamedia.olacraft.world.blockTypes.BlockType;
import ru.olamedia.olacraft.world.location.Location3f;
import ru.olamedia.olacraft.world.location.Location3i;

public interface IBlockRenderer {

	public void render(BlockType type, Location3i location, boolean glsl);

	public void render(BlockType type, Location3f location, boolean glsl);

	public ImmModeMesh getMesh(BlockType type, Location3f location, boolean glsl);
}
