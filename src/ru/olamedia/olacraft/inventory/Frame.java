package ru.olamedia.olacraft.inventory;

import org.olamedia.olacraft.draw.DrawInterface;
import org.olamedia.olacraft.entity.AbstractEntity;

import static org.olamedia.olacraft.blocks.Block.BLOCK_SIZE;
import org.olamedia.olacraft.util.CommonApi;

public class Frame extends AbstractEntity implements DrawInterface {
	public Frame(double x, double y, double z, double width, double height,
			double depth) {
		super(x, y, z, width, height, depth);
	}

	public CommonApi api = CommonApi.instance;

	@Override
	public void update(double delta) {

	}

	@Override
	public void draw() {
		api.draw.texRecti("inventory_frame", x, y, BLOCK_SIZE, BLOCK_SIZE);
		return;
		// Texture t = api.texture.get("inventory_frame");
		// t.bind();
		// glColor3f(1, 1, 1);
		// glLoadIdentity();
		// glTranslated(x, y, 0);
		// glBegin(GL_QUADS);
		// glTexCoord2f(0, 0);
		// glVertex2f(0, 0); // Upper-left
		// glTexCoord2f(1, 0);
		// glVertex2f(BLOCK_SIZE, 0); // Upper-right
		// glTexCoord2f(1, 1);
		// glVertex2f(BLOCK_SIZE, BLOCK_SIZE); // Bottom-right
		// glTexCoord2f(0, 1);
		// glVertex2f(0, BLOCK_SIZE); // Bottom-left
		// glEnd();
		// glLoadIdentity();
	}

}
