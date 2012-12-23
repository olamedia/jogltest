package ru.olamedia.olacraft.modelAnimator;

public class Skeleton extends BoneSet {

	public Bone neck; // 首 - шея
	public Bone shoulders; // 上半身 - верхняя часть тела
	public Bone waist; // 下半身 - нижняя часть корпуса
	public Bone skirtFrontLeft;
	public Bone skirtFrontRight;

	public Arm leftArm;
	public Arm rightArm;

	public Leg leftLeg;
	public Leg rightLeg;

	public Skeleton() {
		super(9);
		neck = new Bone();
		neck.setName("Neck");
		waist = new Bone();
		waist.setName("Waist");
		shoulders = new Bone();
		shoulders.setName("Shoulders");
		skirtFrontLeft = new Bone();
		skirtFrontLeft.setName("Skirt Front Left");
		skirtFrontRight = new Bone();
		skirtFrontRight.setName("Skirt Front Right");
		leftArm = new Arm();
		rightArm = new Arm();
		leftLeg = new Leg();
		rightLeg = new Leg();
		leftArm.setNamePrefix("Left arm ");
		rightArm.setNamePrefix("Right arm ");
		leftLeg.setNamePrefix("Left leg ");
		rightLeg.setNamePrefix("Right leg ");
		setChild(0, neck);
		setChild(1, waist);
		setChild(2, shoulders);
		setChild(3, skirtFrontLeft);
		setChild(4, skirtFrontRight);
		setChild(5, leftArm);
		setChild(6, rightArm);
		setChild(7, leftLeg);
		setChild(8, rightLeg);
	}
}
