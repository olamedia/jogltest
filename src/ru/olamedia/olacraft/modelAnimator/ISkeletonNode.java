package ru.olamedia.olacraft.modelAnimator;

import java.util.Random;

public interface ISkeletonNode {
	public void setName(String name);

	public String getName();

	public ISkeletonNode getChild(int i);

	public void setChild(int i, ISkeletonNode b);

	public int getChildrenCount();

	public abstract void setSpeed(float speed);

	public abstract void copyOrientation(ISkeletonNode b);

	public abstract void setDelta(ISkeletonNode first, ISkeletonNode second, float delta);

	public abstract void reset();

	public abstract void randomize(Random rand, Random prev, float delta);

}