package ru.olamedia.liveEntity;

import javax.vecmath.Vector3f;

import com.jogamp.newt.event.KeyEvent;

import ru.olamedia.camera.Cameraman;
import ru.olamedia.camera.MatrixCamera;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.inventory.Inventory;
import ru.olamedia.olacraft.world.block.Block;
import ru.olamedia.olacraft.world.blockStack.BlockStack;
import ru.olamedia.olacraft.world.blockTypes.GravelBlockType;
import ru.olamedia.olacraft.world.chunk.ChunkUnavailableException;
import ru.olamedia.olacraft.world.location.BlockLocation;
import ru.olamedia.player.RuntimeSettings;
import ru.olamedia.input.KeyListener;
import ru.olamedia.input.Keyboard;

public class LiveEntity implements Cameraman, KeyListener {
	public MatrixCamera camera;
	public MatrixCamera thirdPerson = new MatrixCamera();
	public RuntimeSettings settings = new RuntimeSettings();
	protected Inventory inventory; // every living entity can have inventory
	private float x;
	private float y;
	private float z;
	public Vector3f velocity = new Vector3f(0, 0, 0);
	public Vector3f acceleration = new Vector3f(0, 0, 0);
	public boolean isOrientationChanged = true;

	private float fov = 90f;
	private float mouseSpeed = 1.0f;
	private float maxLookUp = 80.0f;
	private float maxLookDown = -80.0f;
	private float maxHeight = 1.9f;
	private float cameraLevel = 1.8f;

	private float pitch = 0f;
	private float yaw = 0f;
	private float roll = 0f;

	private float walkSpeed = 1.3f;// 1.3-1.5 m/s
	private float runSpeed = 4.5f;// m/s

	protected boolean isWalking = false;
	private boolean isRunning = false;
	private boolean isSneaking = false;
	private boolean isCrouching = false;
	private boolean isViewBobbing = true;
	private float bob = 0f;
	private float hSpeed = runSpeed; // horizontal speed
	private float vVelocity = 0f;

	@SuppressWarnings("unused")
	private float maxHealthPoints;
	private float healthPoints;
	public float qD = 0;
	public float qInvD = 0;
	public boolean inJump = false;
	public boolean inFall = false;
	public boolean onGround = false;
	private float savedX;
	private float savedY;
	private float savedZ;
	private int id;
	private int connectionId;
	private boolean isPositionChanged = false;

	protected void generateInventory() {
		// can be overriden for mobs
		for (int i = 0; i <= 9; i++) {
		}
	}

	public Inventory getInventory() {
		if (null == inventory) {
			inventory = new Inventory();
			generateInventory();
		}
		return inventory;
	}

	public float getHSpeed() {
		return (hSpeed) * (isCrouching ? 0.4f : 1) * (isSneaking ? 0.5f : 1);
	}

	public float getHeight() {
		return maxHeight * (isCrouching ? 0.5f : 1) * (isSneaking ? 0.8f : 1);
	}

	public float getMaxJumpHeight() {
		// ~1.5 for 1.8
		return (float) ((float) (maxHeight * 0.83)) * (isCrouching ? 0.8f : 1f);
	}

	/**
	 * Returns the vertical velocity needed to jump the specified height (based
	 * on current gravity). Uses the Math.sqrt() function.
	 */
	public float getJumpVelocity(float jumpHeight) {
		// use velocity/acceleration formal: v*v = -2 * a(y-y0)
		// (v is jump velocity, a is accel, y-y0 is max height)
		return (float) Math.sqrt(2 * Game.client.getWorldInfo().gravity * jumpHeight);
	}

	public float getCameraLevel() {
		return getHeight() * 0.9f;
	}

	public boolean isEmptyUnderFoot() throws ChunkUnavailableException {
		return canMoveThrough(0, -1, 0);
	}

	/**
	 * @param delta
	 *            the elapsed time since the last frame update in seconds
	 * @throws ChunkUnavailableException
	 */
	public void updateKeyboard(float delta) throws ChunkUnavailableException {
		acceleration.x = 0;
		acceleration.y = 0;
		acceleration.z = 0;
		boolean keyLeft = Keyboard.isKeyDown("playerStrafeLeft");
		boolean keyRight = Keyboard.isKeyDown("playerStrafeRight");
		boolean keyForward = Keyboard.isKeyDown("playerForward");
		boolean keyBack = Keyboard.isKeyDown("playerBack");
		boolean keyJump = Keyboard.isKeyDown("playerJump");
		boolean keySneak = Keyboard.isKeyDown("playerSneak");
		boolean keyCrouch = Keyboard.isKeyDown("playerCrouch");
		boolean keyRenderDistance = Keyboard.isKeyDown("playerRenderDistance");
		if (Keyboard.isKeyDown("playerInventory1")) {
			inventory.select(0);
		}
		if (Keyboard.isKeyDown("playerInventory2")) {
			inventory.select(1);
		}
		if (Keyboard.isKeyDown("playerInventory3")) {
			inventory.select(2);
		}
		if (Keyboard.isKeyDown("playerInventory4")) {
			inventory.select(3);
		}
		if (Keyboard.isKeyDown("playerInventory5")) {
			inventory.select(4);
		}
		if (Keyboard.isKeyDown("playerInventory6")) {
			inventory.select(5);
		}
		if (Keyboard.isKeyDown("playerInventory7")) {
			inventory.select(6);
		}
		if (Keyboard.isKeyDown("playerInventory8")) {
			inventory.select(7);
		}
		if (Keyboard.isKeyDown("playerInventory9")) {
			inventory.select(8);
		}
		if (Keyboard.isKeyDown("playerInventory0")) {
			inventory.select(9);
		}
		if (keyRenderDistance) {

		}
		if (keySneak) {
			if (!isSneaking) {
				isSneaking = true;
				if (!applyPosition()) {
					isSneaking = false;
				}
			}
		} else {
			if (isSneaking) {
				isSneaking = false;
				if (!applyPosition()) {
					isSneaking = true;
				}
			}
		}
		if (keyCrouch) {
			if (!isCrouching) {
				isCrouching = true;
				if (!applyPosition()) {
					isCrouching = false;
				}
			}
		} else {
			if (isCrouching) {
				isCrouching = false;
				if (!applyPosition()) { // Test if we don't have a block over
										// head
					isCrouching = true;
				}
			}
		}
		isWalking = false;
		if ((keyForward && !keyBack) || (keyBack && !keyForward)) {
			Vector3f look = Game.instance.camera.getLook();
			acceleration.x += look.x * (keyBack ? 1 : -1) * 1f;
			acceleration.y += 0;
			acceleration.z += look.z * (keyBack ? 1 : -1) * 1f;
		}
		if ((keyLeft && !keyRight) || (keyRight && !keyLeft)) {
			Vector3f right = Game.instance.camera.getRight();
			acceleration.x += right.x * (keyRight ? 1 : -1) * 1f;
			acceleration.y += 0;
			acceleration.z += right.z * (keyRight ? 1 : -1) * 1f;
		}
		// acceleration.normalize();
		float normalSpeed = 4.5f;
		acceleration.x *= normalSpeed;// running
		acceleration.y *= normalSpeed;
		acceleration.z *= normalSpeed;
		if (!inAir()) {
			// FIX Y TO GROUND LEVEL
			onGround = true;
			inJump = false;
			velocity.y = 0;
			acceleration.y = 0;
			y = getJumperBlock().getY() + 0.5f;
		}
		if (onGround && velocity.length() > 0 && acceleration.length() > 0) {
			// SLOWLY CHANGE DIRECTIONS, BEFORE NEW JUMP, IF MOVING
			float vy = velocity.y;
			float ay = acceleration.y;
			velocity.y = 0;
			acceleration.y = 0;
			float dAngle = velocity.angle(acceleration);
			float speedLimit = 10;
			if (dAngle > 0) {
				// changing angle
				Vector3f newDirection = new Vector3f(velocity);
				newDirection.add(acceleration);
				if (newDirection.length() > 0) {
					newDirection.normalize();
					newDirection.scale(velocity.length());
				}
				velocity.set(newDirection);
				// check velocity
				if (velocity.length() * Math.cos(dAngle) > speedLimit) {
					float deltaSpeed = (float) (velocity.length() * Math.cos(dAngle) - speedLimit);
					velocity.scale((1 / velocity.length()) * (velocity.length() - deltaSpeed * 0.7f));
				}
			} else {
				if (velocity.length() > speedLimit) {
					float deltaSpeed = (float) (velocity.length() - speedLimit);
					velocity.scale((1 / velocity.length()) * (velocity.length() - deltaSpeed * 0.7f));
				}
			}
			velocity.y = vy;
			acceleration.y = ay;
		}
		if (onGround && keyJump) {
			// START NEW JUMP
			inJump = true;
			onGround = false;
			velocity.y = getJumpVelocity(getMaxJumpHeight());
			acceleration.y = velocity.y;
			// vVelocity = getJumpVelocity(getMaxJumpHeight());
			System.out.println("Max jump height " + getMaxJumpHeight());
			System.out.println("Starting velocity " + velocity.y);
		}

		if (onGround) {
			// FIX SPEED WHILE ON GROUND
			if (velocity.length() > normalSpeed) {
				velocity.scale(normalSpeed / velocity.length());
			}
			// APPLY FRICTION
			// Vector3f friction = new Vector3f(velocity);
			// friction.negate();
			// friction.scale(friction.length());
			// friction.scale(delta);
			// velocity.scale((float) Math.exp(-0.2 * delta));
			// acceleration.scale(friction.length());
		}

		if (velocity.length() > 0 && acceleration.length() > 0) {
			qD = Math.abs(velocity.dot(acceleration)) / (velocity.length() * acceleration.length());
		}

		// Vector3f a = new Vector3f(acceleration);
		// if (a.length() > 0) {
		// a.normalize();
		// }
		//
		// if (qD > 0) {
		// qInvD = (float) Math.acos(qD);
		// acceleration.scale(qInvD * 10f);
		// }
		// if (qD > 10) {
		// if (velocity.length() > 0) {
		// velocity.normalize();
		// }
		// velocity.scale(qD);
		// }
		// if (qd > 10) {
		// qd = 10;
		// }
		// velocity.set(acceleration);
		// if (velocity.length() > 1){
		// velocity.scale(1 / velocity.length());
		// }
		// velocity.scale(qd);

		if (!onGround) {
			// APPLY GRAVITY
			acceleration.y = -Game.client.getWorldInfo().gravity;
		}

		// float qangle = (float) Math.abs(Math.acos(qd));
		// if (qangle > 180){
		// acceleration.scale(10f);
		// }else if (qangle > 90){
		// acceleration.scale(5f);
		// }else if (qangle > 45){
		// acceleration.scale(2f);
		// }

		// Quake-like tricks here...
		// Vector3f qa = new Vector3f(acceleration);
		// if (qa.length() != 0) {
		// qa.scale(1 / qa.length());
		// float qd = acceleration.dot(velocity);
		// float qangle = (float) Math.abs(Math.acos(qd));
		// if (qangle > 180) {
		// // fast stop
		// velocity.x *= 0.5f;
		// velocity.z *= 0.5f;
		// }else{
		//
		// }
		// if (qd > 14) {
		// velocity.x *= 1 / qd;
		// velocity.z *= 1 / qd;
		// }
		// }
		if (onGround && (acceleration.length() == 0)) {
			velocity.set(0f, 0f, 0f);
		} else {
			// acceleration.normalize();
			// Vector3f a = new Vector3f(acceleration);
			// a.scale(delta);

			if (velocity.length() == 0) {
				velocity.x = acceleration.x;
				// velocity.y = acceleration.y;
				velocity.z = acceleration.z;
			} else {
				velocity.x += acceleration.x * delta;
				velocity.z += acceleration.z * delta;
				velocity.y += acceleration.y * delta;
			}

			Vector3f move = new Vector3f(velocity);
			move.scale(delta);
			float dx = 0;
			while (move.x != 0) {
				if (move.x > 1) {
					dx = 1;
					move.x -= 1;
				} else if (move.x < -1) {
					dx = -1;
					move.x += 1;
				} else {
					dx = move.x;
					move.x = 0;
				}
				x += dx;
				if (applyPosition()) {
					isWalking = true;
				} else {
					// full stop
					velocity.x = 0;
				}
			}
			float dz = 0;
			while (move.z != 0) {
				if (move.z > 1) {
					dz = 1;
					move.z -= 1;
				} else if (move.z < -1) {
					dz = -1;
					move.z += 1;
				} else {
					dz = move.z;
					move.z = 0;
				}
				z += dz;
				if (applyPosition()) {
					isWalking = true;
				} else {
					// full stop
					velocity.z = 0;
				}
			}
			float dy = 0;
			if (move.y != 0) {
				while (move.y != 0) {
					if (move.y > 1) {
						dy = 1;
						move.y -= 1;
					} else if (move.y < -1) {
						dy = -1;
						move.y += 1;
					} else {
						dy = move.y;
						move.y = 0;
					}
					y += dy;
					if (applyPosition()) {
						isWalking = true;
					} else {
						// full stop
						velocity.y = 0;
					}
				}

			}
		}

	}

	BlockLocation testLoc = new BlockLocation();

	public boolean canMoveThrough(int dx, int dy, int dz) {
		testLoc.set((int) x + dx, (int) Math.ceil(y) + dy, (int) z + dz);
		if (!Game.client.getWorldProvider().isChunkAvailable(testLoc.getChunkLocation())) {
			Game.client.getWorldProvider().loadChunk(testLoc.getChunkLocation());
			return false;
		}

		try {
			return Game.client.getWorldProvider().canMoveThrough((int) x + dx, (int) Math.ceil(y) + dy, (int) z + dz);
		} catch (ChunkUnavailableException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean haveBlockUnder(int dy) throws ChunkUnavailableException {
		return !canMoveThrough(0, -dy, 0);
	}

	public boolean inAir() throws ChunkUnavailableException {
		if (!canMoveThrough(0, -1, 0)) {
			if (y > getBlock(0, -1, 0).getY() + 0.5f) {
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	public boolean underJumpHeight() throws ChunkUnavailableException {
		return haveBlockUnder(1) || haveBlockUnder(2);
	}

	public Block getJumperBlock() throws ChunkUnavailableException {
		if (haveBlockUnder(1)) {
			return getBlock(0, -1, 0);
		}
		if (haveBlockUnder(2)) {
			return getBlock(0, -2, 0);
		}
		return null;
	}

	private Block getBlock(int dx, int dy, int dz) {
		return Game.client.getWorldProvider().getBlock((int) x + dx, (int) Math.ceil(y) + dy, (int) z + dz);
	}

	public void backupPosition() {
		savedX = x;
		savedY = y;
		savedZ = z;
	}

	public void restorePosition() {
		x = savedX;
		y = savedY;
		z = savedZ;
	}

	public boolean hasValidPosition(Block head, boolean checkTop, boolean checkBottom) throws ChunkUnavailableException {
		// Check if we're too near to the wall
		/*
		 * Block[] LRNeighbors = { head.getNeighbor(-1, 0, 0),
		 * head.getNeighbor(1, 0, 0) };
		 * for (Block n : LRNeighbors) {
		 * if (!n.canMoveThrough()) {
		 * return false;
		 * }
		 * if (Math.abs(n.getX() - x) < 0.5) {
		 * return false;
		 * }
		 * }
		 * Block[] FBNeighbors = { head.getNeighbor(0, 0, -1),
		 * head.getNeighbor(0, 0, 1) };
		 * for (Block n : FBNeighbors) {
		 * if (!n.canMoveThrough()) {
		 * return false;
		 * }
		 * if (Math.abs(n.getZ() - z) < 0.5) {
		 * return false;
		 * }
		 * }
		 */
		// if (checkBottom) {
		// Block n = head.getNeighbor(0, -1, 0);
		// if (Math.abs(n.getY() - y) < 0.5) {
		// return false;
		// }
		// }
		// if (checkTop) {
		// Block n = head.getNeighbor(0, 1, 0);
		// if (Math.abs(n.getY() - y) < 0.5) {
		// return false;
		// }
		// }
		return head.canMoveThrough();
	}

	public boolean hasValidPosition() throws ChunkUnavailableException {
		Block foot = getBlock(0, 0, 0);
		Block underFoot = getBlock(0, -1, 0);
		Block head = getBlock(0, (int) getHeight(), 0);
		if (!inJump) {
			if (underFoot.canMoveThrough()) {
				// In AIR while normal walking
				if (isSneaking) {
					// TODO Jumping while Sneaking fixes x,z while jumping
					return false;
				}
			}
		}

		return hasValidPosition(foot, false, true) && hasValidPosition(head, true, false);
	}

	public boolean applyPosition() throws ChunkUnavailableException {
		isPositionChanged = false;
		if (hasValidPosition()) {
			backupPosition();
			isPositionChanged = true;
			if (null != camera) {
				camera.setDirty();
			}
			return true;
		}
		restorePosition();
		return false;
	}

	public void notifyLocationUpdate() {
		// overriden at player class
	}

	public void fixPosition() throws ChunkUnavailableException {
		float dx = x - savedX;
		float dy = y - savedY;
		float dz = z - savedZ;
		restorePosition();
		if (Math.abs(dx) < 1) { // in a block range
			x += dx;
			applyPosition();
		}
		if (Math.abs(dy) < 1) { // in a block range
			y += dy;
			applyPosition();
		}
		if (Math.abs(dz) < 1) { // in a block range
			y += dz;
			applyPosition();
		}
	}

	@Override
	public void update(float delta) {
		isPositionChanged = false;
		backupPosition();
		onGround = false;
		try {
			onGround = !inAir();
			updateKeyboard(delta);
		} catch (ChunkUnavailableException e) {
			e.printStackTrace();
			System.exit(0);
		}
		// Check if position is valid
		// fixPosition();

		if (y < -20) {
			// spawnAt((int) x, (int) z);
		}
		setPitch(Game.instance.camera.getPitch());
		setYaw(Game.instance.camera.getYaw());
		// Game.camera.setRoll(roll);
		// saveTrace();
		// if (isWalking && onGround) {
		// if (!stepsound.isPlaying()) {
		// stepsound.playAsSoundEffect(1f, 0.4f, true);
		// }
		// } else {
		// if (stepsound.isPlaying()) {
		// stepsound.stop();
		// }
		// }
		if (isPositionChanged) {
			isOrientationChanged = true;
			notifyLocationUpdate();
			if (null != camera) {
				camera.setDirty();
			}
		}
	}

	public void setLocation(float x, float y, float z) {
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}

	public void say(String message) {

	}

	public void die() {
		healthPoints = 0;
		LiveEntityEvent e = new LiveEntityEvent(this);
		e.setType(LiveEntityEvent.ON_DIE);
		e.dispatch();
	}

	public void acceptDamage(float amount) {
		healthPoints -= amount;
		if (healthPoints < 0) {
			die();
		}
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public float getCameraX() {
		return x;
	}

	@Override
	public float getCameraY() {
		return y + getCameraLevel();
	}

	private BlockLocation cameraBlockLocation = new BlockLocation();

	public BlockLocation getCameraBlockLocation() {
		cameraBlockLocation.set(x, y, z);
		return cameraBlockLocation;
	}

	@Override
	public float getCameraZ() {
		return z;
	}

	@Override
	public void captureControls() {
		System.out.println("Player took controls");
		Keyboard.setName("playerForward", KeyEvent.VK_W);
		Keyboard.setName("playerBack", KeyEvent.VK_S);
		Keyboard.setName("playerStrafeLeft", KeyEvent.VK_A);
		Keyboard.setName("playerStrafeRight", KeyEvent.VK_D);
		Keyboard.setName("playerJump", KeyEvent.VK_SPACE);
		Keyboard.setName("playerSneak", KeyEvent.VK_SHIFT);
		Keyboard.setName("playerCrouch", KeyEvent.VK_CONTROL);
		Keyboard.setName("playerRenderDistance", KeyEvent.VK_F4);
		Keyboard.setName("playerInventory1", KeyEvent.VK_1);
		Keyboard.setName("playerInventory2", KeyEvent.VK_2);
		Keyboard.setName("playerInventory3", KeyEvent.VK_3);
		Keyboard.setName("playerInventory4", KeyEvent.VK_4);
		Keyboard.setName("playerInventory5", KeyEvent.VK_5);
		Keyboard.setName("playerInventory6", KeyEvent.VK_6);
		Keyboard.setName("playerInventory7", KeyEvent.VK_7);
		Keyboard.setName("playerInventory8", KeyEvent.VK_8);
		Keyboard.setName("playerInventory9", KeyEvent.VK_9);
		Keyboard.setName("playerInventory0", KeyEvent.VK_0);
		Keyboard.setName("playerInventoryToggle", KeyEvent.VK_E);
		if (!isListeningControls) {
			isListeningControls = true;
			Keyboard.attach(this);
		}
	}

	private boolean isListeningControls = false;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(int connectionId) {
		this.connectionId = connectionId;
	}

	@Override
	public void onKeyPressed(String name, KeyEvent e) {
		if (name.equals("playerInventoryToggle")) {
			inventory.toggleGUI();
		}
		if (name.equals("playerRenderDistance")) {
			settings.renderDistance *= 2;
			if (settings.renderDistance > 256) {
				settings.renderDistance = 32;
			}
		}

	}

	@Override
	public void onKeyReleased(String name, KeyEvent e) {

	}

	@Override
	public MatrixCamera getCamera() {
		return camera;
	}

	@Override
	public void setCamera(MatrixCamera camera) {
		this.camera = camera;
		if (null != camera) {
			// thirdPerson.attachTo(this.camera, false);
		}
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}
}
