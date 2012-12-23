package ru.olamedia.olacraft.world.blockTypes;

public class EmptyBlockType extends AbstractBlockType {
	@Override
	public String getName() {
		return "Empty";
	}

	@Override
	public int getMaxStack() {
		return 64;
	}

	@Override
	public String getStackTextureFile() {
		return "texture/empty.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/empty.png";
	}
	@Override
	public boolean isOpaque() {
		return false;
	}
}
