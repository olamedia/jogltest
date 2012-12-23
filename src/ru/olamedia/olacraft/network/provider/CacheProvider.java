package ru.olamedia.olacraft.network.provider;

public class CacheProvider<T> implements IProvider<T> {
	public CacheProvider(IProvider<T> parent) {
		this.parent = parent;
	}

	private T obj;
	private IProvider<T> parent;

	public boolean has() {
		return null != obj || parent.has();
	}

	public void request() {
		parent.request();
	}

	public T get() {
		if (null == obj) {
			obj = parent.get();
		}
		return obj;
	}

	public void put(T obj) {
		this.obj = obj;
	}
}
