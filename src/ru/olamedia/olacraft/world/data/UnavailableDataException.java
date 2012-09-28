package ru.olamedia.olacraft.world.data;

public class UnavailableDataException extends Exception {

	public UnavailableDataException() {
		super();
	}

	public UnavailableDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnavailableDataException(String message) {
		super(message);
	}

	public UnavailableDataException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = -8955947061088863309L;

}
