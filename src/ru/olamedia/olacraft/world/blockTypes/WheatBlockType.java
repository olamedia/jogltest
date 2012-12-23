package ru.olamedia.olacraft.world.blockTypes;

import ru.olamedia.olacraft.world.blockRenderer.AbstractBlockRenderer;
import ru.olamedia.olacraft.world.blockRenderer.CrossQuadsRenderer;

public class WheatBlockType extends AbstractBlockType {

	protected AbstractBlockRenderer renderer = new CrossQuadsRenderer();

	@Override
	public AbstractBlockRenderer getRenderer() {
		return this.renderer;
	}

	@Override
	public String getName() {
		return "Wheat";
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
		return "texture/wheat.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/wheat.png";
	}

	@Override
	public String getFrontTextureFile() {
		return "texture/wheat.png";
	}

	@Override
	public boolean isTimeManaged() {
		return true;
	}
}
