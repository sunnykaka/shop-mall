package com.kariqu.cmscenter.repository.impl;

import com.kariqu.cmscenter.domain.Category;
import com.kariqu.cmscenter.domain.RenderTemplate;
import com.kariqu.cmscenter.domain.Content;
import com.kariqu.cmscenter.repository.CmsRepository;
import com.kariqu.common.pagenavigator.Page;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-8-23
 *        Time: 下午2:45
 */
public class CmsRepositoryImpl extends SqlMapClientDaoSupport implements CmsRepository {

    @Override
    public void createCategory(Category category) {
        getSqlMapClientTemplate().insert("insertCategory", category);
    }

    @Override
    public void updateCategory(Category category) {
        getSqlMapClientTemplate().update("updateCategory", category);
    }

    @Override
    public Category queryCategoryById(int id) {
        return (Category) getSqlMapClientTemplate().queryForObject("queryCategoryById", id);
    }

    @Override
    public Category queryCategoryByName(String name) {
        return (Category) getSqlMapClientTemplate().queryForObject("queryCategoryByName", name);
    }

    @Override
    public List<Category> querySubCategory(int parent) {
        return getSqlMapClientTemplate().queryForList("querySubCategory", parent);
    }

    @Override
    public void deleteCategoryById(int id) {
        getSqlMapClientTemplate().delete("deleteCategoryById", id);
    }

    @Override
    public void createContent(Content content) {
        getSqlMapClientTemplate().insert("insertContent", content);
    }

    @Override
    public void updateContent(Content content) {
        getSqlMapClientTemplate().update("updateContent", content);
    }

    @Override
    public void deleteContentById(int id) {
        getSqlMapClientTemplate().delete("deleteContentById", id);
    }

    @Override
    public Content queryContentById(int id) {
        return (Content) getSqlMapClientTemplate().queryForObject("queryContentById", id);
    }

    @Override
    public List<Content> queryAllContent() {
        return getSqlMapClientTemplate().queryForList("queryAllContent");
    }

    @Override
    public List<Content> queryContentByCategoryId(int categoryId) {
        return getSqlMapClientTemplate().queryForList("queryContentByCategoryId", categoryId);
    }

    @Override
    public void deleteContentByCategoryId(int categoryId) {
        getSqlMapClientTemplate().delete("deleteContentByCategoryId", categoryId);
    }

    @Override
    public Page<Content> queryContentByPage(Page<Content> page, int categoryId) {
        Map param = new HashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        param.put("categoryId", categoryId);
        List<Content> list = getSqlMapClientTemplate().queryForList("queryContentByPageOfCategory", param);
        page.setResult(list);
        page.setTotalCount((Integer) this.getSqlMapClientTemplate().queryForObject("queryContentByPageOfCategoryCount", param));
        return page;
    }

    @Override
    public void createRenderTemplate(RenderTemplate renderTemplate) {
        getSqlMapClientTemplate().insert("insertRenderTemplate", renderTemplate);
    }

    @Override
    public List<RenderTemplate> queryAllRenderTemplate() {
        return getSqlMapClientTemplate().queryForList("queryAllRenderTemplate");
    }

    @Override
    public void updateRenderTemplate(RenderTemplate renderTemplate) {
        getSqlMapClientTemplate().update("updateRenderTemplate", renderTemplate);
    }

    @Override
    public void deleteRenderTemplateById(int id) {
        getSqlMapClientTemplate().delete("deleteRenderTemplateById", id);
    }

    @Override
    public RenderTemplate queryRenderTemplateById(int id) {
        return (RenderTemplate) getSqlMapClientTemplate().queryForObject("queryRenderTemplateById", id);
    }

    @Override
    public List<Category> queryAllCategory() {
        return getSqlMapClientTemplate().queryForList("queryAllCategory");
    }
}
