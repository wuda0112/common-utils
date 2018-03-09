package com.wuda.text;

import java.util.TreeMap;

/**
 * 标记文本解析器.
 *
 * @author wuda
 */
public class MarkupTextParser {

    private char slash = 47;// 斜线
    private char openBracket = 91;// 开方括号"["
    private char closeBracket = 93;// 闭方括号"]"
    private char nul = 0; // null
    private boolean compatiblePeoplesDaily = true; // 是否兼容【人民日报】的标注规范

    /**
     * 解析标记文本.
     * 对于被标记的文本,每个斜线("/")前的内容,我们称之为<i>term</i>,斜线后的内容我们称为<i>pos</i>(Part of speech),对于如下的文本:
     * <pre>
     * 今天/a 下午/b [广州/c 车主/d]/e     停车场/f 内/g 堵/h 3/i    小时/j  [非常 痛苦/k]n
     * </pre>
     * 通过此方法解析后的结果是:
     * <pre>
     * TreeMap treeMap;
     * treeMap.get("今天") = a;
     * treeMap.get("下午") = b;
     * treeMap.get("广州") = c;
     * treeMap.get("车主") = d;
     * treeMap.get("广州车主") = e;
     * treeMap.get("停车场") = f;
     * treeMap.get("内") = g;
     * treeMap.get("堵") = h;
     * treeMap.get("3") = i;
     * treeMap.get("小时") = j;
     * treeMap.get("非常") = null;
     * treeMap.get("痛苦") = k;
     * treeMap.get("非常痛苦") = n.
     * </pre>
     *
     * @param document
     *         标记的文本
     * @return term/pos pair
     */
    public TreeMap<String, String> parse(String document) {
        if (document == null || document.isEmpty()) {
            return null;
        }
        document = document.trim();
        TreeMap<String, String> treeMap = new TreeMap<>((String k1, String k2) -> {
            return 1; // term按照在document中出现的"大致"顺序排序
        });
        //@formatter:off
		/*
		 * 对于每一个【term/pos】这样的单元,我们发现,在文本中他们都是【(分隔符)(term/pos)(分隔符)】这样的结构.因此,由以下3个核心变量就可以解析出term/pos对(pair).
		 * (preDelimiterIndex,slashIndex)范围内的字符表示term的值,
		 * (slashIndex,currentDelimiterIndex)范围内的字符表示pos.
		 * 在上面的例子中【下午/b】这个单元的preDelimiterIndex=4,slashIndex=7,currentDelimiterIndex=9.
		 */
		//@formatter:on
        int preDelimiterIndex; // 上一个分隔符的下标
        int slashIndex = -1; // 斜线的下标
        int currentDelimiterIndex = -1; // 当前分隔符的下标

        StringBuilder termsInBrackets = null;// 中括号里的所有term组成一个整体的term,比如例子中的"[广州/c 车主/d]/e".
        boolean parsingBracketsGrammar = false; // 是否正在解析方括号语法

        String[] termAndPos;
        char[] charArray = document.toCharArray();
        for (int currentIndex = 0; currentIndex < charArray.length; currentIndex++) {
            char currentChar = charArray[currentIndex];
            if (currentChar == slash) { // 当前字符是斜线
                slashIndex = currentIndex;
            } else if (isDelimiter(currentChar)) { // 当前字符是分隔符
                preDelimiterIndex = currentDelimiterIndex;
                currentDelimiterIndex = currentIndex;
                // 提取可能的term和pos
                termAndPos = getTermAndPos(document, preDelimiterIndex, slashIndex, currentDelimiterIndex);
                putIntoMap(treeMap, termAndPos);
                // 中括号里面也可能会有分隔符
                if (parsingBracketsGrammar && termAndPos != null) {
                    termsInBrackets.append(termAndPos[0]);
                }
                /*
                 * 处理连续的分隔符,比如在上面的例子中【3/f】和【小时/g】中间出现了好多分隔符.
                 */
                int count = continuousDelimiterCount(charArray, currentIndex);
                currentDelimiterIndex += count;
                currentIndex = currentDelimiterIndex; // 向前推进
            } else if (currentChar == openBracket) { // 当前字符是开方括号"[".
                char next = next(charArray, currentIndex);
                char previous = previous(charArray, currentIndex);
                if ((previous == nul || isDelimiter(previous))
                        && (next != nul && next != slash && !isDelimiter(next))) {
                    currentDelimiterIndex = currentIndex;// 此时的开方括号"["被认为是分隔符,虽然语义上有点不符
                    parsingBracketsGrammar = true;
                    termsInBrackets = new StringBuilder();
                }
            } else if (currentChar == closeBracket) {// 当前字符是闭方括号"]".
                char next = next(charArray, currentIndex);
                char previous = previous(charArray, currentIndex);
                if ((next == nul || isDelimiter(next) || next == slash || compatiblePeoplesDaily)
                        && (previous != nul && previous != slash && !isDelimiter(previous)) && parsingBracketsGrammar) {
                    preDelimiterIndex = currentDelimiterIndex;
                    currentDelimiterIndex = currentIndex;//此时的闭方括号"]"被认为是分隔符,虽然语义上有点不符
                    // 紧贴着闭中括号左侧的标记内容,比如【[广州/c 车主/d]/e】的"车主/d"紧贴着闭方括号.
                    termAndPos = getTermAndPos(document, preDelimiterIndex, slashIndex, currentDelimiterIndex);
                    putIntoMap(treeMap, termAndPos);
                    if (termAndPos != null) {
                        termsInBrackets.append(termAndPos[0]);
                    }
                    String term = termsInBrackets.toString();// 中括号里的所有term组成一个整体的term
                    String pos = getPosAfterCloseBracket(charArray, currentIndex);// 获取闭中括号"]"后的pos
                    treeMap.put(term, pos);
                    if (pos != null) {
                        currentDelimiterIndex = currentIndex + 1/* 斜线 */ + pos.length() + 1/* 分隔符 */;
                    } else {
                        currentDelimiterIndex = currentIndex + 1/* 分隔符 */;
                    }
                    // 正在方括号语法中,遇到闭中括号,则方括号语法结束
                    parsingBracketsGrammar = false;
                    termsInBrackets = null;
                    currentIndex = currentDelimiterIndex;
                }
            }
        }
        // 处理到数组的最后的时候
        preDelimiterIndex = currentDelimiterIndex;
        currentDelimiterIndex = document.length();
        termAndPos = getTermAndPos(document, preDelimiterIndex, slashIndex, currentDelimiterIndex);
        putIntoMap(treeMap, termAndPos);
        return treeMap;
    }

    private static void putIntoMap(TreeMap<String, String> treeMap, String[] termAndPos) {
        if (termAndPos != null) {
            String term = termAndPos[0];
            String pos = termAndPos[1];
            treeMap.put(term, pos);
        }
    }

    /**
     * 获取term和pos.
     *
     * @param text
     *         source
     * @param preDelimiterIndex
     *         上一个分隔符的下标
     * @param slashIndex
     *         斜线所在的下标
     * @param currentDelimiterIndex
     *         当前分隔符的下标
     * @return null-如果给定的下标不能在给定的文本中定位字符
     */
    private static String[] getTermAndPos(String text, int preDelimiterIndex, int slashIndex,
                                          int currentDelimiterIndex) {
        int length = text.length();
        if (preDelimiterIndex >= length - 1) {
            return null;
        }
        String[] termAndPos = new String[2];
        String term;
        String pos;
        if (slashIndex > preDelimiterIndex && slashIndex < currentDelimiterIndex) { // term有词性
            term = text.substring(preDelimiterIndex + 1, slashIndex);
            pos = text.substring(slashIndex + 1, currentDelimiterIndex);
        } else {
            term = text.substring(preDelimiterIndex + 1, currentDelimiterIndex);
            pos = null;
        }
        term = term.trim().toLowerCase();
        term = term.intern();
        if (pos != null) {
            pos = pos.trim().toLowerCase();
            pos = pos.intern();
        }
        termAndPos[0] = term;
        termAndPos[1] = pos;
        return termAndPos;
    }

    /**
     * 是否分隔符
     *
     * @param ch
     *         char
     * @return true-是
     */
    private static boolean isDelimiter(char ch) {
        return Character.isWhitespace(ch) || Character.isSpaceChar(ch);
    }

    /**
     * 【以utf-8格式编码】的数据,首字符是【65279】.
     */
    @SuppressWarnings("unused")
    private void remove65279() {

    }

    /**
     * <i>currentIndex</i>所在下标的下一个字符
     *
     * @param charArray
     *         source
     * @param currentIndex
     *         当前下标
     * @return 下一个char, 如果下标越界, 则返回null
     */
    private char next(char[] charArray, int currentIndex) {
        int nextIndex = currentIndex + 1;
        if (nextIndex < 0 || nextIndex >= charArray.length) {
            return nul;
        }
        return charArray[nextIndex];
    }

    /**
     * <i>currentIndex</i>所在下标的上一个字符
     *
     * @param charArray
     *         source
     * @param currentIndex
     *         当前下标
     * @return 上一个char, 如果下标越界, 则返回null
     */
    private Character previous(char[] charArray, int currentIndex) {
        int previousIndex = currentIndex - 1;
        if (previousIndex < 0 || previousIndex >= charArray.length) {
            return nul;
        }
        return charArray[previousIndex];
    }

    /**
     * 闭中括号"]"后的pos.
     *
     * @param charArray
     *         source
     * @param closeBracketIndex
     *         闭中括号所在的下标
     * @return null-如果没有pos
     */
    private String getPosAfterCloseBracket(char[] charArray, int closeBracketIndex) {
        if (closeBracketIndex >= charArray.length - 1) { // 最后一个字符了
            return null;
        }
        int nextIndex = closeBracketIndex + 1;

        int slashIndex = -1; // 斜线的下标
        int delimiterIndex = -1; // 分隔符的下标

        while (nextIndex > 0 && nextIndex < charArray.length) {
            char ch = charArray[nextIndex];
            if (ch == slash) { // 斜线
                slashIndex = nextIndex;
            } else if (isDelimiter(ch)) {// 分隔符
                delimiterIndex = nextIndex;
                break;
            }
            nextIndex++;
        }
        if (slashIndex == -1) {
            slashIndex = closeBracketIndex;
        }
        if (delimiterIndex == -1) {
            delimiterIndex = charArray.length - 1;
        }
        String pos = new String(charArray, slashIndex + 1, delimiterIndex - slashIndex);
        if (pos.trim().isEmpty()) {
            return null;
        }
        return pos;
    }

    /**
     * 给定下标<i>currentIndex</i>(不包含)后面有多少个连续的分隔符.
     *
     * @param charArray
     *         source
     * @param currentIndex
     *         当前下标
     * @return 当前下标后有多少个连续的分隔符
     */
    private static int continuousDelimiterCount(char[] charArray, int currentIndex) {
        int i = currentIndex + 1;
        int count = 0;
        while (i < charArray.length && isDelimiter(charArray[i])) {
            i++;
            count++;
        }
        return count;
    }
}
