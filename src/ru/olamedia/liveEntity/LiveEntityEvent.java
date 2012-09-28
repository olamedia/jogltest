package ru.olamedia.liveEntity;

public class LiveEntityEvent {
	public static int ON_DIE;
	private Object source;
	private int type;

	public LiveEntityEvent(Object source) {
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
		LiveEntityEventDispatcher.dispatch(this);
	}
}
