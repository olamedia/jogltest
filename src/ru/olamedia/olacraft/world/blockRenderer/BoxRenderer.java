package ru.olamedia.olacraft.world.blockRenderer;

import java.nio.FloatBuffer;

import javax.vecmath.Point3f;

import ru.olamedia.asset.SpriteRectangle;
import ru.olamedia.geom.ImmModeMesh;
import ru.olamedia.olacraft.world.blockTypes.BlockType;

public class BoxRenderer extends AbstractBlockRenderer {

	public static boolean useTriangles = false;

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

	public void setPoint3f(Point3f p) {
		mesh.glVertex3f(p.x, p.y, p.z);
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
		mesh.setUV(textureLeft, textureTop);
		addTopLeftFrontVertex(); // top left
		mesh.setUV(textureLeft, textureBottom);
		addBottomLeftFrontVertex(); // bottom left
		if (useTriangles) {
			mesh.setUV(textureRight, textureBottom);
			addBottomRightFrontVertex(); // bottom right

			mesh.setUV(textureLeft, textureTop);
			addTopLeftFrontVertex(); // top left
		}
		mesh.setUV(textureRight, textureBottom);
		addBottomRightFrontVertex(); // bottom right
		mesh.setUV(textureRight, textureTop);
		addTopRightFrontVertex(); // top right
	}

	public void addBackQuad() {
		// triangle strip: И
		mesh.setUV(textureLeft, textureTop);
		addTopRightBackVertex();
		mesh.setUV(textureLeft, textureBottom);
		addBottomRightBackVertex();
		if (useTriangles) {
			mesh.setUV(textureRight, textureBottom);
			addBottomLeftBackVertex();

			mesh.setUV(textureLeft, textureTop);
			addTopRightBackVertex();
		}
		mesh.setUV(textureRight, textureBottom);
		addBottomLeftBackVertex();
		mesh.setUV(textureRight, textureTop);
		addTopLeftBackVertex();
	}

	public void addLeftQuad() {
		// triangle strip: И
		mesh.setUV(textureLeft, textureTop);
		addTopLeftBackVertex();
		mesh.setUV(textureLeft, textureBottom);
		addBottomLeftBackVertex();
		if (useTriangles) {
			mesh.setUV(textureRight, textureBottom);
			addBottomLeftFrontVertex();

			mesh.setUV(textureLeft, textureTop);
			addTopLeftBackVertex();
		}
		mesh.setUV(textureRight, textureBottom);
		addBottomLeftFrontVertex();
		mesh.setUV(textureRight, textureTop);
		addTopLeftFrontVertex();
	}

	public void addRightQuad() {
		// triangle strip: И
		mesh.setUV(textureLeft, textureTop);
		addTopRightFrontVertex();
		mesh.setUV(textureLeft, textureBottom);
		addBottomRightFrontVertex();
		if (useTriangles) {
			mesh.setUV(textureRight, textureBottom);
			addBottomRightBackVertex();

			mesh.setUV(textureLeft, textureTop);
			addTopRightFrontVertex();
		}
		mesh.setUV(textureRight, textureBottom);
		addBottomRightBackVertex();
		mesh.setUV(textureRight, textureTop);
		addTopRightBackVertex();
	}

	public void addTopQuad() {
		// triangle strip: И
		mesh.setUV(textureLeft, textureBottom);
		addTopLeftBackVertex();
		mesh.setUV(textureLeft, textureTop);
		addTopLeftFrontVertex();
		if (useTriangles) {
			mesh.setUV(textureRight, textureTop);
			addTopRightFrontVertex();

			mesh.setUV(textureLeft, textureBottom);
			addTopLeftBackVertex();
		}
		mesh.setUV(textureRight, textureTop);
		addTopRightFrontVertex();
		mesh.setUV(textureRight, textureBottom);
		addTopRightBackVertex();
	}

	public void addBottomQuad() {
		// triangle strip: И
		mesh.setUV(textureLeft, textureBottom);
		addBottomLeftFrontVertex();
		mesh.setUV(textureLeft, textureTop);
		addBottomLeftBackVertex();
		if (useTriangles) {
			mesh.setUV(textureRight, textureTop);
			addBottomRightBackVertex();

			mesh.setUV(textureLeft, textureBottom);
			addBottomLeftFrontVertex();
		}
		mesh.setUV(textureRight, textureTop);
		addBottomRightBackVertex();
		mesh.setUV(textureRight, textureBottom);
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

	static float tcfix = 0.00001f;

	public void setTextureOffset(SpriteRectangle texRect) {
		tcfix = (texRect.bottomRight.x - texRect.topLeft.x) / 512;
		textureLeft = texRect.topLeft.x + tcfix;
		textureRight = texRect.bottomRight.x - tcfix;
		textureTop = texRect.topLeft.y + tcfix;
		textureBottom = texRect.bottomRight.y - tcfix;
	}

	@Override
	public int getMeshVertexCount(BlockType currentType, FloatBuffer renderLocation, boolean useShader) {
		return 4 * ((renderBack ? 1 : 0) + (renderFront ? 1 : 0) + (renderLeft ? 1 : 0) + (renderRight ? 1 : 0)
				+ (renderBottom ? 1 : 0) + (renderTop ? 1 : 0));
	}

	@Override
	public void putMesh(ImmModeMesh mesh, BlockType type, FloatBuffer location, boolean useShader) {
		this.mesh = mesh;
		mesh.enableColor4();
		mesh.enableVertex3();
		mesh.enableTexCoord2();
		// mesh.enableNormal3();
		mesh.glTranslate(location.get(0), location.get(1), location.get(2));
		mesh.glColor4f(1, 1, 1, 1);
		setPointOffset(0.5f * scale);
		{
			if (renderBack) {
				setTextureOffset(type.getBackTextureOffset());
				mesh.glNormal3f(0, 0, 1);
				addBackQuad();
			}
			if (renderFront) {
				setTextureOffset(type.getFrontTextureOffset());
				mesh.glNormal3f(0, 0, -1);
				addFrontQuad();
			}
			if (renderLeft) {
				setTextureOffset(type.getLeftTextureOffset());
				mesh.glNormal3f(1, 0, 0);
				addLeftQuad();
			}
			if (renderRight) {
				setTextureOffset(type.getRightTextureOffset());
				mesh.glNormal3f(-1, 0, 1);
				addRightQuad();
			}
			if (renderBottom) {
				setTextureOffset(type.getBottomTextureOffset());
				mesh.glNormal3f(0, -1, 0);
				addBottomQuad();
			}
			if (renderTop) {
				setTextureOffset(type.getTopTextureOffset());
				mesh.glNormal3f(0, 1, 0);
				addTopQuad();
			}
		}
	}

	@Override
	public ImmModeMesh getMesh(BlockType type, FloatBuffer location, boolean glsl) {
		mesh = ImmModeMesh.allocate(4 * 6); // 6 rectangles
		mesh.setGLSL(glsl);
		putMesh(mesh, type, location, glsl);
		mesh.end();
		return mesh;
	}
}
