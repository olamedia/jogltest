package ru.olamedia.liveEntity;

import javax.vecmath.Vector3f;

import com.jogamp.newt.event.KeyEvent;

import ru.olamedia.camera.Cameraman;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.inventory.Inventory;
import ru.olamedia.olacraft.world.block.Block;
import ru.olamedia.olacraft.world.blockStack.BlockStack;
import ru.olamedia.olacraft.world.blockTypes.GravelBlockType;
import ru.olamedia.input.Keyboard;

public class LiveEntity implements Cameraman {
	private Inventory inventory; // every living entity can have inventory
	private float x;
	private float y;
	private float z;
	public Vector3f velocity = new Vector3f(0, 0, 0);
	public Vector3f acceleration = new Vector3f(0, 0, 0);

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

	private boolean isWalking = false;
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
			inventory.binded[i] = new BlockStack(new Block(), (int) Math.random() * 64);
			inventory.binded[i].block.setType(new GravelBlockType());
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

	public boolean isEmptyUnderFoot() {
		return isEmptyBlock(0, -1, 0);
	}

	/**
	 * @param delta
	 *            the elapsed time since the last frame update in seconds
	 */
	public void updateKeyboard(float delta) {
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

		// if (inJump) {
		// float FPS = (float) (delta / 1);
		// float accelY = Game.world.gravity;
		// float dt = (float) (delta / 1); // in seconds
		// vVelocity -= accelY * dt;
		// y += vVelocity * dt;
		// if (vVelocity < 0) {
		// inFall = true;
		// }
		// if (vVelocity > 0) {
		// if (!keyJump) {
		// vVelocity = 0;
		// }
		// }
		// if (!applyPosition()) {
		// if (vVelocity > 0) {
		// // roof
		// vVelocity = 0;
		// } else {
		// y = underFoot().getY() + 1;
		// vVelocity = 0;
		// inJump = false;
		// inFall = false;
		// }
		// }
		//
		// } else {
		// if (!inFall) {
		// if (inAir()) {
		//
		// inJump = true;
		// inFall = true;
		// }
		// }
		// }
		if (!onGround && !inAir()) {
			// LANDING
			onGround = true;
			inJump = false;
			velocity.y = 0;
			acceleration.y = 0;
			y = getJumperBlock().getY() + 1;
		}
		if (onGround && velocity.length() > 0 && acceleration.length() > 0) {
			// BEFORE NEW JUMP
			// Check direction changed
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
					// velocity.scale((float) (10 / velocity.length() *
					// Math.cos(dAngle)));
					// velocity.scale((float) (velocity.length()
					// - (velocity.length() - (10 / velocity.length() *
					// Math.cos(dAngle))) * 0.5f * delta));
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
			// JUMPING
			inJump = true;
			onGround = false;
			velocity.y = getJumpVelocity(getMaxJumpHeight());
			acceleration.y = velocity.y;
			// vVelocity = getJumpVelocity(getMaxJumpHeight());
			System.out.println("Max jump height " + getMaxJumpHeight());
			System.out.println("Starting velocity " + velocity.y);
		}

		if (onGround && !inJump) {
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

		if (!onGround || inAir()) { // 0_o
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

	private Block underFoot() {
		return getBlock(0, -1, 0);
	}

	public boolean isEmptyBlock(int dx, int dy, int dz) {
		return Game.client.getWorldProvider().isEmptyBlock((int) x + dx, (int) Math.ceil(y) + dy, (int) z + dz);
	}

	public boolean haveBlockUnder(int dy) {
		return !isEmptyBlock(0, -dy, 0);
	}

	public boolean inAir() {
		return !haveBlockUnder(1);
	}

	public boolean underJumpHeight() {
		return haveBlockUnder(1) || haveBlockUnder(2);
	}

	public Block getJumperBlock() {
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

	public boolean hasValidPosition() {
		Block foot = getBlock(0, 0, 0);
		Block underFoot = getBlock(0, -1, 0);
		Block head = getBlock(0, (int) getHeight(), 0);
		if (!inJump) {
			if (underFoot.isEmpty()) {
				// In AIR while normal walking
				if (isSneaking) {
					// TODO Jumping while Sneaking fixes x,z while jumping
					return false;
				}
			}
		}
		// Check if we're too near to the wall
		float screenPlane = 0.2f;
		float screenPlaneVertical = 0.4f;
		Block[] headNeighbors = head.getNeighbors();
		for (Block b : headNeighbors) {
			if (b.isEmpty()) {
				continue;
			}
			if (b.getX() != head.getX()) {
				// LEFT or RIGHT
				float testX = b.getX();
				if (testX < head.getX()) {
					// LEFT, fixing
					testX = head.getX();
				}
				float minDistance = Math.abs(testX - getX());
				if (minDistance < screenPlane) {
					return false;
				}
			}
			if (b.getY() > head.getY()) { // Upper block
				float minDistance = Math.abs(b.getY() - getY() + this.getHeight());// player
																					// height
				if (minDistance < screenPlaneVertical) {
					return false;
				}
			}

			if (b.getZ() != head.getZ()) {
				// FRONT OR BACK
				float testZ = b.getZ();
				if (testZ < head.getZ()) {
					// BACK, fixing
					testZ = head.getZ();
				}
				float minDistance = Math.abs(testZ - getZ());
				if (minDistance < screenPlane) {
					return false;
				}
			}
		}
		return foot.isEmpty() && head.isEmpty();
	}

	public boolean applyPosition() {
		isPositionChanged = false;
		if (hasValidPosition()) {
			backupPosition();
			isPositionChanged = true;
			return true;
		}
		restorePosition();
		return false;
	}

	public void notifyLocationUpdate() {
		// overriden at player class
	}

	public void fixPosition() {
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
		Block below = getBlock(0, -1, 0);
		if (y == below.getY() + 1) {
			onGround = true;
		}
		updateKeyboard(delta);
		// Check if position is valid
		// fixPosition();

		if (y < -20) {
			// spawnAt((int) x, (int) z);
		}
		pitch = Game.instance.camera.getPitch();
		yaw = Game.instance.camera.getYaw();
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
			notifyLocationUpdate();
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
	}

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
}
