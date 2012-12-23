package ru.olamedia.olacraft.world.blockTypes.stone;

import ru.olamedia.olacraft.world.blockTypes.AbstractBlockType;

public class ShaleStoneBlockType extends AbstractBlockType {
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
		return "texture/stone/sedimentary/shale.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/stone/sedimentary/shale.png";
	}
}
