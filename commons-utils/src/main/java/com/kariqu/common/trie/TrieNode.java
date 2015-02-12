package com.kariqu.common.trie;

import java.io.Serializable;
import java.util.*;

/**
 * 树节点
 * User: Asion
 * Date: 12-6-22
 * Time: 下午3:36
 */
public class TrieNode implements Serializable {

    /**
     * 节点上的字符
     */
    private char c;

    /**
     * 标记到达这个节点的字符串的频率
     */
    private int occurances;

    /**
     * 节点上存储的数据
     */
    private Set<String> data;

    /**
     * 节点的孩子，多叉树
     */
    private Map<Character, TrieNode> children;

    TrieNode(char c) {
        this.c = c;
        occurances = 0;
        children = null;
    }

    /**
     * 在节点上插入字符串
     *
     * @param s
     * @param position
     * @return
     */
    int insert(String s, int position, String data) {
        if (s == null || position >= s.length())
            return 0;

        // allocate on demand
        if (children == null)
            children = new HashMap<Character, TrieNode>();

        char c = s.charAt(position);
        TrieNode node = children.get(c);

        // make sure we have a child with char c
        if (node == null) {
            node = new TrieNode(c);
            children.put(c, node);
        }

        //如果字符串的最后一个字符被插入完毕，则标记最后一个节点
        if (position == s.length() - 1) {
            node.occurances++;
            if (node.data == null) {
                node.data = new HashSet<String>();
            }
            node.data.add(data);
            return node.occurances;
        } else
            return node.insert(s, position + 1, data);
    }

    /**
     * 在节点上删除字符串
     *
     * @param s
     * @param position
     * @return
     */
    boolean remove(String s, int position) {
        if (children == null || s == null)
            return false;

        char c = s.charAt(position);
        TrieNode n = children.get(c);

        if (n == null)
            return false;

        boolean ret;
        if (position == s.length() - 1) {
            int before = n.occurances;
            n.occurances = 0;
            ret = before > 0;
        } else {
            ret = n.remove(s, position + 1);
        }

        // if we just removed a leaf, prune upwards
        if (n.children == null && n.occurances == 0) {
            children.remove(n.c);
            if (children.size() == 0)
                children = null;
        }

        return ret;
    }

    TrieNode lookup(String s, int position) {
        if (s == null)
            return null;

        if (position >= s.length() || children == null)
            return null;
        else if (position == s.length() - 1)
            return children.get(s.charAt(position));
        else {
            TrieNode n = children.get(s.charAt(position));
            return n == null ? null : n.lookup(s, position + 1);
        }
    }

    // debugging facility
    private void dump(StringBuilder sb, String prefix) {
        sb.append(prefix + c + "(" + occurances + ")" + "\n");

        if (children == null)
            return;

        for (TrieNode n : children.values())
            n.dump(sb, prefix + (c == 0 ? "" : c));
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public char getChar() {
        return c;
    }

    public int getOccurances() {
        return occurances;
    }

    public Collection<String> getData() {
        return data;
    }
}
