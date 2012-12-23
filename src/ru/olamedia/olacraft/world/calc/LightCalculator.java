package ru.olamedia.olacraft.world.calc;

import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.data.ChunkDataNeighbors;
import ru.olamedia.olacraft.world.data.ChunkDataWrapper;
import ru.olamedia.olacraft.world.location.IntLocation;

public class LightCalculator {

	private static final byte fallingLight = 15;
	private static boolean exposed = true;

	public static void calculateVoidLightIgnoreNeighbors(ChunkDataWrapper dataWrapper) {
		precalculateVoidLight(dataWrapper);
		spreadVoidLightIgnoreNeighbors(dataWrapper);
	}

	public static void precalculateVoidLight(ChunkDataWrapper dataWrapper) {
		final ChunkData data = dataWrapper.getData();
		final LightData light = dataWrapper.getLight();
		for (int x = 0; x <= 15; x++) {
			for (int z = 0; z <= 15; z++) {
				// FIXME (heightmap)
				// falling from top
				exposed = true;
				for (byte y = 15; y >= 0; y--) {
					final short id = IntLocation.id(x, y, z);
					if (data.isEmpty(id)) {
						if (exposed) {
							light.setVoidLight(id, fallingLight);
						} else {
							light.setVoidLight(id, (byte) 0);
						}
					} else {
						exposed = false;
						light.setVoidLight(id, (byte) 0);
					}
				}
			}
		}
	}

	public static byte max(byte a, byte b, byte c, byte d) {
		return max(max(a, b), max(c, d));
	}

	public static byte max(byte a, byte b) {
		return a > b ? a : b;
	}

	public static void spreadVoidLightIgnoreNeighbors(ChunkDataWrapper dataWrapper) {
		final ChunkData data = dataWrapper.getData();
		final LightData light = dataWrapper.getLight();
		for (byte i = 0; i < 15; i++) {
			for (byte x = 0; x <= 15; x++) {
				for (byte z = 0; z <= 15; z++) {
					for (byte y = 0; y <= 15; y++) {
						final short id = IntLocation.id(x, y, z);
						if (data.isEmpty(id)) {
							final byte current = light.getVoidLight(id);
							if (current < 15) {
								final byte top = (y == 15) ? 0 : (byte) (light
										.getVoidLight(IntLocation.id(x, y + 1, z)) - 1);
								final byte bottom = (y == 0) ? 0 : (byte) (light.getVoidLight(IntLocation.id(x, y - 1,
										z)) - 1);
								final byte left = (x == 0) ? 0 : (byte) (light
										.getVoidLight(IntLocation.id(x - 1, y, z)) - 1);
								final byte right = (x == 15) ? 0 : (byte) (light.getVoidLight(IntLocation.id(x + 1, y,
										z)) - 1);
								final byte front = (z == 15) ? 0 : (byte) (light.getVoidLight(IntLocation.id(x, y,
										z + 1)) - 1);
								final byte back = (z == 0) ? 0 : (byte) (light
										.getVoidLight(IntLocation.id(x, y, z - 1)) - 1);
								light.setVoidLight(id,
										max(max(top, bottom, left, right), max(max(front, back), current)));
							}
						}
					}
				}
			}
		}
	}

	public static void spreadVoidLight(ChunkDataNeighbors neighbors) {
		final LightData light = neighbors.getCenter().getLight();
		for (byte x = 0; x <= 15; x++) {
			for (byte z = 0; z <= 15; z++) {
				for (byte y = 0; y <= 15; y++) {
					final LightData topData = y == 15 ? neighbors.getTop().getLight() : light;
					final LightData bottomData = y == 0 ? neighbors.getBottom().getLight() : light;
					final LightData leftData = x == 0 ? neighbors.getLeft().getLight() : light;
					final LightData rightData = x == 15 ? neighbors.getRight().getLight() : light;
					final LightData frontData = z == 15 ? neighbors.getFront().getLight() : light;
					final LightData backData = z == 0 ? neighbors.getBack().getLight() : light;
					final short id = IntLocation.id(x, y, z);
					if (neighbors.getCenterData().isEmpty(id)) {
						final byte current = light.getVoidLight(id);
						if (current < 15) {
							final byte top = (byte) (topData.getVoidLight(IntLocation.id(x, y + 1, z)) - 1);
							final byte bottom = (byte) (bottomData.getVoidLight(IntLocation.id(x, y - 1, z)) - 1);
							final byte left = (byte) (leftData.getVoidLight(IntLocation.id(x - 1, y, z)) - 1);
							final byte right = (byte) (rightData.getVoidLight(IntLocation.id(x + 1, y, z)) - 1);
							final byte front = (byte) (frontData.getVoidLight(IntLocation.id(x, y, z + 1)) - 1);
							final byte back = (byte) (backData.getVoidLight(IntLocation.id(x, y, z - 1)) - 1);
							final byte tb = (top > bottom ? top : bottom);
							final byte lr = (left > right ? left : right);
							final byte fb = (front > back ? front : back);
							final byte tblr = (tb > lr ? tb : lr);
							final byte fbc = (fb > current ? fb : current);
							light.setVoidLight(id, (tblr > fbc ? tblr : fbc));
						}
					}
				}
			}
		}
	}

}
