package ru.olamedia.olacraft.world.blockTypes;

public class StoneBlockType extends AbstractBlockType {
	@Override
	public String getName() {
		return "Stone";
	}

	@Override
	public int getMaxStack() {
		return 64;
	}

	@Override
	public String getStackTextureFile() {
		return "texture/terrain-cobb.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/terrain-cobb.png";
	}

	@Override
	public String getFrontTextureFile() {
		return "texture/terrain-cobb.png";
	}
}

