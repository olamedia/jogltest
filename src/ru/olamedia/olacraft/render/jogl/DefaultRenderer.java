package ru.olamedia.olacraft.render.jogl;

import com.jogamp.newt.event.KeyEvent;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import ru.olamedia.input.KeyListener;
import ru.olamedia.input.Keyboard;
import ru.olamedia.input.MouseJail;
import ru.olamedia.input.MouseListener;
import ru.olamedia.olacraft.game.Game;

public class DefaultRenderer implements IRenderer, KeyListener, MouseListener {
	private boolean isHUDEnabled = true;
	GLU glu = new GLU();
	// private static Color BLACK_TRANSPARENT = new Color(0, 0, 0, 0);
	long lastHUD = System.nanoTime();

	public DefaultRenderer() {
		Keyboard.attach(this);
		MouseJail.attach(this);
	}

	@Override
	public void render(GLAutoDrawable drawable) {
		Game.client.getScene().tick();
		Game.client.getScene().render(drawable);
	}

	@Override
	public void onKeyPressed(String name, KeyEvent e) {

	}

	@Override
	public void onKeyReleased(String name, KeyEvent e) {
		// System.out.println(name);
		if (name == "toggleHUD") {
			isHUDEnabled = !isHUDEnabled;
		}
		if (name == "captureMouse") {
			MouseJail.setActive(true);
		}
		if (name == "releaseMouse") {
			MouseJail.setActive(false);
		}
		if (name == "toggleFrustum") {
			Game.instance.camera.isFrustumVisible = !Game.instance.camera.isFrustumVisible;
		}
		if (name == "toggleRenderDistance") {
			int renderDistance = Game.client.getScene().getRenderDistance();
			renderDistance *= 2;
			if (renderDistance > 256) {
				renderDistance = 32;
			}
			Game.client.getScene().setRenderDistance(renderDistance);
		}
	}

	@Override
	public void onMouseMove(float dx, float dy) {
		Game.instance.camera.mouseMoved(dx, dy);
	}

	@Override
	public void onMouseClick() {
		Game.instance.player.onMouseClick();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		
	}
}
