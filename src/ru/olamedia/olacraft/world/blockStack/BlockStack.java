package ru.olamedia.olacraft.world.blockStack;

import ru.olamedia.olacraft.world.block.Block;

public class BlockStack {
	public Block block;
	public int count;

	public BlockStack(Block block, int count) {
		this.block = block;
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
		}
		return new BlockStack(block, c);
	}

	public BlockStack getAll() {
		int c = count;
		count = 0;
		return new BlockStack(block, c);
	}

	/**
	 * 
	 * @return Remaining BlockStack
	 */
	public BlockStack putStack(BlockStack stack) {

		if (block.getType() == stack.block.getType()) {
			int max = block.getType().getMaxStack();
			// Stack
			int total = count + stack.count;
			if (total < max) {
				count = total;
				return new BlockStack(block, 0);
			} else {
				BlockStack remains = new BlockStack(block, total - max);
				count = max;
				return remains;
			}
		} else {
			// Replace
			BlockStack remains = new BlockStack(block, count);
			block = stack.block;
			count = stack.count;
			return remains;
		}
	}

}
