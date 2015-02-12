package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.repository.NavigateCategoryRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-7-5
 * Time: 上午10:13
 */
public class NavigateCategoryRepositoryImpl extends SqlMapClientDaoSupport implements NavigateCategoryRepository {

    @Override
    public void createNavigateCategory(NavigateCategory navigateCategory) {
        getSqlMapClientTemplate().insert("insertNavigateCategory", navigateCategory);
    }

    @Override
    public void insertNavigateAssociation(int navId, List<Integer> categoryIds) {
        for (Integer cid : categoryIds) {
            Map map = new HashMap();
            map.put("navId", navId);
            map.put("cid", cid);
            getSqlMapClientTemplate().insert("insertNavigateAssociation", map);
        }
    }

    @Override
    public Map queryAllAssociation() {
        Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
        List<Integer> navIds = getSqlMapClientTemplate().queryForList("selectAllAssociationNavCategoryIds");
        for (Integer navId : navIds) {
            map.put(navId, queryAssociationByNavCategoryId(navId));
        }
        return map;
    }


    @Override
    public void deleteAllNavigateAssociation(int navId) {
        getSqlMapClientTemplate().delete("deleteNavigateAssociation", navId);
    }

    @Override
    public void deleteAllCategoryAssociation(int categoryId) {
        getSqlMapClientTemplate().delete("deleteCategoryAssociation", categoryId);
    }

    @Override
    public void deleteAllAssociation() {
        getSqlMapClientTemplate().delete("deleteAllAssociation");
    }

    @Override
    public List<NavigateCategory> queryAllRootCategories() {
        return getSqlMapClientTemplate().queryForList("queryAllRootCategories");
    }

    @Override
    public List<NavigateCategory> querySubCategories(int parentId) {
        return getSqlMapClientTemplate().queryForList("queryNavSubCategories", parentId);
    }

    @Override
    public NavigateCategory getNavigateCategoryById(int id) {
        NavigateCategory navigateCategory = (NavigateCategory) getSqlMapClientTemplate().queryForObject("selectNavigateCategory", id);
        if (navigateCategory != null) {
            navigateCategory.setCategoryIds(queryAssociationByNavCategoryId(id));
        }
        return navigateCategory;
    }

    @Override
    public List<Integer> queryAssociationByCategoryId(int categoryId) {
        return getSqlMapClientTemplate().queryForList("selectAssociationNavCategoryIds", categoryId);
    }

    @Override
    public List<Integer> queryAssociationByNavCategoryId(int navCategoryId) {
        return getSqlMapClientTemplate().queryForList("selectAssociationCategoryIds", navCategoryId);
    }

    @Override
    public void updateNavigateCategory(NavigateCategory navigateCategory) {
        getSqlMapClientTemplate().update("updateNavigateCategory", navigateCategory);
    }

    @Override
    public void updateNavigateCategorySettings(int id, String settings) {
        Map param = new HashMap();
        param.put("id", id);
        param.put("settings", settings);
        getSqlMapClientTemplate().update("updateNavigateCategorySettings", param);
    }

    @Override
    public void deleteNavigateCategoryById(int id) {
        getSqlMapClientTemplate().delete("deleteNavigateCategory", id);
    }

    @Override
    public void deleteAllNavigateCategory() {
        getSqlMapClientTemplate().delete("deleteAllNavigateCategory");
    }

    @Override
    public List<NavigateCategory> queryAllNavCategories() {
        return getSqlMapClientTemplate().queryForList("selectAllNavigateCategory");
    }


    @Override
    public int queryNavigateCategoryByNameAndParentId(String name, int parentId) {
        Map param = new HashMap();
        param.put("name", name);
        param.put("parentId", parentId);
        return (Integer) getSqlMapClientTemplate().queryForObject("queryNavigateCategoryByNameAndParentId", param);
    }

    @Override
    public NavigateCategory queryNavCategoryByName(String name) {
        List<NavigateCategory> navigateCategoryList = getSqlMapClientTemplate().queryForList("queryNavigateCategoryByName", name);
        if (navigateCategoryList == null || navigateCategoryList.size() == 0) {
            return null;
        }
        return navigateCategoryList.get(0);
    }

}
