package ru.olamedia.asset;

import java.net.URL;

public class AssetManager {

	public static URL getBaseURL() {
		return AssetManager.class.getResource(AssetManager.class.getSimpleName() + ".class");
	}

	public boolean inJar() {
		// file:jar:c:/path/to/jar/somejar.jar!
		return getBaseURL().toString().startsWith("file:jar:");
		// return getBaseURL().toString().indexOf(".jar!") > 0;
	}

	public static URL getURL(String path) throws AssetNotFoundException {
		URL url = AssetManager.class.getClassLoader().getResource(path);
		if (null == url) {
			throw new AssetNotFoundException(path);
		}
		return url;
	}

	public static Asset getAsset(String path) throws AssetNotFoundException {
		return new Asset(getURL(path));
	}
}
