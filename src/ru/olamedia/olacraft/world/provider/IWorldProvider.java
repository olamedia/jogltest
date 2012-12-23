package ru.olamedia.olacraft.world.provider;

import ru.olamedia.olacraft.world.WorldInfo;

public interface IWorldProvider {
	public boolean hasInfo();

	public void requestInfo();

	public WorldInfo getInfo();
}
