package ru.olamedia.olacraft.world.blockTypes;

public class GravelBlockType extends AbstractBlockType {
	@Override
	public String getName() {
		return "Gravel";
	}

	@Override
	public int getMaxStack() {
		return 64;
	}

	@Override
	public String getStackTextureFile() {
		return "texture/gravel.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/gravel.png";
	}
}
