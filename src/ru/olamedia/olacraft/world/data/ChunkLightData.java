package ru.olamedia.olacraft.world.data;

import ru.olamedia.olacraft.world.dataProvider.AbstractChunkDataProvider;

/**
 * The daylight calculated as sum of sunlight + emitted light
 * The nightlight calculated as sum of emitted light
 * 
 * The light at some time of a day calculated as part of daylight + part of
 * nightlight (ex 30% of night + 70% of daylight) - sum of two array's elements
 */
public class ChunkLightData {
	public static int SIZE = 4096;
	/**
	 * Constant sunlight level
	 */
	public byte[] sunLevel = new byte[SIZE];
	/**
	 * Constant sunlight level
	 */
	public byte[] emittedLevel = new byte[SIZE];
	/**
	 * Constant light level during middle of a day
	 */
	public byte[] daytimeLevel = new byte[SIZE];
	/**
	 * Constant light level during midnight
	 */
	public byte[] nighttimeLevel = new byte[SIZE];
	public boolean isCalculated = false;
	public boolean isSunlevelCalculated = false;
	public byte[] level = new byte[SIZE];

	public static int normalize(int v) {
		int n = v;
		if (n > 15) {
			n = n % 16;
		}
		if (n < 0) {
			n = 16 + n % 16 - 1;
			// v = 15 - v;
		}
		// System.out.println("normalize(" + v + ") = " + n);
		return n;
	}

	public static int getId(int xInsideChunk, int yInsideChunk, int zInsideChunk) {
		xInsideChunk = normalize(xInsideChunk);
		yInsideChunk = normalize(yInsideChunk);
		zInsideChunk = normalize(zInsideChunk);
		int id = xInsideChunk * 16 * 16 + yInsideChunk * 16 + zInsideChunk;
		if (id > SIZE) {
			System.err.println("Exception while getID(" + xInsideChunk + "," + yInsideChunk + "," + zInsideChunk + ")");
			throw new ArrayIndexOutOfBoundsException(id);
		}
		return id;
	}

	private static byte sunlight = 15;

	public void fillSunlight() {
		// simplify: straight from top to bottom, utility to fill top layer
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				sunLevel[getId(x, 15, z)] = sunlight;
			}
		}
	}

	public void copySunlightFromAbove(ChunkLightData above) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				sunLevel[getId(x, 15, z)] = above.sunLevel[getId(x, 0, z)];
			}
		}
		System.out.print("Copy sunlight");
	}

	/**
	 * Sunlight falling down until meets nonempty block from data
	 * 
	 * @param data
	 */
	public void falldownSunlight(ChunkData data) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 14; y >= 0; y--) {
					if (!data.isEmpty(getId(x, y, z))) {
						break;
					} else {
						sunLevel[getId(x, y, z)] = sunLevel[getId(x, y + 1, z)];
					}
				}
			}
		}
	}

	public void receiveNeighborLight(AbstractChunkDataProvider abstractChunkDataProvider) {

	}

}
