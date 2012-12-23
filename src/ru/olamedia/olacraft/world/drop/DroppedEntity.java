package ru.olamedia.olacraft.world.drop;

import ru.olamedia.olacraft.world.blockStack.BlockStack;
import ru.olamedia.olacraft.world.blockTypes.AbstractBlockType;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.location.Location3f;

public class DroppedEntity {
	public DroppedEntity(BlockLocation location, AbstractBlockType type, int count) {
		this.location = new Location3f(location);
		this.location.addRandomOffset(0.4f);
		stack = new BlockStack(type, count);
	}

	public Location3f location;
	public BlockStack stack;
}
