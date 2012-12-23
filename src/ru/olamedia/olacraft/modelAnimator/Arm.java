package ru.olamedia.olacraft.modelAnimator;

public class Arm extends BoneSet {
	// 左腕 - левая рука
	// 左ひじ - левый локоть
	// 左手首 - левое запястье
	// 左袖 - левый рукав
	public Bone top;
	public Bone elbow;
	public Bone wrist;

	public Arm() {
		super(3);
		top = new Bone();
		elbow = new Bone();
		wrist = new Bone();
		setChild(0, top);
		setChild(1, elbow);
		setChild(2, wrist);
	}

	public void setNamePrefix(String prefix) {
		top.setName(prefix + top.getName());
		elbow.setName(prefix + elbow.getName());
		wrist.setName(prefix + wrist.getName());
	}
}
