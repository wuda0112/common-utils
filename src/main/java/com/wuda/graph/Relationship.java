package com.wuda.graph;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 图中两个顶点之间的关系.关系是有方向的,方向总是从{@link #startVertex}指向 {@link #endVertex}.
 *
 * @param <E>
 *         约束顶点元素的类型,没有必要,只是对编译友好.
 * @author wuda
 */
public class Relationship<E extends Comparable<E>> {
    /**
     * 唯一id.
     */
    private long id;
    /**
     * 开始节点.
     */
    private Vertex<E> startVertex;
    /**
     * 结束节点.
     */
    private Vertex<E> endVertex;
    /**
     * 关系类型.
     */
    private RelationshipType relationshipType;

    /**
     * id生成器
     */
    private static final AtomicLong generator = new AtomicLong();

    /**
     * 在遍历过程中,关系所处的状态.
     */
    private RelationshipStatus status = RelationshipStatus.UNDETERMINED;

    /**
     * 在遍历时,关系所处的状态.
     *
     * @author wuda
     */
    enum RelationshipStatus {
        UNDETERMINED, CROSS, TREE, FORWARD, BACKWARD;
    }

    /**
     * 创建一个<i>startVertex</i>到<i>endVertex</i>方向的的关系.
     *
     * @param startVertex
     *         start
     * @param endVertex
     *         end
     * @param relationshipType
     *         关系类型
     */
    Relationship(Vertex<E> startVertex, Vertex<E> endVertex, RelationshipType relationshipType) {
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        this.relationshipType = relationshipType;
        this.id = generator.getAndIncrement();
    }

    /**
     * 获取开始节点.
     *
     * @return the startVertex
     */
    public Vertex<E> getStartVertex() {
        return startVertex;
    }

    /**
     * 获取结束节点.
     *
     * @return the endVertex
     */
    public Vertex<E> getEndVertex() {
        return endVertex;
    }

    /**
     * 获取关系类型.
     *
     * @return the relationshipType
     */
    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    /**
     * 获取id.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * 获取在遍历过程中的状态.
     *
     * @return the status
     */
    RelationshipStatus getStatus() {
        return status;
    }

    /**
     * 设置在遍历过程中的状态.
     *
     * @param status
     *         the status to set
     */
    void setStatus(RelationshipStatus status) {
        this.status = status;
    }

}
