package com.wuda.tree;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * full double-array trie test.
 */
public class FullDoubleArrayTrieTest {

    /**
     * 词典文件.一行一个单词.
     */
    private String file;

    public void setFile(String file) {
        this.file = file;
    }

    /**
     * 将词典中的所有单词插入double-array trie中.
     *
     * @return double-array trie
     */
    public FullDoubleArrayTrie insert() {
        if (file == null) {
            throw new RuntimeException("请先指定词典文件!");
        }
        long start = System.currentTimeMillis();
        FullDoubleArrayTrie doubleArrayTrie = new FullDoubleArrayTrie(25000000);
        AtomicInteger count = new AtomicInteger();
        try {
            Files.lines(Paths.get(file)).filter((String line) -> {
                if (line == null || line.isEmpty()) {
                    return false;
                }
                return true;
            }).forEach(line -> {
                doubleArrayTrie.add(line);
                count.incrementAndGet();
                if (count.intValue() % 1000 == 0) {
                    System.out.println("已经添加单词数:" + count.get() + "," + doubleArrayTrie.toString());
                }
            });
            long end = System.currentTimeMillis();
            System.out.println("插入单词数:" + count.get() + ",总共耗时:" + (end - start) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return doubleArrayTrie;
    }

    /**
     * 搜索刚刚插入的所有单词.
     *
     * @param doubleArrayTrie
     *         double-array trie
     */
    public void searchIn(FullDoubleArrayTrie doubleArrayTrie) {
        if (file == null) {
            throw new RuntimeException("请先指定词典文件!");
        }
        StringBuilder builder = new StringBuilder();
        AtomicInteger count = new AtomicInteger();
        try {
            long start = System.currentTimeMillis();
            Files.lines(Paths.get(file)).parallel().filter(line -> {
                if (line == null || line.isEmpty()) {
                    return false;
                }
                return true;
            }).forEach(line -> {
                boolean contains = doubleArrayTrie.contains(line);
                count.incrementAndGet();
                if (!contains) {
                    builder.append(line);
                    builder.append(",");
                }
            });
            long end = System.currentTimeMillis();
            System.out.println("总共查找次数:" + count.get() + ",总耗时:" + (end - start) + "毫秒");
            if (builder.length() > 0) {
                System.out.println("在trie中没有找到的term有:" + builder.substring(0, builder.length() - 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 搜索刚刚插入的所有单词.
     *
     * @param doubleArrayTrie
     *         double-array trie
     * @param term
     *         term
     * @return 是否包含
     */
    public boolean contains(FullDoubleArrayTrie doubleArrayTrie, String term) {
        return doubleArrayTrie.contains(term);
    }

    public static void main(String[] args) {
        FullDoubleArrayTrieTest test = new FullDoubleArrayTrieTest();
        test.setFile("F:/360.dic"); // 词典所在的文件
//        test.setFile("F:/test.dic"); // 词典所在的文件
        FullDoubleArrayTrie doubleArrayTrie = test.insert();
        String summary = doubleArrayTrie.toString();
        System.out.println(System.currentTimeMillis() + "===============summary==================");
        System.out.println(summary);
        System.out.println(System.currentTimeMillis() + "===============start search==================");
        test.searchIn(doubleArrayTrie);
        System.out.println(System.currentTimeMillis() + "===============end search==================");
        String term = "豆豆鞋";
        System.out.println("contains term " + term + "? " + test.contains(doubleArrayTrie, term));
        String term1 = "糖衣片";
        System.out.println("contains term " + term1 + "? " + test.contains(doubleArrayTrie, term1));
    }
}
