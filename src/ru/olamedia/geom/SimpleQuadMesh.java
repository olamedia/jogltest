package ru.olamedia.geom;

public class SimpleQuadMesh extends Mesh {

	public SimpleQuadMesh(int size) {
		super(size * 4);
	}

	private void addBottomLeftBackVertex() {
		setPoint3f(-0.5f, -0.5f, -0.5f);
	}

	private void addBottomLeftFrontVertex() {
		setPoint3f(-0.5f, -0.5f, 0.5f);
	}

	private void addBottomRightBackVertex() {
		setPoint3f(0.5f, -0.5f, -0.5f);
	}

	private void addBottomRightFrontVertex() {
		setPoint3f(0.5f, -0.5f, 0.5f);
	}

	private void addTopLeftBackVertex() {
		setPoint3f(-0.5f, 0.5f, -0.5f);
	}

	private void addTopLeftFrontVertex() {
		setPoint3f(-0.5f, 0.5f, 0.5f);
	}

	private void addTopRightBackVertex() {
		setPoint3f(0.5f, 0.5f, -0.5f);
	}

	private void addTopRightFrontVertex() {
		setPoint3f(0.5f, 0.5f, 0.5f);
	}

	public void addFrontQuad() {
		// triangle strip: И
		setUV(0, 1);
		addTopLeftFrontVertex(); // top left
		setUV(0, 0);
		addBottomLeftFrontVertex(); // bottom left
		setUV(1, 0);
		addBottomRightFrontVertex(); // bottom right
		setUV(1, 1);
		addTopRightFrontVertex(); // top right
	}

	public void addBackQuad() {
		// triangle strip: И
		setUV(0, 1);
		addTopRightBackVertex();
		setUV(0, 0);
		addBottomRightBackVertex();
		setUV(1, 0);
		addBottomLeftBackVertex();
		setUV(1, 1);
		addTopLeftBackVertex();
	}

	public void addLeftQuad() {
		// triangle strip: И
		setUV(0, 1);
		addTopLeftBackVertex();
		setUV(0, 0);
		addBottomLeftBackVertex();
		setUV(1, 0);
		addBottomLeftFrontVertex();
		setUV(1, 1);
		addTopLeftFrontVertex();
	}

	public void addRightQuad() {
		// triangle strip: И
		setUV(0, 1);
		addTopRightFrontVertex();
		setUV(0, 0);
		addBottomRightFrontVertex();
		setUV(1, 0);
		addBottomRightBackVertex();
		setUV(1, 1);
		addTopRightBackVertex();
	}

	public void addTopQuad() {
		// triangle strip: И
		setUV(0, 0);
		addTopLeftBackVertex();
		setUV(0, 1);
		addTopLeftFrontVertex();
		setUV(1, 1);
		addTopRightFrontVertex();
		setUV(1, 0);
		addTopRightBackVertex();
	}

	public void addBottomQuad() {
		// triangle strip: И
		setUV(0, 0);
		addBottomLeftFrontVertex();
		setUV(0, 1);
		addBottomLeftBackVertex();
		setUV(1, 1);
		addBottomRightBackVertex();
		setUV(1, 0);
		addBottomRightFrontVertex();
	}

	

}
