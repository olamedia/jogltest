package ru.olamedia.math;

public class ViewMatrix extends Matrix4f {
	private Matrix4f translation = new Matrix4f();
	private Matrix4f scale = new Matrix4f();
	private Matrix4f rotation = new Matrix4f();
	private TransformMatrix transform = new TransformMatrix();

	public ViewMatrix() {
		translation.setIdentity();
		scale.setIdentity();
		rotation.setIdentity();
		transform.setIdentity();
		pack();
	}

	public void pack() {
		loadIdentity();
		transform.loadIdentity();
		transform.applyScale(scale);
		transform.applyRotation(rotation);
		transform.applyTranslation(translation);
		apply(transform);
		@SuppressWarnings("unused")
		float[] t = transform.toFloatArray();

		// this.m[12] = 0;
		// this.m[13] = 0;
		// this.m[14] = 0;
		// Fill translation:
		// this.m[3] = -(t[0] * t[12] + t[1] * t[13] + t[2] * t[14]);
		// this.m[7] = -(t[4] * t[12] + t[5] * t[13] + t[6] * t[14]);
		// this.m[11] = (t[8] * t[12] + t[9] * t[13] + t[10] * t[14]);
		// m[12] = -(t[0] * t[12] + t[1] * t[13] + t[2] * t[14]);
		// m[13] = -(t[4] * t[12] + t[5] * t[13] + t[6] * t[14]);
		// m[14] = (t[8] * t[12] + t[9] * t[13] + t[10] * t[14]);
	}

	public float getX() {
		return -translation.get(12);
	}

	public float getY() {
		return -translation.get(13);
	}

	public float getZ() {
		return -translation.get(14);
	}

	/**
	 * @return the translation
	 */
	public Matrix4f getTranslation() {
		return translation;
	}

	/**
	 * @param translation
	 *            the translation to set
	 */
	public void setTranslation(Matrix4f translation) {
		this.translation = translation;
	}

	/**
	 * @return the scale
	 */
	public Matrix4f getScale() {
		return scale;
	}

	/**
	 * @param scale
	 *            the scale to set
	 */
	public void setScale(Matrix4f scale) {
		this.scale = scale;
	}

	/**
	 * @return the rotation
	 */
	public Matrix4f getRotation() {
		return rotation;
	}

	/**
	 * @param rotation
	 *            the rotation to set
	 */
	public void setRotation(Matrix4f rotation) {
		this.rotation = rotation;
	}

	public void rotateX(float degrees) {
		setRotation(getRotation().multiply(Matrix4f.rotateXMatrix(degrees)));
	}

	public void rotateY(float degrees) {
		setRotation(getRotation().multiply(Matrix4f.rotateYMatrix(degrees)));
	}

	public void rotateZ(float degrees) {
		setRotation(getRotation().multiply(Matrix4f.rotateZMatrix(degrees)));
	}

	public Matrix4f getTransform() {
		return transform;
	}
}
