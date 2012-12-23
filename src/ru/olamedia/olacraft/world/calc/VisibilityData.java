package ru.olamedia.olacraft.world.calc;

import ru.olamedia.math.OpenBitSet;

public class VisibilityData {
	private OpenBitSet visibility = new OpenBitSet(4096);

	public void setVisible(short id) {
		visibility.set(id);
	}

	public void setAllInvisible() {
		for (short id = 0; id < 4096; id++) {
			visibility.clear(id);
		}
	}

	public void setInvisible(short id) {
		visibility.clear(id);
	}

	public boolean isVisible(short id) {
		return visibility.get(id);
	}

	public void reset() {
		visibility = null;
	}

}
