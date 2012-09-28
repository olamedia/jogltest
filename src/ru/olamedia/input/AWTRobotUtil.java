package ru.olamedia.input;

/**
 * Copyright 2010 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 * 
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 * 
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */

import java.lang.reflect.InvocationTargetException;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Point;
import java.awt.Robot;

public class AWTRobotUtil {

	private static final int ROBOT_DELAY = 100; // ms

	public static Point getCenterLocation(Object obj, boolean onTitleBarIfWindow) throws InterruptedException,
			InvocationTargetException {
		Component comp = null;
		com.jogamp.newt.Window win = null;

		if (obj instanceof com.jogamp.newt.Window) {
			win = (com.jogamp.newt.Window) obj;
		} else if (obj instanceof Component) {
			comp = (Component) obj;
		} else {
			throw new RuntimeException("Neither AWT nor NEWT: " + obj);
		}

		int x0, y0;
		if (null != comp) {
			java.awt.Point p0 = comp.getLocationOnScreen();
			java.awt.Rectangle r0 = comp.getBounds();
			if (onTitleBarIfWindow && comp instanceof java.awt.Window) {
				java.awt.Window window = (java.awt.Window) comp;
				java.awt.Insets insets = window.getInsets();
				y0 = (int) (p0.getY() + insets.top / 2.0 + .5);
			} else {
				y0 = (int) (p0.getY() + r0.getHeight() / 2.0 + .5);
			}
			x0 = (int) (p0.getX() + r0.getWidth() / 2.0 + .5);
		} else {
			javax.media.nativewindow.util.Point p0 = win.getLocationOnScreen(null);
			if (onTitleBarIfWindow) {
				javax.media.nativewindow.util.InsetsImmutable insets = win.getInsets();
				p0.translate(win.getWidth() / 2, insets.getTopHeight() / 2);
			} else {
				javax.media.nativewindow.util.InsetsImmutable insets = win.getInsets();
				p0.translate(win.getWidth() / 2, (win.getHeight() - insets.getTopHeight()) / 2);
			}
			x0 = p0.getX();
			y0 = p0.getY();
		}

		return new Point(x0, y0);
	}

	/**
	 * centerMouse
	 */
	public static Point centerMouse(Robot robot, Object obj, boolean onTitleBarIfWindow) throws AWTException,
			InterruptedException, InvocationTargetException {

		Point p0 = getCenterLocation(obj, onTitleBarIfWindow);
		// System.err.println("centerMouse: robot pos: " + p0 +
		// ", onTitleBarIfWindow: " + onTitleBarIfWindow);

		robot.mouseMove((int) p0.getX(), (int) p0.getY());
		// robot.delay(ROBOT_DELAY);
		return p0;
	}

}
