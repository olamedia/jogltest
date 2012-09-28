package ru.olamedia.olacraft.game;

public interface IGameWrapper {
	public void setMyId(int connectionId);

	public void spawn(int connectionId, int x, int y, int z);
}
