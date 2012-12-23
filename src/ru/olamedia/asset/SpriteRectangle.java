package ru.olamedia.asset;

public class SpriteRectangle {
	public SpriteOffset topLeft;
	public SpriteOffset bottomRight;

	public SpriteRectangle(float left, float top, float right, float bottom) {
		topLeft = new SpriteOffset(left, top);
		bottomRight = new SpriteOffset(right, bottom);
	}
}
