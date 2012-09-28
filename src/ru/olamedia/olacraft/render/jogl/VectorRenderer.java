package ru.olamedia.olacraft.render.jogl;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import ru.olamedia.math.Vector3f;

public class VectorRenderer {
	public static void render(Vector3f point, Vector3f v, GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glColor3f(1, 0, 0);
		gl.glBegin(GL2.GL_LINES);
		{
			gl.glVertex3f(point.x, point.y, point.z);
			gl.glVertex3f(point.x + v.x, point.y + v.y, point.z + v.z);
		}
		gl.glEnd();
	}
}
