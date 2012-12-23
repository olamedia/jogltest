package ru.olamedia.olacraft.world.blockTypes;

import ru.olamedia.olacraft.world.blockRenderer.AbstractBlockRenderer;
import ru.olamedia.olacraft.world.blockRenderer.CrossQuadsRenderer;

public class TallGrassBlockType extends AbstractBlockType {

	protected AbstractBlockRenderer renderer = new CrossQuadsRenderer();

	@Override
	public AbstractBlockRenderer getRenderer() {
		return this.renderer;
	}

	@Override
	public String getName() {
		return "Tall Grass";
	}

	@Override
	public boolean canMoveThrough() {
		return true;
	}

	public boolean isLoose() {
		return false;
	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	@Override
	public int getMaxStack() {
		return 64;
	}

	@Override
	public String getStackTextureFile() {
		return "texture/terrain-wheatgrass-lightgreen-med.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/terrain-wheatgrass-lightgreen-med.png";
	}

	@Override
	public String getFrontTextureFile() {
		return "texture/terrain-wheatgrass-lightgreen-med.png";
	}

	@Override
	public boolean isTimeManaged() {
		return true;
	}
}
