package ru.olamedia.camera;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLUniformData;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

import jogamp.opengl.ProjectFloat;

import org.openmali.vecmath2.Matrix4f;

import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.world.location.Location3f;
import ru.olamedia.geom.Frustum2;
import ru.olamedia.input.Keyboard;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.geom.Frustum;
import com.jogamp.opengl.util.PMVMatrix;

public class MatrixCamera implements Cameraman {
	protected MatrixCamera attachedCamera = null;
	public PMVMatrix pmvMatrix = new PMVMatrix(true);
	private boolean isDirty = true;
	public GLUniformData pmvMatrixUniform = new GLUniformData("mgl_PMVMatrix", 4, 4, pmvMatrix.glGetPMvMatrixf());

	public boolean isOrientationChanged = true;

	protected float fov = 90f;
	protected float aspect = 1f;
	protected float zNear = 0.1f;
	protected float zFar = 500f;
	private boolean isPitchLocked = true;
	private float minPitch = -80f;
	private float maxPitch = 80f;
	public Frustum frustum = new Frustum();
	public Frustum frustump = new Frustum();
	public ru.olamedia.math.Frustum frustum1 = new ru.olamedia.math.Frustum();
	public Frustum2 frustum2 = new Frustum2();

	private Vector3f position = new Vector3f();
	private float yaw = 0; // around y
	private float pitch = 0;
	private float roll = 0;

	private Vector3f look = new Vector3f();
	private Vector3f nlook = new Vector3f();

	/**
	 * @return the nlook
	 */
	public Vector3f getNlook() {
		return nlook;
	}

	private Vector3f right = new Vector3f();
	private Vector3f up = new Vector3f();
	public boolean isFrustumVisible = false;

	int counter = 0;
	private FloatBuffer mv = pmvMatrix.glGetMvMatrixf();
	final private int mvOffset = mv.position();
	private float[] mulMVP = new float[16]; // as calculated by pmvMatrix
	StringBuilder s = new StringBuilder();

	public Location3f offset = new Location3f();

	public void pack() {
		if (isAttachedToCameraman) {
			position.x = cameraman.getCameraX() + offset.x;
			position.y = cameraman.getCameraY() + offset.y;
			position.z = cameraman.getCameraZ() + offset.z;
			if (cameraman instanceof MatrixCamera) {
				final MatrixCamera cam = (MatrixCamera) cameraman;
				yaw = cam.getYaw();
				pitch = cam.getPitch();
				roll = cam.getRoll();
				zNear = cam.getzNear();
				zFar = cam.getzFar();
				fov = cam.getFov();
				aspect = cam.getAspect();
			}
		}
		pmvMatrix.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		pmvMatrix.glLoadIdentity();
		pmvMatrix.gluPerspective(fov, aspect, zNear, zFar);

		pmvMatrix.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		pmvMatrix.glLoadIdentity();
		pmvMatrix.glRotatef(-pitch, 1, 0, 0);
		pmvMatrix.glRotatef(-yaw, 0, 1, 0);
		pmvMatrix.glRotatef(-roll, 0, 0, 1);
		pmvMatrix.glGetFrustum();
		pmvMatrix.glTranslatef(-position.x, -position.y, -position.z);
		// pmvMatrix.setDirty();
		pmvMatrix.update();
		// mv = pmvMatrix.glGetMatrixf(GLMatrixFunc.GL_MODELVIEW);
		// pmvMatrix.glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, mv);

		right.set(mv.get(mvOffset + 0), mv.get(mvOffset + 4), mv.get(mvOffset + 8));
		look.set(mv.get(mvOffset + 2), mv.get(mvOffset + 6), mv.get(mvOffset + 10));
		// look.negate();
		up.cross(look, right);
		nlook.set(look);
		nlook.negate();
		// System.out.println(frustum2.toString());
		// packFrustum();
		isDirty = false;
		isOrientationChanged = false;
	}

	protected FloatBuffer Mvi = FloatBuffer.allocate(16);
	protected FloatBuffer Pi = FloatBuffer.allocate(16);
	protected ProjectFloat projectFloat = new ProjectFloat(true);

	public void renderFrustum() {
		final GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glDisable(GL2.GL_CULL_FACE);
		gl.glDisable(GL2.GL_DEPTH_TEST);
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glDisable(GL2.GL_ALPHA_TEST);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
		gl.glColor4f(0.2f, 0.2f, 0.7f, 0.3f);
		gl.glBegin(GL2.GL_QUADS);
		flb.v();
		flt.v();
		frt.v();
		frb.v();
		gl.glEnd();
		gl.glBegin(GL2.GL_QUADS);
		nlb.v();
		nlt.v();
		nrt.v();
		nrb.v();
		gl.glEnd();
		gl.glBegin(GL2.GL_LINES);
		gl.glColor4f(1f, 1f, 1f, 0.4f);
		nearc.v();
		farc.v();
		gl.glColor3f(1.0f, 0.0f, 1.0f);
		nlb.v();
		flb.v();
		nlt.v();
		flt.v();
		nrt.v();
		frt.v();
		nrb.v();
		frb.v();
		gl.glEnd();
	}

	private float renderFrustumTang = (float) Math.tan((Math.PI * fov / 180) / 2.0f);
	private float renderFrustumNear = 1f;
	private float renderFrustumFar = 100f;
	private float renderFrustumNh = renderFrustumNear * renderFrustumTang;
	private float renderFrustumNw = renderFrustumNh * aspect;
	private float renderFrustumFh = renderFrustumFar * renderFrustumTang;
	private float renderFrustumFw = renderFrustumFh * aspect;

	public void updateFrustum() {
		frustum.updateByPlanes(pmvMatrix.glGetFrustum().getPlanes());
		projectFloat.gluInvertMatrixf(pmvMatrix.glGetMvMatrixf(), Mvi);
		projectFloat.gluInvertMatrixf(pmvMatrix.glGetPMatrixf(), Pi);
		renderFrustumTang = (float) Math.tan((Math.PI * fov / 180) / 2.0f);
		renderFrustumNear = 1f;
		renderFrustumFar = 100f;
		renderFrustumNh = renderFrustumNear * renderFrustumTang;
		renderFrustumNw = renderFrustumNh * aspect;
		renderFrustumFh = renderFrustumFar * renderFrustumTang;
		renderFrustumFw = renderFrustumFh * aspect;

		eye.set(position.x, position.y, position.z);
		nearc.set(eye);
		nearc.translate(look, -renderFrustumNear);
		farc.set(eye);
		farc.translate(look, -renderFrustumFar);
		flb.set(farc);
		flb.translate(right, -renderFrustumFw / 2);
		flb.translate(up, -renderFrustumFh / 2);
		flt.set(farc);
		flt.translate(right, -renderFrustumFw / 2);
		flt.translate(up, renderFrustumFh / 2);
		frb.set(farc);
		frb.translate(right, renderFrustumFw / 2);
		frb.translate(up, -renderFrustumFh / 2);
		frt.set(farc);
		frt.translate(right, renderFrustumFw / 2);
		frt.translate(up, renderFrustumFh / 2);

		nlb.set(nearc);
		nlb.translate(right, -renderFrustumNw / 2);
		nlb.translate(up, -renderFrustumNh / 2);
		nlt.set(nearc);
		nlt.translate(right, -renderFrustumNw / 2);
		nlt.translate(up, renderFrustumNh / 2);
		nrb.set(nearc);
		nrb.translate(right, renderFrustumNw / 2);
		nrb.translate(up, -renderFrustumNh / 2);
		nrt.set(nearc);
		nrt.translate(right, renderFrustumNw / 2);
		nrt.translate(up, renderFrustumNh / 2);

	}

	private vec eye = new vec();
	private vec nearc = new vec();
	private vec flb = new vec();
	private vec flt = new vec();
	private vec frb = new vec();
	private vec frt = new vec();
	private vec nlb = new vec();
	private vec nlt = new vec();
	private vec nrb = new vec();
	private vec nrt = new vec();
	private vec farc = new vec();
	private GL2 gl2;

	private class vec {
		public float x;
		public float y;
		public float z;

		public void v() {
			gl2.glVertex3f(x, y, z);
		}

		public void set(vec v) {
			set(v.x, v.y, v.z);
		}

		public void set(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public void translate(Vector3f dir, float d) {
			translate(dir.x * d, dir.y * d, dir.z * d);
		}

		public void translate(float x, float y, float z) {
			this.x += x;
			this.y += y;
			this.z += z;
		}
	}

	// private void packFrustum() {
	// ru.olamedia.math.Vector3f eye = new
	// ru.olamedia.math.Vector3f(position.getX(), position.getY(),
	// position.getZ());
	// ru.olamedia.math.Vector3f eyef = eye;// .translate(look, 1f);
	// nearc = eyef.translate(look, zNear);
	// ru.olamedia.math.Vector3f farc = eyef.translate(look, zFar);
	// final float tang = (float) Math.tan((Math.PI * fov / 180) / 2.0f);
	// final float nh = zNear * tang * (isFrustumVisible ? 0.3f : 1);// zNear *
	// // tang;
	// final float nw = nh * aspect * aspect;
	// final float fh = zFar * tang * (isFrustumVisible ? 0.5f : 1);// zNear *
	// // tang;
	// final float fw = fh * aspect * aspect;
	// final ru.olamedia.math.Vector3f nrb = nearc.translate(right, -nw /
	// 2).translate(up, -nh / 2);
	// final ru.olamedia.math.Vector3f nlb = nearc.translate(right, nw /
	// 2).translate(up, -nh / 2);
	// @SuppressWarnings("unused")
	// final ru.olamedia.math.Vector3f nrt = nearc.translate(right, -nw /
	// 2).translate(up, nh / 2);
	// final ru.olamedia.math.Vector3f nlt = nearc.translate(right, nw /
	// 2).translate(up, nh / 2);
	// final ru.olamedia.math.Vector3f frb = farc.translate(right, -fw /
	// 2).translate(up, -fh / 2);
	// final ru.olamedia.math.Vector3f flb = farc.translate(right, fw /
	// 2).translate(up, -fh / 2);
	// final ru.olamedia.math.Vector3f frt = farc.translate(right, -fw /
	// 2).translate(up, fh / 2);
	// final ru.olamedia.math.Vector3f flt = farc.translate(right, fw /
	// 2).translate(up, fh / 2);
	//
	// frustum1.leftPlane.set3Points(nlb, flb, flt);
	// frustum1.leftPlane.n.negate();
	// frustum1.rightPlane.set3Points(nrb, frt, frb);
	// // frustum.rightPlane.n.negate();
	// frustum1.topPlane.set3Points(nlb, frb, flb);// nlt, frt, flt);
	// // frustum.topPlane.n.negate();
	// frustum1.bottomPlane.set3Points(frt, nlt, flt);
	// // frustum.bottomPlane.n.negate();
	// frustum1.nearPlane.set3Points(nlb, nlt, nrb);
	// frustum1.farPlane.set3Points(flt, flb, frb);
	// }

	private GLU glu;

	public void updateControls() {
		updateKeyboard();
		updateMouse();
	}

	public void setUp() {
		final GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadMatrixf(pmvMatrix.glGetPMatrixf());
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		// FloatBuffer pmv = FloatBuffer.allocate(16);
		// pmvMatrix.glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, pmv);
		gl.glLoadMatrixf(pmvMatrix.glGetMvMatrixf());
	}

	public float intersectsRectangle(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4) {
		return Math.min(intersectsTriangle(vertex1, vertex2, vertex3), intersectsTriangle(vertex3, vertex4, vertex1));
	}

	public float intersectsTriangle(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3) {
		// Compute vectors along two edges of the triangle.
		Vector3f edge1 = new Vector3f(), edge2 = new Vector3f();

		edge1.sub(vertex2, vertex1);
		edge2.sub(vertex3, vertex1);

		Vector3f nlook = new Vector3f(look);
		nlook.negate();
		// Compute the determinant.
		Vector3f directionCrossEdge2 = new Vector3f();
		directionCrossEdge2.cross(nlook, edge2);

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

		float triangleV = nlook.dot(distanceCrossEdge1);
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
		isOrientationChanged = true;
		updateMouse();
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
			this.cameraman.update(Game.instance.getFrameDelta());
			return;
		}
		// --- Keyboard
		final int left = Keyboard.isKeyDown("strafeLeft") ? 1 : 0;
		final int right = Keyboard.isKeyDown("strafeRight") ? 1 : 0;
		final int up = Keyboard.isKeyDown("flyForward") ? 1 : 0;
		final int down = Keyboard.isKeyDown("flyBack") ? 1 : 0;
		final int flyUp = Keyboard.isKeyDown("flyUp") ? 1 : 0;
		final int flyDown = Keyboard.isKeyDown("flyDown") ? 1 : 0;
		final float distance = 4f * 4.5f * Game.instance.getFrameDelta(); // runspeed,
																		// m/s
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
		if (aspect != this.aspect) {
			this.aspect = aspect;
			setDirty();
		}
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
		updateMouse();
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

	public void setDirty() {
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

	public void attachTo(Cameraman player, boolean captureControls) {
		this.cameraman = player;
		this.isAttachedToCameraman = true;
		if (captureControls) {
			this.cameraman.captureControls();
		}
		this.cameraman.setCamera(this);
	}

	public void detach(boolean captureControls) {
		this.isAttachedToCameraman = false;
		if (captureControls) {
			this.captureControls();
		}
		this.cameraman.setCamera(null);
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

	@Override
	public MatrixCamera getCamera() {
		return attachedCamera;
	}

	@Override
	public void setCamera(MatrixCamera camera) {
		attachedCamera = camera;
	}

	@Override
	public float getCameraX() {
		return position.x;
	}

	@Override
	public float getCameraY() {
		return position.y - 2;// + look.y * 2;
	}

	@Override
	public float getCameraZ() {
		return position.z;// + look.z * 2;
	}

	@Override
	public void update(float delta) {

	}

	public boolean isDirty() {
		return isDirty;
	}

}
