package ru.olamedia.olacraft.render.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

import ru.olamedia.geom.SimpleQuadMesh;
import ru.olamedia.olacraft.world.chunk.BlockSlice;
import ru.olamedia.texture.TextureManager;

public class BlockRenderer {
	private BlockSlice slice;
	private SimpleQuadMesh mesh;

	public BlockRenderer(BlockSlice slice) {
		this.slice = slice;
	}

	public SimpleQuadMesh getMesh(GL glx) {
		GL2 gl = glx.getGL2();
		// 14739
		SimpleQuadMesh mesh = new SimpleQuadMesh(999999);
		mesh.useColor();
		mesh.useTexture();

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_NEAREST_MIPMAP_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_NEAREST_MIPMAP_NEAREST);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_FASTEST);
		gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
		Texture grass = TextureManager.get("texture/grass.png");
		int tex = grass.getTextureObject(gl);

		TextureCoords tc = grass.getImageTexCoords();
		// mesh.setTextureSize(tc.right(), tc.top());
		System.out.println(grass.getWidth() + " " + grass.getHeight() + " | "
				+ tc.left() + " " + tc.right() + " " + tc.top() + " "
				+ tc.bottom());
		mesh.setTextureSize(grass.getWidth(), grass.getHeight());
		for (int x = 0; x < slice.getWidth(); x++) {
			for (int y = 0; y < slice.getHeight(); y++) {
				for (int z = 0; z < slice.getDepth(); z++) {
					mesh.setTranslation(x, y, z);
					// mesh.setColor4f(0, 1, 0, 1);
					mesh.setColor4f((float) Math.random(),
							(float) Math.random(), (float) Math.random(), 1);
					mesh.setGLTexture(tex);
					if (y == 0) {
						mesh.addBottomQuad();
					}
					if (y == 3) {
						// Math.random();
						mesh.addTopQuad();
					}
					if (y < 4) {
						if (x == 0) {
							mesh.addLeftQuad();
						}
						if (x == slice.getWidth() - 1) {
							mesh.addRightQuad();
						}
						if (z == 0) {
							mesh.addBackQuad();
						}
						if (z == slice.getDepth() - 1) {
							mesh.addFrontQuad();
						}
					}
				}
			}
		}
		mesh.endMesh();
		return mesh;
	}

	public void render(GL glx) {
		if (null == mesh) {
			mesh = getMesh(glx);
		}
		mesh.joglRender(glx);
	}

}
