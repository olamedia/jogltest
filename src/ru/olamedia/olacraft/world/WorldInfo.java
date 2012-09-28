package ru.olamedia.olacraft.world;

import java.io.Serializable;

public class WorldInfo implements Serializable {
	private static final long serialVersionUID = -3669317489158639456L;
	public String name = "world";
	public int minHeight = -128;
	public int maxHeight = 127;
	public float gravity = 9.81f;
}
