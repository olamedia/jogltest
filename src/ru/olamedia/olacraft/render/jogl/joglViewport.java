package ru.olamedia.olacraft.render.jogl;

import java.awt.Font;

import javax.media.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.awt.TextRenderer;

public class joglViewport {
	private GLAutoDrawable drawable;
	private TextRenderer sans11;

	public joglViewport(GLAutoDrawable drawable) {
		this.drawable = drawable;
		sans11 = new TextRenderer(new Font("SansSerif", Font.PLAIN, 11));
	}

	public void drawText(String text, int x, int y) {
		sans11.setColor(0, 0, 0, 0.7f);
		sans11.beginRendering(drawable.getWidth(), drawable.getHeight());
		sans11.draw(text, x - 1, y - 1);
		// sans11.setColor(1, 1, 1, 0.7f);
		// sans11.draw(text, x, y);
		sans11.endRendering();
	}
}
