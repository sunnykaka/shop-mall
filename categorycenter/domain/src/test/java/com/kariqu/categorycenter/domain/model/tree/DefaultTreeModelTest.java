package com.kariqu.categorycenter.domain.model.tree;

import junit.framework.TestCase;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-6-28 下午5:19
 */
public class DefaultTreeModelTest extends TestCase {

    /**
     * 0
     * |       \
     * 1       2
     * |   \  /  \  \
     * 11  12 21 22  23
     */
    Category root = new Category(0);

    Category c1 = new Category(1);
    Category c2 = new Category(2);

    Category c11 = new Category(11);
    Category c12 = new Category(12);
    Category c21 = new Category(21);
    Category c22 = new Category(22);
    Category c23 = new Category(23);
    Node<Category> rootN = new DefaultNode<Category>(root);
    Node<Category> n1 = new DefaultNode<Category>(c1);
    Node<Category> n2 = new DefaultNode<Category>(c2);
    Node<Category> n11 = new DefaultNode<Category>(c11);
    Node<Category> n12 = new DefaultNode<Category>(c12);
    Node<Category> n21 = new DefaultNode<Category>(c21);
    Node<Category> n22 = new DefaultNode<Category>(c22);
    Node<Category> n23 = new DefaultNode<Category>(c23);

    DefaultTreeModel<Category> treeModel = new DefaultTreeModel<Category>();

    public void setUp() {
        n1.setParent(rootN);
        n2.setParent(rootN);

        n11.setParent(n1);
        n12.setParent(n1);

        n21.setParent(n2);
        n22.setParent(n2);
        n23.setParent(n2);


        treeModel.addNode(rootN);

        treeModel.addNode(n11);
        treeModel.addNode(n12);
        treeModel.addNode(n21);
        treeModel.addNode(n22);
        treeModel.addNode(n23);
        treeModel.addNode(n1);
        treeModel.addNode(n2);

    }

    public void testAddNode() throws Exception {
        Category root = new Category(0);

        Category c1 = new Category(1);
        Category c2 = new Category(2);

        Category c11 = new Category(11);
        Category c12 = new Category(12);
        Category c21 = new Category(21);
        Category c22 = new Category(22);
        Category c23 = new Category(23);
        Node<Category> rootN = new DefaultNode<Category>(root);
        Node<Category> n1 = new DefaultNode<Category>(c1);
        Node<Category> n2 = new DefaultNode<Category>(c2);
        Node<Category> n11 = new DefaultNode<Category>(c11);
        Node<Category> n12 = new DefaultNode<Category>(c12);
        Node<Category> n21 = new DefaultNode<Category>(c21);
        Node<Category> n22 = new DefaultNode<Category>(c22);
        Node<Category> n23 = new DefaultNode<Category>(c23);

        DefaultTreeModel<Category> treeModel = new DefaultTreeModel<Category>();
        n11.setParent(n1);
        n12.setParent(n1);
        treeModel.addNode(n11);
        treeModel.addNode(n12);

        n1.setParent(rootN);
        treeModel.addNode(n1);

        n21.setParent(n2);
        n22.setParent(n2);
        n23.setParent(n2);
        treeModel.addNode(n21);
        treeModel.addNode(n22);
        treeModel.addNode(n23);
        n2.setParent(rootN);
        treeModel.addNode(n2);

    }

    public void testGetChildrenByParentId() throws Exception {
        final List<Node<Category>> n2Children = treeModel.getChildrenByParentId(2);
        assertEquals(3, n2Children.size());
        for (Node<Category> n2Child : n2Children) {
            if (n2Child.getModel().getId().equals(21)) {
                assertTrue(treeModel.getNodeById(21) == n2Child);
                continue;
            }
            if (n2Child.getModel().getId().equals(22)) {
                assertTrue(treeModel.getNodeById(22) == n2Child);
                continue;
            }
            if (n2Child.getModel().getId().equals(23)) {
                assertTrue(treeModel.getNodeById(23) == n2Child);
            }

        }
    }

    public void testGetNodeById() throws Exception {


        final Node<Category> node1 = treeModel.getNodeById(1);
        final List<Node<Category>> n1Children = node1.getChildren();
        for (Node<Category> n1Child : n1Children) {
            if (n1Child.getModel().getId().equals(11)) {
                assertTrue(n1Child == treeModel.getNodeById(11));
            }
            if (n1Child.getModel().getId().equals(12)) {
                assertTrue(n1Child == treeModel.getNodeById(12));
            }
            assertTrue(n1Child.isLeaf());

        }


    }
}
