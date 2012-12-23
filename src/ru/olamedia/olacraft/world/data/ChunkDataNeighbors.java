package ru.olamedia.olacraft.world.data;

import ru.olamedia.olacraft.world.calc.LightCalculator;
import ru.olamedia.olacraft.world.calc.VisibilityCalculator;
import ru.olamedia.olacraft.world.location.ChunkLocation;
import ru.olamedia.olacraft.world.provider.WorldProvider;

public class ChunkDataNeighbors {
	private static byte CENTER = 0;
	private static byte LEFT = 1;
	private static byte RIGHT = 2;
	private static byte TOP = 3;
	private static byte BOTTOM = 4;
	private static byte FRONT = 5;
	private static byte BACK = 6;
	private WorldProvider provider;
	private ChunkLocation center;
	private ChunkDataWrapper data;
	private ChunkDataWrapper left;
	private ChunkDataWrapper right;
	private ChunkDataWrapper top;
	private ChunkDataWrapper bottom;
	private ChunkDataWrapper front;
	private ChunkDataWrapper back;
	private ChunkDataWrapper[] chunks;

	public ChunkDataNeighbors() {
		chunks = new ChunkDataWrapper[7];
		for (byte i = 0; i < 7; i++) {
			chunks[i] = new ChunkDataWrapper();
		}
		left = new ChunkDataWrapper();
		right = new ChunkDataWrapper();
		top = new ChunkDataWrapper();
		bottom = new ChunkDataWrapper();
		front = new ChunkDataWrapper();
		back = new ChunkDataWrapper();
		data = new ChunkDataWrapper();
		center = new ChunkLocation();
	}

	public void setProvider(WorldProvider provider) {
		this.provider = provider;
	}

	public void setData(ChunkData data) {
		this.data.setData(data);
		center.set(data.location);
		chunks[CENTER].setData(data);
	}

	public void loadNeighbors() {
		top.setData(provider.getChunk(center.getTop()));
		bottom.setData(provider.getChunk(center.getBottom()));
		left.setData(provider.getChunk(center.getLeft()));
		right.setData(provider.getChunk(center.getRight()));
		front.setData(provider.getChunk(center.getFront()));
		back.setData(provider.getChunk(center.getBack()));
		chunks[TOP].setData(top.getData());
		chunks[BOTTOM].setData(bottom.getData());
		chunks[LEFT].setData(left.getData());
		chunks[RIGHT].setData(right.getData());
		chunks[FRONT].setData(front.getData());
		chunks[BACK].setData(back.getData());
	}

	public ChunkDataWrapper getCenter() {
		return data;
	}

	public ChunkData getCenterData() {
		return data.getData();
	}

	public ChunkDataWrapper getTop() {
		return top;
	}

	public ChunkDataWrapper getBottom() {
		return bottom;
	}

	public ChunkDataWrapper getLeft() {
		return left;
	}

	public ChunkDataWrapper getRight() {
		return right;
	}

	public ChunkDataWrapper getFront() {
		return front;
	}

	public ChunkDataWrapper getBack() {
		return back;
	}

	public ChunkData getTopData() {
		return top.getData();
	}

	public ChunkData getBottomData() {
		return bottom.getData();
	}

	public ChunkData getLeftData() {
		return left.getData();
	}

	public ChunkData getRightData() {
		return right.getData();
	}

	public ChunkData getFrontData() {
		return front.getData();
	}

	public ChunkData getBackData() {
		return back.getData();
	}

	public void calcLight() {
		LightCalculator.calculateVoidLightIgnoreNeighbors(top);
		LightCalculator.calculateVoidLightIgnoreNeighbors(bottom);
		LightCalculator.calculateVoidLightIgnoreNeighbors(left);
		LightCalculator.calculateVoidLightIgnoreNeighbors(right);
		LightCalculator.calculateVoidLightIgnoreNeighbors(front);
		LightCalculator.calculateVoidLightIgnoreNeighbors(back);
		LightCalculator.precalculateVoidLight(data);
		LightCalculator.spreadVoidLight(this);
	}

	public void reset() {
		provider = null;
		data.reset();
		top.reset();
		bottom.reset();
		left.reset();
		right.reset();
		front.reset();
		back.reset();
	}

	public ChunkLocation getLocation() {
		return center;
	}

	public boolean isOpaque(short id) {
		return !data.getData().isEmpty(id) && provider.getTypeRegistry().isOpaque(data.getData().types[id]);
	}

	public boolean isVisible(short id) {
		return data.getVisibility().isVisible(id) && !data.getData().isEmpty(id) && (0 != data.getData().types[id]);
	}

	public void calcVisibility() {
		VisibilityCalculator.precomputeVisibility(this);
		VisibilityCalculator.computeVisibility(data.getVisibility(), provider, getLeftData(), getRightData(),
				getTopData(), getBottomData(), getFrontData(), getBackData());
	}
}
