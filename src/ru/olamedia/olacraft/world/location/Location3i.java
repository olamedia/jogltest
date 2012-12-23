package ru.olamedia.olacraft.world.location;

public class Location3i {
	public int x;
	public int y;
	public int z;

	public byte getByteX() {
		return (byte) (x & 15);
	}

	public byte getByteY() {
		return (byte) (y & 15);
	}

	public byte getByteZ() {
		return (byte) (z & 15);
	}

	public Location3i() {
		x = 0;
		y = 0;
		z = 0;
	}

	public Location3i(float x, float y, float z) {
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
	}

	public Location3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Location3i(Location3i location) {
		x = location.x;
		y = location.y;
		z = location.z;
	}

	public Location3i(Location3f location) {
		x = (int) location.x;
		y = (int) location.y;
		z = (int) location.z;
	}

	public void set(Location3i location) {
		this.x = location.x;
		this.y = location.y;
		this.z = location.z;
	}

	public void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(float x, float y, float z) {
		this.x = (int) x;
		this.y = (int) y;
		this.z = (int) z;
	}

	public String toString() {
		return "Location3i[" + x + "," + y + "," + z + "]";
	}
}
