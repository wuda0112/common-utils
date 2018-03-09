package com.wuda.graph;

import java.util.LinkedList;

/**
 * 两个顶点之间的路径.
 * 
 * @author wuda
 *
 */
public class Path {

	/**
	 * 这条路径中所有的顶点.
	 */
	private LinkedList<Vertex> orderedVertices = new LinkedList<Vertex>();

	/**
	 * 将此顶点添加到此路径的最后.
	 * 
	 * @param v
	 *            {@link Vertex}
	 */
	public void addLast(Vertex v) {
		orderedVertices.addLast(v);
	}

	/**
	 * 将此顶点添加到此路径的最开始.
	 * 
	 * @param v
	 *            {@link Vertex}
	 */
	public void addFirst(Vertex v) {
		orderedVertices.addFirst(v);
	}

	/**
	 * 返回此路径中的所有顶点.
	 * 
	 * @return 所有顶点
	 */
	public LinkedList<Vertex> getVertices() {
		return orderedVertices;
	}

}
