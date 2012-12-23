package ru.olamedia.player;

import java.io.FileNotFoundException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

import jp.nyatla.nymmd.MmdException;
import jp.nyatla.nymmd.MmdMotionPlayerGL2;
import jp.nyatla.nymmd.MmdPmdModel;
import jp.nyatla.nymmd.MmdVmdMotion;
import jp.nyatla.nymmd.types.MmdVector3;
import jp.nyatla.nymmd.types.MmdVector4;

import com.jogamp.newt.event.MouseEvent;

import ru.olamedia.asset.Asset;
import ru.olamedia.asset.AssetManager;
import ru.olamedia.asset.AssetNotFoundException;
import ru.olamedia.game.GameTime;
import ru.olamedia.input.MouseJail;
import ru.olamedia.liveEntity.LiveEntity;
import ru.olamedia.olacraft.game.Game;
import ru.olamedia.olacraft.modelAnimator.Arm;
import ru.olamedia.olacraft.modelAnimator.Bone;
import ru.olamedia.olacraft.modelAnimator.Leg;
import ru.olamedia.olacraft.modelAnimator.ModelAnimator;
import ru.olamedia.olacraft.modelAnimator.Skeleton;
import ru.olamedia.olacraft.network.packet.LiveEntityLocationUpdatePacket;
import ru.olamedia.olacraft.world.block.Block;
import ru.olamedia.olacraft.world.blockStack.BlockStack;
import ru.olamedia.olacraft.world.blockTypes.EmptyBlockType;
import ru.olamedia.olacraft.world.chunk.ChunkUnavailableException;

public class Player extends LiveEntity {
	private MmdMotionPlayerGL2 gl2player;
	private MmdPmdModel model;
	private MmdVmdMotion motion;
	private boolean reverseModelLook = true;

	private float motionTimer = 0;
	private float motionTimerSpeed = 1f;
	private float motionTimerFinish = 1;

	private MmdVmdMotion getMotion() {
		if (null == motion) {
			try {
				final Asset motionAsset = AssetManager.getAsset("models/test.vmd");
				motion = new MmdVmdMotion(motionAsset.getFile());
			} catch (AssetNotFoundException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (MmdException e) {
				e.printStackTrace();
			}
		}
		return motion;
	}

	private MmdPmdModel getModel() {
		if (null == model) {
			try {
				// final Asset modelAsset =
				// AssetManager.getAsset("models/neru/Rin_Kagamine.pmd");
				final Asset modelAsset = AssetManager.getAsset("models/raisen/raisen.pmd");
				// final Asset modelAsset =
				// AssetManager.getAsset("models/marisa/marisa.pmd");
				// final Asset modelAsset =
				// AssetManager.getAsset("models/alice/alice.pmd");
				// final Asset modelAsset =
				// AssetManager.getAsset("models/lat_miku/Normal.pmd");
				// final Asset modelAsset =
				// AssetManager.getAsset("models/rin/rin.pmd");
				model = new MmdPmdModel(modelAsset.getFile());
			} catch (AssetNotFoundException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (MmdException e) {
				e.printStackTrace();
			}
		}
		return model;
	}

	private float motionFrame = 0;
	private float bodyYaw = 0;
	private float cameraYaw = 0;
	private float cameraDeltaYaw = 0;
	private float leftWalkFrame;
	private float walkFrameHalf1;
	private float walkFrameHalf2;
	private float rightWalkFrame;
	private float rightWalkFrameHalf1;
	private float rightWalkFrameHalf2;

	private float clamp360(float value) {
		if (value < 0) {
			return value + (-(int) value / 360) * 360;
		}
		if (value > 0) {
			return value - ((int) value / 360) * 360;
		}
		return value;
	}

	private MmdMotionPlayerGL2 getPlayerModel() throws MmdException {
		if (null == gl2player) {
			gl2player = new MmdMotionPlayerGL2();
			gl2player.setPmd(getModel());
			gl2player.setVmd(getMotion());
			bodyYaw = cameraYaw = camera.getYaw();
		}
		cameraYaw = camera.getYaw() + (reverseModelLook ? -180 : 0);
		final float yawDelta = clamp360(cameraYaw - bodyYaw);
		if (yawDelta > 45) {
			bodyYaw = cameraYaw - 45;
		} else if (yawDelta < -45) {
			bodyYaw = cameraYaw + 45;
		} else {
			bodyYaw += yawDelta * Game.instance.getFrameDelta();
			bodyYaw = clamp360(bodyYaw);
		}
		cameraDeltaYaw = clamp360(cameraYaw - bodyYaw);
		gl2player.lookMeEnable(true);
		// gl2player.setLookVector(camera.getLook().x, camera.getLook().y,
		// camera.getLook().z);
		motionFrame += Game.instance.getFrameDelta();
		if (motionFrame >= getMotion().getMaxFrame() * (100 / 3)) {
			motionFrame = 0;
		}

		if (null != gl2player.m_centerBone) {
			gl2player.m_centerBone.m_vec3Position.x = 0;
			gl2player.m_centerBone.m_vec3Position.y = 0;
			gl2player.m_centerBone.m_vec3Position.z = 0;
		}

		if (null != gl2player.m_pNeckBone) {
			final MmdVector3 euler = new MmdVector3((float) Math.toRadians(-camera.getPitch()),
					(float) Math.toRadians(cameraDeltaYaw), (float) Math.toRadians(camera.getRoll()));
			gl2player.m_pNeckBone.m_vec4Rotate.QuaternionCreateEuler(euler);
			// final MmdVector3 yAxis = new MmdVector3(0, 0, 1);
			// gl2player.m_pNeckBone.m_vec4Rotate.QuaternionCreateAxis(yAxis,
			// (float) Math.toRadians(camera.getYaw()));// .x
			// =
			// camera.getLook().x;
			// gl2player.m_pNeckBone.m_vec4Rotate.y = camera.getLook().y;
			// gl2player.m_pNeckBone.m_vec4Rotate.z = camera.getLook().z;
			// gl2player.m_pNeckBone.m_vec4Rotate.w = 1;
			// .QuaternionCreateEuler(euler);
			// gl2player.manualUpdateNeckBone();
		}
		gl2player.m_leftEyeDestination.lookAt(new MmdVector3(camera.getNlook().x, 0, camera.getNlook().z));
		final float maxFootOffset = 1f;
		final MmdVector3 xAxis = new MmdVector3(1, 0, 0);
		final MmdVector3 yAxis = new MmdVector3(0, 1, 0);

		// gl2player.m_leftFootDestinationIK.m_vec3Position.y = maxFootOffset *
		// walkFrame;
		// gl2player.m_leftKnee.m_vec3Position.y = maxFootOffset * walkFrame;
		gl2player.reset();
		/*
		 * final float hipAngle = 15;
		 * final float hipLegYaw = 5;
		 * final float legPitch = 40;
		 * final float hipLegRoll = -5;
		 * final float lowerBodyYaw = -(float) Math.toRadians(-hipAngle *
		 * walkFrameHalf1 + hipAngle * walkFrameHalf2);
		 * final float leftLegYaw = -(float) Math.toRadians(hipLegYaw *
		 * walkFrameHalf1);
		 * final float rightLegYaw = -(float) Math.toRadians(-hipLegYaw *
		 * walkFrameHalf2);
		 * final float leftLegRoll = -(float) Math.toRadians(-hipLegRoll *
		 * walkFrameHalf1 + hipLegRoll * walkFrameHalf2);
		 * final float rightLegRoll = (float) Math.toRadians(hipLegRoll *
		 * walkFrameHalf1 - hipLegRoll * walkFrameHalf2);
		 * final float leftLegPitch = -(float) Math.toRadians(-legPitch *
		 * walkFrameHalf1 + legPitch * walkFrameHalf2);
		 * final float rightLegPitch = -(float) Math.toRadians(-legPitch *
		 * walkFrameHalf2 + legPitch * walkFrameHalf1);
		 * gl2player.m_lowerBody.m_vec4Rotate.QuaternionCreateAxis(yAxis,
		 * lowerBodyYaw);
		 * final MmdVector3 leftLegEuler = new MmdVector3(leftLegPitch,
		 * leftLegYaw, leftLegRoll);
		 * final MmdVector3 rightLegEuler = new MmdVector3(rightLegPitch,
		 * rightLegYaw, rightLegRoll);
		 * gl2player.m_leftLeg.m_vec4Rotate.QuaternionCreateEuler(leftLegEuler);
		 * gl2player.m_rightLeg.m_vec4Rotate.QuaternionCreateEuler(rightLegEuler)
		 * ;
		 * gl2player.m_leftKnee.m_vec4Rotate.QuaternionCreateAxis(xAxis,
		 * -(float) Math.toRadians(20 * walkFrameHalf2 + 30 * walkFrameHalf1));
		 * gl2player.m_rightKnee.m_vec4Rotate.QuaternionCreateAxis(xAxis,
		 * -(float) Math.toRadians(30 * walkFrameHalf2 + 20 * walkFrameHalf1));
		 * gl2player.m_leftAnkle.m_vec4Rotate.QuaternionCreateAxis(xAxis,
		 * -(float) Math.toRadians(15 * walkFrameHalf2 - 30 * walkFrameHalf1));
		 * gl2player.m_rightAnkle.m_vec4Rotate.QuaternionCreateAxis(xAxis,
		 * -(float) Math.toRadians(-30 * walkFrameHalf2 + 15 * walkFrameHalf1));
		 * gl2player.update();
		 * // gl2player.m_leftFootIK.m_vec3Position.z = 0;
		 * // gl2player.m_leftFootIK.m_vec4Rotate.x = 0;
		 * // gl2player.m_leftFootIK.m_vec4Rotate.y = 0;
		 * // gl2player.m_leftFootIK.m_vec4Rotate.z = 0;
		 * // gl2player.m_leftToe.m_vec3Position.x = 0;
		 * // gl2player.m_leftToe.m_vec3Position.y = 0;
		 * // gl2player.m_leftToe.m_vec3Position.z = 0;
		 * // gl2player.m_leftToe.m_vec4Rotate.x = 0;
		 * // gl2player.m_leftToe.m_vec4Rotate.y = 0;
		 * // gl2player.m_leftToe.m_vec4Rotate.z = 0;
		 * // if (motionTimer > 1) {
		 * // gl2player.m_leftKnee.m_vec3Position.z = maxFootOffset +
		 * maxFootOffset
		 * // * ((1 - (motionTimer - 1) * 2));
		 * // if (gl2player.m_leftKnee.m_vec3Position.z < 0) {
		 * // gl2player.m_leftKnee.m_vec3Position.z = 0;
		 * // }
		 * // }
		 * gl2player.m_rightFootIK.m_vec3Position.x = -maxFootOffset * (1 -
		 * motionTimer);
		 * // gl2player.updateMotion(motionFrame * 1000);
		 */
		return gl2player;
	}

	private MmdVector3 createEuler(float pitch, float yaw, float roll) {
		return new MmdVector3(pitch, yaw, roll);
	}

	private MmdVector3 createEuler(Bone b) {
		return new MmdVector3((float) Math.toRadians(b.getCorrectedPitch()),
				(float) Math.toRadians(b.getCorrectedYaw()), (float) Math.toRadians(b.getCorrectedRoll()));
	}

	private void calcWalkFrames() {
		leftWalkFrame = (float) (motionTimer > 0.5 ? (motionTimer - 0.5) * 2 : 1 - motionTimer * 2);
		if (motionTimer > 0.5f) {
			// Half2
			walkFrameHalf1 = 0;
			walkFrameHalf2 = (motionTimer - 0.5f) * 2f;
			walkFrameHalf2 = 1f - (float) Math.abs(walkFrameHalf2 * 2f - 1f);
		} else {
			// Half1
			walkFrameHalf1 = motionTimer * 2f;
			walkFrameHalf2 = 0;
			walkFrameHalf1 = 1f - (float) Math.abs(walkFrameHalf1 * 2f - 1f);
		}
		rightWalkFrame = 1 - leftWalkFrame;
	}

	private void updateMotionTimer() {
		motionTimer += Game.instance.getFrameDelta() * motionTimerSpeed;
		if (motionTimer >= motionTimerFinish) {
			motionTimer = 0;
		}
	}

	private ModelAnimator animator = new ModelAnimator();

	private MmdMotionPlayerGL2 getOrientedModel() {
		try {
			final MmdMotionPlayerGL2 player = getPlayerModel();
			player.reset();
			final Skeleton skel = animator.deltaOrientation;
			final Leg leftLeg = skel.leftLeg;
			final Leg rightLeg = skel.rightLeg;
			// LEGS
			player.m_leftLeg.m_vec4Rotate.QuaternionCreateEuler(createEuler(leftLeg.top));
			player.m_rightLeg.m_vec4Rotate.QuaternionCreateEuler(createEuler(rightLeg.top));
			player.m_leftKnee.m_vec4Rotate.QuaternionCreateEuler(createEuler(leftLeg.knee));
			player.m_rightKnee.m_vec4Rotate.QuaternionCreateEuler(createEuler(rightLeg.knee));
			player.m_leftAnkle.m_vec4Rotate.QuaternionCreateEuler(createEuler(leftLeg.foot));
			player.m_rightAnkle.m_vec4Rotate.QuaternionCreateEuler(createEuler(rightLeg.foot));
			player.m_rightFootIK.m_vec3Position.y = 0;
			player.m_leftFootIK.m_vec3Position.y = 0;
			// ARMS
			final Arm leftArm = skel.leftArm;
			final Arm rightArm = skel.rightArm;
			player.m_leftArm.m_vec4Rotate.QuaternionCreateEuler(createEuler(leftArm.top));
			player.m_rightArm.m_vec4Rotate.QuaternionCreateEuler(createEuler(rightArm.top));
			player.m_leftElbow.m_vec4Rotate.QuaternionCreateEuler(createEuler(leftArm.elbow));
			player.m_rightElbow.m_vec4Rotate.QuaternionCreateEuler(createEuler(rightArm.elbow));
			player.m_leftWrist.m_vec4Rotate.QuaternionCreateEuler(createEuler(leftArm.wrist));
			player.m_rightWrist.m_vec4Rotate.QuaternionCreateEuler(createEuler(rightArm.wrist));

			player.m_lowerBody.m_vec4Rotate.QuaternionCreateEuler(createEuler(skel.waist));
			player.m_upperBody.m_vec4Rotate.QuaternionCreateEuler(createEuler(skel.shoulders));
			if (null != player.m_skirtFrontLeft) {
				player.m_skirtFrontLeft.m_vec4Rotate.QuaternionCreateEuler(createEuler(skel.skirtFrontLeft));
				player.m_skirtFrontRight.m_vec4Rotate.QuaternionCreateEuler(createEuler(skel.skirtFrontRight));
			}
			// player.m_upperBody.m_vec3Position.z = 1;
			player.update();
			return player;
		} catch (MmdException e) {
			e.printStackTrace();
		}
		return null;
	}

	private int bonesCount = 0;
	private int selectedBone = 0;

	private int bodyYawCorrection = 0;

	public void rotateCW() {
		bodyYawCorrection++;
		if (bodyYawCorrection > 360) {
			bodyYawCorrection -= 360;
		}
	}

	public void rotateCCW() {
		bodyYawCorrection--;
		if (bodyYawCorrection < 0) {
			bodyYawCorrection += 360;
		}
	}

	public void selectNextBone() {
		selectedBone++;
		if (selectedBone > bonesCount) {
			selectedBone = 0;
		}
		Game.client.getScene().selectedBone = animator.finalOrientation.getBone(selectedBone);
	}

	public void render() {
		if (null == Game.client.getScene().selectedBone) {
			bonesCount = Game.client.getScene().bonesCount = animator.deltaOrientation.getBoneCount();
			int i = 0;
			Game.client.getScene().selectedBone = animator.finalOrientation.getBone(selectedBone);
		}
		animator.setPause(Game.client.getScene().boneMode);
		animator.setJumping(inJump ? true : false);
		animator.setWalking(isWalking ? true : false);
		animator.setDelta(Game.instance.getFrameDelta());
		animator.tick();

		/*
		 * if (inJump) {
		 * if (walkFrameHalf1 < 0.9f && walkFrameHalf2 < 0.9f) {
		 * updateMotionTimer();
		 * calcWalkFrames();
		 * }
		 * } else if (isWalking) {
		 * updateMotionTimer();
		 * calcWalkFrames();
		 * } else {
		 * motionTimer -= Game.instance.getDelta() * motionTimerSpeed;
		 * if (motionTimer <= 0) {
		 * motionTimer = 0;
		 * }
		 * leftWalkFrame += Game.instance.getDelta() * (0 - leftWalkFrame) * 5;
		 * rightWalkFrame += Game.instance.getDelta() * (0 - rightWalkFrame) *
		 * 5;
		 * walkFrameHalf1 += Game.instance.getDelta() * (0 - walkFrameHalf1) *
		 * 5;
		 * walkFrameHalf2 += Game.instance.getDelta() * (0 - walkFrameHalf2) *
		 * 5;
		 * }
		 */

		try {
			final MmdMotionPlayerGL2 player = getOrientedModel();
			final GL2 gl = GLContext.getCurrentGL().getGL2();
			gl.glPushMatrix();
			gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
			final float scale = getHeight() / player.height;
			gl.glTranslatef(getX(), getY(), getZ());// - 2.5f
			gl.glScalef(scale, scale, scale);
			// gl.glRotatef(getPitch(), 1, 0, 0);

			gl.glRotatef(bodyYaw + bodyYawCorrection, 0, 1, 0);
			// gl.glRotatef(getRoll(), 0, 0, 1);
			player.render();
			gl.glPopAttrib();
			gl.glPopMatrix();
		} catch (MmdException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyLocationUpdate() {
		LiveEntityLocationUpdatePacket p = new LiveEntityLocationUpdatePacket();
		p.x = getX();
		p.y = getY();
		p.z = getZ();
		Game.client.send(p);
	}

	public Player() {
	}

	public void pickBlock(Block b) {
		BlockStack stack = new BlockStack(b.provider.getChunk(b.location.getChunkLocation()).getType(b.location,
				b.provider), 1);
		if (null != stack.type) {
			// inventory.putBlockStack(stack);
			stack.type.dropBlock(b.provider, b.location);
		}
		b.removeFromWorld();
		try {
			if (inAir()) {
				System.out.println("in air while picking");
				inJump = true;
				onGround = false;
				isOrientationChanged = true;
			}
		} catch (ChunkUnavailableException e) {
			e.printStackTrace();
		}
		// Game.client.getScene().updateNearestBlock();
	}

	public void putBlock(Block b) {
		BlockStack stack = inventory.getSelectedBlockStack();
		if (stack.count > 0) {
			b.setType(stack.type);
			stack.count--;
			if (stack.count == 0) {
				stack.type = new EmptyBlockType();
			}
			b.setLocation(b.provider, b.location.x, b.location.y, b.location.z);
			System.out.println("put block " + b.getType().getClass().getName());
			b.insertToWorld();
			// Game.client.getScene().updateNearestBlock();
		}
	}

	public void onMouseClick(MouseEvent e) {
		if (null != inventory) {
			if (e.getButton() == 1) {
				inventory.click(false);
			}
			if (e.getButton() == 3) {
				inventory.click(true);
			}
		}
		if (e.isConfined() && MouseJail.isActive()) {
			if (e.getButton() == 1) {
				if (null != Game.client.getScene().nearestBlock) {
					pickBlock(Game.client.getScene().nearestBlock);
				}
			}
			if (e.getButton() == 3) {
				if (null != Game.client.getScene().nearestPutBlock) {
					putBlock(Game.client.getScene().nearestPutBlock);
				}
			}
		}
		// Bullet b = new Bullet();
		// b.velocity.set(Game.instance.camera.getLook());
		// b.velocity.negate();
		// b.velocity.scale(100);
		// b.location.set(getX(), getCameraY(), getZ());
		// Game.client.getScene().addBullet(b);
	}
}
