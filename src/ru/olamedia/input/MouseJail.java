package ru.olamedia.input;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;

import ru.olamedia.game.GameFrame;

public class MouseJail extends MouseAdapter {
	public static MouseJail instance = new MouseJail();

	public MouseJail() {
	}

	private static boolean isActive = false;

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
		System.out.println("Mouse jail " + (isActive ? "active" : "not active"));
		MouseJail.isActive = isActive;
		GameFrame.confinePointer(isActive);
		GameFrame.setPointerVisible(!isActive);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.isAltDown()) {
			setActive(false);
		} else {
			setActive(true);
		}
		for (ru.olamedia.input.MouseListener l : listeners) {
			l.onMouseClick();
		}
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
			//moveToCenter();
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
		if (isActive) {
			int cx = GameFrame.getWidth() / 2;
			int cy = GameFrame.getHeight() / 2;
			float dx = e.getX() - cx;
			float dy = e.getY() - cy;
			dx *= sensitivity / 10;
			dy *= sensitivity / 10;
			// System.out.println("Mouse moved " + " dx:" + dx + " dy:" + dy
			// + " x:" + e.getX() + " y:" + e.getY());
			for (ru.olamedia.input.MouseListener l : listeners) {
				l.onMouseMove(dx, dy);
			}
			GameFrame.getWindow().warpPointer(cx, cy);
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
