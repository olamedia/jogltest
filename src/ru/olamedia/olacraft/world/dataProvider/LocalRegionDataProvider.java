package ru.olamedia.olacraft.world.dataProvider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LocalRegionDataProvider {
	private String worldName;
	private String path;

	public LocalRegionDataProvider(String worldName) {
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

}
