package ru.olamedia.olacraft.scene;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Random;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import org.ode4j.ode.DBody;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.util.PMVMatrix;

import ru.olamedia.game.GameFrame;
import ru.olamedia.game.GameLogicThread;
import ru.olamedia.game.GameTime;
import ru.olamedia.geom.ImmModeMesh;
import ru.olamedia.input.KeyListener;
import ru.olamedia.input.Keyboard;
import ru.olamedia.liveEntity.LiveEntity;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.modelAnimator.Bone;
import ru.olamedia.olacraft.physics.GamePhysicsWorld;
import ru.olamedia.olacraft.render.jogl.ChunkRangeRenderer;
import ru.olamedia.olacraft.render.jogl.InventoryRenderer;
import ru.olamedia.olacraft.render.jogl.joglViewport;
import ru.olamedia.olacraft.weapon.Bullet;
import ru.olamedia.olacraft.weapon.BulletScene;
import ru.olamedia.olacraft.world.block.Block;
import ru.olamedia.olacraft.world.blockRenderer.ChunkRenderer;
import ru.olamedia.olacraft.world.blockTypes.BlockType;
import ru.olamedia.olacraft.world.blockTypes.GrassBlockType;
import ru.olamedia.olacraft.world.chunk.BlockSlice;
import ru.olamedia.olacraft.world.chunk.ChunkUnavailableException;
import ru.olamedia.olacraft.world.provider.WorldProvider;
import ru.olamedia.player.Player;
import ru.olamedia.texture.TextureManager;

public class GameScene implements KeyListener {
	private boolean isHUDEnabled = false;
	private boolean thirdPersonMode = true;
	public boolean boneMode = false;
	private boolean renderFrustum = false;
	private HashMap<Integer, LiveEntity> liveEntities = new HashMap<Integer, LiveEntity>();
	WorldProvider provider;
	public joglViewport viewport;
	private BulletScene bullets = new BulletScene();
	private GamePhysicsWorld physics = new GamePhysicsWorld();

	public boolean isInitialized = false;
	BlockSlice viewSlice;
	public GameTime dayTime = new GameTime();

	private ImmModeMesh crosshair;

	public Bone selectedBone = null;
	public int bonesCount = 0;

	private BlockType blockType = new GrassBlockType();

	public GameScene(WorldProvider provider) {
		this.provider = provider;
		pickSlice = new BlockSlice(provider, 10, 10, 10);
		Keyboard.attach(this);
		Keyboard.setName("3_left", KeyEvent.VK_NUMPAD4);
		Keyboard.setName("3_right", KeyEvent.VK_NUMPAD6);
		Keyboard.setName("3_up", KeyEvent.VK_NUMPAD8);
		Keyboard.setName("3_down", KeyEvent.VK_NUMPAD2);
		Keyboard.setName("3_in", KeyEvent.VK_NUMPAD7);
		Keyboard.setName("3_out", KeyEvent.VK_NUMPAD1);
		Keyboard.setName("3_mode", KeyEvent.VK_F6);
		Keyboard.setName("hud", KeyEvent.VK_F9);
		Keyboard.setName("bone_mode", KeyEvent.VK_B);
		Keyboard.setName("next", KeyEvent.VK_N);
		Keyboard.setName("rotate_cw", KeyEvent.VK_RIGHT);
		Keyboard.setName("rotate_ccw", KeyEvent.VK_LEFT);
	}

	public void addBullet(Bullet b) {
		bullets.add(b);
		DBody body = physics.createBody();
		body.setPosition(b.location.x, b.location.y, b.location.z);
		body.setLinearVel(b.velocity.x, b.velocity.y, b.velocity.z);
		/*
		 * DMass mass = OdeHelper.createMass();
		 * mass.setMass(10);
		 * mass.setI(OdeHelper.c);
		 * body.setMass(mass);
		 */
		b.body = body;
	}

	public int getBulletsCount() {
		return bullets.getCount();
	}

	public void buildCrosshair() {
		final int width = GameFrame.getGLWidth();
		final int height = GameFrame.getGLHeight();
		final int w = 1;
		final int l = 5;
		crosshair = ImmModeMesh.allocate(8);// 2 rectangles
		// crosshair.setServer(true);
		// crosshair.setGLSL(true);
		crosshair.enableColor4();
		crosshair.enableVertex2();
		crosshair.setColor(0f, 1f, 1f, 0.7f);
		crosshair.beginQuads();
		{
			crosshair.glRectf(width / 2 - w, height / 2 - l * 2, width / 2 + w, height / 2 + l * 2); // vertical
			crosshair.glRectf(width / 2 - l * 2, height / 2 - w, width / 2 - 1, height / 2 + w); // horizontal
			crosshair.glRectf(width / 2 + 1, height / 2 - w, width / 2 + l * 2, height / 2 + w); // horizontal
		}
		crosshair.end();
	}

	public void init(GLAutoDrawable drawable) {
		if (isInitialized) {
			return;
		}
		final GL2ES2 gl = GLContext.getCurrentGL().getGL2ES2();
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);// _MIPMAP_NEAREST
		registerTextures();
		gl.glActiveTexture(GL2.GL_TEXTURE1);
		gl.glBindTexture(blockType.getTopTexture().getTarget(), blockType.getTopTexture().getTextureObject(gl));
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		isInitialized = true;
		dayTime.init();
		viewport = new joglViewport(drawable);
		buildCrosshair();
		GameLogicThread.instance.start();
	}

	private void registerTextures() {
		provider.registerTextures();
		TextureManager.writeSprite("sprite.png");
		TextureManager.finishSprite();
	}

	public ChunkRangeRenderer blockRenderer = new ChunkRangeRenderer(viewSlice);
	GLU glu = new GLU();

	public void registerLiveEntity(LiveEntity entity) {
		// liveEntityIncrement++;
		// entity.setId(liveEntityIncrement);
		liveEntities.put(entity.getConnectionId(), entity);
	}

	private InventoryRenderer inventoryRenderer;

	public Player player;

	public void registerPlayer(LiveEntity player) {
		inventoryRenderer = new InventoryRenderer(player.getInventory());
		this.player = (Player) player;
		// this.player.getInventory().buildMeshes();
	}

	public LiveEntity getLiveEntity(int connectionId) {
		if (liveEntities.containsKey(connectionId)) {
			return liveEntities.get(connectionId);
		}
		return null;
	}

	public HashMap<Integer, LiveEntity> getLiveEntities() {
		return liveEntities;
	}

	public Block nearestBlock = null;
	public Block nearestPutBlock = null;
	public BlockSlice pickSlice;
	private Random rand = new Random();

	public void tick() {
		if (rand.nextFloat() > 0.95f) {
			final GL2ES2 gl = GLContext.getCurrentGL().getGL2ES2();
			// ChunkRenderer.getShader().enable();
			dayTime.sunColor.put(0, 1);
			dayTime.sunColor.put(1, 1);
			dayTime.sunColor.put(2, 1);
			// ChunkRenderer.getShader().getState().uniform(gl,
			// ChunkRenderer.sunColor);
			// ChunkRenderer.getShader().disable();
			final float[] clearColor = dayTime.getClearColor();
			// gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], 1);
			gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
		}
	}

	float dLook = 2;
	float dUp = 0;
	float dRight = 0;

	public void render(GLAutoDrawable drawable) {
		if (null == player || !Game.instance.isRunning()) {
			// not running, just clear screen
			final GL2 gl = drawable.getGL().getGL2();
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
			gl.glClearColor(49f / 255f, 49f / 255f, 49f / 255f, 1);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
		init(drawable);
		// final float[] clearColor = time.getClearColor();
		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		// gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
		// gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], 1);
		// gl.glClearColor(49f / 255f, 119f / 255f, 243f / 255f, 1);
		gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
		// GOING 3D
		if (player.camera.isDirty()) {
			player.camera.offset.x = 0;
			player.camera.offset.y = 0;
			player.camera.offset.z = 0;
			player.camera.pack();
			if (thirdPersonMode) {
				player.camera.offset.x = player.camera.getLook().x * dLook + player.camera.getUp().x * dUp
						+ player.camera.getRight().x * dRight;
				player.camera.offset.y = player.camera.getLook().y * dLook + player.camera.getUp().y * dUp
						+ player.camera.getRight().y * dRight;
				player.camera.offset.z = player.camera.getLook().z * dLook + player.camera.getUp().z * dUp
						+ player.camera.getRight().z * dRight;
				player.camera.pack();
				player.camera.updateFrustum();
				blockRenderer.updateFrustumCulling();
			}
			ChunkRenderer.getShader().enable();
			ChunkRenderer.getShader().getState().uniform(gl, ChunkRenderer.pmvMatrixUniform);
			ChunkRenderer.getShader().disable();
		}
		player.camera.setUp();
		// gl.glPushMatrix();
		// gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);

		// RENDER SUN
		/*
		 * gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
		 * gl.glEnable(GL2.GL_BLEND); // Enable Blending
		 * gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE); // Set Blending Mode To
		 * // Mix Based On SRC
		 * // Alpha
		 * GLUquadric sun = glu.gluNewQuadric();
		 * glu.gluQuadricDrawStyle(sun, GLU.GLU_FILL);
		 * glu.gluQuadricNormals(sun, GLU.GLU_SMOOTH);
		 * gl.glPushMatrix();
		 * gl.glTranslatef(time.sun.getX() + player.getCameraX(),
		 * time.sun.getY(), time.sun.getZ() + player.getCameraZ());
		 * gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		 * gl.glColor4f(1, 1, 1, 0.02f);
		 * gl.glColor4f((float) 251 / 255, (float) 255 / 255, (float) 228 / 255,
		 * 0.02f);
		 * glu.gluSphere(sun, 100f, 10, 10);
		 * glu.gluSphere(sun, 90f, 10, 10);
		 * glu.gluSphere(sun, 80f, 10, 10);
		 * glu.gluSphere(sun, 70f, 10, 10);
		 * glu.gluSphere(sun, 60f, 10, 10);
		 * glu.gluSphere(sun, 50f, 10, 10);
		 * glu.gluSphere(sun, 40f, 10, 10);
		 * glu.gluSphere(sun, 35f, 10, 10);
		 * gl.glColor4f(1, 1, 1, 1f);
		 * glu.gluSphere(sun, 30f, 10, 10);
		 * gl.glPopMatrix();
		 * gl.glPopAttrib();
		 */

		// RENDER BLOCKS
		// gl.glColor4f(0f, 1f, 0, 1);
		gl.glShadeModel(GL2.GL_FLAT);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_CULL_FACE);
		// gl.glColorMask(false, false, false, false);
		// gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);// _MIPMAP_NEAREST
		ChunkRenderer.enableShader();
		// ChunkRenderer.getShader().getState().uniform(gl,
		// ChunkRenderer.sunColor);
		gl.glActiveTexture(GL2.GL_TEXTURE1);
		// gl.glBindTexture(blockType.getTopTexture().getTarget(),
		// blockType.getTopTexture().getTextureObject(gl));
		gl.glDisable(GL2.GL_BLEND);
		blockRenderer.render(ChunkRangeRenderer.OPAQUE_PASS);
		gl.glDisable(GL2.GL_BLEND);
		// gl.glEnable(GL2.GL_BLEND);
		// gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		// gl.glBlendEquationSeparate(GL2.GL_FUNC_ADD, GL2.GL_FUNC_ADD); // copy
		// blockRenderer.render(ChunkRangeRenderer.ALPHA_PASS);
		// gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glDisable(GL2.GL_CULL_FACE);
		gl.glDisable(GL2.GL_DEPTH_TEST);
		gl.glDisable(GL2.GL_BLEND);
		// gl.glDisable(GL2.GL_ALPHA_TEST);
		ChunkRenderer.disableShader();
		gl.glActiveTexture(GL2.GL_TEXTURE0);
		// gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
		// gl.glShadeModel(GL2.GL_FLAT);
		// gl.glCullFace(GL2.GL_BACK);
		// gl.glEnable(GL2.GL_FOG);
		// gl.glFogf(GL2.GL_FOG_MODE, GL2.GL_LINEAR);
		// gl.glFogf(GL2.GL_FOG_MODE, GL2.GL_EXP);
		// gl.glFogf(GL2.GL_FOG_START, player.settings.renderDistance / 2 -
		// player.settings.renderDistance / 10);
		// gl.glFogf(GL2.GL_FOG_END, player.settings.renderDistance / 2);
		// gl.glFogf(GL2.GL_FOG_DENSITY, 0.002f);
		// new float[] { 49f / 255f, 119f / 255f, 243f / 255f }
		// gl.glFogfv(GL2.GL_FOG_COLOR, new float[] { 1, 1, 1, 0.2f }, 0);
		// gl.glPopAttrib();
		// RENDER ANYTHING ELSE
		/*
		 * gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
		 * GLUquadric qobj0 = glu.gluNewQuadric();
		 * glu.gluQuadricDrawStyle(qobj0, GLU.GLU_FILL);
		 * glu.gluQuadricNormals(qobj0, GLU.GLU_SMOOTH);
		 * for (LiveEntity entity : liveEntities.values()) {
		 * gl.glPushMatrix();
		 * gl.glTranslatef(entity.getX(), entity.getCameraY(), entity.getZ());
		 * gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		 * glu.gluSphere(qobj0, 0.03f, 10, 10);
		 * gl.glPopMatrix();
		 * }
		 * gl.glPopAttrib();
		 */
		if (nearestBlock != null) {
			gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
			if (nearestBlock != null) {
				nearestBlock.renderFrame();
			}
			gl.glPopAttrib();
		}
		if (thirdPersonMode && renderFrustum) {
			player.camera.renderFrustum();
		}
		// bullets.render(drawable);
		// gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
		// player.render();
		// gl.glPopAttrib();
		// gl.glPopMatrix();

		// testObject.render();
		gl.glPopAttrib();
		// GOIND 2D
		if (isHUDEnabled) {
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();
			final int width = GameFrame.getGLWidth();
			final int height = GameFrame.getGLHeight();
			glu.gluOrtho2D(0, width, height, 0);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
			// gl.glPushMatrix();
			// renderHUD();

			// MAP++
			// gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
			// gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
			// int msz = 100;
			// gl.glColor4f(0.3f, 0.3f, 0.3f, 0.7f);
			// gl.glRectf(width - msz - 12, 8, width - 8, msz + 12);
			// gl.glColor4f(0.9f, 0.9f, 0.9f, 1);
			// gl.glRectf(width - msz - 10, 10, width - 10, msz + 10);
			// gl.glColor4f(0.0f, 0.0f, 0.0f, 0.9f);

			// MAP--
			// crosshair
			if (null != crosshair) {
				gl.glDisable(GL2.GL_TEXTURE_2D);
				gl.glDisable(GL2.GL_CULL_FACE);
				gl.glDisable(GL2.GL_DEPTH_TEST);
				gl.glDisable(GL2.GL_BLEND);
				gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
				gl.glEnable(GL2.GL_BLEND);
				gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE);
				gl.glBlendEquation(GL2.GL_FUNC_SUBTRACT);
				// ChunkRenderer.enableShader();
				crosshair.draw();
				// ChunkRenderer.disableShader();
				gl.glBlendEquation(GL2.GL_FUNC_ADD);
				gl.glDisable(GL2.GL_BLEND);
			} else {
				System.err.println("no crosshair");
			}
			// gl.glColor4f(1f, 1f, 1f, 0.7f);
			// gl.glRectf(width / 2 - 1, height / 2 - 10, width / 2 + 1, height
			// / 2
			// + 10); // vertical
			// gl.glRectf(width / 2 - 10, height / 2 - 1, width / 2 + 10, height
			// / 2
			// + 1); // horizontal
			// TODO ENABLE
			// inventory
			// player.getInventory().renderGUI();
			// inventoryprivate PMVMatrix matrix;
			// if (null != inventoryRenderer) {
			// // inventoryRenderer.render(drawable);
			// }
			gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
			viewport.beginRendering();
			gl.glEnable(GL2.GL_BLEND);
			// gl.glBlendColor(0, 0, 0, 1);
			gl.glBlendFunc(GL2.GL_ZERO, GL2.GL_ONE_MINUS_SRC_ALPHA);
			//gl.glBlendFuncSeparate(GL2.GL_DST_COLOR, GL2.GL_ONE_MINUS_SRC_ALPHA, GL2.GL_SRC_ALPHA,
			//		GL2.GL_ONE_MINUS_DST_COLOR);
			gl.glBlendEquation(GL2.GL_FUNC_ADD);
			viewport.drawText("⌀ " + player.settings.renderDistance, 10, height - 20);
			viewport.drawText("avg fps: " + (int) Game.fpsTimer.getAvgFps(), 10, height - 35);
			viewport.drawText("fps: " + (int) Game.fpsTimer.getFps(), 10, height - 50);
			final MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
			final MemoryUsage nonheap = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
			viewport.drawText("heap: " + (heap.getUsed() / (1024 * 1024)) + "/" + (heap.getMax() / (1024 * 1024)), 10,
					height - 65);
			viewport.drawText("non heap: " + (nonheap.getUsed() / (1024 * 1024)) + "/"
					+ (nonheap.getMax() / (1024 * 1024)), 10, height - 80);
			// viewport.drawText("b: " + blockRenderer.visibleBlocks, 10, height
			// -
			// 80);
			if (null != blockRenderer.big) {
				viewport.drawText("v: " + blockRenderer.big.getVertexCount(), 10, height - 95);
			}
			viewport.drawText("c: " + blockRenderer.testedChunks, 10, height - 105);
			//
			viewport.drawText("y: " + Game.instance.player.getY(), width - 100 - 10, height - 100 - 20);
			viewport.drawText("y: " + Game.instance.player.getCameraY() + " (cam)", width - 100 - 10, height - 100 - 30);
			viewport.drawText("x: " + Game.instance.player.getX(), width - 100 - 10, height - 100 - 40);
			viewport.drawText("z: " + Game.instance.player.getZ(), width - 100 - 10, height - 100 - 50);
			if (boneMode) {
				viewport.drawText("bones count: " + bonesCount, width - 100 - 10, height - 100 - 80);
			}
			if (boneMode && null != selectedBone) {
				viewport.drawText("bone: " + selectedBone.getName(), width - 100 - 10, height - 100 - 90);
				viewport.drawText("pitch: " + selectedBone.getPitch(), width - 100 - 10, height - 100 - 100);
				viewport.drawText("__yaw: " + selectedBone.getYaw(), width - 100 - 10, height - 100 - 110);
				viewport.drawText("_roll: " + selectedBone.getRoll(), width - 100 - 10, height - 100 - 120);
			}
			viewport.endRendering();
			gl.glDisable(GL2.GL_BLEND);
			gl.glBlendEquation(GL2.GL_FUNC_ADD);
			gl.glBlendColor(0, 0, 0, 0);
			gl.glPopAttrib();

			// viewport.drawText("players: " + liveEntities.size(), width - msz
			// -
			// 10, height - msz - 70);
			// viewport.drawText("bullets: " + getBulletsCount(), width - msz -
			// 10,
			// height - msz - 95);
			// viewport.drawText("y velocity: " + player.velocity.y +
			// " y accel: " +
			// player.acceleration.y + " inJump: "
			// + player.inJump + " onGround: " + player.onGround, width - msz -
			// 350
			// - 10, height - msz - 110);
			// viewport.drawText("rdistance: " + Options.renderDistance, width -
			// msz
			// - 10, height - msz - 155);

			// ChunkSlice cs = viewSlice.getChunkSlice();
			// viewport.drawText("slice x: " + cs.getX() + ".." + (cs.getX() +
			// cs.getWidth() - 1) + " y: " + cs.getY() + ".."
			// + (cs.getY() + cs.getHeight() - 1) + " z: " + cs.getZ() + ".." +
			// (cs.getZ() + cs.getDepth() - 1), width
			// - msz * 2 - 10, height - msz - 170);
			// viewport.drawText("time: " + time.getDateTimeString(), width -
			// msz *
			// 2 - 10, height - msz - 185);
			// if (nearestBlock != null) {
			// viewport.drawText("pick: " + nearestBlock.getX() + "," +
			// nearestBlock.getY() + "," + nearestBlock.getZ()
			// + " d " + nearestBlock.getDistance(Game.instance.camera), width -
			// msz
			// * 2 - 10, height - msz - 200);
			// viewport.drawText(
			// "edge: " + nearestBlock.location.isChunkEdge() + " left " +
			// nearestBlock.location.isChunkLeftEdge(),
			// width - msz * 2 - 10, height - msz - 210);
			// }

			// BlockLocation camloc = player.getCameraBlockLocation();
			// viewport.drawText("cam chunk: " + camloc.getChunkLocation(),
			// width -
			// 200 - msz * 2 - 10, height - msz - 225);
			// Chunk camc =
			// viewSlice.getChunkSlice().getChunk(camloc.getChunkLocation());
			// viewport.drawText("cam chunk: " + camc.isMeshCostructed +
			// " mesh: " +
			// camc.mesh, width - 200 - msz * 2 - 10,
			// height - msz - 235);
			// viewport.drawText(
			// "chunk available: " + camc.isAvailable() + " chunk empty: "
			// + (camc.isAvailable() ? camc.isEmpty() : "unknown"), width - 200
			// -
			// msz * 2 - 10, height - msz
			// - 245);

			// gl.glPopAttrib();
			// gl.glPopMatrix();
			// gl.glFlush();
			disposeMeshes();
			gl.glPopAttrib();
		}
	}

	public void disposeMeshes() {
		// blockRenderer.chunkSlice.disposeMeshes();
	}

	public Player getPlayer() {
		return player;
	}

	public PMVMatrix getPmvMatrix() {
		return Game.instance.camera.pmvMatrix;
	}

	public void reshape(GLAutoDrawable drawable) {
		buildCrosshair();
		if (null != player) {
			// player.getInventory().buildMeshes();
		}
	}

	@Override
	public void onKeyPressed(String name, KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_F6) {
			thirdPersonMode = !thirdPersonMode;
		}
		if (e.getKeyCode() == KeyEvent.VK_B) {
			boneMode = !boneMode;
		}
		final boolean thirdPersonControlsEnabled = thirdPersonMode && !boneMode;
		if (thirdPersonControlsEnabled) {
			final int shiftSpeed = 10;
			if (e.getKeyCode() == KeyEvent.VK_NUMPAD4) {
				dRight += -0.1 * shiftSpeed;
			}
			if (e.getKeyCode() == KeyEvent.VK_NUMPAD6) {
				dRight += 0.1 * shiftSpeed;
			}
			if (e.getKeyCode() == KeyEvent.VK_NUMPAD8) {
				dUp += 0.1 * shiftSpeed;
			}
			if (e.getKeyCode() == KeyEvent.VK_NUMPAD2) {
				dUp += -0.1 * shiftSpeed;
			}
			if (e.getKeyCode() == KeyEvent.VK_NUMPAD1) {
				dLook += 0.1 * shiftSpeed;
			}
			if (e.getKeyCode() == KeyEvent.VK_NUMPAD7) {
				dLook += -0.1 * shiftSpeed;
			}
		}
		if (boneMode && null != selectedBone) {
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				player.rotateCW();
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				player.rotateCCW();
			}
			if (e.getKeyCode() == KeyEvent.VK_NUMPAD4) {
				selectedBone.setRoll(selectedBone.getRoll() + 1);
			}
			if (e.getKeyCode() == KeyEvent.VK_NUMPAD6) {
				selectedBone.setRoll(selectedBone.getRoll() - 1);
			}
			if (e.getKeyCode() == KeyEvent.VK_NUMPAD8) {
				selectedBone.setPitch(selectedBone.getPitch() + 1);
			}
			if (e.getKeyCode() == KeyEvent.VK_NUMPAD2) {
				selectedBone.setPitch(selectedBone.getPitch() - 1);
			}
			if (e.getKeyCode() == KeyEvent.VK_NUMPAD1) {
				selectedBone.setYaw(selectedBone.getYaw() + 1);
			}
			if (e.getKeyCode() == KeyEvent.VK_NUMPAD7) {
				selectedBone.setYaw(selectedBone.getYaw() - 1);
			}
			if (e.getKeyCode() == KeyEvent.VK_N) {
				player.selectNextBone();
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_F7) {
			renderFrustum = !renderFrustum;
		}
		if (e.getKeyCode() == KeyEvent.VK_F9) {
			isHUDEnabled = !isHUDEnabled;
		}
		if (null != player) {
			player.camera.setDirty();
		}
	}

	@Override
	public void onKeyReleased(String name, KeyEvent e) {

	}
}
