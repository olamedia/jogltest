package ru.olamedia.game;

import java.nio.FloatBuffer;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLUniformData;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.network.discovery.DiscoveryThread;
import ru.olamedia.olacraft.render.jogl.DefaultRenderer;
import ru.olamedia.olacraft.render.jogl.IRenderer;
import ru.olamedia.olacraft.world.chunk.ChunkMeshBulder;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.tasks.TaskManager;

import com.jogamp.opengl.JoglVersion;
import com.jogamp.opengl.util.PMVMatrix;

public class GameManager implements GLEventListener {
	public static GameManager instance;
	private GameFrame frame;
	private ClientGame clientGame;
	private ServerGame serverGame;
	private IRenderer renderer;
	private MainMenu menu;

	public GameManager() {
		instance = this;
		tests();
	}

	private void tests() {
		BlockLocation b;
		b = new BlockLocation(0, 0, 0);
		assert b.getChunkLocation().x == 0;
		assert b.getChunkLocation().getBlockLocation().x == 0;
		b = new BlockLocation(14, 0, 0);
		assert b.getChunkLocation().x == 0;
		assert b.getChunkLocation().getBlockLocation().x == 0;
		b = new BlockLocation(15, 0, 0);
		assert b.getChunkLocation().x == 0;
		assert b.getChunkLocation().getBlockLocation().x == 0;
		b = new BlockLocation(16, 0, 0);
		assert b.getChunkLocation().x == 1;
		assert b.getChunkLocation().getBlockLocation().x == 0;
		b = new BlockLocation(-1, 0, 0);
		assert b.getChunkLocation().x == -1;
		assert b.getChunkLocation().getBlockLocation().x == -1;
		b = new BlockLocation(-14, 0, 0);
		assert b.getChunkLocation().x == -1;
		b = new BlockLocation(-15, 0, 0);
		assert b.getChunkLocation().x == -1;
		assert b.getChunkLocation().getBlockLocation().x == -1;
		b = new BlockLocation(-16, 0, 0);
		assert b.getChunkLocation().x == -1;
		assert b.getChunkLocation().getBlockLocation().x == -1;
		b = new BlockLocation(-17, 0, 0);
		assert b.getChunkLocation().x == -2;
		assert b.getChunkLocation().getBlockLocation().x == -17;
	}

	private void createServerGame() {
		if (null == serverGame) {
			serverGame = new ServerGame(this);
		}
	}

	private void createClientGame() {
		if (null == clientGame) {
			clientGame = new ClientGame(this);
		}
	}

	public void startServerGame() {
		createServerGame();
		serverGame.start();
	}

	public void startClientGame() {
		createClientGame();
		clientGame.start();
	}

	public void resumeClientGame() {
		createClientGame();
		clientGame.resume();
	}

	public void finishClientGame() {
		createClientGame();
		clientGame.finish();
	}

	public void resumeServerGame() {
		createServerGame();
		serverGame.resume();
	}

	public void finishServerGame() {
		createServerGame();
		clientGame.finish();
		serverGame.dispose();
	}

	private void init() {
		this.frame = new GameFrame();
		menu = new MainMenu();
		this.renderer = new DefaultRenderer();
		GameFrame.getFrame().getContentPane().add(menu);
		menu.setVisible(true);
		GameFrame.getFrame().validate();

	}
	private PMVMatrix m;
	private void glTest(GLAutoDrawable drawable) {
		// m = new PMVMatrix(true);
		// m.glGetMviMatrixf();
		// m.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		// m.glLoadIdentity();
		// m.gluPerspective(30, 1, 1, 100);
		// m.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		// m.glLoadIdentity();
		// m.glTranslatef(5, 6, 7);
		// m.glRotatef(50, 0, 1, 0);
		// m.glRotatef(30, 1, 0, 0);
		// m.setDirty();
		// m.update();
		//
		// GLUniformData pmvMatrixUniform = new GLUniformData("mgl_PMVMatrix",
		// 4, 4, m.glGetPMvMatrixf());
		//
		// FloatBuffer pmv = m.glGetMviMatrixf();
		// pmv = m.glGetPMatrixf();
		// for (int i = 0; i < 16; i++) {
		// System.out.println(pmv.get(i) + " == ");
		// }
		// GL2 gl = drawable.getGL().getGL2();
		// gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		// gl.glLoadIdentity();
		// GLU glu = new GLU();
		// glu.gluPerspective(30, 1, 1, 100);
		// gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		// gl.glLoadIdentity();
		// gl.glTranslatef(5, 6, 7);
		// gl.glRotatef(50, 0, 1, 0);
		// gl.glRotatef(30, 1, 0, 0);
		//
		// FloatBuffer glmv = FloatBuffer.allocate(16);
		// gl.glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, glmv);
		// for (int i = 0; i < 16; i++) {
		// System.out.println(pmv.get(i) + " == " + glmv.get(i));
		// }
	//	System.exit(0);
	}

	public void start() {
		init();
		while (!QuitAdapter.shouldQuit) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void dispose() {
		TaskManager.stopAll();
		if (null != Game.server) {
			Game.server.dispose();
		}
		if (null != Game.client) {
			Game.client.dispose();
		}
		if (null != GameFrame.animator) {
			if (GameFrame.animator.isStarted()) {
				GameFrame.animator.stop();
			}
		}
		frame.dispose();
		if (ChunkMeshBulder.instance.isAlive()) {
			ChunkMeshBulder.instance.interrupt();
		}
		// Get all threads
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for (Thread t : threadSet) {
			if (t instanceof DiscoveryThread) {
				t.interrupt();
			}
			if (t instanceof ChunkMeshBulder) {
				t.interrupt();
			}
		}
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// GLContext.getContext().getGL()
		GL2ES2 gl = drawable.getGL().getGL2ES2();
		// drawable.setGL(new DebugGL2ES2(gl));
		System.err.println(JoglVersion.getGLInfo(drawable.getGL(), null));
		System.err.println(Thread.currentThread() + " Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
		System.err.println(Thread.currentThread() + " INIT GL IS: " + gl.getClass().getName());
		System.err.println(Thread.currentThread() + " GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
		System.err.println(Thread.currentThread() + " GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
		System.err.println(Thread.currentThread() + " GL_VERSION: " + gl.glGetString(GL.GL_VERSION));
		System.err.println(Thread.currentThread() + " GL Profile: " + gl.getGLProfile());
		System.err.println(Thread.currentThread() + " GL:" + gl);
		System.err.println(Thread.currentThread() + " GL_VERSION=" + gl.glGetString(GL.GL_VERSION));
		renderer.init(drawable);
		glTest(drawable);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2ES2 gl = drawable.getGL().getGL2ES2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		// gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		renderer.render(drawable);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL gl = drawable.getGL().getGL2ES2();
		gl.glViewport(0, 0, width, height);
	}

	public void showMainMenu() {
		menu.setVisible(true);
	}

	public void hideMainMenu() {
		menu.setVisible(false);
	}

}
