package ru.olamedia.olacraft.world.data;

import ru.olamedia.math.OpenBitSet;
import ru.olamedia.olacraft.world.calc.LightData;
import ru.olamedia.olacraft.world.calc.VisibilityData;

public class ChunkDataWrapper {
	private ChunkData data;
	private LightData light;
	private VisibilityData visibility;

	public ChunkDataWrapper() {
		light = new LightData();
		visibility = new VisibilityData();
	}

	public void setData(ChunkData data) {
		this.data = data;
	}

	public ChunkData getData() {
		return data;
	}

	public LightData getLight() {
		return light;
	}

	public VisibilityData getVisibility() {
		return visibility;
	}

	public void reset() {
		data = null;
		light.reset();
		light = null;
		visibility.reset();
		visibility = null;
	}
}
