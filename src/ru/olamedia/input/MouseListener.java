package ru.olamedia.input;

import com.jogamp.newt.event.MouseEvent;

public interface MouseListener {
	public void onMouseMove(float dx, float dy);
	public void onMouseClick(MouseEvent e);
}
