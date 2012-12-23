package ru.olamedia.olacraft.world.calc;

import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.data.ChunkDataNeighbors;
import ru.olamedia.olacraft.world.location.IntLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class VisibilityCalculator {
	/**
	 * Compute visibility ("visible" if any side have non-opaque neighbor)
	 * Leaving side blocks invisible. Use computeVisibility(WorldProvider) to
	 * compute visibility of side blocks
	 */
	public static void precomputeVisibility(ChunkDataNeighbors neighbors) {
		final VisibilityData v = neighbors.getCenter().getVisibility();
		// first pass, make all blocks invisible, except side blocks
		v.setAllInvisible();
		// second pass, make some blocks visible
		short id = 0;
		for (byte x = 0; x <= 15; x++) {
			for (byte y = 0; y <= 15; y++) {
				for (byte z = 0; z <= 15; z++) {
					if (!neighbors.isOpaque(id)) {
						setVisible(v, (byte) (x - 1), y, z);
						setVisible(v, (byte) (x + 1), y, z);
						setVisible(v, x, (byte) (y - 1), z);
						setVisible(v, x, (byte) (y + 1), z);
						setVisible(v, x, y, (byte) (z - 1));
						setVisible(v, x, y, (byte) (z + 1));
					}
					id++;
				}
			}
		}
	}

	public static void computeVisibility(VisibilityData v, WorldProvider provider, ChunkData left, ChunkData right,
			ChunkData top, ChunkData bottom, ChunkData front, ChunkData back) {
		// compute left/right
		byte x, y, z;
		short id;
		for (y = 0; y <= 15; y++) {
			for (z = 0; z <= 15; z++) {
				x = 15;
				id = IntLocation.id(x, y, z);
				if (!left.isOpaque(provider, id)) {
					x = 0;
					setVisible(v, x, y, z);
				}
				x = 0;
				id = IntLocation.id(x, y, z);
				if (!right.isOpaque(provider, id)) {
					x = 15;
					setVisible(v, x, y, z);
				}
			}
		}
		// top/bottom
		for (x = 0; x <= 15; x++) {
			for (z = 0; z <= 15; z++) {
				y = 15;
				id = IntLocation.id(x, y, z);
				if (!bottom.isOpaque(provider, id)) {
					y = 0;
					setVisible(v, x, y, z);
				}
				y = 0;
				id = IntLocation.id(x, y, z);
				if (!top.isOpaque(provider, id)) {
					y = 15;
					setVisible(v, x, y, z);
				}
			}
		}
		// front/back
		for (x = 0; x <= 15; x++) {
			for (y = 0; y <= 15; y++) {
				z = 15;
				id = IntLocation.id(x, y, z);
				if (!back.isOpaque(provider, id)) {
					z = 0;
					setVisible(v, x, y, z);
				}
				z = 0;
				id = IntLocation.id(x, y, z);
				if (!front.isOpaque(provider, id)) {
					z = 15;
					setVisible(v, x, y, z);
				}
			}
		}
	}

	private static void setVisible(VisibilityData v, byte x, byte y, byte z) {
		if (IntLocation.inRange(x, y, z)) {
			v.setVisible(IntLocation.id(x, y, z));
		}
	}
}
