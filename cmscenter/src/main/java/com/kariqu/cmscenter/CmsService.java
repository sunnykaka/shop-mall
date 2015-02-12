package com.kariqu.cmscenter;

import com.kariqu.cmscenter.domain.Category;
import com.kariqu.cmscenter.domain.RenderTemplate;
import com.kariqu.cmscenter.domain.Content;

import java.util.List;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-8-23
 *        Time: 上午11:14
 */
public interface CmsService {

    void createCategory(Category category);

    void updateCategory(Category category);

    Category queryCategoryById(int id);

    Category queryCategoryByName(String name);

    /**
     * 查询全部栏目，每个栏目不加载自己的子栏目
     *
     * @return
     */
    List<Category> queryAllCategory();

    /**
     * 查询类目列表，每个类目都包含它的子类目
     *
     * @return
     */
    List<Category> queryCategoryList();

    /**
     * 只查询某个类目的子类目
     *
     * @param parent
     * @return
     */
    List<Category> querySubCategory(int parent);


    void deleteCategoryById(int id);

    /**
     * 保存超文本，保存的时候同时生成一个静态文件放到静态文件服务器
     *
     * @param content
     */
    void createContent(Content content);

    void updateContent(Content content);

    List<Content> queryAllContent();


    void deleteContentById(int id);

    Content queryContentById(int id);

    List<Content> queryContentByCategoryId(int id);

    void createRenderTemplate(RenderTemplate renderTemplate);

    void updateRenderTemplate(RenderTemplate renderTemplate);

    void deleteRenderTemplateById(int id);

    RenderTemplate queryRenderTemplateById(int id);

    List<RenderTemplate> queryAllRenderTemplate();
}
