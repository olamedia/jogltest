package ru.olamedia.geom;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLContext;
import javax.media.opengl.fixedfunc.GLPointerFunc;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.GLArrayDataServer;

public class ImmModeMesh {
	private IndexedMeshBulder indexed = IndexedMeshBulder.instance;
	private boolean useIndexed = false;
	protected GLArrayDataServer interleaved;
	protected FloatBuffer buf;
	private int vertexCount = 0;
	protected int mode = GL2.GL_QUADS;
	protected boolean isGLSL = false;
	protected byte vertexComponent = 0;
	protected byte normalComponent = 0;
	protected byte colorComponent = 0;
	protected byte texCoordComponent = 0;
	protected float red = 1;
	protected float green = 1;
	protected float blue = 1;
	protected float alpha = 1;
	protected float offsetX = 0;
	protected float offsetY = 0;
	protected float offsetZ = 0;
	protected float x = 0;
	protected float y = 0;
	protected float z = 0;
	protected float nx = 0;
	protected float ny = 0;
	protected float nz = 0;
	protected float u = 0;
	protected float v = 0;
	protected boolean isFinished = false;
	protected boolean isServer = false;

	public void setGLSL(boolean glsl) {
		this.isGLSL = glsl;
	}

	public void makeServer() {
		if (null == interleaved) {
			if (isGLSL) {
				allocateGLSLBuffer();
			} else {
				allocateFixedBuffer();
			}
			if (null != buf) {
				buf.flip();
				interleaved.put(buf);
				buf.clear();
				buf = null;
			}
			interleaved.seal(true);
			isServer = true;
		}
	}

	public Buffer getBuffer() {
		if (!isServer) {
			return buf;
		}
		return interleaved.getBuffer();
	}

	public void put(ImmModeMesh mesh) {
		if (isServer) {
			final FloatBuffer b = (FloatBuffer) mesh.getBuffer();
			b.rewind();
			interleaved.put(b);
		} else {
			final FloatBuffer b = (FloatBuffer) mesh.getBuffer();
			b.rewind();
			if (null == buf) {
				allocateBuffer();
			}
			growBufferIfNecessary(b.remaining());
			buf.put(b);
		}
		vertexCount += mesh.getVertexCount();
	}

	public void destroy() {
		if (isServer) {
			interleaved.destroy(GLContext.getCurrentGL().getGL2ES2());
			interleaved = null;
		} else {
			if (null != buf) {
				buf.clear();
				buf = null;
			}
		}
	}

	public int getComponents() {
		return vertexComponent + normalComponent + colorComponent + texCoordComponent;
	}

	protected void allocateBuffer() {
		if (!isServer) {
			buf = Buffers.newDirectFloatBuffer(getComponents() * getVertexCount());
		} else {
			if (isGLSL) {
				allocateGLSLBuffer();
			} else {
				allocateFixedBuffer();
			}
		}
	}

	protected final void growBuffer(int additionalElements) {
		final int osize = (buf != null) ? buf.capacity() : 0;
		final int nsize = osize + (additionalElements * getComponents());
		FloatBuffer newFBuffer = Buffers.newDirectFloatBuffer(nsize);
		if (buf != null) {
			buf.flip();
			newFBuffer.put((FloatBuffer) buf);
		}
		buf = newFBuffer;
	}

	protected void allocateFixedBuffer() {
		final GLArrayDataServer buf = GLArrayDataServer.createFixedInterleaved(getComponents(), GL.GL_FLOAT, false,
				getVertexCount(), GL.GL_STATIC_DRAW);
		if (vertexComponent != 0) {
			buf.addFixedSubArray(GLPointerFunc.GL_VERTEX_ARRAY, vertexComponent, GL.GL_ARRAY_BUFFER);
		}
		if (colorComponent != 0) {
			buf.addFixedSubArray(GLPointerFunc.GL_COLOR_ARRAY, colorComponent, GL.GL_ARRAY_BUFFER);
		}
		if (normalComponent != 0) {
			buf.addFixedSubArray(GLPointerFunc.GL_NORMAL_ARRAY, normalComponent, GL.GL_ARRAY_BUFFER);
		}
		if (texCoordComponent != 0) {
			buf.addFixedSubArray(GLPointerFunc.GL_TEXTURE_COORD_ARRAY, texCoordComponent, GL.GL_ARRAY_BUFFER);
		}
		interleaved = buf;
	}

	protected void allocateGLSLBuffer() {
		final GLArrayDataServer buf = GLArrayDataServer.createGLSLInterleaved(getComponents(), GL.GL_FLOAT, false,
				getVertexCount(), GL.GL_STATIC_DRAW);
		if (vertexComponent != 0) {
			buf.addGLSLSubArray("mesh_vertices", vertexComponent, GL.GL_ARRAY_BUFFER);
		}
		if (colorComponent != 0) {
			buf.addGLSLSubArray("mesh_colors", colorComponent, GL.GL_ARRAY_BUFFER);
		}
		if (normalComponent != 0) {
			buf.addGLSLSubArray("mesh_normal", normalComponent, GL.GL_ARRAY_BUFFER);
		}
		if (texCoordComponent != 0) {
			buf.addGLSLSubArray("mesh_texCoord", texCoordComponent, GL.GL_ARRAY_BUFFER);
		}
		buf.rewind();
		interleaved = buf;
	}

	protected final boolean growBufferIfNecessary(int spare) {
		if (buf == null || buf.remaining() < spare) {
			growBuffer(spare);
			return true;
		}
		return false;
	}

	private void putf(float f) {
		if (isServer) {
			if (null == interleaved) {
				allocateBuffer();
			}
			interleaved.putf(f);
		} else {
			// buf.put(f);
			growBufferIfNecessary(256);
			Buffers.putf(buf, f);
		}
	}

	protected void putVertex() {
		if (vertexComponent > 0) {
			putf(x);
			putf(y);
			if (vertexComponent > 2) {
				putf(z);
			}
		}
		if (colorComponent > 0) {
			putf(red);
			putf(green);
			putf(blue);
			if (colorComponent > 3) {
				putf(alpha);
			}
		}
		if (normalComponent != 0) {
			putf(nx);
			putf(ny);
			if (normalComponent > 2) {
				putf(nz);
			}
		}
		if (texCoordComponent > 0) {
			putf(u);
			putf(v);
		}
	}

	public void getIndexed() {

	}

	public void enableColor3() {
		colorComponent = 3;
		if (useIndexed) {
			indexed.setColorComponent(colorComponent);
		}
	}

	public void enableColor4() {
		colorComponent = 4;
		if (useIndexed) {
			indexed.setColorComponent(colorComponent);
		}
	}

	public void enableVertex2() {
		vertexComponent = 2;
		if (useIndexed) {
			indexed.setVertexComponent(vertexComponent);
		}
	}

	public void enableVertex3() {
		vertexComponent = 3;
		if (useIndexed) {
			indexed.setVertexComponent(vertexComponent);
		}
	}

	public void enableNormal3() {
		normalComponent = 3;
		if (useIndexed) {
			indexed.setNormalComponent(normalComponent);
		}
	}

	public void enableTexCoord2() {
		texCoordComponent = 2;
		if (useIndexed) {
			indexed.setUVComponent(texCoordComponent);
		}
	}

	public void enableTexCoord4() {
		texCoordComponent = 4;
		if (useIndexed) {
			indexed.setUVComponent(texCoordComponent);
		}
	}

	public void glBegin(int mode) {
		this.mode = mode;
		allocateBuffer();
		if (useIndexed) {
			indexed.reset();
		}
	}

	public void beginQuads() {
		glBegin(GL2.GL_QUADS);
	}

	public void beginTriangles() {
		glBegin(GL2.GL_TRIANGLES);
	}

	public void glEnd() {
		if (useIndexed) {
			indexed.end();
			final IntBuffer ind = indexed.getIndices();
			System.out.println("glEnd: " + vertexCount + " / " + ind.limit());
		}
		if (isServer) {
			if (null == interleaved) {
				allocateBuffer();
			}
			interleaved.seal(true);
		}
		isFinished = true;
	}

	public void end() {
		glEnd();
	}

	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}

	public int getVBOName() {
		return interleaved.getVBOName();
	}

	public int getMode() {
		return mode;
	}

	public boolean draw() {
		if (!isFinished) {
			return false;
		}
		makeServer();
		final GL2ES2 gl = GLContext.getCurrentGL().getGL2ES2();
		interleaved.enableBuffer(gl, true);
		// Вывод геометрии VBO выполняется такими же функциями, как и при
		// использовании буфера в оперативной памяти.
		gl.glDrawArrays(mode, 0, interleaved.getElementCount());
		interleaved.enableBuffer(gl, false);
		//gl.glFlush();
		return true;
	}

	public void setColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		if (useIndexed) {
			indexed.glColor3f(red, green, blue);
		}
	}

	public void setColor(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		if (useIndexed) {
			indexed.glColor4f(red, green, blue, alpha);
		}
	}

	public void glColor3f(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		if (useIndexed) {
			indexed.glColor3f(red, green, blue);
		}
	}

	public void glColor4f(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		if (useIndexed) {
			indexed.glColor4f(red, green, blue, alpha);
		}
	}

	public void glVertex3f(float x, float y, float z) {
		this.x = offsetX + x;
		this.y = offsetY + y;
		this.z = offsetZ + z;
		putVertex();
		if (useIndexed) {
			indexed.glVertex3f(x, y, z);
		}
	}

	public void glVertex2f(float x, float y) {
		this.x = offsetX + x;
		this.y = offsetY + y;
		putVertex();
		if (useIndexed) {
			indexed.glVertex2f(x, y);
		}
	}

	public void setUV(float u, float v) {
		this.u = u;
		this.v = v;
		if (useIndexed) {
			indexed.setUV(u, v);
		}
	}

	public void glTexCoord2f(float u, float v) {
		this.u = u;
		this.v = v;
		if (useIndexed) {
			indexed.setUV(u, v);
		}
	}

	public void glRectf(float x1, float y1, float x2, float y2) {
		glVertex2f(x1, y1);
		glVertex2f(x1, y2);
		glVertex2f(x2, y2);
		glVertex2f(x2, y1);
	}

	protected ImmModeMesh(int vertexCount) {
		this.setVertexCount(vertexCount);
	}

	public static ImmModeMesh allocate(int elementCount) {
		return new ImmModeMesh(elementCount);
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void setVertexCount(int vertexCount) {
		this.vertexCount = vertexCount;
	}

	public void glTranslate(float x, float y, float z) {
		offsetX = x;
		offsetY = y;
		offsetZ = z;
		if (useIndexed) {
			indexed.glTranslate(x, y, z);
		}
	}

	public void dispose() {
		if (null != interleaved) {
			interleaved.destroy(GLContext.getCurrentGL().getGL2ES2());
		}
		if (null != buf) {
			buf.clear();
			buf = null;
		}
	}

	public void compact() {
		if (null != interleaved) {
			if (null != interleaved.getBuffer()) {
				interleaved.getBuffer().flip();
				((FloatBuffer) interleaved.getBuffer()).compact();
			}
		}
	}

	public void glNormal3f(float nx, float ny, float nz) {
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
		if (useIndexed) {
			indexed.glNormal3f(nx, ny, nz);
		}
	}
}
