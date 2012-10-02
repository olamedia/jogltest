package ru.olamedia.olacraft.world.data;

import java.io.Serializable;

/**
 * Heightmap
 * Useful when looking for spawn location, calculating light
 * 
 * @author olamedia
 * 
 */
public class HeightMap implements Serializable {
	private static final long serialVersionUID = -6777972159522169977L;
	public byte[][] map; // -128..127
	public int width;
	public int height;
	public HeightMap(){
		
	}
	public HeightMap(int width, int height) {
		this.width = width;
		this.height = height;
		map = new byte[width][height];
	}

	public void setHeight(int x, int y, int height) {
		map[x][y] = (byte) height;
	}

	public int getHeight(int x, int y) {
		return map[x][y];
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int x = 0; x < map.length; x++){
			for (int z = 0; z < map[x].length; z++){
				b.append(map[x][z]);
				b.append(",");
			}
			b.append("\n");
		}
		return b.toString();
	}
}
