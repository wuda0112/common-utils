package com.wuda.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 二叉搜索树.
 *
 * @param <K>
 *         the type of keys maintained by this tree
 * @param <V>
 *         the type of mapped values
 * @author wuda
 */
public class BinaryTree<K extends Comparable<K>, V> {

    /**
     * 根节点.
     */
    private BinaryTreeNode<K, V> root = null;
    /**
     * 节点数.
     */
    private int size;

    /**
     * {@link K}和{@link V}被封装成{@link BinaryTreeNode},然后加入到此树中.如果树中已经存在一个key等于此
     * {@link K}的节点,则此节点的值被新的{@link V}替换.
     *
     * @param key
     *         节点的key
     * @param value
     *         节点的value
     * @return 当前{@link K}所代表的节点.
     */
    public BinaryTreeNode<K, V> put(K key, V value) {
        if (root == null) {
            root = new BinaryTreeNode<>(key, value);
            return root;
        }
        SearchExplain explain = search(root, key);
        BinaryTreeNode<K, V> searching = explain.searching;

        BinaryTreeNode<K, V> node;

        if (searching != null) { // 找到了
            searching.value = value; // 用新的value替换
            node = searching;
        } else {
            BinaryTreeNode<K, V> itsParent = explain.itsParent;
            node = new BinaryTreeNode<>(key, value);
            node.parent = itsParent;
            if (key.compareTo(itsParent.key) == 1) {
                itsParent.right = node;
            } else {
                itsParent.left = node;
            }
            node.depth = itsParent.depth + 1;
            updateAboveHeight(node); // 更新父节点及祖先节点的高度
            size++;
        }
        return node;
    }

    /**
     * 删除树中{@link K}所代表的节点,并且返回被删除的节点.如果返回<code>null</code>,则表示树中不存在这样的节点.
     *
     * @param key
     *         节点的key
     * @return 被删除的节点
     */
    public BinaryTreeNode<K, V> remove(K key) {
        return null;
    }

    /**
     * Returns the node to which the specified key is mapped, or {@code null} if
     * this tree contains no mapping for the key.
     *
     * @param key
     *         the key whose associated node is to be returned
     * @return the node to which the specified key is mapped, or {@code null} if
     * this tree contains no mapping for the key
     */
    public BinaryTreeNode<K, V> get(K key) {
        return search(root, key).searching;
    }

    /**
     * 先序遍历.
     *
     * @return 节点列表
     */
    public List<BinaryTreeNode<K, V>> preorder() {
        List<BinaryTreeNode<K, V>> list = new ArrayList<>(size);
        LinkedList<BinaryTreeNode<K, V>> back_trace = new LinkedList<>();// 用于回溯
        BinaryTreeNode<K, V> current = root;
        while (current != null) {
            list.add(current); // 类似于"访问"当前节点
            if (current.right != null) {
                back_trace.addLast(current.right); // 右节点入队列,用于回溯
            }
            current = current.left; // 向左移动
            if (current == null) { // 上一步刚刚向左移动,如果等于null成立则表示已经不能向左了
                /*
                 * 回溯.
                 */
                current = back_trace.removeLast();// addLast然后又removeLast,达到【后进先出】的效果,表示【从下往上回溯】
            }
        }
        return list;
    }

    /**
     * 中序遍历.
     *
     * @return 节点列表
     */
    public List<BinaryTreeNode<K, V>> inorder() {
        return null;
    }

    /**
     * 从<i>current</i>节点开始查找<i>key</i>所代表的节点,返回包含<i>key</i>的节点以及此节点的父节点.
     *
     * @param current
     *         从当前的节点开始查找
     * @param key
     *         the key whose associated node is to be returned
     * @return SearchExplain
     */
    private SearchExplain search(BinaryTreeNode<K, V> current, K key) {
        if (current == null) {
            current = root;
        }
        BinaryTreeNode<K, V> itsParent = current.parent;
        while (current != null) {
            if (key.equals(current.key)) { // 找到
                break;
            }
            itsParent = current;
            current = key.compareTo(current.key) == 1 ? current.right : current.left; // 决定向左还是向右
        }
        SearchExplain explain = new SearchExplain(current, itsParent);
        return explain;
    }

    /**
     * 更新当前节点的父节点和祖先节点的高度.
     *
     * @param current
     *         当前节点
     */
    private void updateAboveHeight(BinaryTreeNode<K, V> current) {
        BinaryTreeNode<K, V> parent;
        while (current != null) {
            parent = current.parent;
            if (parent != null) {
                parent.height++;
            }
            current = parent;
        }
    }

    /**
     * 搜索结果信息,包含被搜索的节点以及被搜索节点的父节点.
     *
     * @author wuda
     */
    private class SearchExplain {
        private BinaryTreeNode<K, V> searching; // 被搜索的节点
        private BinaryTreeNode<K, V> itsParent; // 被搜索节点的父节点

        private SearchExplain(BinaryTreeNode<K, V> searching, BinaryTreeNode<K, V> itsParent) {
            this.searching = searching;
            this.itsParent = itsParent;
        }
    }

    /**
     * 二叉树的节点类.
     *
     * @author wuda
     */
    static class BinaryTreeNode<K extends Comparable<K>, V> {

        K key; // key
        V value; // value
        /**
         * 左孩子.
         */
        BinaryTreeNode<K, V> left = null;
        /**
         * 右孩子.
         */
        BinaryTreeNode<K, V> right = null;
        /**
         * 父节点.
         */
        BinaryTreeNode<K, V> parent = null;
        /**
         * 高度.
         */
        int height = 0;
        /**
         * 深度.
         */
        int depth = 0;

        /**
         * 构造节点.
         *
         * @param key
         *         key
         * @param value
         *         value
         */
        BinaryTreeNode(K key, V value) {
            this.key = key;
            this.value = value;
        }

    }
}
