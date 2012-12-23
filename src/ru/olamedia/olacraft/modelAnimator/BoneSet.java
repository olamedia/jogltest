package ru.olamedia.olacraft.modelAnimator;

import java.util.Iterator;
import java.util.Random;

public class BoneSet implements ISkeletonNode, Iterable<Bone> {

	private class BonesIterator implements Iterator<Bone> {
		private BoneSet set;
		private Iterator<Bone> childIterator = null;
		private int current = 0;

		public BonesIterator(BoneSet boneSet) {
			set = boneSet;
		}

		@Override
		public boolean hasNext() {
			if (null == childIterator) {
				return current < set.getChildrenCount();
			} else {
				return childIterator.hasNext() || current < set.getChildrenCount();
			}
		}

		private boolean nextIsChild;

		@Override
		public Bone next() {
			if (null == childIterator) {
				ISkeletonNode node = set.getChild(current);
				if (node instanceof Bone) {
					Bone b = (Bone) node;
					current++;
					return b;
				} else if (node instanceof BoneSet) {
					childIterator = ((BoneSet) node).iterator();
					current++;
				}
			}
			if (null != childIterator) {
				nextIsChild = childIterator.hasNext();
				if (nextIsChild) {
					Bone b = childIterator.next();
					return b;
				}
				if (!nextIsChild) {
					childIterator = null;
				}
			}
			if (current < set.getChildrenCount()) {
				return next();
			}
			return null;
		}

		@Override
		public void remove() {

		}

	}

	public BoneSet(int bonesCount) {
		nodes = new ISkeletonNode[bonesCount];
		for (int i = 0; i < bonesCount; i++) {
			// bones[i] = new Bone();
		}
	}

	private ISkeletonNode[] nodes;
	private String name;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getChildrenCount() {
		return nodes.length;
	}

	public ISkeletonNode getChild(int i) {
		return nodes[i];
	}

	@Override
	public void setSpeed(float speed) {
		for (ISkeletonNode b : nodes) {
			b.setSpeed(speed);
		}
	}

	@Override
	public void copyOrientation(ISkeletonNode bs) {
		for (int i = 0; i < nodes.length; i++) {
			nodes[i].copyOrientation(bs.getChild(i));
		}
	}

	@Override
	public void setDelta(ISkeletonNode first, ISkeletonNode second, float delta) {
		for (int i = 0; i < nodes.length; i++) {
			nodes[i].setDelta(first.getChild(i), second.getChild(i), delta);
		}
	}

	@Override
	public void reset() {
		for (int i = 0; i < nodes.length; i++) {
			nodes[i].reset();
		}
	}

	@Override
	public void randomize(Random rand, Random prev, float delta) {
		for (int i = 0; i < nodes.length; i++) {
			nodes[i].randomize(rand, prev, delta);
		}
	}

	@Override
	public void setChild(int i, ISkeletonNode node) {
		nodes[i] = node;
	}

	@Override
	public Iterator<Bone> iterator() {
		return new BonesIterator(this);
	}

	public int getBoneCount() {
		int n = 0;
		Iterator<Bone> it = iterator();
		while (it.hasNext()) {
			it.next();
			n++;
		}
		return n;
	}

	public Bone getBone(int i) {
		int j = 0;
		Iterator<Bone> it = iterator();
		Bone b = null;
		while (it.hasNext() && j <= i) {
			b = it.next();
			j++;
		}
		return b;
	}
}
