package ru.olamedia.olacraft.world.location;

public class Location3f {
	public float x;
	public float y;
	public float z;

	public Location3f() {
		x = 0;
		y = 0;
		z = 0;
	}

	public Location3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Location3f(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Location3f(Location3f location) {
		x = location.x;
		y = location.y;
		z = location.z;
	}

	public Location3f(Location3i location) {
		x = location.x;
		y = location.y;
		z = location.z;
	}

	public String toString() {
		return "Location3f[" + x + "," + y + "," + z + "]";
	}

	public void addRandomOffset(float d) {
		x += Math.random() * d * 2 - d;
		z += Math.random() * d * 2 - d;
	}
}
