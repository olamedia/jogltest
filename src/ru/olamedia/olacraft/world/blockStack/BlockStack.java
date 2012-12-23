package ru.olamedia.olacraft.world.blockStack;

import ru.olamedia.olacraft.world.blockTypes.AbstractBlockType;
import ru.olamedia.olacraft.world.blockTypes.EmptyBlockType;

public class BlockStack {
	public AbstractBlockType type;
	public int count = 0;
	public boolean hidden = false; // do not render

	public BlockStack(AbstractBlockType type, int count) {
		this.type = type;
		this.count = count;
	}

	public BlockStack get() {
		return get(1);
	}

	public BlockStack get(int getcount) {
		int c = 0;
		if (count >= getcount) {
			c = getcount;
			count -= getcount;
		} else {
			c = count;
			count = 0;
			type = new EmptyBlockType();
		}
		return new BlockStack(type, c);
	}

	public BlockStack getAll() {
		int c = count;
		count = 0;
		return new BlockStack(type, c);
	}

	/**
	 * 
	 */
	public void putStack(BlockStack stack) {
		if (!type.getClass().getName().equals(stack.type.getClass().getName())) {
			try {
				type = stack.type.getClass().newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} // replace
		}
		int max = type.getMaxStack();
		// Stack
		int total = count + stack.count;
		if (total < max) {
			count = total;
			stack.count = 0;
			stack.type = new EmptyBlockType();
		} else {
			count = max;
			stack.count = total - max;
		}
	}

}
