package ru.olamedia.asset;

public class AssetNotFoundException extends Exception {

	public AssetNotFoundException() {
		super();
	}

	public AssetNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssetNotFoundException(String message) {
		super(message);
	}

	public AssetNotFoundException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = 2197816222986044998L;
}
