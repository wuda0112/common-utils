package com.wuda.text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在一段文本中,提取重复出现的字符串.比如,文本内容是:"豆豆鞋报价,豆豆鞋批发",因为"豆豆鞋"重复出现,因此被提取出来.
 * 对于人来说,'豆豆鞋'是一个词,理应提取出来,但是,对于机器,尤其是没有词典时,如何提取出来呢?我们认为,一篇文章中,
 * 反复出现的字符串很有可能就是一个词,这里称为<i>term</i>,因此基于这样的规则,提取出这些反复出现的term.
 *
 * @author wuda
 */
public class RepeatedTermExtractor {

    /**
     * term长度的最小值;小于此值,即使重复出现,也不会被提取出来.
     */
    private int minTermLength = 2;

    /**
     * term长度的最大值;大于此值,即使重复出现,也不会被提取出来.
     */
    private int maxTermLength = 5;

    /**
     * 从输入文本中提取重复出现的字符串.
     *
     * @param text
     *         文本内容
     * @return key - term , value - term出现的频数,freq
     */
    public Map<String, AtomicInteger> getTerms(String text) {
        if (text == null || text.isEmpty()
                || text.length() < minTermLength * 2 /*text中至少能包含两个term,才有必要去查找*/) {
            return null;
        }
        text = text.trim().toLowerCase();
        int offset = 0;
        int length = 0;
        int compareOffset = offset + minTermLength;
        char[] charArray = text.toCharArray();
        Map<String, AtomicInteger> map = null;
        while (offset + length < charArray.length) {
            length = 0;
            boolean meetSeparator = false;//遇到了分隔符
            while (compareOffset + length < charArray.length) {
                char ch = charArray[offset + length];
                if (Punctuation.isSymbol(ch)) {
                    meetSeparator = true;
                    /*
                     * 此时【offset+length】所在的下标是此separator在数组的下标,减一可以定位到separator之前的位置,
                     * 保证length只包含'有效的'字符.
                     */
                    length = length - 1;
                    break;
                }
                char compare = charArray[compareOffset + length];
                if (ch == compare) {
                    length++; // 向前推进
                } else {
                    break;
                }
            }
            if (length >= minTermLength && length <= maxTermLength) { // 成功提取term
                if (map == null) {
                    map = new HashMap<>();
                }
                String term = new String(charArray, offset, length);
                offset = offset + length;
                if (meetSeparator) {
                    offset = offset + 2; // 跳过separator.
                }
                putIntoMap(map, term); // 收集term
                compareOffset = offset + minTermLength;
            } else {
                if (meetSeparator) {
                    offset = offset + length + 2; // 不能构成term,并且又遇到了分隔符,则offset到分隔符(包含)的内容全部跳过
                    compareOffset = offset + minTermLength;
                } else {
                    compareOffset++;
                    if (compareOffset + minTermLength > charArray.length) {
                        offset++;
                        compareOffset = offset + minTermLength;
                    }
                }
            }
        }
        return map;
    }

    /**
     * term被允许的最小长度.
     *
     * @return 最小长度
     */
    public int getMinTermLength() {
        return minTermLength;
    }

    /**
     * 小于此长度的term,即使重复出现多次,也不提取.
     *
     * @param minTermLength
     *         最小长度
     */
    public void setMinTermLength(int minTermLength) {
        if (minTermLength <= 1) {
            throw new IllegalArgumentException("minTermLength必须大于1");
        }
        if (minTermLength > maxTermLength) {
            throw new IllegalArgumentException("minTermLength必须小于等于maxTermLength,maxTermLength=" + maxTermLength);
        }
        this.minTermLength = minTermLength;
    }

    /**
     * term被允许的最大长度.
     *
     * @return 最大长度
     */
    public int getMaxTermLength() {
        return maxTermLength;
    }

    /**
     * 大于此长度的term,即使重复出现多次,也不提取.
     *
     * @param maxTermLength
     *         最大长度
     */
    public void setMaxTermLength(int maxTermLength) {
        if (maxTermLength < minTermLength) {
            throw new IllegalArgumentException("maxTermLength必须大于等于minTermLength,minTermLength=" + minTermLength);
        }
        this.maxTermLength = maxTermLength;
    }

    private void putIntoMap(Map<String, AtomicInteger> map, String term) {
        if (map.containsKey(term)) {
            map.get(term).incrementAndGet();
        } else {
            map.put(term, new AtomicInteger(2));
        }
    }
}
