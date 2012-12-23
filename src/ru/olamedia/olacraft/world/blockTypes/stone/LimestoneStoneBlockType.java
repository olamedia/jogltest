package ru.olamedia.olacraft.world.blockTypes.stone;

import ru.olamedia.olacraft.world.blockTypes.AbstractBlockType;

public class LimestoneStoneBlockType extends AbstractBlockType {
	@Override
	public String getName() {
		return "Limestone";
	}

	@Override
	public int getMaxStack() {
		return 64;
	}

	@Override
	public String getStackTextureFile() {
		return "texture/stone/sedimentary/limestone.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/stone/sedimentary/limestone.png";
	}
}
