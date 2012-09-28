package ru.olamedia.game;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;

import ru.olamedia.asset.AssetManager;
import ru.olamedia.asset.AssetNotFoundException;
import ru.olamedia.input.Keyboard;
import ru.olamedia.input.MouseJail;
import ru.olamedia.olacraft.OlaCraft;

import jogamp.newt.awt.NewtFactoryAWT;

import com.jogamp.newt.Display;
import com.jogamp.newt.Screen;
import com.jogamp.newt.awt.NewtCanvasAWT;
import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

public class GameFrame {
	// java.awt.SystemTray
	// http://www.oracle.com/technetwork/articles/javase/systemtray-139788.html

	public static GameFrame instance;

	Display display;
	Screen screen;
	int screenId;
	GLProfile glProfile;
	GLCapabilities caps;
	protected Frame awtFrame;
	protected static GLWindow glWindow;
	protected static JFrame jFrame;
	int width = 854;
	int height = 480;
	public static Animator animator;
	NewtCanvasAWT newtCanvasAWT;
	private boolean glMode = false;

	public void initGL() {
		if (null == newtCanvasAWT) {
			glProfile = GLProfile.get(GLProfile.GL2);// Default();
			// ES2
			caps = new GLCapabilities(glProfile);
			caps.setHardwareAccelerated(true);
			caps.setDoubleBuffered(true);
			caps.setBackgroundOpaque(false);

			display = NewtFactoryAWT.createDisplay(null);
			screen = NewtFactoryAWT.createScreen(display, screenId);
			glWindow = GLWindow.create(screen, caps);// GLWindow.create(screen,
			// caps);
			newtCanvasAWT = new NewtCanvasAWT(glWindow);
			glWindow.setUndecorated(false);
			glWindow.setPointerVisible(true);
			glWindow.confinePointer(false);
			glWindow.addWindowListener(new QuitAdapter());
			animator = new Animator(glWindow);
			animator.setRunAsFastAsPossible(true); // By default there is a
													// brief
													// pause in the animation
													// loop
			animator.start();
			glWindow.addMouseListener(MouseJail.instance);
			glWindow.addKeyListener(Keyboard.instance);
			glWindow.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					super.keyReleased(e);
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						glWindow.confinePointer(false);
						glWindow.setPointerVisible(true);
					}
				}
			});
			// animator.setUpdateFPSFrames(100, System.err);
			jFrame.add(newtCanvasAWT);
			glWindow.addGLEventListener(GameManager.instance);
		}
	}

	public void setGLMode() {
		if (!glMode) {
			initGL();
			glMode = true;
			newtCanvasAWT.setVisible(true);
		}
	}

	public void setUIMode() {
		if (glMode) {
			glMode = false;
			newtCanvasAWT.setVisible(false);
		}
	}

	public static int getX() {
		return jFrame.getX();
	}

	public static int getY() {
		return jFrame.getY();
	}

	public static int getWidth() {
		if (null == glWindow) {
			return jFrame.getWidth();
		}
		return glWindow.getWidth();
	}

	public static int getHeight() {
		if (null == glWindow) {
			return jFrame.getHeight();
		}
		return glWindow.getHeight();
	}

	public GameFrame() {
		instance = this;
		jFrame = new JFrame();
		jFrame.setMinimumSize(new Dimension(200, 200));
		jFrame.setSize(width, height);
		jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		jFrame.setTitle("OlaCraft " + OlaCraft.version);
		setIcons();
		// glWindow.setLocation(100, 100);
		jFrame.addWindowListener(new QuitAdapter());
		jFrame.setVisible(true);
	}

	private void setIcons() {
		List<Image> icons = new ArrayList<Image>();
		try {
			icons.add(getImage("icon16x16.png"));
			icons.add(getImage("icon32x32.png"));
			icons.add(getImage("icon64x64.png"));
			icons.add(getImage("icon128x128.png"));
			icons.add(getImage("icon256x256.png"));
		} catch (AssetNotFoundException e1) {
			e1.printStackTrace();
		}
		// if (!icons.isEmpty()) {
		// awtFrame.setIconImage(getImage("icon32x32.png"));
		jFrame.setIconImages(icons);
		// }
	}

	private Image getImage(String filename) throws AssetNotFoundException {
		String iconFile = AssetManager.getAsset("ru/olamedia/game/" + filename).getFile();
		return Toolkit.getDefaultToolkit().createImage(iconFile);
	}

	public Animator getAnimator() {
		return animator;
	}

	public static void confinePointer(boolean confine) {
		if (glWindow != null) {
			glWindow.confinePointer(confine);
		}
	}

	public static void setPointerVisible(boolean visible) {
		if (glWindow != null) {
			glWindow.setPointerVisible(visible);
		}
	}

	public static GLWindow getWindow() {
		return glWindow;
	}

	public static JFrame getFrame() {
		return jFrame;
	}

	public void dispose() {
		// glWindow.destroy();
		// newtCanvasAWT.destroy();
		jFrame.dispose();
		// System.err.close();
	}
}
