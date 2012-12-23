package ru.olamedia.olacraft.network.provider;

abstract public class RemoteProvider<T> implements IProvider<T> {
	private T obj;

	public boolean has() {
		return null != obj;
	}

	abstract public void request();

	public T get() {
		return obj;
	}

	public void put(T obj) {
		this.obj = obj;
	}
}
