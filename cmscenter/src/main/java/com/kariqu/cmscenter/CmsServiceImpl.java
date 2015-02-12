package com.kariqu.cmscenter;

import com.kariqu.cmscenter.domain.Category;
import com.kariqu.cmscenter.domain.RenderTemplate;
import com.kariqu.cmscenter.domain.Content;
import com.kariqu.cmscenter.repository.CmsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-8-23
 *        Time: 下午2:53
 */
public class CmsServiceImpl implements CmsService {


    @Autowired
    private CmsRepository cmsRepository;


    @Override
    public void createCategory(Category category) {
        cmsRepository.createCategory(category);
    }

    @Override
    public void updateCategory(Category category) {
        cmsRepository.updateCategory(category);
    }

    @Override
    public Category queryCategoryById(int id) {
        return cmsRepository.queryCategoryById(id);
    }

    @Override
    public Category queryCategoryByName(String name) {
        return cmsRepository.queryCategoryByName(name);
    }

    @Override
    public List<Category> queryAllCategory() {
        return cmsRepository.queryAllCategory();
    }

    @Override
    public List<Category> queryCategoryList() {
        List<Category> categories = cmsRepository.querySubCategory(-1);
        for (Category category : categories) {
            category.setChildren(cmsRepository.querySubCategory(category.getId()));
        }
        return categories;
    }

    @Override
    public List<Category> querySubCategory(int parent) {
        return cmsRepository.querySubCategory(parent);
    }

    @Override
    public void deleteCategoryById(int id) {
        cmsRepository.deleteCategoryById(id);
        cmsRepository.deleteContentByCategoryId(id);
    }

    @Override
    public void createContent(Content content) {
        cmsRepository.createContent(content);
    }

    @Override
    public void updateContent(Content content) {
        cmsRepository.updateContent(content);
    }

    @Override
    public List<Content> queryAllContent() {
        return cmsRepository.queryAllContent();
    }

    @Override
    public void deleteContentById(int id) {
        cmsRepository.deleteContentById(id);
    }

    @Override
    public Content queryContentById(int id) {
        return cmsRepository.queryContentById(id);
    }

    @Override
    public List<Content> queryContentByCategoryId(int id) {
        return cmsRepository.queryContentByCategoryId(id);
    }

    @Override
    public void createRenderTemplate(RenderTemplate renderTemplate) {
        cmsRepository.createRenderTemplate(renderTemplate);
    }

    @Override
    public void updateRenderTemplate(RenderTemplate renderTemplate) {
        cmsRepository.updateRenderTemplate(renderTemplate);
    }

    @Override
    public void deleteRenderTemplateById(int categoryId) {
        cmsRepository.deleteRenderTemplateById(categoryId);
    }

    @Override
    public RenderTemplate queryRenderTemplateById(int id) {
        return cmsRepository.queryRenderTemplateById(id);
    }

    @Override
    public List<RenderTemplate> queryAllRenderTemplate() {
        return cmsRepository.queryAllRenderTemplate();
    }

}
