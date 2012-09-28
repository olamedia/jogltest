package ru.olamedia.olacraft.render.jogl;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import ru.olamedia.math.Plane;

public class PlaneRenderer {
	private static float getZ(Plane p, float x, float y) {
		return -(p.n.x * x + p.n.y * y + p.d) / p.n.z;
	}

	private static float getY(Plane p, float x, float z) {
		return -(p.n.x * x + p.n.z * z + p.d) / p.n.y;
	}

	private static float getX(Plane p, float y, float z) {
		return -(p.n.y * y + p.n.z * z + p.d) / p.n.x;
	}

	public static void render(Plane p, GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		float size = 100;
		float step = size / 5;
		for (float x = -size; x <= size; x += step) {
			for (float y = -size; y <= size; y += step) {
				// nx * x + ny * y + nz * z + d = 0
				// (z = nx * x + ny * y + d) / nz
				float z = getZ(p, x, y);
				float x2 = x + step;
				float y2 = y;
				float z2 = getZ(p, x2, y2);
				float x3 = x + step;
				float y3 = y + step;
				float z3 = getZ(p, x3, y3);
				float x4 = x;
				float y4 = y + step;
				float z4 = getZ(p, x4, y4);
				gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
				gl.glBegin(GL2.GL_QUADS);
				{
					gl.glVertex3f(x, y, z);
					gl.glVertex3f(x2, y2, z2);
					gl.glVertex3f(x3, y3, z3);
					gl.glVertex3f(x4, y4, z4);
				}
				gl.glEnd();
			}
		}
		for (float x = -size; x <= size; x += step) {
			for (float z = -size; z <= size; z += step) {
				// nx * x + ny * y + nz * z + d = 0
				// (z = nx * x + ny * y + d) / nz
				float y = getY(p, x, z);
				float x2 = x + step;
				float z2 = z;
				float y2 = getY(p, x2, z2);
				float x3 = x + step;
				float z3 = z + step;
				float y3 = getY(p, x3, z3);
				float x4 = x;
				float z4 = z + step;
				float y4 = getY(p, x4, z4);
				gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
				gl.glBegin(GL2.GL_QUADS);
				{
					gl.glVertex3f(x, y, z);
					gl.glVertex3f(x2, y2, z2);
					gl.glVertex3f(x3, y3, z3);
					gl.glVertex3f(x4, y4, z4);
				}
				gl.glEnd();
			}
		}

		for (float y = -size; y <= size; y += step) {
			for (float z = -size; z <= size; z += step) {
				// nx * x + ny * y + nz * z + d = 0
				// (z = nx * x + ny * y + d) / nz
				float x = getX(p, y, z);
				float y2 = y + step;
				float z2 = z;
				float x2 = getX(p, y2, z2);
				float y3 = y + step;
				float z3 = z + step;
				float x3 = getX(p, y3, z3);
				float y4 = y;
				float z4 = z + step;
				float x4 = getX(p, y4, z4);
				gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
				gl.glBegin(GL2.GL_QUADS);
				{
					gl.glVertex3f(x, y, z);
					gl.glVertex3f(x2, y2, z2);
					gl.glVertex3f(x3, y3, z3);
					gl.glVertex3f(x4, y4, z4);
				}
				gl.glEnd();
			}
		}
	}
}
