package ru.olamedia.olacraft.world.blockTypes.stone;

import ru.olamedia.olacraft.world.blockTypes.AbstractBlockType;

public class SandstoneStoneBlockType extends AbstractBlockType {
	@Override
	public String getName() {
		return "Sandstone";
	}

	@Override
	public int getMaxStack() {
		return 64;
	}

	@Override
	public String getStackTextureFile() {
		return "texture/stone/sedimentary/sandstone.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/stone/sedimentary/sandstone.png";
	}
}
