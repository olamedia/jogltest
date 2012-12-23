package ru.olamedia.olacraft.world.blockTypes.stone;

import ru.olamedia.olacraft.world.blockTypes.AbstractBlockType;

public class BrecciaStoneBlockType extends AbstractBlockType {
	@Override
	public String getName() {
		return "Breccia";
	}

	@Override
	public int getMaxStack() {
		return 64;
	}

	@Override
	public String getStackTextureFile() {
		return "texture/stone/sedimentary/breccia.png";
	}

	@Override
	public String getTopTextureFile() {
		return "texture/stone/sedimentary/breccia.png";
	}
}
