package com.kariqu.cmscenter.repository;

import com.kariqu.cmscenter.domain.Category;
import com.kariqu.cmscenter.domain.RenderTemplate;
import com.kariqu.cmscenter.domain.Content;
import com.kariqu.common.pagenavigator.Page;

import java.util.List;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-8-23
 *        Time: 下午2:34
 */
public interface CmsRepository {

    void createCategory(Category category);

    void updateCategory(Category category);

    Category queryCategoryById(int id);

    Category queryCategoryByName(String name);

    /**
     * 根据父类目查询它的直接子类目列表
     *
     * @param parent
     * @return
     */
    List<Category> querySubCategory(int parent);


    void deleteCategoryById(int id);

    void createContent(Content content);

    void updateContent(Content content);

    void deleteContentById(int id);

    void deleteContentByCategoryId(int categoryId);

    Content queryContentById(int id);
    
    List<Content> queryAllContent();

    List<Content> queryContentByCategoryId(int id);

    Page<Content> queryContentByPage(Page<Content> page, int categoryId);

    void createRenderTemplate(RenderTemplate renderTemplate);

    List<RenderTemplate> queryAllRenderTemplate();

    void updateRenderTemplate(RenderTemplate renderTemplate);

    void deleteRenderTemplateById(int id);

    RenderTemplate queryRenderTemplateById(int id);

    List<Category> queryAllCategory();

}
