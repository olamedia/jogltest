package ru.olamedia.geom;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLContext;
import javax.media.opengl.fixedfunc.GLPointerFunc;

import com.jogamp.opengl.util.GLArrayDataServer;
import com.jogamp.opengl.util.texture.Texture;

public class Mesh {
	private FloatBuffer buffer;
	private float[] data;
	private int ptr;
	private int vertexCount;
	private int vertexPtr;
	private float xOffset;
	private float yOffset;
	private float zOffset;

	private boolean useColor;
	private float red = 1f;
	private float green = 1f;
	private float blue = 1f;
	private float alpha = 1f;

	private boolean useTexture;
	private float GLTexture;
	private float u = 0f;
	private float v = 0f;
	@SuppressWarnings("unused")
	private float uFactor = 1f;
	@SuppressWarnings("unused")
	private float vFactor = 1f;

	private boolean wireframe = false;

	private static int vertexSize = 10;

	private GLArrayDataServer interleaved;
	private HashMap<Integer, Integer> materials = new HashMap<Integer, Integer>();
	private HashMap<Integer, GLArrayDataServer> arrays = new HashMap<Integer, GLArrayDataServer>();

	public void setTexture(Texture tex) {
		if (null != tex) {
			setTextureSize(tex.getWidth(), tex.getHeight());
			setGLTexture(tex.getTextureObject(null));
		}
	}

	public void setGLTexture(int tex) {
		this.GLTexture = tex;
	}

	@SuppressWarnings("unused")
	private static boolean useVbo = true;

	private static boolean useQuad = true;
	private static boolean useDisplayList = false;
	private DisplayList DL;

	public Mesh(int size) {
		vertexCount = 0;
		data = new float[size * vertexSize];
		// data = new float[size * vertexSize];
		vertexPtr = 0;
		useTexture = false;
		useColor = false;
	}

	private static FloatBuffer generateFloatBuffer(int size) {
		return ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	/**
	 * Reallocates an array with a new size, and copies the contents of the old
	 * array to the new array.
	 * 
	 * @param oldArray
	 *            the old array, to be reallocated.
	 * @param newSize
	 *            the new array size.
	 * @return A new array with the same contents.
	 */
	private static float[] resizeArray(float[] oldArray, int newSize) {
		int oldSize = oldArray.length;
		float[] newArray = new float[newSize];
		int preserveLength = Math.min(oldSize, newSize);
		if (preserveLength > 0)
			System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
		return newArray;
	}

	public void compact() {
		// data = resizeArray(data, vertexCount * vertexSize);
		int size = vertexCount * vertexSize;
		buffer = generateFloatBuffer(size);
		buffer.position(0);
		buffer.put(data, 0, size);
		data = null;
		// calc vertex count for each material
		for (int n = 0; n < vertexCount; n++) {
			int tex = (int) buffer.get(vertexSize * n + 9);
			if (!materials.containsKey(tex)) {
				materials.put(tex, 1);
			} else {
				materials.put(tex, materials.get(tex) + 1);
			}
		}
		for (Integer m : materials.keySet()) {
			int matVertCount = materials.get(m);
			final GLArrayDataServer interleaved = GLArrayDataServer.createFixedInterleaved(9, GL2.GL_FLOAT, false,
					matVertCount, GL.GL_STATIC_DRAW);
			interleaved.addFixedSubArray(GLPointerFunc.GL_VERTEX_ARRAY, 3, GL.GL_ARRAY_BUFFER);
			interleaved.addFixedSubArray(GLPointerFunc.GL_COLOR_ARRAY, 4, GL.GL_ARRAY_BUFFER);
			interleaved.addFixedSubArray(GLPointerFunc.GL_TEXTURE_COORD_ARRAY, 2, GL.GL_ARRAY_BUFFER);
			arrays.put(m, interleaved);
		}
		for (int n = 0; n < vertexCount; n++) {
			int m = (int) buffer.get(vertexSize * n + 9);
			final GLArrayDataServer interleaved = arrays.get(m);
			interleaved.putf(buffer.get(vertexSize * n + 0));
			interleaved.putf(buffer.get(vertexSize * n + 1));
			interleaved.putf(buffer.get(vertexSize * n + 2));
			interleaved.putf(buffer.get(vertexSize * n + 3));
			interleaved.putf(buffer.get(vertexSize * n + 4));
			interleaved.putf(buffer.get(vertexSize * n + 5));
			interleaved.putf(buffer.get(vertexSize * n + 6));
			interleaved.putf(buffer.get(vertexSize * n + 7));
			interleaved.putf(buffer.get(vertexSize * n + 8));
		}
		for (Integer m : materials.keySet()) {
			final GLArrayDataServer interleaved = arrays.get(m);
			interleaved.seal(true);
		}
		// interleaved.put(buffer);
		if (vertexCount > 0) {
			// System.out.println(interleaved);
		}
		/*
		 * buffer.position(0);
		 * buffer.limit(size * vertexSize);
		 * buffer.position(0);
		 * buffer.limit(vertexCount * vertexSize);
		 * buffer.compact();
		 * buffer.position(0);
		 */
	}

	public void endMesh() {
		compact();
	}

	public void setTranslation(float x, float y, float z) {
		xOffset = x;
		yOffset = y;
		zOffset = z;
	}

	public void setPoint3f(float x, float y, float z) {
		ptr = vertexPtr * vertexSize;
		data[ptr + 0] = x + xOffset;
		// buffer.put(x + xOffset);
		data[ptr + 1] = y + yOffset;
		// buffer.put(y + yOffset);
		data[ptr + 2] = z + zOffset;
		// buffer.put(z + zOffset);
		if (useColor) {
			data[ptr + 3] = red;
			// buffer.put(red);
			data[ptr + 4] = green;
			// buffer.put(green);
			data[ptr + 5] = blue;
			// buffer.put(blue);
			data[ptr + 6] = alpha;
			// buffer.put(alpha);
		} else {
			// buffer.put(1f);
			// buffer.put(1f);
			// buffer.put(1f);
			// buffer.put(1f);
		}
		if (useTexture) {
			// buffer.put(GLTexture);
			data[ptr + 7] = u;// * uFactor;
			// buffer.put(u);
			data[ptr + 8] = v;// * vFactor;
			data[ptr + 9] = GLTexture;
			// buffer.put(v);
		} else {
			// buffer.put(0f);
			// buffer.put(0f);
			// buffer.put(0f);
		}
		vertexPtr++;
		vertexCount++;
	}

	public void setColor4f(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public void useColor() {
		useColor = true;
	}

	public void useTexture() {
		useTexture = true;
	}

	public void setUV(float u, float v) {
		this.u = u;
		this.v = v;
	}

	public void setTextureSize(float uSize, float vSize) {
		this.uFactor = uSize;
		this.vFactor = vSize;
	}

	public boolean joglIsVBOAvailable(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		return gl.isFunctionAvailable("glGenBuffers") && gl.isFunctionAvailable("glBindBuffer")
				&& gl.isFunctionAvailable("glBufferData") && gl.isFunctionAvailable("glDeleteBuffers");
	}

	public void joglCreateVBO(GLAutoDrawable drawable) {
		@SuppressWarnings("unused")
		GL2 gl = drawable.getGL().getGL2();
		// gl.glInterleavedArrays(GL2.GL_T2F_C4F_N3F_V3F, stride, pointer)

	}

	public void joglRender() {
		if (vertexCount < 1) {
			return;
		}
		GL glx = GLContext.getCurrentGL();
		if (useVbo) {
			//GL2ES2 gl = glx.getGL2ES2();
			GL2 gl = glx.getGL2();
			for (Integer m : materials.keySet()) {
				gl.glEnable(GL.GL_TEXTURE_2D);
				gl.glBindTexture(GL.GL_TEXTURE_2D, (int) m);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
				arrays.get(m).enableBuffer(gl, true);
				gl.glEnable(GL.GL_CULL_FACE);
				gl.glEnable(GL.GL_DEPTH_TEST);
				//gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
				gl.glDrawArrays(GL2.GL_QUADS, 0, arrays.get(m).getElementCount());
				arrays.get(m).enableBuffer(gl, false);
				//System.exit(0);
			}
		} else {
			GL2 gl = glx.getGL2();
			if (useDisplayList) {
				gl.glEnable(GL2.GL_CULL_FACE);
				gl.glEnable(GL2.GL_DEPTH_TEST);
			}
			if (wireframe) {
				// Set wireframe mode
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
			} else {
				gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);
			}
			if (useDisplayList) {
				if (DL == null) {
					DL = new DisplayList(glx);
				} else {
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
					DL.render();
					return;
				}
				DL.begin();
			}
			gl.glPushAttrib(GL2.GL_ENABLE_BIT | GL2.GL_POLYGON_BIT | GL2.GL_TEXTURE_BIT);
			if (useTexture) {
				gl.glEnable(GL.GL_TEXTURE_2D);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
			}
			for (int quadI = 0; quadI < vertexCount / 4; quadI++) {
				ptr = (quadI * 4 + 0) * vertexSize;
				if (useTexture) {
					gl.glBindTexture(GL.GL_TEXTURE_2D, (int) buffer.get(ptr + 9));
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
					// gl.glDisable(GL.GL_BLEND);
					// gl.glTexParameterf(GL.GL_TEXTURE_2D,
					// GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, 0);
				}
				if (useQuad) {
					gl.glBegin(GL2.GL_QUADS);
				} else {
					gl.glBegin(GL2.GL_TRIANGLE_STRIP);
				}
				{
					for (int i = 0; i < 4; i++) {
						int k = (useQuad) ? i : ((i == 2) ? 3 : (i == 3 ? 2 : i));
						ptr = (quadI * 4 + k) * vertexSize;
						if (useColor) {
							gl.glColor4f(buffer.get(ptr + 3), buffer.get(ptr + 4), buffer.get(ptr + 5),
									buffer.get(ptr + 6));
						}
						if (useTexture) {
							float u = buffer.get(ptr + 7);
							float v = buffer.get(ptr + 8);
							gl.glTexCoord2f(u, v);
						}
						gl.glVertex3f(buffer.get(ptr), buffer.get(ptr + 1), buffer.get(ptr + 2));
					}
				}
				gl.glEnd();
			}
			gl.glPopAttrib();
			if (useDisplayList) {
				DL.end();
				DL.render();
			}
		}
	}

	public int getVertexCount() {
		return vertexCount;
	}
}
