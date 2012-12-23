package ru.olamedia.olacraft.world.blockTypes.stone;

import ru.olamedia.olacraft.world.blockTypes.AbstractBlockType;

public class SiltstoneStoneBlockType extends AbstractBlockType {
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
		return "texture/stone/sedimentary/siltstone.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/stone/sedimentary/siltstone.png";
	}
}
