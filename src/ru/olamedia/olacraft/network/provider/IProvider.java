package ru.olamedia.olacraft.network.provider;

public interface IProvider<T> {
	public boolean has();

	public void request();

	public T get();

	public void put(T obj);
}
