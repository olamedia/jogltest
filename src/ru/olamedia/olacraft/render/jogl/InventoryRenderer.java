package ru.olamedia.olacraft.render.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.texture.Texture;

import ru.olamedia.olacraft.inventory.Inventory;
import ru.olamedia.olacraft.world.blockStack.BlockStack;
import ru.olamedia.olacraft.world.blockTypes.EmptyBlockType;
import ru.olamedia.texture.TextureManager;

public class InventoryRenderer {
	private Inventory inventory;
	private BlockStackRenderer stackRenderer;

	int stackSize = 32;
	int spacing = 2;
	int padding = 2;
	int x;
	int y;

	public InventoryRenderer(Inventory inventory) {
		this.inventory = inventory;
		stackRenderer = new BlockStackRenderer();
	}

	public void render(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
		int vWidth = drawable.getWidth();
		int vHeight = drawable.getHeight();
		// Draw GUI

		int width = stackSize * Inventory.BIND_NUM + spacing * (Inventory.BIND_NUM - 1) + padding * 2;
		int height = stackSize + padding * 2;
		x = (vWidth - width) / 2;
		y = (vHeight - height) - 10;
		gl.glRecti(x, y, x + width, y + height);
		// Draw stacks
		gl.glEnable(GL2.GL_TEXTURE_2D);
		for (int i = 0; i < Inventory.BIND_NUM; i++) {
			renderStack(i, drawable);
		}
		gl.glPopAttrib();
	}

	public void renderStack(int i, GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		BlockStack stack = inventory.binded[i];
		int sx = x + padding + stackSize * i + spacing * i;
		int sy = y + padding;
		if (null != stack && !(stack.type instanceof EmptyBlockType)) {
			Texture tex = TextureManager.get(stack.type.getStackTextureFile());
			if (null != tex) {
				tex.bind(gl);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
				gl.glColor3f(1, 1, 1);
				int x1 = sx;
				int x2 = sx + stackSize;
				int y1 = sy;
				int y2 = sy + stackSize;
				gl.glBegin(GL2.GL_QUADS);
				{
					gl.glTexCoord2f(0, 0);
					gl.glVertex2f(x1, y1);
					gl.glTexCoord2f(0, 1);
					gl.glVertex2f(x1, y2);
					gl.glTexCoord2f(1, 1);
					gl.glVertex2f(x2, y2);
					gl.glTexCoord2f(1, 0);
					gl.glVertex2f(x2, y1);
				}
				gl.glEnd();
				// gl.glRecti(sx, sy, sx + stackSize, sy + stackSize);
			}
		} else {
			gl.glDisable(GL2.GL_TEXTURE_2D);
			float gray = 0.5f;
			gl.glColor3f(gray, gray, gray);
			gl.glRecti(sx, sy, sx + stackSize, sy + stackSize);
			gl.glEnable(GL2.GL_TEXTURE_2D);
		}
	}
}
