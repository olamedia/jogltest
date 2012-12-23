package ru.olamedia.olacraft.world.fluid;

import java.util.HashMap;

public class FluidLevel {
	public HashMap<Integer, FluidLine> lines = new HashMap<Integer, FluidLine>();
	public float fluidLevel = 1f;
	public int size = 0;
}
