package ru.olamedia.geom;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class IndexedMeshBulder {
	protected static IndexedMeshBulder instance = new IndexedMeshBulder();
	protected IntBuffer indices = IntBuffer.allocate(4096);
	protected FloatBuffer vertex = FloatBuffer.allocate(4096 * 3);
	protected FloatBuffer color = FloatBuffer.allocate(4096 * 4);
	protected FloatBuffer normal = FloatBuffer.allocate(4096 * 3);
	protected FloatBuffer uv = FloatBuffer.allocate(4096 * 2);
	protected int indexCount = 0;
	protected int vertexCount = 0;
	protected int colorCount = 0;
	protected int normalCount = 0;
	protected int uvCount = 0;

	protected byte vertexComponent = 0;
	protected byte normalComponent = 0;
	protected byte colorComponent = 0;
	protected byte uvComponent = 0;

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

	public void reset() {
		vertex.clear();
		color.clear();
		normal.clear();
		uv.clear();
		indices.clear();
		vertexCount = indexCount = vertexComponent = colorComponent = normalComponent = uvComponent = 0;
	}
	
	public void end(){
		indices.flip();
		vertex.flip();
		color.flip();
		normal.flip();
		uv.flip();
	}

	public IntBuffer getIndices() {
		IntBuffer ind = IntBuffer.allocate(indices.limit());
		ind.put(indices);
		return ind;
	}

	private boolean _sameVertex(int i) {
		if (vertexComponent != 0) {
			if (vertex.get(i * vertexComponent) == z) {
				if (vertex.get(i * vertexComponent + 1) == y) {
					if ((vertexComponent == 2) || (vertex.get(i * vertexComponent) == z)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean _sameColor(int i) {
		if (colorComponent != 0) {
			if (color.get(i * colorComponent) == red) {
				if (color.get(i * colorComponent + 1) == green) {
					if (color.get(i * colorComponent + 2) == blue) {
						if ((colorComponent == 2) || (color.get(i * colorComponent) == alpha)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private boolean _sameUV(int i) {
		if (uvComponent != 0) {
			if (uv.get(i * uvComponent) == u) {
				if (uv.get(i * uvComponent + 1) == v) {
					return true;
				}
			}
		}
		return false;
	}

	private void _addVertex() {
		if (vertexComponent != 0) {
			vertex.put(x);
			vertex.put(y);
			if (vertexComponent > 2) {
				vertex.put(z);
			}
		}
		if (colorComponent != 0) {
			color.put(red);
			color.put(green);
			color.put(blue);
			if (colorComponent > 3) {
				color.put(alpha);
			}
		}
		if (normalComponent != 0) {
			normal.put(nx);
			normal.put(ny);
			if (normalComponent > 2) {
				normal.put(nz);
			}
		}
		if (uvComponent != 0) {
			uv.put(u);
			uv.put(v);
		}
		indices.put(indexCount);
		indexCount++;
	}

	private void putVertex() {
		vertexCount++;
		for (int i = 0; i < vertexCount; i++) {
			if (_sameVertex(i) && _sameColor(i) && _sameUV(i)) {
				indices.put(i);
				return;
			}
		}
		_addVertex();
	}

	public void setVertexComponent(byte vertexComponent) {
		this.vertexComponent = vertexComponent;
	}

	public void setNormalComponent(byte normalComponent) {
		this.normalComponent = normalComponent;
	}

	public void setColorComponent(byte colorComponent) {
		this.colorComponent = colorComponent;
	}

	public void setUVComponent(byte uvComponent) {
		this.uvComponent = uvComponent;
	}

	private int comps() {
		return vertexComponent + normalComponent + colorComponent + uvComponent;
	}

	public void glTranslate(float x, float y, float z) {
		offsetX = x;
		offsetY = y;
		offsetZ = z;
	}

	public void glColor3f(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void glColor4f(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public void glVertex3f(float x, float y, float z) {
		this.x = offsetX + x;
		this.y = offsetY + y;
		this.z = offsetZ + z;
		putVertex();
	}

	public void glVertex2f(float x, float y) {
		this.x = offsetX + x;
		this.y = offsetY + y;
		putVertex();
	}

	public void glNormal3f(float nx, float ny, float nz) {
		this.nx = nx;
		this.ny = ny;
		this.nz = nz;
	}

	public void setUV(float u, float v) {
		this.u = u;
		this.v = v;
	}

}
