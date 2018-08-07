package com.wuda.lang;

import java.util.*;

/**
 * 原始类型的int list,使用数组方式实现.
 * 此实现中{@link List}接口的大部分方法都不支持,已经实现的方法也使用了其他名称,
 * 比如:{@link List#add(Object)},对应的实现是{@link #addInt(int)}.
 *
 * @author wuda
 * @since 1.0.2
 */
public class IntArrayList implements List {

    /**
     * 不使用int[]的原因是: 避免重复的数组检查,扩容等操作.
     */
    private IntArray intArray;

    private int size;

    /**
     * 构造一个指定容量的list.
     *
     * @param initialCapacity
     *         数组容量
     */
    public IntArrayList(int initialCapacity) {
        if (initialCapacity >= 0) {
            intArray = new IntArray(initialCapacity);
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " +
                    initialCapacity);
        }
    }

    /**
     * 构造一个默认容量的array.
     */
    public IntArrayList() {
        this(16);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size > 0;
    }

    @Override
    public void clear() {
        size = 0;
    }

    /**
     * 获取指定下标处的值.
     *
     * @param index
     *         数组下标
     * @return 此下标处的值
     * @throws IndexOutOfBoundsException
     *         如果下标越界
     */
    public int getInt(int index) {
        return intArray.get(index);
    }

    /**
     * 在list末尾添加value.
     *
     * @param value
     *         value
     * @throws IndexOutOfBoundsException
     *         如果下标越界
     */
    public void addInt(int value) {
        if (size() + 1 > intArray.length()) {
            intArray.grow(size());
        }
        intArray.set(size++, value);
    }

    /**
     * 返回int array.
     *
     * @return array
     */
    public int[] toIntArray() {
        if (isEmpty()) {
            return new int[0];
        }
        return Arrays.copyOfRange(intArray.getArray(), 0, size);
    }

    @Override
    public Iterator iterator() {
        return new Iterator() {

            private int index;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public Object next() {
                if (index >= size()) {
                    throw new NoSuchElementException();
                }
                return intArray.get(index++);
            }
        };
    }


    @Override
    public Object get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object set(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new UnsupportedOperationException();
    }
}
