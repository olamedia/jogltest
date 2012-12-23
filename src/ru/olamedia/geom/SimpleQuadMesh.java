package ru.olamedia.geom;

import javax.vecmath.Point3f;

import ru.olamedia.asset.SpriteRectangle;

public class SimpleQuadMesh extends Mesh {

	public boolean restart = true;

	public Point3f bottomLeftBack = new Point3f();
	public Point3f bottomLeftFront = new Point3f();
	public Point3f bottomRightBack = new Point3f();
	public Point3f bottomRightFront = new Point3f();

	public Point3f topLeftBack = new Point3f();
	public Point3f topLeftFront = new Point3f();
	public Point3f topRightBack = new Point3f();
	public Point3f topRightFront = new Point3f();

	public float textureTop = 1;
	public float textureBottom = 0;
	public float textureLeft = 0;
	public float textureRight = 1;

	public void restart() {
		restart = true;
	}

	public void start() {
		restart = false;
	}

	public SimpleQuadMesh(int size) {
		super(size * 4);
	}

	public void addBottomLeftBackVertex() {
		setPoint3f(bottomLeftBack);
	}

	public void addBottomLeftFrontVertex() {
		setPoint3f(bottomLeftFront);
	}

	public void addBottomRightBackVertex() {
		setPoint3f(bottomRightBack);
	}

	public void addBottomRightFrontVertex() {
		setPoint3f(bottomRightFront);
	}

	public void addTopLeftBackVertex() {
		setPoint3f(topLeftBack);
	}

	public void addTopLeftFrontVertex() {
		setPoint3f(topLeftFront);
	}

	public void addTopRightBackVertex() {
		setPoint3f(topRightBack);
	}

	public void addTopRightFrontVertex() {
		setPoint3f(topRightFront);
	}

	public void addFrontQuad() {
		// triangle strip: И
		setUV(textureLeft, textureTop);
		addTopLeftFrontVertex(); // top left
		setUV(textureLeft, textureBottom);
		addBottomLeftFrontVertex(); // bottom left
		setUV(textureRight, textureBottom);
		addBottomRightFrontVertex(); // bottom right
		setUV(textureRight, textureTop);
		addTopRightFrontVertex(); // top right
	}

	public void addBackQuad() {
		// triangle strip: И
		setUV(textureLeft, textureTop);
		addTopRightBackVertex();
		setUV(textureLeft, textureBottom);
		addBottomRightBackVertex();
		setUV(textureRight, textureBottom);
		addBottomLeftBackVertex();
		setUV(textureRight, textureTop);
		addTopLeftBackVertex();
	}

	public void addLeftQuad() {
		// triangle strip: И
		setUV(textureLeft, textureTop);
		addTopLeftBackVertex();
		setUV(textureLeft, textureBottom);
		addBottomLeftBackVertex();
		setUV(textureRight, textureBottom);
		addBottomLeftFrontVertex();
		setUV(textureRight, textureTop);
		addTopLeftFrontVertex();
	}

	public void addRightQuad() {
		// triangle strip: И
		setUV(textureLeft, textureTop);
		addTopRightFrontVertex();
		setUV(textureLeft, textureBottom);
		addBottomRightFrontVertex();
		setUV(textureRight, textureBottom);
		addBottomRightBackVertex();
		setUV(textureRight, textureTop);
		addTopRightBackVertex();
	}

	public void addTopQuad() {
		// triangle strip: И
		setUV(textureLeft, textureBottom);
		addTopLeftBackVertex();
		setUV(textureLeft, textureTop);
		addTopLeftFrontVertex();
		setUV(textureRight, textureTop);
		addTopRightFrontVertex();
		setUV(textureRight, textureBottom);
		addTopRightBackVertex();
	}

	public void addBottomQuad() {
		// triangle strip: И
		setUV(textureLeft, textureBottom);
		addBottomLeftFrontVertex();
		setUV(textureLeft, textureTop);
		addBottomLeftBackVertex();
		setUV(textureRight, textureTop);
		addBottomRightBackVertex();
		setUV(textureRight, textureBottom);
		addBottomRightFrontVertex();
	}

	public void setPointOffset(float offset) {
		bottomLeftBack.x = -offset;
		bottomLeftBack.y = -offset;
		bottomLeftBack.z = -offset;
		//
		bottomLeftFront.x = -offset;
		bottomLeftFront.y = -offset;
		bottomLeftFront.z = offset;
		//
		bottomRightBack.x = offset;
		bottomRightBack.y = -offset;
		bottomRightBack.z = -offset;
		//
		bottomRightFront.x = offset;
		bottomRightFront.y = -offset;
		bottomRightFront.z = offset;
		//
		topLeftBack.x = -offset;
		topLeftBack.y = offset;
		topLeftBack.z = -offset;
		//
		topLeftFront.x = -offset;
		topLeftFront.y = offset;
		topLeftFront.z = offset;
		//
		topRightBack.x = offset;
		topRightBack.y = offset;
		topRightBack.z = -offset;
		//
		topRightFront.x = offset;
		topRightFront.y = offset;
		topRightFront.z = offset;

	}

	public void setTextureOffset(SpriteRectangle offset) {
		if (null != offset) {
			// System.out.print("Offset " + "[" + offset.topLeft.x + "," +
			// offset.topLeft.y + "]");
			textureTop = offset.topLeft.y;
			textureLeft = offset.topLeft.x;
			textureRight = offset.bottomRight.x;
			textureBottom = offset.bottomRight.y;
		}
	}

}
