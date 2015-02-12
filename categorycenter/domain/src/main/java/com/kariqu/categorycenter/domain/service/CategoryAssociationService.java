package com.kariqu.categorycenter.domain.service;

import java.util.List;
import java.util.Map;

/**
 * 类目的前后关联服务
 * User: Asion
 * Date: 11-10-25
 * Time: 下午1:13
 */
public interface CategoryAssociationService {

    /**
     * 创建前台类目到后台类目的关联，可以是一对多
     * 当且仅当创建完关联之后，前台类目才可能有类目属性，所有重新关联的的时候类目属性也要随之同步
     *
     * @param navCategoryId
     * @param categoryIds
     */

    public void createNavigateCategoryAssociation(int navCategoryId, List<Integer> categoryIds);

    /**
     * 根据后台类目ID查询关联的所有前台叶子ID
     *
     * @param categoryId
     * @return
     */
    List<Integer> queryAssociationByCategoryId(int categoryId);


    /**
     * 根据前台类目ID查询关联的后台类目ID
     *
     * @param navCategoryId
     * @return
     */
    List<Integer> queryAssociationByNavCategoryId(int navCategoryId);


    /**
     * 删除所有关联
     */
    void deleteAllAssociation();

    /**
     * 查询所有的关联对，Map中是以前台类目ID为key的List
     *
     * @return
     */
    Map<Integer, List<Integer>> queryAllAssociation();

}
