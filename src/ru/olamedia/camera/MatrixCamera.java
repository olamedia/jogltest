package ru.olamedia.camera;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;
import javax.vecmath.Matrix4f;

import org.openmali.FastMath;

import static org.openmali.FastMath.*;

import ru.olamedia.olacraft.game.Game;
import ru.olamedia.input.Keyboard;
import ru.olamedia.math.Frustum;
import ru.olamedia.olacraft.render.jogl.PlaneRenderer;
import ru.olamedia.olacraft.render.jogl.VectorRenderer;

import com.jogamp.newt.event.KeyEvent;

public class MatrixCamera {
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
	public Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f translationMatrix = new Matrix4f();
	private Matrix4f xRotationMatrix = new Matrix4f();
	private Matrix4f yRotationMatrix = new Matrix4f();
	private Matrix4f zRotationMatrix = new Matrix4f();
	private Matrix4f rotationMatrix = new Matrix4f();
	public Matrix4f viewMatrix = new Matrix4f();
	public Matrix4f worldMatrix = new Matrix4f();

	private Vector3f look = new Vector3f();
	private Vector3f right = new Vector3f();
	private Vector3f up = new Vector3f();
	public boolean isFrustumVisible = false;

	@SuppressWarnings("unused")
	private org.openmali.vecmath2.Matrix4f matrixToOpenMali(Matrix4f m) {
		return new org.openmali.vecmath2.Matrix4f(matrixToTransposeArray(m));
	}

	public void pack() {
		if (isAttachedToCameraman) {
			position.x = cameraman.getCameraX();
			position.y = cameraman.getCameraY();
			position.z = cameraman.getCameraZ();
		}

		worldMatrix.setIdentity();
		packProjectionMatrix();
		// projectionMatrix.transpose();
		// worldMatrix.mul(projectionMatrix);
		translationMatrix.setIdentity();
		translationMatrix.m03 = position.x;
		translationMatrix.m13 = position.y - 0.5f; // FIXME y is looking greater
													// than it should
		translationMatrix.m23 = position.z;
		packRotation();
		packView();
		// after view matrix created, retrieve vectors:
		viewMatrix.invert();
		viewMatrix.transpose();
		packLookVector();
		packRightVector();
		packUpVector();
		packFrustum();
		// worldMatrix.mul(projectionMatrix, viewMatrix);
		// worldMatrix.transpose();
		// // oglViewMatrix.set(viewMatrix);
		// // oglViewMatrix.transpose();
		// frustum = FrustumUtil.extractFrustum(worldMatrix);
		// Matrix4f vm = new Matrix4f(viewMatrix);
		// vm.invert();
		// frustum.compute(matrixToOpenMali(projectionMatrix),
		// matrixToOpenMali(vm));
		// ......
		// finally
		// ......
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

	private float[] matrixToTransposeArray(Matrix4f m) {
		return new float[] {
				//
				m.m00, m.m01, m.m02, m.m03,//
				m.m10, m.m11, m.m12, m.m13,//
				m.m20, m.m21, m.m22, m.m23,//
				m.m30, m.m31, m.m32, m.m33,//
		};
	}

	private float[] matrixToArray(Matrix4f m) {
		return new float[] {
				//
				m.m00, m.m10, m.m20, m.m30,//
				m.m01, m.m11, m.m21, m.m31,//
				m.m02, m.m12, m.m22, m.m32,//
				m.m03, m.m13, m.m23, m.m33,//
		};
	}

	private GLU glu;

	public void setUp(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		updateKeyboard();
		updateMouse();
		if (glu == null) {
			glu = new GLU();
		}
		// gl.glMatrixMode(GL2.GL_PROJECTION);
		// gl.glLoadIdentity();
		// glu.gluPerspective(100f, aspect, 0.2, 1000);
		loadProjectionMatrix(drawable);
		loadViewMatrix(drawable);

		gl.glColor3f(1, 0, 0);
		PlaneRenderer.render(frustum.leftPlane, drawable);
		gl.glColor3f(1, 1, 0);
		PlaneRenderer.render(frustum.rightPlane, drawable);
		gl.glColor3f(1, 0, 1);
		PlaneRenderer.render(frustum.topPlane, drawable);
		gl.glColor3f(1, 1, 1);
		PlaneRenderer.render(frustum.bottomPlane, drawable);

		VectorRenderer.render(nearc, frustum.leftPlane.n, drawable);
	}

	private void loadViewMatrix(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadTransposeMatrixf(matrixToArray(viewMatrix), 0);
	}

	private void loadProjectionMatrix(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadTransposeMatrixf(matrixToArray(projectionMatrix), 0);
	}

	private void packProjectionMatrix() {
		projectionMatrix.setZero();
		final float tang = FastMath.tan(FastMath.toRad(fov) / 2.0f);
		final float size = zNear * tang;
		float left = -size, right = size, bottom = -size / aspect, top = size / aspect;
		// First Column
		projectionMatrix.m00 = 2 * zNear / (right - left);
		// Second Column
		projectionMatrix.m11 = 2 * zNear / (top - bottom);
		// Third Column
		projectionMatrix.m20 = (right + left) / (right - left);
		projectionMatrix.m21 = (top + bottom) / (top - bottom);
		projectionMatrix.m22 = -(zFar + zNear) / (zFar - zNear);
		projectionMatrix.m23 = -1;
		// Fourth Column
		projectionMatrix.m32 = -(2 * zFar * zNear) / (zFar - zNear);
	}

	private void packRotation() {
		xRotationMatrix.rotX(toRad(pitch));
		yRotationMatrix.rotY(toRad(yaw));
		zRotationMatrix.rotZ(toRad(roll));

		rotationMatrix.setIdentity();
		rotationMatrix.mul(zRotationMatrix);
		rotationMatrix.mul(yRotationMatrix);
		rotationMatrix.mul(xRotationMatrix);
	}

	private void translatePoint(Vector3f point, Vector3f direction, float delta) {
		point.x += direction.x * delta;
		point.y += direction.y * delta;
		point.z += direction.z * delta;
	}

	private void translate(Vector3f direction, float delta) {
		translatePoint(position, direction, delta);
	}

	private void translate(float dx, float dy, float dz) {
		translate(right, dx);
		translate(up, dy);
		translate(look, -dz);
	}

	private void packView() {
		viewMatrix.setIdentity();
		viewMatrix.mul(translationMatrix);
		viewMatrix.mul(rotationMatrix);

	}

	private void packUpVector() {
		up.set(viewMatrix.m01, viewMatrix.m11, viewMatrix.m21);
	}

	private void packRightVector() {
		right.set(viewMatrix.m00, viewMatrix.m10, viewMatrix.m20);
	}

	private void packLookVector() {
		look.set(viewMatrix.m02, viewMatrix.m12, viewMatrix.m22);
	}

	public MatrixCamera() {
		right = new Vector3f(1, 0, 0);
		up = new Vector3f(0, 1, 0);
		look = new Vector3f(0, 0, 1);
		isPitchLocked = true;
		pack();
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
	}

	public void updateMouse() {
		if (isPitchLocked) {
			if (pitch < minPitch) {
				pitch = minPitch;
			} else if (pitch > maxPitch) {
				pitch = maxPitch;
			}
		}
		pack();
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
			pack();
			// System.out.println("Moving... " + position.getX() + " "
			// + position.getY() + " " + position.getZ());
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
	}

	public void setY(float y) {
		position.y = y;
	}

	public void setZ(float z) {
		position.z = z;
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

	public float[] getViewMatrixArray() {
		return matrixToArray(viewMatrix);
	}

	public float[] getProjectionMatrixArray() {
		return matrixToArray(viewMatrix);
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
