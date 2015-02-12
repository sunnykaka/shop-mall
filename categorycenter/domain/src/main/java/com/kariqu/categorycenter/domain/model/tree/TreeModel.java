package com.kariqu.categorycenter.domain.model.tree;

import java.io.Serializable;
import java.util.List;

/**
 * 类目树形结构 领域对象
 *
 * @Author: Tiger
 * @Since: 11-6-25 下午2:42
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
public interface TreeModel<Model extends TreeModelType<? extends Serializable>> {

    /**
     * 增加一个新的节点,node需要指定父亲节点 ,并且只能从树根开始构造
     * @param node
     */
    void addNode(Node<Model> node);

    /**
     * @param modelId
     * @return
     */
    Node<Model> getNodeById(Serializable modelId);

    /**
     * 根据父亲节点的ID查询所有的孩子节点
     *
     * @param parentId
     * @return
     */
    List<Node<Model>> getChildrenByParentId(Serializable parentId);
}
