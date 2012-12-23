package ru.olamedia.olacraft.world.calc;

public class LightData {
	private byte[] light = new byte[4096];

	public byte getVoidLight(short id) {
		return (byte) (light[id] & 15);
	}

	public void setVoidLight(short id, byte value) {
		if (value != getVoidLight(id)) {
			// voidLightChanged = true;
		}
		light[id] = (byte) ((getEmitLight(id) << 4) | value);
	}

	public byte getEmitLight(short id) {
		return (byte) ((light[id] >> 4) & 15);
	}

	public void setEmitLight(short id, byte value) {
		light[id] = (byte) ((light[id] & 15) | (value << 4));
	}

	public void reset() {
		light = null;
	}
}
