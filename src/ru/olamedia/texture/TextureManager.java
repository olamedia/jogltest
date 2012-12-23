package ru.olamedia.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.media.opengl.GLProfile;

import ru.olamedia.asset.AssetManager;
import ru.olamedia.asset.AssetNotFoundException;
import ru.olamedia.asset.Sprite;
import ru.olamedia.asset.SpriteRectangle;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class TextureManager {
	private static HashMap<String, Texture> list = new HashMap<String, Texture>();
	private static HashMap<String, Sprite> currentSpriteList = new HashMap<String, Sprite>();
	private static HashMap<String, SpriteRectangle> offsets = new HashMap<String, SpriteRectangle>();
	private static Sprite currentSprite = new Sprite(256, 256, 32, 32); // 64
																		// tiles

	public static void writeSprite(String filename) {
		currentSprite.write(filename);
	}

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

	public static Texture get(String key, InputStream stream, String format) {
		if (!list.containsKey(key)) {
			try {
				list.put(key, TextureIO.newTexture(stream, true, format));
			} catch (IOException e) {
				list.put(key, null);
				e.printStackTrace();
			}
		}
		return list.get(key);
	}

	public static Texture makeTextureFromBufferedImage(BufferedImage b) {
		return null;
	}

	public static Texture getSprite(String filename) {
		if (!list.containsKey(filename)) {
			try {
				SpriteRectangle offset = currentSprite.addImage(filename);
				offsets.put(filename, offset);
				currentSpriteList.put(filename, currentSprite);
			} catch (AssetNotFoundException e) {
				e.printStackTrace();
			}
			return get(filename);
		}
		return list.get(filename);
	}

	public static SpriteRectangle getSpriteOffset(String filename) {
		if (!offsets.containsKey(filename)) {
			getSprite(filename);
			if (!offsets.containsKey(filename)) {
				return null;
			}
		}
		return offsets.get(filename);
	}

	public static void finishSprite() {
		GLProfile gp = GLProfile.getGL2ES2();
		Texture texture = AWTTextureIO.newTexture(gp, currentSprite.getImage(), true);
		currentSprite.dispose();
		for (String key : currentSpriteList.keySet()) {
			list.put(key, texture);
		}
		currentSpriteList.clear();
	}
}
