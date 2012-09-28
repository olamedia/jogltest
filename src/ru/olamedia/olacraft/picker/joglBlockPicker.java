package ru.olamedia.olacraft.picker;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import ru.olamedia.olacraft.world.block.Block;
import ru.olamedia.olacraft.world.provider.ChunkProvider;

public class joglBlockPicker {
	ChunkProvider provider;

	public void setChunkProvider(ChunkProvider provider) {
		this.provider = provider;
	}

	public Block pickBlock(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		FloatBuffer projMatrix = FloatBuffer.allocate(16);
		FloatBuffer modelMatrix = FloatBuffer.allocate(16);
		gl.glGetFloatv(GL2.GL_PROJECTION, projMatrix);
		gl.glGetFloatv(GL2.GL_MODELVIEW, modelMatrix);

		return null;
	}
}
