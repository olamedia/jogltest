package ru.olamedia.texture;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import ru.olamedia.asset.AssetManager;
import ru.olamedia.asset.AssetNotFoundException;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class TextureManager {
	private static HashMap<String, Texture> list = new HashMap<String, Texture>();

	public static Texture get(String filename) {
		if (!list.containsKey(filename)) {
			try {
				return get(filename, AssetManager.getAsset(filename).getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (AssetNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		return list.get(filename);
	}

	public static Texture get(String key, InputStream stream) {
		if (!list.containsKey(key)) {
			try {
				list.put(key, TextureIO.newTexture(stream, true, "PNG"));
			} catch (IOException e) {
				list.put(key, null);
				e.printStackTrace();
			}
		}
		return list.get(key);
	}
}
