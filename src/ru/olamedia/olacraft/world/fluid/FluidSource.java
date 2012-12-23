package ru.olamedia.olacraft.world.fluid;

import java.util.HashMap;
import java.util.List;

import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class FluidSource {
	public HashMap<Integer, FluidLevel> levels = new HashMap<Integer, FluidLevel>();
	int minY = 0;
	int maxY = 0;

	public FluidSource(int y) {
		minY = y;
		maxY = y;
	}

	public void flow(WorldProvider provider, BlockLocation location, float amount) {
		ChunkData data = provider.getChunk(location.getChunkLocation());
		if (!data.isEmpty(location)) {
			return;
		}
		FluidLevel level = null;
		int y = location.y;
		while (null == level || amount > 0) {
			level = getFluidLevel(provider, location);
			if (null == level) {
				return;
			}
			if (level.size < amount) {
				level.fluidLevel = 1f;
				amount--;
			} else {
				level.fluidLevel = ((float) level.size) / amount;
			}
			levels.put(y, level);
			y++;
		}
	}

	private FluidLevel getFluidLevel(WorldProvider provider, BlockLocation location) {
		ChunkData data = provider.getChunk(location.getChunkLocation());
		if (!data.isEmpty(location)) {
			return null;
		}
		FluidLevel level = new FluidLevel();
		FluidLine startLine = getFluidLine(provider, location);
		
		if (location.z > 0) {
			BlockLocation test = new BlockLocation(location);
			for (test.z = location.z - 1; test.z >= 0; test.z--) {
				test.x = startLine.start;
				FluidLine nextLine = getFluidLine(provider, test);
				if (null != nextLine) {
					test.x = nextLine.finish + 2;
					if (test.x <= 15) {
						// continue
					}else{
						//
					}
				}
			}
		}
		return null;
	}

	private FluidLine getFluidLine(WorldProvider provider, BlockLocation location) {
		ChunkData data = provider.getChunk(location.getChunkLocation());
		if (!data.isEmpty(location)) {
			return null;
		}
		FluidLine line = new FluidLine(location);
		line.start = line.finish = location.x;
		BlockLocation test = new BlockLocation(location);
		if (location.x > 0) {
			for (test.x = location.x - 1; test.x >= 0; test.x--) {
				if (!data.isEmpty(test)) {
					break;
				}
				line.start = test.x;
			}
		} else {
			line.start = 0;
		}
		if (location.x < 15) {
			for (test.x = location.x + 1; test.x <= 15; test.x++) {
				if (!data.isEmpty(test)) {
					break;
				}
				line.finish = test.x;
			}
		} else {
			line.finish = 15;
		}
		return line;
	}
}
