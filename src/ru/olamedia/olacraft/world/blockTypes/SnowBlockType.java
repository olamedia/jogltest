package ru.olamedia.olacraft.world.blockTypes;

public class SnowBlockType extends AbstractBlockType {
	@Override
	public String getName() {
		return "Snow";
	}

	public boolean isLoose() {
		return true;
	}

	@Override
	public int getMaxStack() {
		return 1;
	}

	@Override
	public String getStackTextureFile() {
		return "texture/snow.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/snow.png";
	}
}
