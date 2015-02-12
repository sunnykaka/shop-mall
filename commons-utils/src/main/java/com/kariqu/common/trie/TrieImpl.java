package com.kariqu.common.trie;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * Trie的实现
 * User: Asion
 * Date: 12-6-22
 * Time: 下午3:16
 */
public class TrieImpl implements Trie, Serializable {

    // 伪节点，放一个编码为0的字符
    private final TrieNode root;

    // 唯一字符串个数
    private int size;

    // 是否大小写
    private boolean caseSensitive;

    /**
     * @param caseSensitive If this Trie should be case-insensitive to the words it encounters.
     *                      Case-insensitivity is accomplished by converting String arguments to all functions to
     *                      lower-case before proceeding.
     */
    public TrieImpl(boolean caseSensitive) {
        root = new TrieNode((char) 0);
        size = 0;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public int insert(String word) {
        return insert(word, "");
    }

    @Override
    public int insert(String word, String data) {
        if (InvalidWord(word))
            return 0;
        int i = root.insert(caseSensitive ? word : word.toLowerCase(), 0, data);
        if (i == 1) {
            size++;
        }
        return i;
    }

    @Override
    public Collection<String> findData(String word) {
        if (InvalidWord(word)) {
            return Collections.EMPTY_LIST;
        }
        TrieNode node = root.lookup(caseSensitive ? word : word.toLowerCase(), 0);
        return node == null ? Collections.EMPTY_LIST : node.getData();
    }

    @Override
    public boolean remove(String word) {
        if (InvalidWord(word))
            return false;
        if (root.remove(caseSensitive ? word : word.toLowerCase(), 0)) {
            size--;
            return true;
        }
        return false;
    }

    @Override
    public int frequency(String word) {
        if (InvalidWord(word))
            return 0;
        TrieNode n = root.lookup(caseSensitive ? word : word.toLowerCase(), 0);
        return n == null ? 0 : n.getOccurances();
    }

    @Override
    public boolean contains(String word) {
        if (InvalidWord(word))
            return false;
        return root.lookup(caseSensitive ? word : word.toLowerCase(), 0) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    @Override
    public String bestMatch(String word, long max_time) {
        if (InvalidWord(word)) {
            return "";
        }

        if (!caseSensitive)
            word = word.toLowerCase();

        // we store candidate nodes in a pqueue in an attempt to find the optimal
        // match ASAP which can be useful for a necessary early exit
        PriorityQueue<DYMNode> pq = new PriorityQueue<DYMNode>();

        DYMNode best = new DYMNode(root, Distance.LD("", word), "", false);
        DYMNode cur = best;
        TrieNode tmp;
        int count = 0;
        long start_time = System.currentTimeMillis();

        while (true) {
            if (count++ % 1000 == 0 && (System.currentTimeMillis() - start_time) > max_time)
                break;

            if (cur.node.getChildren() != null) {
                for (char c : cur.node.getChildren().keySet()) {
                    tmp = cur.node.getChildren().get(c);
                    String tWord = cur.word + c;
                    int distance = Distance.LD(tWord, word);

                    // only add possibly better matches to the pqueue
                    if (distance <= cur.distance) {
                        if (tmp.getOccurances() == 0)
                            pq.add(new DYMNode(tmp, distance, tWord, false));
                        else
                            pq.add(new DYMNode(tmp, distance, tWord, true));
                    }
                }
            }

            DYMNode n = pq.poll();

            if (n == null)
                break;

            cur = n;
            if (n.wordExists)
                // if n is more optimal, set it as best
                if (n.distance < best.distance || (n.distance == best.distance && n.node.getOccurances() > best.node.getOccurances()))
                    best = n;
        }
        return best.word;
    }

    @Override
    public TrieNode getRootNode() {
        return root;
    }


    /**
     * 判断是否是无效字符串
     *
     * @param word
     * @return
     */
    private boolean InvalidWord(String word) {
        if (word == null || word.equals("")) {
            return true;
        }
        return false;
    }
}
/**
 * A node in a trie.
 * @author Brian Harris (brian_bcharris_net)
 */


/**
 * Utility class for finding a best match to a word.
 *
 * @author Brian Harris (brian_bcharris_net)
 */
class DYMNode implements Comparable<DYMNode> {
    TrieNode node;
    String word;
    int distance;
    boolean wordExists;

    DYMNode(TrieNode node, int distance, String word, boolean wordExists) {
        this.node = node;
        this.distance = distance;
        this.word = word;
        this.wordExists = wordExists;
    }

    // break ties of distance with frequency
    public int compareTo(DYMNode n) {
        if (distance == n.distance)
            return n.node.getOccurances() - node.getOccurances();
        return distance - n.distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null)
            return false;

        if (!(o instanceof DYMNode))
            return false;

        DYMNode n = (DYMNode) o;
        return distance == n.distance && n.node.getOccurances() == node.getOccurances();
    }

    @Override
    public int hashCode() {
        int hash = 31;
        hash += distance * 31;
        hash += node.getOccurances() * 31;
        return hash;
    }

    @Override
    public String toString() {
        return word + ":" + distance;
    }
}