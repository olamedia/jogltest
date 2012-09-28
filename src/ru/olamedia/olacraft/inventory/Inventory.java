package ru.olamedia.olacraft.inventory;

import ru.olamedia.olacraft.world.block.Block;
import ru.olamedia.olacraft.world.blockStack.BlockStack;

public class Inventory {
	public static int BIND_NUM = 10;
	public BlockStack[] binded = new BlockStack[BIND_NUM];
	public BlockStack selected;
	public int selectedId;
	private boolean isInventoryGUIOpen = false;

	public Inventory() {
	}

	public void init() {
		for (int i = 0; i < BIND_NUM; i++) {
			Block block = new Block(x + bindedWrapperPadding + i * BLOCK_SIZE + bindedSpacing * i, y
					+ bindedWrapperPadding + 0, 0, "gravel");
			binded[i] = new BlockStack(block, 64);
		}
		binded[0].block.setName("dirt");
		binded[1].block.setName("grass");
		binded[2].block.setName("water");
		binded[3].block.setName("wood");
		binded[4].block.setName("asphalt");
		binded[5].block.setName("torch");
		binded[8].block.setName("grass");
		binded[9].block.setName("dirt");
		frame = new Frame(0, 0, 0, BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
		select(0);
	}

	public void onKeyDown() {
		// System.out.println("keyName: " + api.keyboard.keyName);
		int key = Keyboard.getEventKey();
		if (key == Keyboard.KEY_1) {
			select(0);
		}
		if (key == Keyboard.KEY_2) {
			select(1);
		}
		if (key == Keyboard.KEY_3) {
			select(2);
		}
		if (key == Keyboard.KEY_4) {
			select(3);
		}
		if (key == Keyboard.KEY_5) {
			select(4);
		}
		if (key == Keyboard.KEY_6) {
			select(5);
		}
		if (key == Keyboard.KEY_7) {
			select(6);
		}
		if (key == Keyboard.KEY_8) {
			select(7);
		}
		if (key == Keyboard.KEY_9) {
			select(8);
		}
		if (key == Keyboard.KEY_0) {
			select(9);
		}
		if (key == Keyboard.KEY_E) {
			isInventoryGUIOpen = !isInventoryGUIOpen;
		}

	}

	public void select(int i) {
		selected = binded[i];
	}
}
