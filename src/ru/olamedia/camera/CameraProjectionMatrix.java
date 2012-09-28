package ru.olamedia.camera;

import org.openmali.FastMath;
import org.openmali.vecmath2.Matrix4f;

public class CameraProjectionMatrix extends Matrix4f {

	/**
	 * Creates a mesa-style perspective projection transform, that mimics a
	 * standard, camera-based, view-model.
	 * 
	 * @param fovy
	 *            specifies the field of view in the y direction, in radians
	 * @param aspect
	 *            specifies the aspect ratio and thus the field of view in the x
	 *            direction. The aspect ratio is the ratio of x to y, or width
	 *            to height.
	 * @param zNear
	 *            the distance to the frustum's near clipping plane. This value
	 *            must be positive, (the value -zNear is the location of the
	 *            near clip plane).
	 * @param zFar
	 *            the distance to the frustum's far clipping plane.
	 */
	public final void perspectiveMesa(float fovy, float aspect, float zNear,
			float zFar) {
		final float ymax = zNear * FastMath.tan(fovy);
		final float ymin = -ymax;
		final float xmin = ymin * aspect;
		final float xmax = ymax * aspect;

		// don't call glFrustum() because of error semantics (covglu)
		frustumMesa(xmin, xmax, ymin, ymax, zNear, zFar);
	}

	/**
	 * Creates a masa-style perspective projection transform, that mimics a
	 * standard, camera-based, view-model. The frustum function-call establishes
	 * a view-model with the eye at the apex of a symmetric view frustum. The
	 * arguments define the frustum and its associated perspective projection:
	 * (left, bottom, -near) and (right, top, -near) specify the point on the
	 * near clipping plane that maps onto the lower-left and upper-right corners
	 * of the window respectively, assuming the eye is located at (0, 0, 0).
	 * 
	 * @param left
	 *            the vertical line on the left edge of the near clipping plane
	 *            mapped to the left edge of the graphics window
	 * @param right
	 *            the vertical line on the right edge of the near clipping plane
	 *            mapped to the right edge of the graphics window
	 * @param bottom
	 *            the horizontal line on the bottom edge of the near clipping
	 *            plane mapped to the bottom edge of the graphics window
	 * @param top
	 *            the horizontal line on the top edge of the near
	 * @param zNear
	 *            the distance to the frustum's near clipping plane. This value
	 *            must be positive, (the value -near is the location of the near
	 *            clip plane).
	 * @param zFar
	 *            the distance to the frustum's far clipping plane. This value
	 *            must be positive, and must be greater than near.
	 */
	public final void frustumMesa(float left, float right, float bottom,
			float top, float zNear, float zFar) {
		final float x = (2.0f * zNear) / (right - left);
		final float y = (2.0f * zNear) / (top - bottom);
		final float a = (right + left) / (right - left);
		final float b = (top + bottom) / (top - bottom);
		final float c = -(zFar + zNear) / (zFar - zNear);
		final float d = -(2.0f * zFar * zNear) / (zFar - zNear);

		this.set(x, 0f, 0f, 0f, 0f, y, 0f, 0f, a, b, c, -1f, 0f, 0f, d, 0f);

	}

}
