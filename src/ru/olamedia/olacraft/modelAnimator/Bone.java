package ru.olamedia.olacraft.modelAnimator;

import java.util.Random;

public class Bone implements ISkeletonNode {
	private String name;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	private float speed = 1f;
	private float yaw;
	private float pitch;
	private float roll;
	private float yawCorrection = 0;
	private float pitchCorrection = 0;
	private float rollCorrection = 0;
	@SuppressWarnings("unused")
	private boolean isAnglesModified = false;
	private boolean isMatrixModified = false;

	@Override
	public int getChildrenCount() {
		return 0;
	}

	@Override
	public Bone getChild(int i) {
		return null;
	}

	private void updateAngles() {
		if (isMatrixModified) {

		}
	}

	/*
	 * private void updateMatrix() {
	 * if (isAnglesModified) {
	 * 
	 * }
	 * }
	 */

	public float getSpeed() {
		return speed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.olamedia.olacraft.modelAnimator.ISkeletonNode#setSpeed(float)
	 */
	@Override
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.olamedia.olacraft.modelAnimator.ISkeletonNode#copyOrientation(ru.olamedia
	 * .olacraft.modelAnimator.Bone)
	 */
	@Override
	public void copyOrientation(ISkeletonNode node) {
		final Bone b = (Bone) node;
		setPitch(b.getPitch());
		setPitchCorrection(b.getPitchCorrection());
		setYaw(b.getYaw());
		setYawCorrection(b.getYawCorrection());
		setRoll(b.getRoll());
		setRollCorrection(b.getRollCorrection());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.olamedia.olacraft.modelAnimator.ISkeletonNode#setDelta(ru.olamedia
	 * .olacraft.modelAnimator.Bone, ru.olamedia.olacraft.modelAnimator.Bone,
	 * float)
	 */
	@Override
	public void setDelta(ISkeletonNode firstNode, ISkeletonNode secondNode, float delta) {
		final Bone first = (Bone) firstNode;
		final Bone second = (Bone) secondNode;
		final float dPitch = second.getPitch() - first.getPitch();
		setPitch(first.getPitch() + dPitch * getSpeed() * delta);
		final float dYaw = second.getYaw() - first.getYaw();
		setYaw(first.getYaw() + dYaw * getSpeed() * delta);
		final float dRoll = second.getRoll() - first.getRoll();
		setRoll(first.getRoll() + dRoll * getSpeed() * delta);
	}

	public float getYaw() {
		updateAngles();
		return yaw;
	}

	public float getCorrectedYaw() {
		return getYawCorrection() + getYaw();
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
		isAnglesModified = true;
	}

	public float getPitch() {
		updateAngles();
		return pitch;
	}

	public float getCorrectedPitch() {
		return getPitchCorrection() + getPitch();
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
		isAnglesModified = true;
	}

	public float getRoll() {
		updateAngles();
		return roll;
	}

	public float getCorrectedRoll() {
		return getRollCorrection() + getRoll();
	}

	public void setRoll(float roll) {
		this.roll = roll;
		isAnglesModified = true;
	}

	private final float yawRand = 5f;
	private final float pitchRand = 1f;
	private final float rollRand = 5f;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.olamedia.olacraft.modelAnimator.ISkeletonNode#reset()
	 */
	@Override
	public void reset() {
		yaw = 0;
		pitch = 0;
		roll = 0;
	}

	private float nextFloatDelta(Random rand, Random prev, float delta) {
		final float first = prev.nextFloat();
		final float second = rand.nextFloat();
		return first + (second - first) * delta;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.olamedia.olacraft.modelAnimator.ISkeletonNode#randomize(java.util.
	 * Random, java.util.Random, float)
	 */
	@Override
	public void randomize(Random rand, Random prev, float delta) {
		// delta = 1;
		setYaw(yaw + (nextFloatDelta(rand, prev, delta) - 0.5f) * yawRand);
		setPitch(pitch + (nextFloatDelta(rand, prev, delta) - 0.5f) * pitchRand);
		setRoll(roll + (nextFloatDelta(rand, prev, delta) - 0.5f) * rollRand);
	}

	public float getYawCorrection() {
		return yawCorrection;
	}

	public void setYawCorrection(float yawCorrection) {
		this.yawCorrection = yawCorrection;
	}

	public float getPitchCorrection() {
		return pitchCorrection;
	}

	public void setPitchCorrection(float pitchCorrection) {
		this.pitchCorrection = pitchCorrection;
	}

	public float getRollCorrection() {
		return rollCorrection;
	}

	public void setRollCorrection(float rollCorrection) {
		this.rollCorrection = rollCorrection;
	}

	@Override
	public void setChild(int i, ISkeletonNode b) {

	}
}
