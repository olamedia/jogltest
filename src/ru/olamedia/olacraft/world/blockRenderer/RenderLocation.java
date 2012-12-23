package ru.olamedia.olacraft.world.blockRenderer;

import java.nio.IntBuffer;

import ru.olamedia.olacraft.world.data.ChunkData;
import ru.olamedia.olacraft.world.location.Location3f;
import ru.olamedia.olacraft.world.location.Location3i;

public class RenderLocation extends Location3f {
	public RenderLocation(float x, float y, float z) {
		super(x, y, z);
	}

	public RenderLocation(Location3i location) {
		super(location);
	}

	public RenderLocation(Location3f location) {
		super(location);
	}

	public RenderLocation(IntBuffer location) {
		super(location.get(0), location.get(1), location.get(2));
	}

	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int clampId() {
		return ChunkData.ClampID((int) x, (int) y, (int) z);
	}
}
