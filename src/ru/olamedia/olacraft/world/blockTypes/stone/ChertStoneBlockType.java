package ru.olamedia.olacraft.world.blockTypes.stone;

import ru.olamedia.olacraft.world.blockTypes.AbstractBlockType;

public class ChertStoneBlockType extends AbstractBlockType {
	@Override
	public String getName() {
		return "Chert";
	}

	@Override
	public int getMaxStack() {
		return 64;
	}

	@Override
	public String getStackTextureFile() {
		return "texture/stone/sedimentary/chert.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/stone/sedimentary/chert.png";
	}
}
