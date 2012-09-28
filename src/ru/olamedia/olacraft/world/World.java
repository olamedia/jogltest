package ru.olamedia.olacraft.world;

import ru.olamedia.olacraft.world.block.BlockRegistry;
import ru.olamedia.olacraft.world.blockTypes.GrassBlockType;

public class World {
	private BlockRegistry blockRegistry;
	public void setup() {
		blockRegistry = new BlockRegistry();
		blockRegistry.registerBlockType(GrassBlockType.class);
	}
}
