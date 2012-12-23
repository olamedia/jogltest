package ru.olamedia.olacraft.modelAnimator;

public class ModelAnimator {
	private boolean isWalking = false;
	private boolean isCrawling = false;
	private boolean isCrouching = false;
	private boolean isJumping = false;
	private boolean isFalling = false;
	private boolean isStrafingLeft = false;
	private boolean isStrafingRight = false;
	private boolean isMovingForward = false;
	private boolean isMovingBackward = false;
	private float delta;

	private Skeleton currentOrientation = new Skeleton();
	public Skeleton finalOrientation = new Skeleton();
	public Skeleton deltaOrientation = new Skeleton();

	public ModelAnimator() {
		generateFrames();
	}

	public boolean isWalking() {
		return isWalking;
	}

	public void setWalking(boolean isWalking) {
		this.isWalking = isWalking;
	}

	public boolean isCrawling() {
		return isCrawling;
	}

	public void setCrawling(boolean isCrawling) {
		this.isCrawling = isCrawling;
	}

	public boolean isCrouching() {
		return isCrouching;
	}

	public void setCrouching(boolean isCrouching) {
		this.isCrouching = isCrouching;
	}

	public boolean isJumping() {
		return isJumping;
	}

	public void setJumping(boolean isJumping) {
		this.isJumping = isJumping;
	}

	public boolean isFalling() {
		return isFalling;
	}

	public void setFalling(boolean isFalling) {
		this.isFalling = isFalling;
	}

	public boolean isStrafingLeft() {
		return isStrafingLeft;
	}

	public void setStrafingLeft(boolean isStrafingLeft) {
		this.isStrafingLeft = isStrafingLeft;
	}

	public boolean isStrafingRight() {
		return isStrafingRight;
	}

	public void setStrafingRight(boolean isStrafingRight) {
		this.isStrafingRight = isStrafingRight;
	}

	public boolean isMovingForward() {
		return isMovingForward;
	}

	public void setMovingForward(boolean isMovingForward) {
		this.isMovingForward = isMovingForward;
	}

	public boolean isMovingBackward() {
		return isMovingBackward;
	}

	public void setMovingBackward(boolean isMovingBackward) {
		this.isMovingBackward = isMovingBackward;
	}

	public float getDelta() {
		return delta;
	}

	public void setDelta(float delta) {
		this.delta = delta;
	}

	// Frames
	private Skeleton walkLeftLegForward = new Skeleton();
	private Skeleton walkRightLegForward = new Skeleton();
	private Skeleton standLeftLegApart = new Skeleton();
	private Skeleton standRightLegApart = new Skeleton();

	private void generateFrames() {
		// walkLeftLegForward.leftLeg.top.setYaw(-5);
		walkLeftLegForward.skirtFrontLeft.setPitch(30);
		walkLeftLegForward.skirtFrontLeft.setYaw(30);
		walkLeftLegForward.skirtFrontRight.setPitch(-5);
		walkRightLegForward.skirtFrontRight.setPitch(30);
		walkRightLegForward.skirtFrontRight.setYaw(-30);
		walkRightLegForward.skirtFrontLeft.setPitch(-5);
		// walkLeftLegForward.skirtFrontLeft.setYaw(40);
		// walkLeftLegForward.skirtFrontLeft.setRoll(40);
		// walkLeftLegForward.rightLeg.top.setYaw(5);
		walkLeftLegForward.leftLeg.top.setPitch(35);
		walkLeftLegForward.leftLeg.knee.setPitch(-30);
		walkLeftLegForward.leftLeg.foot.setPitch(25);
		walkLeftLegForward.leftLeg.top.setYaw(-10);
		walkLeftLegForward.leftLeg.knee.setYaw(-10);
		walkLeftLegForward.rightLeg.top.setYaw(-10);
		walkLeftLegForward.rightLeg.knee.setYaw(-10);
		walkLeftLegForward.rightLeg.top.setPitch(-10);
		walkLeftLegForward.rightLeg.knee.setPitch(-15);
		walkLeftLegForward.rightLeg.foot.setPitch(8);
		/*walkLeftLegForward.leftLeg.top.setPitch(59);
		walkLeftLegForward.leftLeg.top.setYaw(-9);
		walkLeftLegForward.leftLeg.top.setRoll(10);
		walkLeftLegForward.leftLeg.knee.setPitch(-100);
		walkLeftLegForward.leftLeg.knee.setYaw(-9);*/
		walkRightLegForward.leftLeg.copyOrientation(walkLeftLegForward.rightLeg);
		walkRightLegForward.rightLeg.copyOrientation(walkLeftLegForward.leftLeg);
		walkRightLegForward.rightLeg.top.setYaw(10);
		walkRightLegForward.rightLeg.knee.setYaw(10);
		walkRightLegForward.leftLeg.top.setYaw(10);
		walkRightLegForward.leftLeg.knee.setYaw(10);

		walkLeftLegForward.waist.setYaw(18);
		walkRightLegForward.waist.setYaw(-18);
		walkLeftLegForward.shoulders.setYaw(-10);
		walkLeftLegForward.shoulders.setRoll(-4);
		walkRightLegForward.shoulders.setYaw(10);
		walkRightLegForward.shoulders.setRoll(4);
		// ARMS
		walkLeftLegForward.rightArm.top.setPitch(15);
		walkLeftLegForward.rightArm.top.setRoll(-7);
		walkLeftLegForward.rightArm.elbow.setYaw(-90);
		walkLeftLegForward.rightArm.wrist.setYaw(0);
		walkLeftLegForward.rightArm.wrist.setPitch(0);
		walkLeftLegForward.rightArm.wrist.setRoll(50);
		walkLeftLegForward.leftArm.wrist.setRoll(30);
		walkLeftLegForward.leftArm.top.setPitch(-14);
		walkLeftLegForward.leftArm.top.setYaw(-2);
		walkLeftLegForward.leftArm.top.setRoll(17);
		// walkRightLegForward.leftArm.elbow.setRoll(90);
		walkRightLegForward.leftArm.top.setPitch(15);
		walkRightLegForward.leftArm.top.setRoll(7);
		walkRightLegForward.leftArm.elbow.setYaw(90);
		walkRightLegForward.leftArm.wrist.setYaw(0);
		walkRightLegForward.leftArm.wrist.setPitch(0);
		walkRightLegForward.leftArm.wrist.setRoll(-50);
		walkRightLegForward.rightArm.wrist.setRoll(-30);
		walkRightLegForward.rightArm.top.setPitch(-14);
		walkRightLegForward.rightArm.top.setYaw(2);
		walkRightLegForward.rightArm.top.setRoll(-17);

		standLeftLegApart.leftLeg.top.setRoll(-10);
		standLeftLegApart.leftLeg.foot.setYaw(-10);
		standRightLegApart.rightLeg.top.setRoll(10);
		standRightLegApart.rightLeg.foot.setYaw(10);
	}

	private boolean areLegsApart = false;
	private boolean areLegsTogether = true;
	private boolean isLeftLegAhead = false;
	private boolean isLegReturning = false;
	private float walkStepTimer = 0;
	private float walkStepTime = 0.25f;
	private float standTimer = 0;
	private float standTime = 3f;
	private boolean isStandLeftLegApart = true;

	private void standTick() {
		standTimer += getDelta();
		if (standTimer >= standTime) {
			// Switch legs
			standTimer -= standTime;
			isStandLeftLegApart = !isStandLeftLegApart;
		}
	}

	private void setLegsStanding() {
		if (isStandLeftLegApart) {
			finalOrientation.leftLeg.copyOrientation(standLeftLegApart.leftLeg);
			finalOrientation.rightLeg.copyOrientation(standLeftLegApart.rightLeg);
		} else {
			finalOrientation.leftLeg.copyOrientation(standRightLegApart.leftLeg);
			finalOrientation.rightLeg.copyOrientation(standRightLegApart.rightLeg);
		}
	}

	private void walkTick() {
		walkStepTimer += getDelta();
		if (walkStepTimer >= walkStepTime) {
			// Switch step, legs
			walkStepTimer -= walkStepTime;
			if (areLegsTogether) {
				areLegsTogether = false;
				areLegsApart = true;
				isLeftLegAhead = !isLeftLegAhead;
				isLegReturning = false;
			} else {
				areLegsTogether = true;
				areLegsApart = false;
				isLegReturning = true;
			}
		}
	}

	private void setLegsWalking() {
		if (isLeftLegAhead) {
			finalOrientation.leftLeg.copyOrientation(walkLeftLegForward.leftLeg);
			finalOrientation.rightLeg.copyOrientation(walkLeftLegForward.rightLeg);
			finalOrientation.waist.copyOrientation(walkLeftLegForward.waist);
			finalOrientation.shoulders.copyOrientation(walkLeftLegForward.shoulders);
			finalOrientation.leftArm.copyOrientation(walkLeftLegForward.leftArm);
			finalOrientation.rightArm.copyOrientation(walkLeftLegForward.rightArm);
			finalOrientation.skirtFrontLeft.copyOrientation(walkLeftLegForward.skirtFrontLeft);
			finalOrientation.skirtFrontRight.copyOrientation(walkLeftLegForward.skirtFrontRight);
		} else {
			finalOrientation.leftLeg.copyOrientation(walkRightLegForward.leftLeg);
			finalOrientation.rightLeg.copyOrientation(walkRightLegForward.rightLeg);
			finalOrientation.waist.copyOrientation(walkRightLegForward.waist);
			finalOrientation.shoulders.copyOrientation(walkRightLegForward.shoulders);
			finalOrientation.leftArm.copyOrientation(walkRightLegForward.leftArm);
			finalOrientation.rightArm.copyOrientation(walkRightLegForward.rightArm);
			finalOrientation.skirtFrontLeft.copyOrientation(walkRightLegForward.skirtFrontLeft);
			finalOrientation.skirtFrontRight.copyOrientation(walkRightLegForward.skirtFrontRight);
		}
	}

	private Randomizer leftLegRandomizer = new Randomizer(0.5f, 7f);
	private Randomizer rightLegRandomizer = new Randomizer(0.5f, 7f);

	private void randomize() {
		rightLegRandomizer.setPaused(!leftLegRandomizer.isPaused());
		rightLegRandomizer.tick(delta);
		leftLegRandomizer.tick(delta);
		leftLegRandomizer.randomize(finalOrientation.leftLeg);
		rightLegRandomizer.randomize(finalOrientation.rightLeg);
	}

	private void fixClothOrientation() {

	}

	public void tick() {
		if (!demoMode) {
			finalOrientation.reset();
		}
		if (!demoMode) {
			// deltaOrientation.reset();
			if (isJumping()) {
				// air motion
				while (!areLegsApart) {
					walkTick();
					setLegsWalking();
				}
			} else {
				// on-ground motion
				if (isWalking()) {
					walkTick();
					if (isCrawling()) {

					} else if (isCrouching()) {

					} else {
						// standing
						setLegsWalking();
					}
				} else {
					// make legs together, switch forward leg
					while (!areLegsTogether) {
						walkTick();
						standTick();
						setLegsWalking();
						setLegsStanding();
					}
				}
			}
			randomize();
			// System.err.println("[leg] " +
			// finalOrientation.leftLeg.top.getPitch());
			fixClothOrientation();
			deltaOrientation.leftArm.top.setRollCorrection(-50f);
			deltaOrientation.rightArm.top.setRollCorrection(-deltaOrientation.leftArm.top.getRollCorrection());
			deltaOrientation.setSpeed(4f);
			deltaOrientation.skirtFrontLeft.setSpeed(1f);
			deltaOrientation.skirtFrontRight.setSpeed(1f);
			if (isLeftLegAhead && !isLegReturning) {
				deltaOrientation.skirtFrontLeft.setSpeed(4f);
			}
			if (!isLeftLegAhead && !isLegReturning) {
				deltaOrientation.skirtFrontRight.setSpeed(4f);
			}
		}else{
			deltaOrientation.setSpeed(4f);
		}
		deltaOrientation.setDelta(currentOrientation, finalOrientation, getDelta());
		// deltaOrientation.copyOrientation(finalOrientation);
		currentOrientation.copyOrientation(deltaOrientation);
	}

	private boolean demoMode = false;

	public void setPause(boolean boneMode) {
		demoMode = boneMode;
	}

}
