package ru.olamedia.olacraft.render.jogl;

import javax.media.opengl.GLAutoDrawable;

public interface IRenderer {
	public void render(GLAutoDrawable drawable);
	public void init(GLAutoDrawable drawable);
	public void reshape(GLAutoDrawable drawable);
}
