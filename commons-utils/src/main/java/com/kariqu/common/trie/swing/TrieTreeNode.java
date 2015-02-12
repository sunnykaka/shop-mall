package com.kariqu.common.trie.swing;


import com.kariqu.common.trie.TrieNode;

import javax.swing.tree.TreeNode;
import java.util.*;

/**
 * User: Asion
 * Date: 12-6-22
 * Time: 下午3:29
 */
// A TreeNode for a TrieNode
class TrieTreeNode implements TreeNode, Comparable<TrieTreeNode> {
    final TrieTreeNode parent;
    final TrieNode trieNode;

    // flyweight design pattern
    private static final Map<TrieNode, TrieTreeNode> tNodeMapping = new HashMap<TrieNode, TrieTreeNode>();

    private TrieTreeNode(TrieTreeNode parent, TrieNode trieNode) {
        this.parent = parent;
        this.trieNode = trieNode;
    }

    public static TrieTreeNode newInstance(TrieTreeNode parent, TrieNode trieNode) {
        if (tNodeMapping.containsKey(trieNode))
            return tNodeMapping.get(trieNode);
        else {
            TrieTreeNode trieTreeNode = new TrieTreeNode(parent, trieNode);
            tNodeMapping.put(trieNode, trieTreeNode);
            return trieTreeNode;
        }
    }

    public int compareTo(TrieTreeNode o) {
        return trieNode.getChar() - o.trieNode.getChar();
    }

    public Enumeration children() {
        if (trieNode.getChildren() == null)
            return Collections.enumeration(Collections.EMPTY_SET);

        Set<Map.Entry<Character, TrieNode>> entries = trieNode.getChildren().entrySet();
        List<TrieTreeNode> trieTreeNodes = new ArrayList<TrieTreeNode>(entries.size());

        for (Map.Entry<Character, TrieNode> entry : entries) {
            trieTreeNodes.add(newInstance(this, entry.getValue()));
        }

        Collections.sort(trieTreeNodes);

        return Collections.enumeration(trieTreeNodes);
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public TreeNode getChildAt(int childIndex) {
        if (trieNode.getChildren() == null)
            return null;

        Enumeration e = children();
        int i = 0;
        while (e.hasMoreElements()) {
            if (i++ == childIndex)
                return (TreeNode) e.nextElement();
            else
                e.nextElement();
        }

        return null;
    }

    public int getChildCount() {
        if (trieNode.getChildren() == null)
            return 0;

        return trieNode.getChildren().entrySet().size();
    }

    public int getIndex(TreeNode node) {
        Enumeration e = children();
        int i = 0;
        while (e.hasMoreElements()) {
            TreeNode treeNode = (TreeNode) e.nextElement();
            if (treeNode.equals(node))
                return i;

            i++;
        }
        return -1;
    }

    public TreeNode getParent() {
        return parent;
    }

    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    private String getWord() {
        if (parent == null)
            return "";

        return parent.getWord() + trieNode.getChar();
    }

    @Override
    public String toString() {
        return getWord();
    }
}