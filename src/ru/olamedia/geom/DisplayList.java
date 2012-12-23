package ru.olamedia.geom;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

public class DisplayList {
	private GL2 gl;
	private int glDL;

	public DisplayList(GL glx) {
		gl = glx.getGL2();
		glDL = gl.glGenLists(1);
	}

	public void start() {
		gl.glNewList(glDL, GL2.GL_COMPILE);
	}

	public void stop() {
		gl.glEndList();
	}

	public void render() {
		gl.glCallList(glDL);
	}

	public void destroy() {
		gl.glDeleteLists(glDL, 1);
	}

	public void begin() {
		start();
	}

	public void end() {
		stop();
	}
}
