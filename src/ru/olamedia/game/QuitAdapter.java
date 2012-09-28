package ru.olamedia.game;

import com.jogamp.newt.event.*;

public class QuitAdapter extends WindowAdapter implements WindowListener, KeyListener, java.awt.event.WindowListener {
	public static boolean shouldQuit = false;

	public boolean shouldQuit() {
		return shouldQuit;
	}

	public void windowDestroyNotify(WindowEvent e) {
		System.err.println("QUIT Window " + Thread.currentThread());
		shouldQuit = true;
	}

	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == 'q') {
			System.err.println("QUIT Key " + Thread.currentThread());
			shouldQuit = true;
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void windowActivated(java.awt.event.WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(java.awt.event.WindowEvent arg0) {
		// TODO Auto-generated method stub
		shouldQuit = true;
	}

	@Override
	public void windowClosing(java.awt.event.WindowEvent arg0) {
		System.err.println("QUIT Window " + Thread.currentThread());
		shouldQuit = true;
	}

	@Override
	public void windowDeactivated(java.awt.event.WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(java.awt.event.WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(java.awt.event.WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(java.awt.event.WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
