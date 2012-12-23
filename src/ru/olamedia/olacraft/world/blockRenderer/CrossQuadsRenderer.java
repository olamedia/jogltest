package ru.olamedia.olacraft.world.blockRenderer;

import java.nio.FloatBuffer;
import java.util.HashMap;

import ru.olamedia.geom.ImmModeMesh;
import ru.olamedia.olacraft.world.blockTypes.BlockType;

public class CrossQuadsRenderer extends BoxRenderer {
	public static boolean useTriangles = false;
	private HashMap<Integer, ImmModeMesh> precompiled = new HashMap<Integer, ImmModeMesh>();

	public void addLeftQuad() {
		// triangle strip: И
		mesh.setUV(textureLeft, textureTop);
		addTopLeftBackVertex();
		mesh.setUV(textureLeft, textureBottom);
		addBottomLeftBackVertex();
		if (useTriangles) {
			mesh.setUV(textureRight, textureBottom);
			addBottomRightFrontVertex();

			mesh.setUV(textureLeft, textureTop);
			addTopLeftBackVertex();
		}
		mesh.setUV(textureRight, textureBottom);
		addBottomRightFrontVertex();
		mesh.setUV(textureRight, textureTop);
		addTopRightFrontVertex();
		// mirror
		mesh.setUV(textureLeft, textureTop);
		addTopRightFrontVertex();
		mesh.setUV(textureLeft, textureBottom);
		addBottomRightFrontVertex();
		if (useTriangles) {
			mesh.setUV(textureRight, textureBottom);
			addBottomLeftBackVertex();

			mesh.setUV(textureLeft, textureTop);
			addTopRightFrontVertex();
		}
		mesh.setUV(textureRight, textureBottom);
		addBottomLeftBackVertex();
		mesh.setUV(textureRight, textureTop);
		addTopLeftBackVertex();
	}

	public void addRightQuad() {
		// triangle strip: И
		mesh.setUV(textureLeft, textureTop);
		addTopRightBackVertex();
		mesh.setUV(textureLeft, textureBottom);
		addBottomRightBackVertex();
		if (useTriangles) {
			mesh.setUV(textureLeft, textureTop);
			addTopRightBackVertex();

			mesh.setUV(textureRight, textureBottom);
			addBottomLeftFrontVertex();
		}
		mesh.setUV(textureRight, textureBottom);
		addBottomLeftFrontVertex();
		mesh.setUV(textureRight, textureTop);
		addTopLeftFrontVertex();
		// mirror
		mesh.setUV(textureLeft, textureTop);
		addTopLeftFrontVertex();
		mesh.setUV(textureLeft, textureBottom);
		addBottomLeftFrontVertex();
		if (useTriangles) {
			mesh.setUV(textureRight, textureBottom);
			addBottomRightBackVertex();

			mesh.setUV(textureLeft, textureTop);
			addTopLeftFrontVertex();
		}
		mesh.setUV(textureRight, textureBottom);
		addBottomRightBackVertex();
		mesh.setUV(textureRight, textureTop);
		addTopRightBackVertex();
	}

	@Override
	public int getMeshVertexCount(BlockType currentType, FloatBuffer renderLocation, boolean useShader) {
		return 4 * 4;
	}

	@Override
	public void putMesh(ImmModeMesh mesh, BlockType type, FloatBuffer location, boolean useShader) {
		this.mesh = mesh;
		// mesh.enableNormal3();
		mesh.glNormal3f(1, 0, 0);
		mesh.glTranslate(location.get(0), location.get(1), location.get(2));
		mesh.glColor4f(1, 1, 1, 1f);
		setPointOffset(0.5f * scale);
		{
			setTextureOffset(type.getLeftTextureOffset());
			addLeftQuad();
			addRightQuad();
		}
	}

	@Override
	public ImmModeMesh getMesh(BlockType type, FloatBuffer location, boolean glsl) {
		mesh = ImmModeMesh.allocate(4 * 4); // 6 rectangles
		mesh.enableColor4();
		mesh.enableVertex3();
		mesh.enableTexCoord2();
		mesh.setGLSL(glsl);
		putMesh(mesh, type, location, glsl);
		mesh.end();
		return mesh;
	}

}
