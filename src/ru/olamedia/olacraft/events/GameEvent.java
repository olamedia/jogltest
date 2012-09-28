package ru.olamedia.olacraft.events;

public class GameEvent {
	public static int GAME_START = GameEventRegistry.get("game.start");
	public static int PLAYER_SPAWN = GameEventRegistry.get("player.spawn");
	private Object source;
	private int type;

	public GameEvent(Object source) {
		this.setSource(source);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public void dispatch() {
		GameEventDispatcher.dispatch(this);
	}
}
