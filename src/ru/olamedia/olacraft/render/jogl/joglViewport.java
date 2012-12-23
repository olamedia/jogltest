package ru.olamedia.olacraft.render.jogl;

import java.awt.Font;

import javax.media.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.awt.TextRenderer;

public class joglViewport {
	private GLAutoDrawable drawable;
	private TextRenderer sans11Black;
	private TextRenderer sans11White;

	// private TextRenderer sans12;

	public joglViewport(GLAutoDrawable drawable) {
		this.drawable = drawable;
		//sans11Black = new TextRenderer(new Font("SansSerif", Font.PLAIN, 11));
		//sans11Black.setColor(0, 0, 0, 1f);
		sans11White = new TextRenderer(new Font("SansSerif", Font.PLAIN, 11));
		sans11White.setColor(0, 0, 0, 0.9f);
		// sans12 = new TextRenderer(new Font("SansSerif", Font.PLAIN, 11));
	}

	public void beginRendering() {
		sans11White.beginRendering(drawable.getWidth(), drawable.getHeight());
		//sans11Black.beginRendering(drawable.getWidth(), drawable.getHeight());
	}

	public void endRendering() {
		sans11White.flush();
		sans11White.endRendering();
		//sans11Black.endRendering();
	}

	public void drawText(String text, int x, int y) {
		// sans12.beginRendering(drawable.getWidth(), drawable.getHeight());
		// sans12.setColor(1, 1, 1, 0.9f);
		// sans12.draw(text, x, y);
		// sans12.endRendering();
		//sans11White.draw(text, x - 1, y);
		//sans11White.draw(text, x + 1, y);
		//sans11White.draw(text, x, y - 1);
		//sans11White.draw(text, x, y + 1);
		sans11White.draw(text, x, y);

	}
}
