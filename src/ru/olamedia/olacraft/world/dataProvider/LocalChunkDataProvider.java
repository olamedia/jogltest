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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import ru.olamedia.olacraft.world.data.RegionData;
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

	@SuppressWarnings("unused")
	@Override
	public RegionData getRegion(RegionLocation regionLocation) {
		String filename = path + File.separator + regionLocation.getFilename();
		RegionData data = null;
		if (true) {
			return generateRegion(regionLocation);
		}
		File chunkFile = new File(filename);
		if (false && chunkFile.exists()) {
			InputStream in;
			try {
				FileInputStream fIn = new FileInputStream(chunkFile);
				in = new GZIPInputStream(fIn);
				data = RegionData.loadFrom(in);
				in.close();
				fIn.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			data = generateRegion(regionLocation);
			OutputStream out;
			try {
				chunkFile.createNewFile();
				FileOutputStream fOut = new FileOutputStream(chunkFile);
				out = new GZIPOutputStream(fOut);
				data.writeTo(out);
				out.close();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public RegionData generateRegion(RegionLocation regionLocation) {
		RegionData data = new RegionData();
		data.location = regionLocation;
		RegionGenerator generator = new RegionGenerator();
		try {
			generator.setSeed(getSeed());
		} catch (IOException e) {
			e.printStackTrace();
		}
		generator.generate(data);
		return data;
	}

}
