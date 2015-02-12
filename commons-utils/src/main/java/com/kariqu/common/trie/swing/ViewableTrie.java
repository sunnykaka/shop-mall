package com.kariqu.common.trie.swing;

import com.kariqu.common.trie.TrieImpl;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * 可视化的Trie树
 * User: Asion
 * Date: 12-6-22
 * Time: 下午3:20
 */
public class ViewableTrie extends TrieImpl {

    private final TrieTreeModel treeModel;

    public ViewableTrie(boolean caseSensitive) {
        super(caseSensitive);
        treeModel = new TrieTreeModel(this);
    }

    @Override
    public int insert(String word) {
        int ret = super.insert(word);
        if (ret == 1) {
            treeModel.nodeStructureChanged((TreeNode) treeModel.getRoot());
        }
        return ret;
    }

    @Override
    public boolean remove(String word) {
        if (super.remove(word)) {
            treeModel.nodeStructureChanged((TreeNode) treeModel.getRoot());
            return true;
        }
        return false;
    }

    /**
     * Get a TreeModel representing this Trie.
     *
     * @return A TreeModel which will be updated with changes to this Trie.
     */
    public TreeModel getTreeModel() {
        return treeModel;
    }
}