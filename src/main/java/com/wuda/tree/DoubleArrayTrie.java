package com.wuda.tree;

import java.util.*;

/**
 * double-array trie,根据论文
 * <a href="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.14.8665&rep=rep1&type=pdf">An Efficient Implementation of Trie Structures</a>
 * 实现.
 *
 * @author wuda
 * @version 1.0
 */
public class DoubleArrayTrie {

    /**
     * 实现该算法最核心的元素.
     */
    private int[] base; // represent the BASE array.
    private int[] check; // represent the CHECK array
    private char[] tail; // represent the TAIL array
    private int pos = 1; // the pointer to TAIL array
    private char separator = '#';
    private char garbage = '?';

    /**
     * root node position.
     */
    private int rootPosition = 1;

    /**
     * 默认的容量.
     */
    private int default_capacity = 8;

    /**
     * 构造一个double-array trie,使用默认的容量.
     */
    public DoubleArrayTrie() {
        init(default_capacity);
    }

    /**
     * 构造一个double-array trie,使用指定的容量.不是term数量,无法精确double-array的长度,只是一个大致估计的值.
     *
     * @param capacity
     *         容量
     */
    public DoubleArrayTrie(int capacity) {
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
        base = new int[capacity];
        check = new int[capacity];
        tail = new char[capacity];
        ensureExplicitDoubleArrayCapacity(rootPosition + 1);
        setBase(rootPosition,1);
    }

    /**
     * 字符对应的code point,主要是为了测试用.
     */
    private Map<Character, Integer> codePointMap;

    /**
     * 添加指定的term到trie中.
     *
     * @param term
     *         term to be appended to this trie
     */
    public void add(String term) {
        if (term == null || term.trim().isEmpty()) {
            return;
        }
        term = term.trim().toLowerCase() + separator;
        int length = term.length();
        /*
         *if there is an arc g(n,a) =m on the reduced trie, then BASE [ n ] +a=m and CHECK [ m ]= n.
         * 因此用 n 表示 from node , m 表示 to node ,字符 a 表示两个节点之间的 arc.
         */
        int n = rootPosition; // 总是从root开始
        int a, m;
        int check_m; // 在m处,CHECK数组的值
        int base_n; // 在n处,BASE数组的值
        for (int index = 0; index < length; index++) {
            a = getCodePoint(term, index);
            ensureExplicitDoubleArrayCapacity(n + 1);
            base_n = base[n];
            // Case 3 occurs.
            if (base_n < 0) { // 此时,base_n的值指向TAIL数组
                // case 3 step 3
                String remainingInTail = retrievalRemainingFromTail(-base_n, true);
                String remainingOfCurrent = term.substring(index);
                if (remainingInTail.equals(remainingOfCurrent)) {
                    // 对比成功,说明当前正在插入的字符串之前已经成功插入到了trie中
                    break;
                } else {
                    String commonPrefix = getCommonPrefix(remainingInTail, remainingOfCurrent);
                    int temp = -base_n; // case 3 step 4
                    int commonPrefixLength = 0;
                    if (commonPrefix != null) { // 处理公共前缀
                        commonPrefixLength = commonPrefix.length();
                        for (int i = 0; i < commonPrefixLength; i++) {
                            char c = commonPrefix.charAt(i);
                            a = getCodePoint(c);
                            int q = x_check(c); // case 3 step 5
                            ensureExplicitDoubleArrayCapacity(n + 1);
                            setBase(n,q);// case 3 step 6
                            m = q + a;
                            ensureExplicitDoubleArrayCapacity(m + 1);
                            check[m] = n;
                            assert base[check[m]] >= 0 : "tail pointer node不能作为其他节点的父节点!";
                            n = m;
                        }
                    }
                    int separate_node_index = commonPrefixLength; // the separate node index
                    // case 3 step 7
                    char separate_node_1 = remainingInTail.charAt(separate_node_index);
                    char separate_node_2 = remainingOfCurrent.charAt(separate_node_index);
                    int q = x_check(separate_node_1, separate_node_2);
                    ensureExplicitDoubleArrayCapacity(n + 1);
                    setBase(n,q);
                    // case 3 step 8
                    int m_1 = q + getCodePoint(separate_node_1);
                    setBase(m_1,-temp);
                    check[m_1] = n;
                    assert base[check[m_1]] >= 0 : "tail pointer node不能作为其他节点的父节点!";
                    // case 3 step 9
                    int insertion_count_1 = remainingInTail.length() - (commonPrefixLength + 1/*separate node*/);
                    int separatorPos = insertIntoTailArray(remainingInTail, separate_node_index + 1, insertion_count_1, temp);
                    // 这里其实不做也可以,只是为了满足论文中的定义,也为了更好的可视化,便于调试
                    clearTailArray(separatorPos + 1, commonPrefixLength + 1/*和之前相比,就是公共前缀和separate node从TAIL数组中移除了*/);
                    // case 3 step 10
                    int m_2 = q + getCodePoint(separate_node_2);
                    setBase(m_2,-pos);
                    check[m_2] = n;
                    assert base[check[m_2]] >= 0 : "tail pointer node不能作为其他节点的父节点!";
                    int insertion_count_2 = remainingOfCurrent.length() - (commonPrefixLength + 1/*separate node*/);
                    insertIntoTailArray(remainingOfCurrent, separate_node_index + 1, insertion_count_2, this.pos);
                    // case 3 step 11
                    ensureExplicitPos(insertion_count_2);
                    break;
                }
            }
            m = base_n + a; // g(n,a) = m
            ensureExplicitDoubleArrayCapacity(m + 1);
            check_m = check[m];
            // The value 0 in CHECK [m] indicates insertion of the rest of the word,That is store into TAIL the remaining string
            // node m called separate node.
            if (check_m == 0) {
                int offset = index + 1;
                int count = length - offset;
                insertIntoTailArray(term, offset, count, this.pos);
                setBase(m,-pos);
                check[m] = n;
                assert base[check[m]] >= 0 : "tail pointer node不能作为其他节点的父节点!";
                ensureExplicitPos(count);
                break;
            } else if (check_m != n) {
                // Case 4 occurs

                // Case 4 step 11　中,【the original BASE node number, i.e. 3, where the inconsistency
                //　was generated (see step 3) as pivot】,通常情况下就是节点n,但是有一种情况下,就不是n了,即：冲突节点的子节点【包含节点n】
                int inconsistencyPivotNode = n;

                int collisionNode = check_m;// 发生冲突的节点
                // case 4 step 2
                int temp_node_1 = m;
                // case 4 step 3
                List<Character> arcsLeavingCurrentNode = arcsLeaving(n);
                List<Character> arcsLeavingCollisionNode = arcsLeaving(collisionNode);
                // case 4 step 4
                int modifyNode; // 两个节点冲突,最终决定要调整的节点
                List<Character> arcsLeavingModifyNode;
                List<Character> arcsLeavingModifyNodeForXCheck;
                if (m == n/*node经过一个arc后指向自己*/ || arcsLeavingCurrentNode.size() + 1 < arcsLeavingCollisionNode.size()) {
                    modifyNode = n;
                    arcsLeavingModifyNode = arcsLeavingCurrentNode;
                    arcsLeavingModifyNodeForXCheck = new ArrayList<>(arcsLeavingCurrentNode.size() + 1);
                    arcsLeavingModifyNodeForXCheck.addAll(arcsLeavingCurrentNode);
                    arcsLeavingModifyNodeForXCheck.add(term.charAt(index));//在使用x_check计算q值时,当前冲突的character也需要参与
                } else {
                    modifyNode = collisionNode;
                    arcsLeavingModifyNode = arcsLeavingCollisionNode;
                    arcsLeavingModifyNodeForXCheck = arcsLeavingCollisionNode;
                }
                // case 4 step 5
                int temp_base = base[modifyNode];
                int q = x_check(arcsLeavingModifyNodeForXCheck);
                setBase(modifyNode,q);
                int temp_node_2;
                for (char c : arcsLeavingModifyNode) {
                    // case 4 step 6
                    int codePoint = getCodePoint(c);
                    temp_node_1 = temp_base + codePoint;
                    temp_node_2 = base[modifyNode] + codePoint;
                    if (temp_node_1 == n) {
                        // 如果对应到论文【Figure 7】中，冲突的node 1 和 node 3,本来node 1 的子节点是node 4,
                        // 但是如果我们假设node 1 的子节点不是node 4,而是node 3 的话，就会发生这里的情况,此时node 3也要被新节点替换了.
                        inconsistencyPivotNode = temp_node_2;
                    }
                    int max = Math.max(temp_node_1, temp_node_2);
                    ensureExplicitDoubleArrayCapacity(max + 1);
                    setBase(temp_node_2, base[temp_node_1]);
                    check[temp_node_2] = check[temp_node_1];
                    assert base[check[temp_node_2]] >= 0 : "tail pointer node不能作为其他节点的父节点!";
                    // case 4 step 7
                    if (base[temp_node_1] > 0) {
                        List<Integer> children = nodesLeaving(temp_node_1);
                        for (int child : children) {
                            check[child] = temp_node_2;
                            assert base[check[child]] >= 0 : "tail pointer node不能作为其他节点的父节点!";
                        }
                    }
                    // case 4 step 8
                    setBase(temp_node_1, 0);
                    check[temp_node_1] = 0;
                }
                /*
                 * 以下其实都可以抽取公共,但是为了满足论文中的定义,方便理解，所以就这样处理.对于数据结构的实现,
                 * 理由理解是很重要的.
                 */
                // case 4 step 11
                int temp_node = base[inconsistencyPivotNode] + a;
                // case 4 step 12
                setBase(temp_node,-pos);
                check[temp_node] = inconsistencyPivotNode;
                assert base[check[temp_node]] >= 0 : "tail pointer node不能作为其他节点的父节点!";
                // case 4 step 13
                int offset = index + 1;
                int count = length - offset;
                insertIntoTailArray(term, offset, count, this.pos);
                // case 4 step 14
                ensureExplicitPos(count);
                break;
            }
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
        int n = rootPosition;
        int m = 0;
        int a;
        int base_n; //在base数组下标n处的值
        int check_m; //在check数组下标m出的值
        int doubleArrayLength = base.length;
        term = term.trim().toLowerCase() + separator;
        for (int index = 0; index < term.length(); index++) {
            if (n > doubleArrayLength || m > doubleArrayLength) {
                return false;
            }
            base_n = base[n];
            if (base_n < 0) {
                String remaining = retrievalRemainingFromTail(-base_n, true);
                if (compare(term, index, remaining)) {
                    return true;
                }
            }
            a = getCodePoint(term, index);
            m = base_n + a;
            check_m = check[m];
            if (check_m != n) {
                return false;
            }
            n = m;// forward
        }
        /*
         *考虑一种包含情形,比如then和the,并且在查找时,是在term最后加了separator以后去查找的.
         */
        return true;
    }

    /**
     * <i>src</i>指定位置(包含)以后的所有字符组成的字符串与<i>target</i>对比.
     *
     * @param src
     *         原字符串
     * @param start
     *         指定位置(包含)
     * @param target
     *         　对比的字符串
     * @return true-如果相等
     */
    private boolean compare(String src, int start, String target) {
        if (src == null || target == null) {
            return false;
        }
        if (src.length() - start != target.length()) {
            return false;
        }
        int index_1 = start;
        int index_2 = 0;
        for (; index_2 < target.length(); index_1++, index_2++) {
            if (src.charAt(index_1) != target.charAt(index_2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * found the characters that correspond to the arcs leaving that node.
     * 找出所有离开<i>node</i>的arcs(即character).
     *
     * @param n
     *         node index number,即在double-array中的下标
     * @return 所有离开这个节点的character
     */
    private List<Character> arcsLeaving(int n) {
        // TODO
        // 这里会很慢,因为要遍历整个check数组
        int base_n = base[n];
        List<Character> characters = new ArrayList<>();
        for (int m = rootPosition; m < check.length; m++) {
            int check_m = check[m];
            if (check_m == n) {
                int a = m - base_n; // g(n,a)=m　的逆推
                char ch = getCharacterByCodePoint(a);
                characters.add(ch);
            }
        }
        return characters;
    }

    /**
     * 找出所有离开<i>node</i>的子节点(children).
     *
     * @param node
     *         node index number,即在double-array中的下标
     * @return 所有离开这个节点的children
     * @see #arcsLeaving(int)
     */
    private List<Integer> nodesLeaving(int node) {
        // TODO
        // 这里会很慢,因为要遍历整个check数组
        List<Integer> children = new ArrayList<>();
        for (int m = rootPosition; m < check.length; m++) {
            int check_m = check[m];
            if (check_m == node) {
                children.add(m);
            }
        }
        return children;
    }

    /**
     * 根据code point,返回对应的character.
     *
     * @param codePoint
     *         code point
     * @return character
     * @see #getCodePoint(char)
     */
    private char getCharacterByCodePoint(int codePoint) {
        if (codePointMap != null) {
            Set<Map.Entry<Character, Integer>> entrySet = codePointMap.entrySet();
            for (Map.Entry<Character, Integer> entry : entrySet) {
                if (entry.getValue() == codePoint) {
                    return entry.getKey();
                }
            }
            throw new RuntimeException("code point : " + codePoint + " 没有对应的 character !");
        }
        return (char) codePoint;
    }

    /**
     * 计算{@link #pos}值.
     *
     * @param insertionCharCount
     *         插入TAIL数组的字符数.
     */
    private void ensureExplicitPos(int insertionCharCount) {
        this.pos = this.pos + insertionCharCount;
    }

    /**
     * 从TAIL数组的offset开始,将后面的count个字符清理(按照论文中的定义,即设置成<i>garbage</i>).
     *
     * @param offset
     *         从offset(包含)位置开始
     * @param count
     *         被清理的字符数
     */
    private void clearTailArray(int offset, int count) {
        for (int index = offset; count > 0; index++, count--) {
            tail[index] = garbage;
        }
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
    private int x_check(char... array) {
        int q = 1;
        boolean find = false;
        int index = 0;
        while (!find) {
            for (; index < array.length; index++) {
                char c = array[index];
                int a = getCodePoint(c);
                int m = q + a; // 有没有发现和g(n,a)=m的关系?
                ensureExplicitDoubleArrayCapacity(m + 1);
                if (check[m] != 0) {
                    index = 0;
                    q++; // 当前q不适合,重新寻找
                    break;
                }
            }
            find = index == array.length; // 如果最后一个字符也成功处理,则表明已经找到满足条件的q
        }
        return q;
    }

    /**
     * 和{@link #x_check(char...)}定义一致.
     *
     * @param list
     *         character list
     * @return the q
     * @see #x_check(char...)
     */
    private int x_check(List<Character> list) {
        char[] chars = new char[list.size()];
        for (int index = 0; index < list.size(); index++) {
            chars[index] = list.get(index);
        }
        return x_check(chars);
    }

    /**
     * 从TAIL数组中取回字符串.取回的内容是:指定下标到第一个{@link #separator}的所有字符.
     *
     * @param from
     *         从tail数组的这个位置(包含)开始
     * @param containSeparator
     *         是否包含{@link #separator}
     * @return string
     */
    private String retrievalRemainingFromTail(int from, boolean containSeparator) {
        StringBuilder builder = new StringBuilder();
        int index = from; // 从from处开始
        char c = tail[index];
        while (c != separator) {
            builder.append(c);
            c = tail[++index];
        }
        if (containSeparator) {
            builder.append(c); // separator
        }
        return builder.toString();
    }

    /**
     * 找出两个字符串的公共前缀.
     *
     * @param one
     *         one
     * @param another
     *         another
     * @return 公共前缀, null-表示没有公共前缀
     */
    private String getCommonPrefix(String one, String another) {
        StringBuilder builder = null;
        for (int index = 0; index < one.length(); index++) {
            if (another.length() <= index) {
                break;
            }
            char c = one.charAt(index);
            if (c == another.charAt(index)) {
                if (builder == null) {
                    builder = new StringBuilder();
                }
                builder.append(c);
            } else {
                break;
            }
        }
        return builder == null ? null : builder.toString();
    }

    /**
     * 将字符串中指定的字符插入TAIL数组中.
     *
     * @param src
     *         元素字符串
     * @param offset
     *         The initial offset
     * @param count
     *         The length
     * @param tailPos
     *         从TAIL数组的此下标开始
     * @return 本次最后一个字符插入的位置.
     */
    private int insertIntoTailArray(String src, int offset, int count, int tailPos) {
        assert count > 0;
        ensureExplicitTailArrayCapacity(tailPos + count);
        for (int index = offset; count > 0; index++, count--) {
            tail[tailPos] = src.charAt(index);
            tailPos++;
        }
        return tailPos - 1;
    }

    /**
     * 获取character对应的code point.
     *
     * @param item
     *         string
     * @return Returns the code point value at the
     * specified index
     */
    private int getCodePoint(String item, int index) {
        char c = item.charAt(index);
        return getCodePoint(c);
    }

    /**
     * 获取character对应的code point,不一定是unicode code point,也可以自己定义一个映射表,比如,有如下的映射表:
     * <pre>
     *      a : 1
     *      b : 2
     *      c : 3
     *      ......
     * </pre>
     * 则给定character a 时,返回<i>1</i>.
     *
     * @param c
     *         character
     * @return Returns the code point
     */
    private int getCodePoint(char c) {
        if (codePointMap != null) {
            Integer codePoint = codePointMap.get(c);
            if (codePoint == null) {
                throw new RuntimeException("character " + c + " 没有指定code point !");
            }
            return codePoint;
        }
        return c;
    }

    /**
     * 确保BASE和CHECK数组的容量够用.参考{@link java.util.ArrayList#ensureExplicitCapacity(int)}.
     *
     * @param minCapacity
     *         the desired minimum capacity
     */
    private void ensureExplicitDoubleArrayCapacity(int minCapacity) {
        if (minCapacity > base.length) {
            growDoubleArray(minCapacity);
        }
    }

    /**
     * 确保TAIL数组的容量够用.参考{@link java.util.ArrayList#ensureExplicitCapacity(int)}.
     *
     * @param minCapacity
     *         the desired minimum capacity
     */
    private void ensureExplicitTailArrayCapacity(int minCapacity) {
        if (minCapacity > tail.length) {
            growTail(minCapacity);
        }
    }

    /**
     * BASE和CHECK扩容.参考 {@link java.util.ArrayList#grow(int)}.
     *
     * @param minCapacity
     *         the desired minimum capacity
     */
    private void growDoubleArray(int minCapacity) {
        // BASE和CHECK数组的长度永远一致,所以取任意一个即可
        int oldCapacity = base.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }
        base = Arrays.copyOf(base, newCapacity);
        check = Arrays.copyOf(check, newCapacity);
    }

    /**
     * TAIL扩容.参考 {@link java.util.ArrayList#grow(int)}.
     *
     * @param minCapacity
     *         the desired minimum capacity
     */
    private void growTail(int minCapacity) {
        int oldCapacity = tail.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }
        tail = Arrays.copyOf(tail, newCapacity);
    }

    /**
     * 定义character和它对应的code point.此方法主要用于测试时使用.
     *
     * @param character
     *         character
     * @param codePoint
     *         code point value
     */
    public void defineCodePoint(Character character, Integer codePoint) {
        if (codePointMap == null) {
            codePointMap = new HashMap<>();
        }
        if (codePointMap.containsKey(character) && codePointMap.get(character).intValue() != codePoint.intValue()) {
            throw new RuntimeException("character=" + character + ",已经存在,并且两次的code point值不一致!");
        }
        codePointMap.put(character, codePoint);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        long doubleArrayLength = base.length;
        long tailLength = tail.length;
        int wastedInDoubleArray = wastedInDoubleArray();
        int wastedInTailArray = wastedInTailArray();
        builder.append("base length:");
        builder.append(doubleArrayLength);
        builder.append(",check length:");
        builder.append(doubleArrayLength);
        builder.append(",tail length:");
        builder.append(tailLength);
        builder.append("\nunused length in base:");
        builder.append(base.length - wastedInDoubleArray);
        builder.append(",unused length in check:");
        builder.append(check.length - wastedInDoubleArray);
        builder.append(",unused length in tail:");
        builder.append(tail.length - wastedInTailArray);
        builder.append("\nramUsedMB:");
        builder.append(ramUsedMB());
        builder.append(",ramUsedKB:");
        builder.append(ramUsedKB());
        builder.append(",ramUsedB:");
        builder.append(ramUsedB());
        return builder.toString();
    }

    /**
     * 返回double-array trie数据结构的视图.只能是数据量小的时候返回,方便测试用.
     *
     * @return view
     */
    public String view() {
        StringBuilder baseBuilder = new StringBuilder("[");
        for (int i : base) {
            baseBuilder.append(i);
            baseBuilder.append(",");
        }
        baseBuilder.delete(baseBuilder.length() - 1, baseBuilder.length());
        baseBuilder.append("]");

        StringBuilder checkBuilder = new StringBuilder("[");
        for (int i : check) {
            checkBuilder.append(i);
            checkBuilder.append(",");
        }
        checkBuilder.delete(checkBuilder.length() - 1, checkBuilder.length());
        checkBuilder.append("]");

        StringBuilder tailBuilder = new StringBuilder("[");
        for (char c : tail) {
            tailBuilder.append(c);
            tailBuilder.append(",");
        }
        tailBuilder.delete(tailBuilder.length() - 1, tailBuilder.length());
        tailBuilder.append("]");

        return "base :" +
                baseBuilder +
                "\n" +
                "check:" +
                checkBuilder +
                "\n" +
                "tail :" +
                tailBuilder +
                "\n" +
                "pos:" +
                pos;
    }

    /**
     * 返回大致的内存使用量,以b为单位.
     *
     * @return 大致的内存使用量
     */
    public long ramUsedB() {
        long bytesCount = base.length * 4;//一个数字4字节
        bytesCount += check.length * 4;
        bytesCount += tail.length * 2; // 一个 char 2字节
        return bytesCount;
    }

    /**
     * 返回大致的内存使用量,以kb为单位.不足1kb时,以1kb处理.
     *
     * @return 大致的内存使用量
     */
    public long ramUsedKB() {
        long bytesCount = ramUsedB();
        long unit = 1024;
        if (bytesCount < unit) {
            return 1;
        }
        return bytesCount / unit;
    }

    /**
     * 返回大致的内存使用量,以mb为单位.不足1mb时,以1mb处理.
     *
     * @return 大致的内存使用量
     */
    public long ramUsedMB() {
        long bytesCount = ramUsedB();
        int unit = 1048576;// 1024 * 1024;
        if (bytesCount < unit) {
            return 1;
        }
        return bytesCount / unit;
    }

    /**
     * 由于数组是动态扩容,因此很有可能扩容后有部分空间是没有被使用的.
     * double-array尾部,连续的0就是未使用的空间.
     *
     * @return waste start offset,表示从这个位置开始(包含)至double-array的尾部都是未使用的,
     * 如果返回-1,则表示没有任何浪费,所有分配空间都被使用了
     */
    private int wastedInDoubleArray() {
        int index = base.length - 1;
        int value = base[index];
        while (index > 0 && value == 0) {
            index--;
            value = base[index];
        }
        int offset = index + 1;
        return offset == base.length ? -1 : offset;
    }

    /**
     * 由于数组是动态扩容,因此很有可能扩容后有部分空间是没有被使用的.
     * <i>TAIL</i>数组尾部,连续的<code>null</code>就是未使用的空间.
     *
     * @return waste start offset, 表示从这个位置开始(包含)至tail数组的尾部都是未使用的,
     * 如果返回-1,则表示没有任何浪费,所有分配空间都被使用了
     */
    private int wastedInTailArray() {
        int index = tail.length - 1;
        char value = tail[index];
        while (index > 0 && value == 0) {
            index--;
            value = tail[index];
        }
        int offset = index + 1;
        return offset == tail.length ? -1 : offset;
    }

    /**
     * 在BASE数组的<i>index</i>处设置值.
     *
     * @param index
     *         BASE数组下标
     * @param value
     *         index处设置成给定的此value
     */
    private void setBase(int index, int value) {
        ensureExplicitDoubleArrayCapacity(index + 1);
        base[index] = value;
    }

}