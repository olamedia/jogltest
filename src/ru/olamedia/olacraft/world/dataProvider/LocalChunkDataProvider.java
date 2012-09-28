package ru.olamedia.olacraft.world.dataProvider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.data.RegionData;
import ru.olamedia.olacraft.world.generator.HeightMapGenerator;
import ru.olamedia.olacraft.world.generator.RegionGenerator;
import ru.olamedia.olacraft.world.location.RegionLocation;

public class LocalChunkDataProvider extends AbstractChunkDataProvider {
	private String worldName;
	private String path;

	private static boolean DEBUG = true;

	private void debug(String s) {
		if (DEBUG) {
			System.out.println("[LocalChunkDataProvider] " + s);
		}
	}

	public LocalChunkDataProvider(String worldName) {
		this.worldName = worldName;
		path = "data" + File.separator + this.worldName;
		File worldDir = new File(path);
		if (!worldDir.isDirectory()) {
			worldDir.mkdirs();
		}
	}

	private int[] seed;

	private int[] getSeed() throws IOException {
		if (null == seed) {
			String filename = path + File.separator + "world.seed";
			File seedFile = new File(filename);
			// md5 - 32x0-f = 16 bytes or 8 short int
			seed = new int[8];
			if (seedFile.exists()) {
				InputStream in = null;
				DataInputStream din = null;
				in = new FileInputStream(seedFile);
				din = new DataInputStream(in);
				for (int i = 0; i < 8; i++) {
					seed[i] = din.readShort();
				}
				din.close();
				in.close();
			} else {
				OutputStream out = new FileOutputStream(seedFile);
				DataOutputStream dout = new DataOutputStream(out);
				for (int i = 0; i < 8; i++) {
					seed[i] = (int) (Integer.MAX_VALUE * Math.random());
					dout.writeShort(seed[i]);
				}
				dout.close();
				out.close();
			}
		}
		return seed;
	}

	@Override
	public boolean isRegionAvailable(RegionLocation regionLocation) {
		return true;
	}

	@Override
	public void loadRegion(RegionLocation regionLocation) {
		// do nothing...
		debug("loadRegion(" + regionLocation + ")");
	}

	private ChunkData createChunk(int chunkX, int chunkY, int chunkZ) {
		debug("createChunk " + chunkX + " " + chunkY + " " + chunkZ);
		ChunkData data = new ChunkData();
		try {
			getSeed();
		} catch (IOException e) {
			e.printStackTrace();
		}
		HeightMapGenerator.minValue = 1;
		HeightMapGenerator.maxValue = 64;
		HeightMapGenerator.init();
		HeightMapGenerator.seed = seed[0];
		int[][] heightMap = HeightMapGenerator.getChunkHeightMap(chunkX, chunkZ);
		for (int y = 0; y < 16; y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					data.setEmpty(ChunkData.getId(x, y, z), (heightMap[x][z] < chunkY * 16 + y));
				}
			}
		}
		return data;
	}

	@Override
	public RegionData getRegion(RegionLocation regionLocation) {
		String filename = path + File.separator + regionLocation.getFilename();
		RegionData data = null;
		// TODO READ/WRITE FILE
		File chunkFile = new File(filename);
		if (chunkFile.exists()) {
			InputStream in;
			try {
				in = new FileInputStream(chunkFile);
				data = RegionData.loadFrom(in);
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			data = generateRegion(regionLocation);
			try {
				chunkFile.createNewFile();
				FileOutputStream out = new FileOutputStream(chunkFile);
				data.writeTo(out);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public RegionData generateRegion(RegionLocation regionLocation) {
		RegionData data = new RegionData();
		data.location = regionLocation;
		// TODO FILL HERE
		RegionGenerator generator = new RegionGenerator();
		try {
			generator.setSeed(getSeed());
		} catch (IOException e) {
			e.printStackTrace();
		}
		generator.generate(data);
		return data;
	}

	public ChunkData get(int chunkX, int chunkY, int chunkZ) {
		debug("get " + chunkX + " " + chunkY + " " + chunkZ);
		ChunkData data = null;
		String filename = path + File.separator + chunkX + "_" + chunkY + "_" + chunkZ + ".chunk";
		/*
		 * File chunkFile = new File(filename);
		 * if (chunkFile.exists()) {
		 * try {
		 * InputStream in = new FileInputStream(chunkFile);
		 * DataInputStream din = new DataInputStream(in);
		 * data = new ChunkData();
		 * data.readFrom(din);
		 * din.close();
		 * in.close();
		 * } catch (FileNotFoundException e) {
		 * e.printStackTrace();
		 * } catch (IOException e) {
		 * e.printStackTrace();
		 * }
		 * } else {
		 */
		data = createChunk(chunkX, chunkY, chunkZ);
		/*
		 * OutputStream out = null;
		 * ByteArrayOutputStream bout = null;
		 * DataOutputStream dout = null;
		 * try {
		 * chunkFile.createNewFile();
		 * out = new FileOutputStream(chunkFile);
		 * // bout = new ByteArrayOutputStream(4096);
		 * dout = new DataOutputStream(out);
		 * data.writeTo(dout);
		 * // dout.flush();
		 * // out.write(bout.toByteArray());
		 * out.flush();
		 * } catch (IOException e) {
		 * e.printStackTrace();
		 * } finally {
		 * if (null != dout) {
		 * try {
		 * dout.close();
		 * } catch (IOException e) {
		 * e.printStackTrace();
		 * }
		 * }
		 * if (null != bout) {
		 * try {
		 * bout.close();
		 * } catch (IOException e) {
		 * e.printStackTrace();
		 * }
		 * }
		 * if (null != out) {
		 * try {
		 * out.close();
		 * } catch (IOException e) {
		 * e.printStackTrace();
		 * }
		 * }
		 * }
		 * }
		 */
		return data;
	}

}
