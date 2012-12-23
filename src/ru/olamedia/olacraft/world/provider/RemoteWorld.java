package ru.olamedia.olacraft.world.provider;

import ru.olamedia.olacraft.network.provider.CacheProvider;
import ru.olamedia.olacraft.network.provider.RemoteProvider;
import ru.olamedia.olacraft.world.WorldInfo;

public class RemoteWorld implements IWorldProvider {
	private CacheProvider<WorldInfo> info;

	public RemoteWorld() {
		info = new CacheProvider<WorldInfo>(new RemoteProvider<WorldInfo>() {
			@Override
			public void request() {

			}
		});
	}

	public boolean hasInfo() {
		return info.has();
	}

	public void requestInfo() {
		info.request();
	}

	@Override
	public WorldInfo getInfo() {
		return info.get();
	}

}
