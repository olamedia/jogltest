package ru.olamedia.olacraft.world.fluid;

import java.util.ArrayList;

import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class FluidLine {
	public int y;
	public int z;
	public int start;
	public int finish;
	public int size;

	public FluidLine(int x, int y, int z) {
		this.start = this.finish = x;
		this.y = y;
		this.z = z;
	}

	public FluidLine(BlockLocation location) {
		this.start = this.finish = location.x;
		this.y = location.y;
		this.z = location.z;
	}

	public ArrayList<FluidLine> getSideLines(WorldProvider provider) {
		ArrayList<FluidLine> sideLines;
		sideLines = getSideLines(provider, z + 1);
		sideLines.addAll(getSideLines(provider, z - 1));
		return sideLines;
	}

	public ArrayList<FluidLine> getSideLines(WorldProvider provider, int z) {
		ArrayList<FluidLine> sideLines = new ArrayList<FluidLine>();
		BlockLocation test = new BlockLocation(start, y, z);
		boolean found = false;
		FluidLine side = null;
		while (test.x < finish) {
			ChunkData data = provider.getChunk(test.getChunkLocation());
			if (data.isEmpty(test)) {
				found = true;
				side = new FluidLine(test);
			} else {
				if (found) {
					side.finish = test.x;
					sideLines.add(side);
					found = false;
				}
			}
			test.x++;
		}
		return sideLines;
	}
}
