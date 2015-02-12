package com.kariqu.common.trie.swing;

import com.kariqu.common.trie.TrieImpl;

import javax.swing.tree.DefaultTreeModel;

/**
 * User: Asion
 * Date: 12-6-22
 * Time: 下午3:20
 */
// A TreeModel for a Trie
public class TrieTreeModel extends DefaultTreeModel {
    TrieTreeModel(TrieImpl trie) {
        super(TrieTreeNode.newInstance(null, trie.getRootNode()));
    }
}
