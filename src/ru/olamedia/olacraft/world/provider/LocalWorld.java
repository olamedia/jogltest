package ru.olamedia.olacraft.world.provider;

import ru.olamedia.olacraft.network.provider.LocalProvider;
import ru.olamedia.olacraft.world.WorldInfo;

public class LocalWorld implements IWorldProvider {
	private LocalProvider<WorldInfo> info;

	public LocalWorld() {
		info.put(new WorldInfo());
	}

	public boolean hasInfo() {
		return true;
	}

	public void requestInfo() {
		// do nothing
	}

	public WorldInfo getInfo() {
		return info.get();
	}
}
