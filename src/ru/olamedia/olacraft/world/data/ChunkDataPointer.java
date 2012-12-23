package ru.olamedia.olacraft.world.data;

import ru.olamedia.olacraft.world.chunk.Chunk;

/**
 * pointer.reset()
 * while (pointer.hasNext()){
 * 		int id = pointer.getId()
 * 		...
 * 		pointer.next()
 * }
 * @author olamedia
 *
 */
public class ChunkDataPointer {
	private int x = 0;
	private int y = 0;
	private int z = 0;
	private int id = 0;

	public ChunkDataPointer() {
		reset();
	}

	public ChunkDataPointer(int x, int y, int z) {
		if (x >= 0 && x < 16) {
			if (y >= 0 && y < 16) {
				if (z >= 0 && z < 16) {
					this.x = x;
					this.y = y;
					this.z = z;
					id = this.x * 16 * 16 + this.y * 16 + this.z;
					return;
				}
			}
		}
		this.x = Chunk.in(x);
		this.y = Chunk.in(y);
		this.z = Chunk.in(z);
		id = this.x * 16 * 16 + this.y * 16 + this.z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getId() {
		return id;
	}

	public void reset() {
		x = 0;
		y = 0;
		z = 0;
		id = 0;
	}

	public boolean hasNext() {
		return id < 4096;
	}

	public void next() {
		id++;
		x++;
		if (x > 15) {
			x = 0;
			y++;
			if (y > 15) {
				y = 0;
				z++;
			}
		}
	}
}
