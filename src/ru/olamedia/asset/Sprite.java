package ru.olamedia.asset;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {
	private BufferedImage combinedImage;
	Graphics g;
	int width;
	int height;
	int w;
	int h;
	int sizeX;
	int sizeY;
	int size;
	int length = 0;
	SpriteOffset current;
	public void dispose(){
		if (null != combinedImage){
			combinedImage = null;
		}
		if (null != g){
			g.dispose();
		}
	}

	int waiting = 0;

	public BufferedImage getImage() {
		return combinedImage;
	}

	public Sprite(int width, int height, int w, int h) {
		this.width = width;
		this.height = height;
		this.w = w;
		this.h = h;
		sizeX = width / w;
		sizeY = height / h;
		size = sizeY * sizeX;
		current = new SpriteOffset(0, 0);
		combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g = combinedImage.getGraphics();
		/*g.setColor(Color.darkGray);
		g.setPaintMode();
		g.fillRect(0, 0, width, height);*/
	}

	public void next() {
		current.x += w;
		if (current.x + w > width) {
			current.x = 0;
			current.y += h;
		}
	}

	public SpriteRectangle addImage(String path) throws AssetNotFoundException {
		final Asset asset = AssetManager.getAsset(path);
		BufferedImage image;
		try {
			image = ImageIO.read(asset.getInputStream());
			return addImage(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public SpriteRectangle addImage(BufferedImage img) {
		if (current.y + h > height) {
			throw new RuntimeException("Out of bounds while creating Sprite");
		}
		final int x = (int) current.x;
		final int y = (int) current.y;
		waiting++;
		// img.getWidth(new ImageObserver() {
		// @Override
		// public boolean imageUpdate(Image img1, int arg1, int arg2, int arg3,
		// int arg4, int arg5) {
		//System.out.println("Sprite: draw " + img.getWidth(null));
		/*g.setColor(Color.magenta);
		g.fillRect(x, y, w, h);*/
		g.drawImage(img, x, y, w, h, null, null);
		waiting--;
		// return false;
		// }
		// });
		SpriteRectangle area = new SpriteRectangle((1 / (float) width) * current.x, (1 / (float) height) * current.y,
				(1 / (float) width) * (current.x + 16), (1 / (float) height) * (current.y + 16));
		next();
		return area;
	}

	public void write(String path) {
		/*
		 * while (waiting > 0) {
		 * try {
		 * Thread.sleep(100);
		 * } catch (InterruptedException e) {
		 * e.printStackTrace();
		 * }
		 * }
		 */
		g.dispose();
		try {
			ImageIO.write(combinedImage, "PNG", new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
