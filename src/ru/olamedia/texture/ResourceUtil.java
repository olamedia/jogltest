package ru.olamedia.texture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceUtil {

	private static URL baseURL;

	private static ResourceUtil instance;

	public static ResourceUtil getInstance() {
		if (null == instance) {
			instance = new ResourceUtil();
		}
		return instance;
	}

	public static URL getInternalBaseURL() {
		if (null == baseURL) {
			URL url = getInstance().getClass().getResource("ResourceUtil.class");
			// URL back = null;
			// try {
			// back = new URL(url, "..");
			// } catch (MalformedURLException e1) {
			// e1.printStackTrace();
			// }
			// System.out.println("Back:" + back);
			// System.out.println("Class:" + url);
			int p = url.toString().indexOf("jar!");
			if (p > 0) {
				// in local jar:
				// jar:file:/E:/com/mindprod/thepackage/thepackage.jar!/com/mindprod/thepackage/images/blueball.gif
				// in remote jar:
				// jar:http://mindprod.com/thepackage.jar!/com/mindprod/thepackage/images/blueball.gif
				try {
					baseURL = new URL(url.toString().substring(0, p + 4));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			} else {
				// in local file:
				// file:/E:/com/mindprod/thepackage/images/blueball.gif
				// in remote file:
				// http://mindprod.com/com/mindprod/the...s/blueball.gif
				int l = url.toString().length() - (ResourceUtil.class.toString()).length();
				try {
					baseURL = new URL(url.toString().substring(0, l));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		return baseURL;
	}

	public static String getInternalFilename(String fn) {
		return getInternalBaseURL() + fn;
	}

	public static URL getInternalResource(String internalPath) throws MalformedURLException {
		URL url = ResourceUtil.class.getClassLoader().getResource(internalPath);
		if (url == null) {
			System.out.println(internalPath + " not found");
		} else {
			System.out.println(url.toString());
		}
		return url;
		// return new URL(getInternalBaseURL(), internalPath);
	}

	public static URL getURL(String internalPath) {
		try {
			return new URL(getInternalBaseURL(), internalPath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getFilename(String internalPath) {
		try {
			URL url = new URL(getInternalBaseURL(), internalPath);
			return url.getFile();
		} catch (MalformedURLException e) {
			System.err.println("Problems with " + internalPath);
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream getInternalInputStream(String internalPath) {
		try {
			return getInternalResource(internalPath).openStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File getFile(String fn) {
		URL url = null;
		try {
			url = new URL(getInternalFilename(fn));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		System.out.println("Fn:" + fn);
		System.out.println("Base:" + getInternalBaseURL());
		System.out.println("Internal:" + getInternalFilename(fn));
		System.out.println("Url:" + url);
		File f;
		try {
			f = new File(url.toURI());
		} catch (URISyntaxException e) {
			f = new File(url.getPath());
		}
		return f;
	}
}
