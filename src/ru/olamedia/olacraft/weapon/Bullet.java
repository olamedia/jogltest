package ru.olamedia.olacraft.weapon;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.ode4j.ode.DBody;

public class Bullet {
	public Point3f location = new Point3f();
	public Vector3f velocity = new Vector3f();
	public Vector3f acceleration = new Vector3f(0, -0.98f, 0);
	public DBody body;
	public float width = 0.05f;
	public float height = 0.05f;
	public float depth = 0.25f;
	public boolean toRemove = false;
	private static GLU glu = new GLU();

	public void update(float deltams) {
		// acceleration.set(velocity);
		// acceleration.negate();
		// acceleration.scale(0.1f);
		// acceleration.y += -0.98f;
		// velocity.x += acceleration.x * deltams;
		// velocity.y += acceleration.y * deltams;
		// velocity.z += acceleration.z * deltams;
		// float step = deltams;
		// location.x += velocity.x * deltams;
		// location.y += velocity.y * deltams;
		// location.z += velocity.z * deltams;
		if (body.getPosition().get1() < 0 || body.getPosition().get1() > 100) {
			// FIXME
			toRemove = true;
		}
	}

	public void render(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glPushMatrix();
		gl.glTranslated(body.getPosition().get0(), body.getPosition().get1(), body.getPosition().get2());
		GLUquadric bulletGeom = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(bulletGeom, GLU.GLU_FILL);
		glu.gluQuadricNormals(bulletGeom, GLU.GLU_SMOOTH);
		glu.gluDisk(bulletGeom, 0.3, 0.4, 5, 5);
		gl.glPopMatrix();
	}
}
