package com.wuda.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * double-array trie,基于论文
 * <a href="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.14.8665&rep=rep1&type=pdf">
 * An Efficient Implementation of Trie Structures
 * </a>
 * 实现,但是有一些改动,比如:TAIL数组没有了.
 *
 * @author wuda
 * @version 1.0
 */
public class AbstractDoubleArrayTrie {
    /**
     * the BASE array.
     * 负值: end node; 0: 未使用的节点; 正值: 已使用的正常节点
     */
    private IntArray base;
    /**
     * the CHECK array.
     * 0: 未使用; 正值: 已使用的正常节点
     */
    private IntArray check;
    /**
     * leavingArc[n]代表从节点n发出的所有的弧.
     */
    private List<Character>[] arcsLeaving;
    /**
     * 正如论文中说的,为了区分类似the很then这样的单词,在单词后加上区隔符.
     */
    private char separator = '#';
    /**
     * 内部指针,在论文中是指向TAIL数组.
     */
    private int pos = 1;

    /**
     * root node position.
     */
    private int rootPosition = 1;
    /**
     * base,check数组使用的最大下标值.
     */
    private int maxIndex;

    private int maxQ;
    private int maxChildCount;
    private int collisionCount;

    /**
     * 构造一个double-array trie,使用默认的容量.
     */
    public AbstractDoubleArrayTrie() {
        this(8);
    }

    /**
     * 构造一个double-array trie,使用指定的容量.不是term数量,无法精确double-array的长度,只是一个大致估计的值.
     *
     * @param capacity
     *         容量
     */
    public AbstractDoubleArrayTrie(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Illegal Capacity: " +
                    capacity);
        }
        init(capacity);
    }

    /**
     * 初始化各个属性.
     *
     * @param capacity
     *         容量
     */
    private void init(int capacity) {
        base = new IntArray(capacity);
        check = new IntArray(capacity);
        arcsLeaving = new LinkedList[capacity];
        ensureExplicitDoubleArrayCapacity(rootPosition + 1);
        setIntArray(base, rootPosition, 1);
    }

    /**
     * 添加指定的term到trie中.
     *
     * @param term
     *         term to be appended to this trie
     */
    public void add(String term) {
        if (term == null || term.isEmpty()) {
            return;
        }
        term = term.trim();
        if (term.isEmpty()) {
            return;
        }
        int n = rootPosition;
        int a, m;
        boolean transferCorrect = false;
        char c;
        for (int index = 0; index <= term.length(); index++) {
            if (index < term.length()) {
                c = Character.toLowerCase(term.charAt(index));
            } else {
                // 避免了term+separator这样的字符串相加操作,这个操作很巧妙,i like it
                c = separator;
            }
            int baseN = getIntArray(base, n, true);
            if (baseN < 0) {
                throw new RuntimeException("结尾符号:" + separator + "不能区分单词!当前term:" + term);
            } else if (baseN == 0) {
                int q = x_check(c);
                setIntArray(base, n, q);
                baseN = getIntArray(base, n, false);
            }
            a = getCodePoint(c);
            m = baseN + a;
            if (m == n) System.out.println("node经过一个arc后指向自己!");
            int checkM = getIntArray(check, m, true);
            if (checkM == 0) {
                assert m != n : "node经过一个arc后指向自己,并且准备将父节点设置为自己!";
                setIntArray(check, m, n);
            } else if (checkM != n) { // 论文中的 insertion case 4
                List<Character> arcsLeavingN = arcsLeaving(n);
                List<Character> arcsLeavingCheckM = arcsLeaving(checkM);
                int modifyNode;
                Character candidateArc = null;
                if (m == n // node经过一个arc后指向自己
                        /*
                         * 如果对应到论文【Figure 7】中，冲突的node 1 和 node 3,本来node 1 的子节点是node 4,
                         * 但是如果我们假设node 1 的子节点不是node 4,而是node 3 的话，就会发生这里的情况,此时node 3也要被新节点替换了.
                         */
                        || getIntArray(check, n, false) == checkM
                        || (arcsLeavingN.size() + 1 < arcsLeavingCheckM.size())) {
                    modifyNode = n;
                    candidateArc = c;//在使用x_check计算q值时,当前冲突的character也需要参与
                } else {
                    modifyNode = checkM;
                }
                modifyBase(modifyNode, candidateArc);
                baseN = getIntArray(base, n, false);
                m = baseN + a;
                setIntArray(check, m, n);
            } else {
                transferCorrect = true;
            }
            if (!transferCorrect) {
                if (c == separator) {
                    setIntArray(base, m, -pos);
                    pos++;
                }
                addLeavingArc(n, c);
            }
            transferCorrect = false;
            n = m;
        }
    }

    /**
     * 此trie中是否包含给定的<i>term</i>.
     *
     * @param term
     *         term
     * @return true-如果包含,false-不包含
     */
    public boolean contains(String term) {
        if (term == null || term.isEmpty()) {
            return false;
        }
        term = term.trim();
        if (term.isEmpty()) {
            return false;
        }
        int n = rootPosition;
        int a, m;
        for (int index = 0; index <= term.length(); index++) {
            char c;
            if (index < term.length()) {
                c = Character.toLowerCase(term.charAt(index));
            } else {
                c = separator;
            }
            if (!rangeCheck(n)) return false;
            int baseN = getIntArray(base, n, false);
            a = getCodePoint(c);
            m = baseN + a;
            if (!rangeCheck(m)) return false;
            if (getIntArray(check, m, false) != n) {
                return false;
            }
            if (c == separator && getIntArray(base, m, false) < 0) {
                return true;
            }
            n = m;
        }
        return false;
    }

    private boolean rangeCheck(int index) {
        return base.length() > index;
    }

    /**
     * 将node和它的arc关联起来.
     *
     * @param node
     *         node
     * @param leavingArc
     *         arc
     */
    private void addLeavingArc(int node, Character leavingArc) {
        ensureExplicitDoubleArrayCapacity(node + 1);
        List<Character> leavingArcs = arcsLeaving(node);
        if (leavingArcs == null) {
            leavingArcs = new LinkedList<>();
            this.arcsLeaving[node] = leavingArcs;
        }
        leavingArcs.add(leavingArc);
    }

    /**
     * 调整base[node]处的值.当调整后,
     * <ul>
     * <li>node节点的所有子节点</li>
     * <li>以及这些子节点的子节点</li>
     * <li>被调整节点的leavingArcs</li>
     * </ul>
     * 都需要跟着一起被调整.
     *
     * @param node
     *         节点编号
     * @param candidateArc
     *         即将加入node节点的后续arc
     */
    private void modifyBase(int node, Character candidateArc) {
        collisionCount++;
        int originalBaseN = getIntArray(base, node, false);
        List<Character> arcsLeavingNode = arcsLeaving(node);
        int newBaseN = x_check(arcsLeavingNode, candidateArc);
        assert newBaseN != originalBaseN : "x_check计算出的新值与原值相等!";
        setIntArray(base, node, newBaseN);
        for (char ch : arcsLeavingNode) {
            int a = getCodePoint(ch);
            int originalM = originalBaseN + a;
            int currentM = newBaseN + a;
            setIntArray(base, currentM, getIntArray(base, originalM, false));
            setIntArray(check, currentM, getIntArray(check, originalM, false));
            arcsLeaving[currentM] = arcsLeaving[originalM];
            if (ch != separator) { //　区隔符所在的arc的[输出节点]没有子节点
                List<Integer> nodesLeavingOriginalM = nodesLeaving(originalM);
                for (int nodeLeavingOriginalM : nodesLeavingOriginalM) {
                    setIntArray(check, nodeLeavingOriginalM, currentM);
                }
            }
            setIntArray(base, originalM, 0);
            setIntArray(check, originalM, 0);
            arcsLeaving[originalM] = null;
        }
    }

    /**
     * 所有离开node的arc.
     *
     * @param node
     *         节点
     * @return 此节点的所有离开的arc.
     */
    private List<Character> arcsLeaving(int node) {
        return arcsLeaving[node];
    }

    /**
     * node的所有子节点.
     *
     * @param node
     *         节点
     * @return 此节点的所有子节点
     */
    private List<Integer> nodesLeaving(int node) {
        List<Character> arcsLeaving = arcsLeaving(node);
        List<Integer> children = new ArrayList<>(arcsLeaving.size());
        int baseN = getIntArray(base, node, false);
        for (char ch : arcsLeaving) {
            int m = baseN + getCodePoint(ch);
            children.add(m);
        }
        return children;
    }

    /**
     * 为给定的char返回唯一的code point.比如unicode字符集中,每个字符对应一个唯一的码点.
     *
     * @param ch
     *         char
     * @return code point
     */
    private int getCodePoint(char ch) {
        int codePoint = (int) ch;
        assert codePoint != 0 : "code point 等于0";
        return codePoint;
    }

    /**
     * 根据code point找到对应的code.reverse of {@link #getCodePoint(char)}
     *
     * @param codePoint
     *         code point
     * @return char
     * @see #getCodePoint(char)
     */
    private char getCode(int codePoint) {
        return (char) codePoint;
    }

    /**
     * 确保BASE和CHECK数组的容量够用.参考{@link java.util.ArrayList#ensureExplicitCapacity(int)}.
     *
     * @param minCapacity
     *         the desired minimum capacity
     */
    private void ensureExplicitDoubleArrayCapacity(int minCapacity) {
        if (minCapacity > base.length()) {
            growDoubleArray(minCapacity);
        }
    }

    /**
     * 数组扩容.参考 {@link java.util.ArrayList#grow(int)}.
     *
     * @param minCapacity
     *         the desired minimum capacity
     */
    private void growDoubleArray(int minCapacity) {
        int newCapacity = base.grow(minCapacity);
        check.grow(minCapacity);
        arcsLeaving = Arrays.copyOf(arcsLeaving, newCapacity);
    }

    /**
     * returns the minimum integer q such that q> 0 and CHECK [ q+c ] =0 for all c in LIST.
     * q always starts with the value 1 and has unitary increments at analysis time.
     * <p>
     * 有没有发现,这里要找的q,其实就是 g(n,a)=m 定义中,BASE[n]的值.
     * </p>
     *
     * @param array
     *         character array
     * @return minimum q
     */
    private int x_check(Character... array) {
        int q = 1;
        if (maxIndex > 100000) {
            q = (int) (maxIndex * 0.87);
        }
        boolean find = false;
        int index = 0;
        while (!find) {
            for (; index < array.length; index++) {
                char c = array[index];
                int a = getCodePoint(c);
                int m = q + a; // 有没有发现和g(n,a)=m的关系?
                ensureExplicitDoubleArrayCapacity(m + 1);
                if (getIntArray(check, m, false) != 0) {
                    index = 0;
                    q++; // 当前q不适合,重新寻找
                    break;
                }
            }
            find = index == array.length; // 如果最后一个字符也成功处理,则表明已经找到满足条件的q
        }
        maxQ = Math.max(maxQ, q);
        return q;
    }

    private int x_check(List<Character> childrenArc, Character candidateArc) {
        int size = childrenArc.size();
        maxChildCount = Math.max(maxChildCount, size);
        if (candidateArc != null) {
            size = size + 1;
        }
        Character[] chars = new Character[size];
        for (int index = 0; index < childrenArc.size(); index++) {
            chars[index] = childrenArc.get(index);
        }
        if (candidateArc != null) {
            chars[childrenArc.size()] = candidateArc;
        }
        return x_check(chars);
    }

    /**
     * set value.
     *
     * @param intArray
     *         目标数组
     * @param index
     *         数组下标
     * @param value
     *         值
     */
    private void setIntArray(IntArray intArray, int index, int value) {
        ensureExplicitDoubleArrayCapacity(index + 1);
        intArray.set(index, value);
        maxIndex = Math.max(maxIndex, index);
    }

    /**
     * get value.
     *
     * @param intArray
     *         目标数组
     * @param index
     *         数组下标
     * @param expandCapacity
     *         当下标值大于数组容量时,是否需要动态扩容数组
     * @return int value
     */
    private int getIntArray(IntArray intArray, int index, boolean expandCapacity) {
        if (expandCapacity) {
            ensureExplicitDoubleArrayCapacity(index + 1);
        }
        return intArray.get(index);
    }

    @Override
    public String toString() {
        return "maxIndex:" + maxIndex + ",base.length:" + base.length()
                + ",maxQ:" + maxQ + ",maxChildCount:" + maxChildCount + ",collisionCount:" + collisionCount;
    }
}
