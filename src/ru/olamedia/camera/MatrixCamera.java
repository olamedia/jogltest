package ru.olamedia.camera;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLUniformData;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

import org.openmali.FastMath;

import ru.olamedia.olacraft.game.Game;
import ru.olamedia.input.Keyboard;
import ru.olamedia.math.Frustum;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.util.PMVMatrix;

public class MatrixCamera {

	public PMVMatrix pmvMatrix = new PMVMatrix(true);
	private boolean isDirty = true;
	public GLUniformData pmvMatrixUniform;

	protected float fov = 90f;
	protected float aspect = 1f;
	protected float zNear = 0.1f;
	protected float zFar = 1000f;
	private boolean isPitchLocked = true;
	private float minPitch = -80f;
	private float maxPitch = 80f;
	public Frustum frustum = new Frustum();

	private Vector3f position = new Vector3f();
	private float yaw = 0; // around y
	private float pitch = 0;
	private float roll = 0;

	private Vector3f look = new Vector3f();
	private Vector3f right = new Vector3f();
	private Vector3f up = new Vector3f();
	public boolean isFrustumVisible = false;

	int counter = 0;

	public void pack() {
		if (isAttachedToCameraman) {
			position.x = cameraman.getCameraX();
			position.y = cameraman.getCameraY();
			position.z = cameraman.getCameraZ();
		}
		pmvMatrix.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		pmvMatrix.glLoadIdentity();
		pmvMatrix.gluPerspective(fov, aspect, zNear, zFar);

		pmvMatrix.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		pmvMatrix.glLoadIdentity();
		pmvMatrix.glRotatef(360 - pitch, 1, 0, 0);
		pmvMatrix.glRotatef(360 - yaw, 0, 1, 0);
		pmvMatrix.glRotatef(360 - roll, 0, 0, 1);
		pmvMatrix.glTranslatef(-position.x, -position.y, -position.z);
		// pmvMatrix.setDirty();
		pmvMatrix.update();
		FloatBuffer mv = FloatBuffer.allocate(16);
		pmvMatrix.glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, mv);

		right.set(mv.get(0), mv.get(4), mv.get(8));
		look.set(mv.get(2), mv.get(6), mv.get(10));
		up.cross(look, right);

		pmvMatrixUniform = new GLUniformData("mgl_PMVMatrix", 4, 4, pmvMatrix.glGetPMvMatrixf());

		packFrustum();
	}

	private ru.olamedia.math.Vector3f nearc;

	private void packFrustum() {
		float nearD = zNear + (isFrustumVisible ? 0.2f : 0);// zNear;
		float farD = zFar - (isFrustumVisible ? 1f : 0);// zFar;
		ru.olamedia.math.Vector3f eye = new ru.olamedia.math.Vector3f(position.getX(), position.getY(), position.getZ());
		ru.olamedia.math.Vector3f eyef = eye;// .translate(look, 1f);
		nearc = eyef.translate(look, nearD);
		ru.olamedia.math.Vector3f farc = eyef.translate(look, farD);
		final float tang = FastMath.tan(FastMath.toRad(fov) / 2.0f);
		float nh = nearD * tang * (isFrustumVisible ? 0.3f : 1);// zNear * tang;
		float nw = nh * aspect * aspect;
		float fh = farD * tang * (isFrustumVisible ? 0.5f : 1);// zNear * tang;
		float fw = fh * aspect * aspect;
		ru.olamedia.math.Vector3f nrb = nearc.translate(right, -nw / 2).translate(up, -nh / 2);
		ru.olamedia.math.Vector3f nlb = nearc.translate(right, nw / 2).translate(up, -nh / 2);
		@SuppressWarnings("unused")
		ru.olamedia.math.Vector3f nrt = nearc.translate(right, -nw / 2).translate(up, nh / 2);
		ru.olamedia.math.Vector3f nlt = nearc.translate(right, nw / 2).translate(up, nh / 2);
		ru.olamedia.math.Vector3f frb = farc.translate(right, -fw / 2).translate(up, -fh / 2);
		ru.olamedia.math.Vector3f flb = farc.translate(right, fw / 2).translate(up, -fh / 2);
		ru.olamedia.math.Vector3f frt = farc.translate(right, -fw / 2).translate(up, fh / 2);
		ru.olamedia.math.Vector3f flt = farc.translate(right, fw / 2).translate(up, fh / 2);

		frustum.leftPlane.set3Points(nlb, flb, flt);
		frustum.leftPlane.n.negate();
		frustum.rightPlane.set3Points(nrb, frt, frb);
		// frustum.rightPlane.n.negate();
		frustum.topPlane.set3Points(nlb, frb, flb);// nlt, frt, flt);
		// frustum.topPlane.n.negate();
		frustum.bottomPlane.set3Points(frt, nlt, flt);
		// frustum.bottomPlane.n.negate();
		frustum.nearPlane.set3Points(nlb, nlt, nrb);
		frustum.farPlane.set3Points(flt, flb, frb);
	}

	private GLU glu;

	public void setUp(GLAutoDrawable drawable) {
		updateKeyboard();
		updateMouse();
		if (glu == null) {
			glu = new GLU();
		}
		if (isDirty) {
			pack();
		}
		GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadMatrixf(pmvMatrix.glGetPMatrixf());
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		FloatBuffer pmv = FloatBuffer.allocate(16);
		pmvMatrix.glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, pmv);
		gl.glLoadMatrixf(pmv);
	}

	public float intersectsRectangle(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4) {
		return Math.min(intersectsTriangle(vertex1, vertex2, vertex3), intersectsTriangle(vertex3, vertex4, vertex1));
	}

	public float intersectsTriangle(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3) {
		// Compute vectors along two edges of the triangle.
		Vector3f edge1 = new Vector3f(), edge2 = new Vector3f();

		edge1.sub(vertex2, vertex1);
		edge2.sub(vertex3, vertex1);

		// Compute the determinant.
		Vector3f directionCrossEdge2 = new Vector3f();
		directionCrossEdge2.cross(look, edge2);

		float determinant = directionCrossEdge2.dot(edge1);
		// If the ray and triangle are parallel, there is no collision.
		if (determinant > -.0000001f && determinant < .0000001f) {
			return Float.MAX_VALUE;
		}

		float inverseDeterminant = 1.0f / determinant;

		// Calculate the U parameter of the intersection point.
		Vector3f distanceVector = new Vector3f();
		distanceVector.sub(position, vertex1);

		float triangleU = directionCrossEdge2.dot(distanceVector);
		triangleU *= inverseDeterminant;

		// Make sure the U is inside the triangle.
		if (triangleU < 0 || triangleU > 1) {
			return Float.MAX_VALUE;
		}

		// Calculate the V parameter of the intersection point.
		Vector3f distanceCrossEdge1 = new Vector3f();
		distanceCrossEdge1.cross(distanceVector, edge1);

		float triangleV = look.dot(distanceCrossEdge1);
		triangleV *= inverseDeterminant;

		// Make sure the V is inside the triangle.
		if (triangleV < 0 || triangleU + triangleV > 1) {
			return Float.MAX_VALUE;
		}

		// Get the distance to the face from our ray origin
		float rayDistance = distanceCrossEdge1.dot(edge2);
		rayDistance *= inverseDeterminant;

		// Is the triangle behind us?
		if (rayDistance < 0) {
			rayDistance *= -1;
			return Float.MAX_VALUE;
		}
		return rayDistance;
	}

	private void translatePoint(Vector3f point, Vector3f direction, float delta) {
		point.x += direction.x * delta;
		point.y += direction.y * delta;
		point.z += direction.z * delta;
		setDirty();
	}

	private void translate(Vector3f direction, float delta) {
		translatePoint(position, direction, delta);
	}

	private void translate(float dx, float dy, float dz) {
		translate(right, dx);
		translate(up, dy);
		translate(look, -dz);
	}

	public MatrixCamera() {
		right = new Vector3f(1, 0, 0);
		up = new Vector3f(0, 1, 0);
		look = new Vector3f(0, 0, 1);
		isPitchLocked = true;
		setDirty();
	}

	public void captureControls() {
		Keyboard.setName("flyForward", KeyEvent.VK_W);
		Keyboard.setName("flyBack", KeyEvent.VK_S);
		Keyboard.setName("strafeLeft", KeyEvent.VK_A);
		Keyboard.setName("strafeRight", KeyEvent.VK_D);
		Keyboard.setName("flyUp", KeyEvent.VK_SPACE);
		Keyboard.setName("flyDown", KeyEvent.VK_SHIFT);
	}

	public void mouseMoved(float dx, float dy) {
		yaw += -dx;
		pitch += -dy;
		yaw = yaw % 360;
		pitch = pitch % 360;
		setDirty();
	}

	public void updateMouse() {
		if (isPitchLocked) {
			if (pitch < minPitch) {
				pitch = minPitch;
			} else if (pitch > maxPitch) {
				pitch = maxPitch;
			}
		}
	}

	public void lockPitch(float min, float max) {
		this.minPitch = min;
		this.maxPitch = max;
		isPitchLocked = true;
	}

	public void unlockPitch() {
		isPitchLocked = false;
	}

	public void updateKeyboard() {
		if (isAttachedToCameraman) {
			this.cameraman.update(Game.instance.getDelta());
			return;
		}
		// --- Keyboard
		int left = Keyboard.isKeyDown("strafeLeft") ? 1 : 0;
		int right = Keyboard.isKeyDown("strafeRight") ? 1 : 0;
		int up = Keyboard.isKeyDown("flyForward") ? 1 : 0;
		int down = Keyboard.isKeyDown("flyBack") ? 1 : 0;
		int flyUp = Keyboard.isKeyDown("flyUp") ? 1 : 0;
		int flyDown = Keyboard.isKeyDown("flyDown") ? 1 : 0;
		float distance = 4f * 4.5f * Game.instance.getDelta(); // runspeed, m/s
		if (up + down + right + left + flyDown + flyUp > 0) {
			translate(//
					right * distance - left * distance,//
					(isAttachedToCameraman ? 0 : flyUp * distance - flyDown * distance),//
					up * distance - down * distance//
			);
			setDirty();
		}
	}

	protected Cameraman cameraman = null;
	protected boolean isAttachedToCameraman = false;
	protected float distanceFromCameraman = 0; // third-person view / 0 for
	protected boolean lookToCameraman = false; // back/front third-person view

	/**
	 * @return the fov
	 */
	public float getFov() {
		return fov;
	}

	/**
	 * @param fov
	 *            the fov to set
	 */
	public void setFov(float fov) {
		this.fov = fov;
		setDirty();
	}

	/**
	 * @return the aspect
	 */
	public float getAspect() {
		return aspect;
	}

	/**
	 * @param aspect
	 *            the aspect to set
	 */
	public void setAspect(float aspect) {
		this.aspect = aspect;
		setDirty();
	}

	/**
	 * @return the zNear
	 */
	public float getzNear() {
		return zNear;
	}

	/**
	 * @param zNear
	 *            the zNear to set
	 */
	public void setzNear(float zNear) {
		this.zNear = zNear;
		setDirty();
	}

	/**
	 * @return the zFar
	 */
	public float getzFar() {
		return zFar;
	}

	/**
	 * @param zFar
	 *            the zFar to set
	 */
	public void setzFar(float zFar) {
		this.zFar = zFar;
		setDirty();
	}

	/**
	 * @return the yaw
	 */
	public float getYaw() {
		return yaw;
	}

	/**
	 * @param yaw
	 *            the yaw to set
	 */
	public void setYaw(float yaw) {
		this.yaw = yaw;
		setDirty();
	}

	/**
	 * @return the pitch
	 */
	public float getPitch() {
		return pitch;
	}

	/**
	 * @param pitch
	 *            the pitch to set
	 */
	public void setPitch(float pitch) {
		this.pitch = pitch;
		setDirty();
	}

	/**
	 * @return the cameraman
	 */
	public Cameraman getCameraman() {
		return cameraman;
	}

	/**
	 * @param cameraman
	 *            the cameraman to set
	 */
	public void setCameraman(Cameraman cameraman) {
		this.cameraman = cameraman;
	}

	/**
	 * @return the isAttachedToCameraman
	 */
	public boolean isAttachedToCameraman() {
		return isAttachedToCameraman;
	}

	/**
	 * @param isAttachedToCameraman
	 *            the isAttachedToCameraman to set
	 */
	public void setAttachedToCameraman(boolean isAttachedToCameraman) {
		this.isAttachedToCameraman = isAttachedToCameraman;
	}

	/**
	 * @return the distanceFromCameraman
	 */
	public float getDistanceFromCameraman() {
		return distanceFromCameraman;
	}

	/**
	 * @param distanceFromCameraman
	 *            the distanceFromCameraman to set
	 */
	public void setDistanceFromCameraman(float distanceFromCameraman) {
		this.distanceFromCameraman = distanceFromCameraman;
	}

	/**
	 * @return the lookToCameraman
	 */
	public boolean isLookToCameraman() {
		return lookToCameraman;
	}

	/**
	 * @param lookToCameraman
	 *            the lookToCameraman to set
	 */
	public void setLookToCameraman(boolean lookToCameraman) {
		this.lookToCameraman = lookToCameraman;
	}

	public void setX(float x) {
		position.x = x;
		setDirty();
	}

	public void setY(float y) {
		position.y = y;
		setDirty();
	}

	public void setZ(float z) {
		position.z = z;
		setDirty();
	}

	private void setDirty() {
		isDirty = true;
	}

	public float getX() {
		return position.x;
	}

	public float getY() {
		return position.y;
	}

	public float getZ() {
		return position.z;
	}

	public float getRoll() {
		return roll;
	}

	public void attachTo(Cameraman player) {
		this.cameraman = player;
		this.isAttachedToCameraman = true;
		this.cameraman.captureControls();
	}

	public void detach() {
		this.isAttachedToCameraman = false;
		this.captureControls();
	}

	/**
	 * @return the look
	 */
	public Vector3f getLook() {
		return look;
	}

	/**
	 * @return the right
	 */
	public Vector3f getRight() {
		return right;
	}

	/**
	 * @return the up
	 */
	public Vector3f getUp() {
		return up;
	}

}
