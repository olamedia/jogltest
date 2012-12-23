package ru.olamedia.olacraft.world.blockTypes;

public class IceBlockType extends AbstractBlockType {
	@Override
	public String getName() {
		return "Ice";
	}

	public boolean isLoose() {
		return true;
	}
	
	@Override
	public boolean isOpaque() {
		return true;
	}

	@Override
	public int getMaxStack() {
		return 1;
	}

	@Override
	public String getStackTextureFile() {
		return "texture/ice.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/ice.png";
	}
}
