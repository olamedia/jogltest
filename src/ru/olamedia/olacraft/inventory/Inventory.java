package ru.olamedia.olacraft.inventory;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

import com.jogamp.opengl.util.texture.Texture;

import ru.olamedia.asset.SpriteRectangle;
import ru.olamedia.game.GameFrame;
import ru.olamedia.geom.ImmModeMesh;
import ru.olamedia.input.MouseJail;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.world.blockStack.BlockStack;
import ru.olamedia.olacraft.world.blockTypes.EmptyBlockType;
import ru.olamedia.olacraft.world.blockTypes.GravelBlockType;
import ru.olamedia.olacraft.world.blockTypes.WheatBlockType;
import ru.olamedia.olacraft.world.blockTypes.stone.BrecciaStoneBlockType;
import ru.olamedia.texture.TextureManager;

public class Inventory {
	public static int BIND_NUM = 10;
	public static int BACKPACK_NUM = 10 * 5;
	public static int TOTAL_NUM = BIND_NUM + BACKPACK_NUM;
	public BlockStack[] binded = new BlockStack[TOTAL_NUM];
	public BlockStack selected;
	public int selectedId = 0;
	private boolean isGUIOpened = false;

	private int[] bindedX = new int[TOTAL_NUM];
	private int[] bindedY = new int[TOTAL_NUM];

	public BlockStack picked = null;
	public int pickedId = 0;

	private ImmModeMesh bindedGUI;

	public void pickHover(boolean half) {
		System.out.println("pick hover " + MouseJail.instance.y);
		int x = MouseJail.instance.x;
		int y = MouseJail.instance.y;
		for (int i = 0; i < TOTAL_NUM; i++) {
			if (x >= bindedX[i] && x <= bindedX[i] + 32 && y >= bindedY[i] && y <= bindedY[i] + 32) {
				if (binded[i].count > 0) {
					int pickCount = binded[i].count;
					if (pickCount > 1 && half) {
						pickCount = pickCount / 2;
					}
					System.out.println("pick binded");
					pickedId = i;
					picked = binded[pickedId].get(pickCount);
				}
			}
		}
	}

	public void placePicked() {
		if (null == picked) {
			return;
		}
		int x = MouseJail.instance.x;
		int y = MouseJail.instance.y;
		for (int i = 0; i < TOTAL_NUM; i++) {
			if (x >= bindedX[i] && x <= bindedX[i] + 32 && y >= bindedY[i] && y <= bindedY[i] + 32) {
				if (picked.type.getClass().getName().equals(binded[i].type.getClass().getName())
						|| binded[i].count == 0) {
					// same type or empty slot
					binded[i].putStack(picked);
					if (picked.count == 0) {
						picked = null;
					} else {
						binded[pickedId] = picked; // place picked to hovered
						picked = null;
					}
				} else {
					// different types
					// swap
					binded[pickedId].putStack(picked);
					picked = binded[pickedId];
					binded[pickedId] = binded[i];
					binded[i] = picked;
					picked = null;
				}
			}
		}
	}

	public void putPickedBack() {
		if (null != picked) {
			binded[pickedId].putStack(picked); // put back
			picked = null;
		}
	}

	public Inventory() {
		for (int i = 0; i < TOTAL_NUM; i++) {
			binded[i] = new BlockStack(new EmptyBlockType(), 0);
		}
		binded[0].type = new WheatBlockType();
		binded[0].count = 60;
		binded[1].type = new BrecciaStoneBlockType();
		binded[1].count = 60;
	}

	public void openGUI() {
		isGUIOpened = true;
		MouseJail.enable();
		MouseJail.setActive(false);
		MouseJail.disable();
	}

	public void buildMeshes() {
		bindedGUI = ImmModeMesh.allocate(2 * 4 + BIND_NUM * 4);
		bindedGUI.enableColor3();
		bindedGUI.enableVertex2();
		int x = 0 + GameFrame.getGLWidth() / 2 - width / 2;
		int y = GameFrame.getGLHeight() - height - padding;
		bindedGUI.glBegin(GL2.GL_QUADS);
		{
			bindedGUI.glColor3f(0.2f, 0.2f, 0.2f);
			bindedGUI.glRectf(x, y, x + width, y + height);
			for (int i = 0; i < BIND_NUM; i++) {
				bindedX[i] = x + padding + spacing * i + 34 * i + 1;
				bindedY[i] = y + padding + 1;
				int fx = x + padding + spacing * i + 34 * i;
				int fy = y + padding;
				// left
				bindedGUI.glColor3f(0.0f, 0.0f, 0.0f);
				bindedGUI.glRectf(fx, fy, fx + spacing / 2, fy + 34);
				// right
				bindedGUI.glColor3f(0.5f, 0.5f, 0.5f);
				bindedGUI.glRectf(fx + 33, fy, fx + 33 + spacing / 2, fy + 34);
				// top
				bindedGUI.glColor3f(0.2f, 0.2f, 0.2f);
				bindedGUI.glRectf(fx, fy, fx + 34, fy + spacing / 2);
				// bottom
				bindedGUI.glColor3f(0.3f, 0.3f, 0.3f);
				bindedGUI.glRectf(fx, fy + 33, fx + 34, fy + 33 + spacing / 2);
				bindedGUI.glColor3f(0.6f, 0.6f, 0.6f);
				bindedGUI.glRectf(fx + 1, fy + 1, fx + 1 + 32, fy + 1 + 32);
			}
			// frame
			bindedGUI.glColor3f(0.2f, 0.2f, 0.2f);
			int fx = x + padding + spacing * selectedId + 34 * selectedId;
			int fy = y + padding;
			bindedGUI.glColor3f(15f / 15f, 6f / 15f, 0f);
			bindedGUI.glRectf(fx, fy, fx + 32 + 2, fy + 32 + 2);
			bindedGUI.glColor3f(0.8f, 0.8f, 0.8f);
			bindedGUI.glRectf(fx + 1, fy + 1, fx + 1 + 32, fy + 1 + 32);
		}
		bindedGUI.glEnd();
	}

	public BlockStack getSelectedBlockStack() {
		return binded[selectedId];
	}

	/**
	 * 
	 */
	public void putBlockStack(BlockStack external) {
		System.out.println("put stack " + external.type.getClass().getName());
		// looking for same type
		int i = 0;
		while (i < TOTAL_NUM && external.count > 0) {
			if (binded[i].type.getClass().getName() == external.type.getClass().getName()) {
				System.out.println("append stack");
				binded[i].putStack(external);
			}
			i++;
		}
		i = 0;
		// looking for empty slot
		while (i < TOTAL_NUM && external.count > 0) {
			if (binded[i].count == 0) { // empty
				System.out.println("replace stack");
				binded[i].putStack(external);
			}
			i++;
		}
	}

	public void closeGUI() {
		isGUIOpened = false;
		MouseJail.enable();
		MouseJail.setActive(true);
		if (null != picked) {
			putPickedBack();
		}
	}

	private int spacing = 2;
	private int padding = spacing / 2 + 1;
	private int width = 34 * BIND_NUM + padding * 2 + spacing * (BIND_NUM - 1);
	private int height = 34 + padding * 2;

	public void renderGUIClosed() {
		// render bottom line
		GL2 gl = GLContext.getCurrentGL().getGL2();

		int x = 0 + GameFrame.getGLWidth() / 2 - width / 2;
		int y = GameFrame.getGLHeight() - height - padding;
		/*
		 * gl.glColor3f(0.8f, 0.8f, 0.8f);
		 * gl.glRecti(x, y, x + width, y + height);
		 * for (int i = 0; i < BIND_NUM; i++) {
		 * bindedX[i] = x + padding + spacing * i + 32 * i + 1;
		 * bindedY[i] = y + padding + 1;
		 * int fx = x + padding + spacing * i + 32 * i;
		 * int fy = y + padding;
		 * // left
		 * gl.glColor3f(0.6f, 0.6f, 0.6f);
		 * gl.glRecti(fx, fy, fx + spacing / 2, fy + 32);
		 * // right
		 * gl.glColor3f(0.5f, 0.5f, 0.5f);
		 * gl.glRecti(fx, fy, fx + spacing / 2, fy + 32);
		 * // top
		 * gl.glColor3f(0.4f, 0.4f, 0.4f);
		 * gl.glRecti(fx, fy, fx + 32, fy + spacing / 2);
		 * // bottom
		 * gl.glColor3f(0.3f, 0.3f, 0.3f);
		 * gl.glRecti(fx, fy, fx + 32, fy + spacing / 2);
		 * }
		 */
		if (null != bindedGUI) {
			bindedGUI.draw();
		}

		gl.glEnable(GL2.GL_TEXTURE_2D);
		for (int i = 0; i < BIND_NUM; i++) {
			BlockStack stack = binded[i];
			if (null != stack && !(stack.type instanceof EmptyBlockType) && stack.count > 0 && !stack.hidden) {
				Texture tex = TextureManager.getSprite(stack.type.getStackTextureFile());
				SpriteRectangle uv = TextureManager.getSpriteOffset(stack.type.getStackTextureFile());
				tex.bind(gl);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
				gl.glColor3f(1, 1, 1);
				int sx = x + padding + spacing * i + 34 * i + 1;
				int sy = y + padding + 1;
				gl.glTexCoord4f(uv.topLeft.x, uv.topLeft.y, uv.bottomRight.x, uv.bottomRight.y);
				gl.glBegin(GL2.GL_QUADS);
				{
					gl.glTexCoord2f(uv.topLeft.x, uv.topLeft.y);
					gl.glVertex2f(sx, sy);
					gl.glTexCoord2f(uv.topLeft.x, uv.bottomRight.y);
					gl.glVertex2f(sx, sy + 32);
					gl.glTexCoord2f(uv.bottomRight.x, uv.bottomRight.y);
					gl.glVertex2f(sx + 32, sy + 32);
					gl.glTexCoord2f(uv.bottomRight.x, uv.topLeft.y);
					gl.glVertex2f(sx + 32, sy);
				}
				gl.glEnd();
				// if (stack.count > 0) {
				gl.glColor4f(1, 1, 1, 1);
				Game.client.getScene().viewport.drawText("" + stack.count, sx, GameFrame.getGLHeight() - sy - 10);
				// }
			}
		}
		if (null != picked) {
			// draw picked
			BlockStack stack = picked;
			if (null != stack && !(stack.type instanceof EmptyBlockType) && stack.count > 0) {
				Texture tex = TextureManager.getSprite(stack.type.getStackTextureFile());
				SpriteRectangle uv = TextureManager.getSpriteOffset(stack.type.getStackTextureFile());
				tex.bind(gl);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
				gl.glColor3f(1, 1, 1);
				int sx = MouseJail.instance.x - 16;
				int sy = MouseJail.instance.y - 16;
				gl.glTexCoord4f(uv.topLeft.x, uv.topLeft.y, uv.bottomRight.x, uv.bottomRight.y);
				gl.glBegin(GL2.GL_QUADS);
				{
					gl.glTexCoord2f(uv.topLeft.x, uv.topLeft.y);
					gl.glVertex2f(sx, sy);
					gl.glTexCoord2f(uv.topLeft.x, uv.bottomRight.y);
					gl.glVertex2f(sx, sy + 32);
					gl.glTexCoord2f(uv.bottomRight.x, uv.bottomRight.y);
					gl.glVertex2f(sx + 32, sy + 32);
					gl.glTexCoord2f(uv.bottomRight.x, uv.topLeft.y);
					gl.glVertex2f(sx + 32, sy);
				}
				gl.glEnd();
				// if (stack.count > 0) {
				gl.glColor4f(1, 1, 1, 1);
				Game.client.getScene().viewport.drawText("" + stack.count, sx, GameFrame.getGLHeight() - sy - 10);
				// }
			}
		}
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	public void renderGUIOpened() {
		GL2 gl = GLContext.getCurrentGL().getGL2();

		int oHeight = 300;
		int x = 0 + GameFrame.getGLWidth() / 2 - width / 2;
		int y = GameFrame.getGLHeight() - oHeight - height - padding - 32;
		gl.glColor3f(0.8f, 0.8f, 0.8f);
		gl.glRecti(x, y, x + width, y + oHeight);
		for (int ni = BIND_NUM; ni < BIND_NUM + BACKPACK_NUM; ni++) {
			int dy = ni / 10;
			int dx = ni - dy * 10;
			bindedX[ni] = x + padding + spacing * dx + 32 * dx + 1;
			bindedY[ni] = y + padding + spacing * dy + 32 * dy + 1;
			int fx = x + padding + spacing * dx + 32 * dx;
			int fy = y + padding + spacing * dy + 32 * dy;
			// left
			gl.glColor3f(0.6f, 0.6f, 0.6f);
			gl.glRecti(fx, fy, fx + spacing / 2, fy + 32);
			// right
			gl.glColor3f(0.5f, 0.5f, 0.5f);
			gl.glRecti(fx, fy, fx + spacing / 2, fy + 32);
			// top
			gl.glColor3f(0.4f, 0.4f, 0.4f);
			gl.glRecti(fx, fy, fx + 32, fy + spacing / 2);
			// bottom
			gl.glColor3f(0.3f, 0.3f, 0.3f);
			gl.glRecti(fx, fy, fx + 32, fy + spacing / 2);
		}
		gl.glColor3f(0.2f, 0.2f, 0.2f);
		int fx = x + padding + spacing * selectedId + 32 * selectedId;
		int fy = y + padding;
		gl.glRecti(fx, fy, fx + 32 + 2, fy + 32 + 2);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		for (int ni = BIND_NUM; ni < BIND_NUM + BACKPACK_NUM; ni++) {
			int dy = ni / 10;
			int dx = ni - dy * 10;
			bindedX[ni] = x + padding + spacing * dx + 32 * dx + 1;
			bindedY[ni] = y + padding + spacing * dy + 32 * dy + 1;
			int sx = x + padding + spacing * dx + 32 * dx + 1;
			int sy = y + padding + spacing * dy + 32 * dy + 1;
			BlockStack stack = binded[ni];
			if (null != stack && !(stack.type instanceof EmptyBlockType) && stack.count > 0 && !stack.hidden) {
				Texture tex = TextureManager.getSprite(stack.type.getStackTextureFile());
				SpriteRectangle uv = TextureManager.getSpriteOffset(stack.type.getStackTextureFile());
				tex.bind(gl);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
				gl.glColor3f(1, 1, 1);
				gl.glTexCoord4f(uv.topLeft.x, uv.topLeft.y, uv.bottomRight.x, uv.bottomRight.y);
				gl.glBegin(GL2.GL_QUADS);
				{
					gl.glTexCoord2f(uv.topLeft.x, uv.topLeft.y);
					gl.glVertex2f(sx, sy);
					gl.glTexCoord2f(uv.topLeft.x, uv.bottomRight.y);
					gl.glVertex2f(sx, sy + 32);
					gl.glTexCoord2f(uv.bottomRight.x, uv.bottomRight.y);
					gl.glVertex2f(sx + 32, sy + 32);
					gl.glTexCoord2f(uv.bottomRight.x, uv.topLeft.y);
					gl.glVertex2f(sx + 32, sy);
				}
				gl.glEnd();
				// if (stack.count > 0) {
				gl.glColor4f(1, 1, 1, 1);
				Game.client.getScene().viewport.drawText("" + stack.count, sx, GameFrame.getGLHeight() - sy - 10);
				// }
			}
		}
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	public void renderGUI() {
		if (isGUIOpened) {
			renderGUIOpened();
		} else {
			if (null != picked) {
				putPickedBack();
			}
		}
		renderGUIClosed();
	}

	public void select(int i) {
		selectedId = i;
		selected = binded[i];
		buildMeshes();
	}

	public void toggleGUI() {
		if (isGUIOpened) {
			closeGUI();
		} else {
			openGUI();
		}
		buildMeshes();
	}

	public boolean isGUIOpened() {
		return isGUIOpened;
	}

	public void click(boolean half) {
		if (null == picked) {
			pickHover(half);
		} else {
			placePicked();
		}
		buildMeshes();
	}
}
