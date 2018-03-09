package com.wuda.graph;

/**
 * 图的顶点之间的方向.
 * 
 * @author wuda
 *
 */
public enum Direction {
	/**
	 * Defines outgoing relationships.
	 */
	OUTGOING,
	/**
	 * Defines incoming relationships.
	 */
	INCOMING,
	/**
	 * Defines both incoming and outgoing relationships.
	 */
	BOTH;
}
