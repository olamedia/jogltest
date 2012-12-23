package ru.olamedia.input;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;

import ru.olamedia.game.GameFrame;

public class MouseJail extends MouseAdapter {
	public static MouseJail instance = new MouseJail();

	public int x;
	public int y;

	public MouseJail() {
	}

	private static boolean isActive = false;
	private static boolean isEnabled = true; // disabled if GUI is opened

	public static void disable() {
		isEnabled = false;
	}

	public static void enable() {
		isEnabled = true;
	}

	/**
	 * @return the isActive
	 */
	public static boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive
	 *            the isActive to set
	 */
	public static void setActive(boolean isActive) {
		if (isEnabled) {
			System.out.println("Mouse jail " + (isActive ? "active" : "not active"));
			MouseJail.isActive = isActive;
			GameFrame.confinePointer(isActive);
			GameFrame.setPointerVisible(!isActive);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// if (isEnabled) {
		if (e.isAltDown()) {
			setActive(false);
		} else {
			setActive(true);
		}
		for (ru.olamedia.input.MouseListener l : listeners) {
			l.onMouseClick(e);
		}
		// }
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		System.out.println("Entered");

	}

	@Override
	public void mouseExited(MouseEvent e) {
		System.out.println("Exited");
		isActive = false;
		if (isActive) {
			// moveToCenter();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		onMove(e);
	}

	private float sensitivity = 2f;

	private void onMove(MouseEvent e) {
		x = e.getX();
		y = e.getY();
		if (isActive) {
			final float dx = (e.getX() - GameFrame.getGLWidth() / 2) * sensitivity / 10;
			final float dy = (e.getY() - GameFrame.getGLHeight() / 2) * sensitivity / 10;
			// System.out.println("Mouse moved " + " dx:" + dx + " dy:" + dy
			// + " x:" + e.getX() + " y:" + e.getY());
			for (ru.olamedia.input.MouseListener l : listeners) {
				l.onMouseMove(dx, dy);
			}
			GameFrame.getWindow().warpPointer(GameFrame.getGLWidth() / 2, GameFrame.getGLHeight() / 2);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		onMove(e);
	}

	private static List<ru.olamedia.input.MouseListener> listeners = new ArrayList<ru.olamedia.input.MouseListener>();

	public static void attach(ru.olamedia.input.MouseListener l) {
		listeners.add(l);
	}

}
