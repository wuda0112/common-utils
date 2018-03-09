package com.wuda.tree;

import java.util.LinkedList;
import java.util.List;

/**
 * 基本的树型结构.first-child next-sibling方式的实现.树中的节点有一些限制条件
 * <ul>
 * <li>兄弟节点之间,{@link Node#element}是唯一的,即不能存在任意两个兄弟节点的{@link Node#element}
 * 一样.但是如果节点不是兄弟关系,则他们的{@link Node#element}是可以一样的.</li>
 * <li>节点的{@link Node#element}虽然是任意类型, 但是我们会检查数据类型,使他只能是<Strong>基本数据类型</Strong>.
 * </li>
 * <li>root节点是默认生成的,它的{@link Node#element}等于"root",而且至少为root绑定一个子节点,即至少调用一次
 * {@link BasicTree#createRelationShip(Node, Node)},然后第一个Node等于
 * {@link BasicTree#getRoot()}</li>
 * </ul>
 *
 * @param <T>
 *         节点中包含的元素类型
 * @author wuda
 */
public class BasicTree<T extends Comparable<T>> {

    /**
     * 树的根节点.
     */
    private Node<T> root = new Node<>(null);

    /**
     * 创建一个新的节点.
     *
     * @param element
     *         节点的元素
     * @return a new node
     */
    public Node<T> createNode(T element) {
        if (element == null) {
            throw new NullPointerException("element 为null");
        }
        return new Node<>(element);
    }

    /**
     * 创建两个节点的关系.很明显,第一个是父节点,第二个是子节点.
     *
     * @param parent
     *         父节点
     * @param child
     *         子节点
     * @throws AlreadyHasParentException
     *         子节点已经拥有父节点
     * @throws DuplicateElementException
     *         在给定的父节点下,已经有一个子节点的{@link Node#getElement()}和child的相同
     */
    public void createRelationShip(Node<T> parent, Node<T> child)
            throws AlreadyHasParentException, DuplicateElementException {
        if (parent == null) {
            throw new NullPointerException("父节点不能为空");
        }
        if (child.parent != null && !child.parent.equals(parent)) {
            throw new AlreadyHasParentException("child 已经拥有了一个父节点,并且这个父节点不是当前提供的父节点");
        }
        if (find(parent, child.element) != null) {
            throw new DuplicateElementException("在给定的父节点下,已经有一个子节点的元素和child的元素相同");
        }
        if (parent.firstChild == null) {
            parent.firstChild = child;
        } else {
            Node<T> sibling = parent.firstChild;
            while (sibling != null && sibling.nextSibling != null) {
                sibling = sibling.nextSibling;
            }
            sibling.nextSibling = child;
        }
        child.parent = parent;
    }

    /**
     * 寻找父节点下的指定元素的子节点.
     *
     * @param parent
     *         父节点
     * @param childElement
     *         子节点的元素
     * @return 子节点, null-如果没有找到
     */
    public Node<T> find(Node<T> parent, T childElement) {
        if (parent == null) {
            return null;
        }
        Node<T> child = parent.firstChild;
        while (child != null) {
            if (child.getElement().compareTo(childElement) == 0) {// 找到
                return child;
            }
            child = child.nextSibling;
        }
        return null;
    }

    /**
     * 深度优先遍历.数据量多的时候不能调用.
     *
     * @param start
     *         开始节点
     * @return 此节点的所有后裔, 包括此节点, 并且此节点一定是位于集合的第一个位置
     */
    public List<Node<T>> dfs(Node<T> start) {
        if (start == null) {
            return null;
        }
        LinkedList<Node<T>> backtrack = new LinkedList<>();
        List<Node<T>> children = new LinkedList<>();
        backtrack.addFirst(start);
        Node<T> current;
        while (!backtrack.isEmpty()) {
            current = backtrack.removeFirst();
            while (current != null) {
                if (current.nextSibling != null) {
                    backtrack.addFirst(current.nextSibling);
                }
                children.add(current);
                current = current.firstChild;
            }
        }
        return children;
    }

    /**
     * 获取树的根节点.
     *
     * @return the root
     */
    public Node<T> getRoot() {
        return root;
    }

    /**
     * 树的节点.
     *
     * @param <T>
     *         元素的类型
     * @author wuda
     */
    public static class Node<T extends Comparable<T>> {
        /**
         * 当前节点的第一个子节点.
         */
        private Node<T> firstChild = null;
        /**
         * 当前节点的兄弟节点.
         */
        private Node<T> nextSibling = null;
        /**
         * 当前节点的父节点.
         */
        private Node<T> parent = null;
        /**
         * 兄弟节点之间,element是唯一的,即不能存在任意两个兄弟节点的element一样.如果节点不是兄弟关系,
         * 则他们的element是可以一样的.虽然这里是泛型, 但是我们会检查数据类型,使他只能是基本数据类型.
         */
        private T element;

        /**
         * 构建一个节点.
         *
         * @param element
         *         节点的元素.
         */
        Node(T element) {
            this.element = element;
        }

        @Override
        public String toString() {
            return element.toString();
        }

        /**
         * @return the firstChild
         */
        Node<T> getFirstChild() {
            return firstChild;
        }

        /**
         * @param firstChild
         *         the firstChild to set
         */
        void setFirstChild(Node<T> firstChild) {
            this.firstChild = firstChild;
        }

        /**
         * @return the nextSibling
         */
        Node<T> getNextSibling() {
            return nextSibling;
        }

        /**
         * @param nextSibling
         *         the nextSibling to set
         */
        void setNextSibling(Node<T> nextSibling) {
            this.nextSibling = nextSibling;
        }

        /**
         * 获取当前节点的父节点.
         *
         * @return the parent
         */
        public Node<T> getParent() {
            return parent;
        }

        /**
         * @param parent
         *         the parent to set
         */
        void setParent(Node<T> parent) {
            this.parent = parent;
        }

        /**
         * 获取节点的元素.
         *
         * @return the element
         */
        public T getElement() {
            return element;
        }

        /**
         * @param element
         *         the element to set
         */
        void setElement(T element) {
            this.element = element;
        }

        /**
         * 获取节点的深度.
         *
         * @return the depth
         */
        public int getDepth() {
            int tmpDepth = 0;
            Node<T> parent = this.parent;
            while (parent != null) {
                tmpDepth++;
                parent = parent.parent;
            }
            return tmpDepth;
        }
    }
}
