package ru.olamedia.olacraft.world.blockTypes;

public class DirtBlockType extends AbstractBlockType {
	@Override
	public String getName() {
		return "Dirt";
	}

	public boolean isLoose() {
		return true;
	}

	@Override
	public int getMaxStack() {
		return 64;
	}

	@Override
	public String getStackTextureFile() {
		return "texture/dirt.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/dirt.png";
	}
}
