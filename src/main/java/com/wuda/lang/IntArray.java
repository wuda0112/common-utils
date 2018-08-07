package com.wuda.tree;

import java.util.Arrays;

/**
 * 包裹int数组,提供相应的方法.
 *
 * @author wuda
 */
public class IntArray {
    /**
     * the array.
     */
    private int[] array;

    /**
     * 构造一个默认容量的array.
     */
    public IntArray() {
        this(8);
    }

    /**
     * 构造一个指定容量的array.
     *
     * @param initialCapacity
     *         数组容量
     */
    public IntArray(int initialCapacity) {
        if (initialCapacity >= 0) {
            this.array = new int[initialCapacity];
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " +
                    initialCapacity);
        }
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
    public int get(int index) throws IndexOutOfBoundsException {
        rangeCheck(index);
        return array[index];
    }

    /**
     * 在指定下标处设置value.
     *
     * @param index
     *         下标
     * @param value
     *         value
     * @throws IndexOutOfBoundsException
     *         如果下标越界
     */
    public void set(int index, int value) throws IndexOutOfBoundsException {
        rangeCheck(index);
        array[index] = value;
    }

    /**
     * 返回数组的长度.
     *
     * @return length
     */
    public int length() {
        return array.length;
    }

    /**
     * 获取实际被包裹的array.
     *
     * @return array
     */
    public int[] getArray() {
        return array;
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     *
     * @see java.util.ArrayList#rangeCheck(int)
     */
    private void rangeCheck(int index) {
        if (index >= array.length)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     *
     * @see java.util.ArrayList#outOfBoundsMsg(int)
     */
    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", array length: " + array.length;
    }


    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Increases the capacity to ensure that it can hold at least the
     * number of elements specified by the minimum capacity argument.
     *
     * @param minCapacity
     *         the desired minimum capacity
     * @return 实际使用的capacity.传入的容量必须经过检查, 然后计算出实际扩容的容量.
     * @see java.util.ArrayList#grow(int)
     */
    public int grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = array.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        array = Arrays.copyOf(array, newCapacity);
        return newCapacity;
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }
}
