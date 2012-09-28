package ru.olamedia.olacraft.world.provider;

import ru.olamedia.olacraft.world.block.Block;

public interface IChunkProvider {
	public boolean isEmptyBlock(int x, int y, int z);
	public Block getBlock(int x, int y, int z);
}
