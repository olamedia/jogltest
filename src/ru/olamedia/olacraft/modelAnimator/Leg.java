package ru.olamedia.olacraft.modelAnimator;

public class Leg extends BoneSet {

	public Bone top;
	public Bone knee;
	public Bone foot;

	public Leg() {
		super(3);
		top = new Bone();
		knee = new Bone();
		foot = new Bone();
		setChild(0, top);
		setChild(1, knee);
		setChild(2, foot);
		top.setName("Top");
		knee.setName("Knee");
		foot.setName("Foot");
	}

	public void setNamePrefix(String prefix) {
		top.setName(prefix + top.getName());
		knee.setName(prefix + knee.getName());
		foot.setName(prefix + foot.getName());
	}
}
