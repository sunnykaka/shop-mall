package com.kariqu.common.trie;

import java.util.Collection;

/**
 * Trie树抽象接口
 * User: Asion
 * Date: 12-6-22
 * Time: 下午3:15
 */
public interface Trie {

    /**
     * 插入一个字符串到trie树中
     *
     * @param word The word to insert into the trie.
     * @return 字符串在trie中被加入的次数
     */
    int insert(String word);


    /**
     * 将一个字符串和这个字符串索引的数据放入trie树
     *
     * @param word
     * @param data
     * @return
     */
    int insert(String word, String data);


    /**
     * 通过字符串在trie树中搜索数据
     *
     * @param word
     * @return
     */
    Collection<String> findData(String word);


    /**
     * 指定某个时间返回于指定字符窜最佳匹配的字符串
     * 指定时间是毫秒数
     *
     * @param word     The word to approximate.
     * @param max_time How long before returning the current best match (via early exit), in millis.
     * @return The best match to the word that exists in this trie.
     */
    String bestMatch(String word, long max_time);

    /**
     * 检查trie树中是否包含某个字符串
     *
     * @param word The word to check.
     * @return If this trie contains the word.
     */
    boolean contains(String word);

    /**
     * 返回字符串在trie树中的频率
     *
     * @param word The word to check.
     * @return 字符串在trie中加入的次数
     */
    int frequency(String word);


    /**
     * 在trie中删除一个字符串
     *
     * @param word
     * @return
     */
    boolean remove(String word);

    /**
     * @return 返回tire中唯一字符串的个数
     */
    int size();


    /**
     * 返回根节点，这个节点是一个伪节点
     *
     * @return
     */
    TrieNode getRootNode();

}
