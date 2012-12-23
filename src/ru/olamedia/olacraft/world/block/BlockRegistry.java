package ru.olamedia.olacraft.world.block;

import java.util.HashMap;

import ru.olamedia.olacraft.world.blockTypes.AbstractBlockType;

public class BlockRegistry {

	private HashMap<Integer, String> names = new HashMap<Integer, String>();
	private HashMap<Integer, Boolean> opaque = new HashMap<Integer, Boolean>();
	private HashMap<Integer, Boolean> canMoveThrough = new HashMap<Integer, Boolean>();
	private HashMap<Integer, AbstractBlockType> types = new HashMap<Integer, AbstractBlockType>();
	private HashMap<String, Integer> ids = new HashMap<String, Integer>();
	private int autoincrement = 0;

	private BlockRegistry worldRegistry;

	public BlockRegistry() {
		
	}

	public AbstractBlockType getBlockType(int id) {
		return types.get(id);
	}

	public String getBlockHumanId(int id) {
		return names.get(id);
	}

	public boolean isOpaque(int id) {
		return opaque.get(id);
	}

	public int registerBlockType(AbstractBlockType type) {
		autoincrement++;
		int id = autoincrement;
		String className = type.getClass().getName();
		names.put(id, className);
		types.put(id, type);
		opaque.put(id, type.isOpaque());
		canMoveThrough.put(id, type.canMoveThrough());
		ids.put(className, id);
		return autoincrement;
	}

	public BlockRegistry getWorldRegistry() {
		return worldRegistry;
	}

	public void setWorldRegistry(BlockRegistry worldRegistry) {
		this.worldRegistry = worldRegistry;
	}

	public int getBlockIdByClassName(String className) {
		return ids.get(className);
	}

	public boolean canMoveThrough(int id) {
		return canMoveThrough.get(id);
	}

}
