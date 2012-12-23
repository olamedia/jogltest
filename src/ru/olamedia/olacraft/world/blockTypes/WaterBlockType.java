package ru.olamedia.olacraft.world.blockTypes;

public class WaterBlockType extends AbstractBlockType {
	@Override
	public String getName() {
		return "Water";
	}

	public boolean isLoose() {
		return true;
	}

	@Override
	public int getMaxStack() {
		return 1;
	}
	
	@Override
	public boolean isOpaque() {
		return false;
	}

	@Override
	public boolean hideTouchedSides() {
		return true;
	}
	
	@Override
	public boolean canMoveThrough() {
		return true;
	}
	
	@Override
	public String getStackTextureFile() {
		return "texture/water.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/water.png";
	}
}
