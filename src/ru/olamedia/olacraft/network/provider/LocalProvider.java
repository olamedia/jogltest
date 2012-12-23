package ru.olamedia.olacraft.network.provider;

public class LocalProvider<T> implements IProvider<T> {
	private T obj;

	public boolean has() {
		return true;
	}

	public void request() {

	}

	public T get() {
		return obj;
	}

	public void put(T obj) {
		this.obj = obj;
	}
}
