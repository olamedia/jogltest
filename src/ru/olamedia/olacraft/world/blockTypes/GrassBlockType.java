package ru.olamedia.olacraft.world.blockTypes;

public class GrassBlockType extends AbstractBlockType {
	@Override
	public String getName() {
		return "Grass";
	}

	@Override
	public int getMaxStack() {
		return 64;
	}

	@Override
	public String getStackTextureFile() {
		return "texture/terrain-grassolive.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/terrain-grassolive.png";
	}

	@Override
	public String getFrontTextureFile() {
		return "texture/terrain-ledgeolive.png";
	}
}
