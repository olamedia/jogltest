package ru.olamedia.asset;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Asset {
	protected URL url;

	public boolean inJar() {
		return url.toString().startsWith("file:jar:");
	}

	public Asset(URL url) {
		this.url = url;
	}

	public URL getURL() {
		return url;
	}

	public InputStream getInputStream() throws IOException {
		return url.openStream();
	}

	public String getFile() {
		return url.getFile();
	}
}
