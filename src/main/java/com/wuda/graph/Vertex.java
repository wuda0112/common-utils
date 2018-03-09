package com.wuda.graph;

import java.util.LinkedList;

/**
 * 图的顶点.
 *
 * @param <E>
 *         顶点中元素类型
 * @author wuda
 */
public class Vertex<E extends Comparable<E>> {

    /**
     * 返回顶点中的元素.
     *
     * @return 此顶点中包含的元素
     */
    public E getElement() {
        return element;
    }

    /**
     * 顶点中的元素.
     */
    private E element;

    /**
     * 标签.使用标签可以对顶点进行分组.
     */
    private LinkedList<Label> labels = null;

    /**
     * 可以说是此顶点与“边”的关联关系,也可以说是此顶点的邻接顶点集合.
     */
    private LinkedList<Relationship<E>> relationships = new LinkedList<>();

    /**
     * 在遍历时,顶点所处的状态.
     */
    private VertexStatus vertexStatus = VertexStatus.UNDISCOVERED;

    /**
     * 在遍历时,此顶点的父顶点.
     */
    private Vertex parent = null;

    /**
     * 对比两个顶点.
     *
     * @param another
     *         另外一个顶点
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    public int compareTo(Vertex<E> another) {
        return this.element.compareTo(another.element);
    }

    /**
     * 标记顶点在遍历过程中的状态.
     *
     * @author wuda
     */
    enum VertexStatus {
        UNDISCOVERED, DISCOVERED, VISITED;
    }

    /**
     * 构造一个顶点.
     *
     * @param element
     *         顶点包含的元素
     */
    public Vertex(E element) {
        this.element = element;
    }

    /**
     * 以当前顶点为起始顶点,<i>end</i>为结束顶点,创建两个顶点之间的关系.
     * 如果这两个顶点之间已经存在这种类型的关系,则直接返会,如果没有的话则创建关系并且返回.
     *
     * @param end
     *         结束顶点
     * @param relationshipType
     *         关系的类型
     * @return 两个顶点之间的关系对象
     */
    public Relationship createRelationshipTo(Vertex<E> end, RelationshipType relationshipType) {
        for (Relationship relationship : relationships) {
            if (relationship.getEndVertex().compareTo(end) == 0
                    && relationship.getRelationshipType().equals(relationshipType)) {
                return relationship;
            }
        }
        Relationship relationship = new Relationship(this, end, relationshipType);
        relationships.addLast(relationship);
        AdjacencyListGraph.registerNewRelationship(relationship);
        return relationship;
    }

    /**
     * 获取当前顶点到指定顶点之间的所有关系.
     *
     * @param to
     *         end vertex
     * @return 所有的关系
     */
    public LinkedList<Relationship<E>> getRelationships(Vertex<E> to) {
        LinkedList<Relationship<E>> relationships = this.getRelationships();
        if (relationships == null || relationships.isEmpty()) {
            return null;
        }
        LinkedList<Relationship<E>> relationshipLinkedList = new LinkedList<>();
        for (Relationship relationship : relationships) {
            Vertex v = relationship.getEndVertex();
            if (v.equals(to)) {
                relationshipLinkedList.add(relationship);
            }
        }
        return relationshipLinkedList;
    }

    /**
     * 返回当前顶点的所有关系,即当前顶点的所有邻接顶点.
     *
     * @return the relationships
     */
    public LinkedList<Relationship<E>> getRelationships() {
        return relationships;
    }

    /**
     * 为顶点添加标签.标签的作用是可以对顶点分类.
     *
     * @param label
     *         标签
     */
    public void addLabel(Label label) {
        labels.addLast(label);
    }

    /**
     * 返回此顶点拥有的所有标签.
     *
     * @return 顶点拥有的所有标签
     */
    public LinkedList<Label> getLabels() {
        return labels;
    }

    /**
     * 顶点在遍历过程中所处的状态.
     *
     * @return the status
     */
    VertexStatus getStatus() {
        return vertexStatus;
    }

    /**
     * 设置顶点在遍历过程中所处的状态.
     *
     * @param status
     *         the status to set
     */
    void setStatus(VertexStatus status) {
        this.vertexStatus = status;
    }

    /**
     * 顶点在遍历过程中的父顶点.
     *
     * @return the parent
     */
    Vertex getParent() {
        return parent;
    }

    /**
     * 设置顶点在遍历过程中的父顶点.
     *
     * @param parent
     *         the parent to set
     */
    void setParent(Vertex parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return element.toString();
    }

}
