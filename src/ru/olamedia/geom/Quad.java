package ru.olamedia.geom;

public class Quad {
	private float[] vertices;
	private int vertexCount;

	public void addVertex(float x, float y, float z) {
		vertices[vertexCount] = x;
		vertices[vertexCount + 1] = y;
		vertices[vertexCount + 2] = z;
	}
}
