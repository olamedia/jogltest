package ru.olamedia.vbo;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLUniformData;

import com.jogamp.opengl.util.PMVMatrix;

public class VBO {
	private GLAutoDrawable drawable;
	private PMVMatrix pmvMatrix;
	private GLUniformData pmvMatrixUniform;
	private GLUniformData colorUniform;
	private int[] vboIda = new int[10];

	public VBO(GLAutoDrawable drawable) {
		this.drawable = drawable;
		GL2ES2 gl = drawable.getGL().getGL2ES2();
		// gl.glGenBuffersARB(1, vboIda, 0);
	}

	public void setDrawable(GLAutoDrawable drawable) {
		this.drawable = drawable;
	}

	public void render() {

	}
}
