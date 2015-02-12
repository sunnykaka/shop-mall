package com.kariqu.cmscenter;

import com.kariqu.cmscenter.domain.Category;
import com.kariqu.cmscenter.domain.RenderTemplate;
import com.kariqu.cmscenter.domain.Content;
import com.kariqu.cmscenter.domain.TemplateType;
import com.kariqu.cmscenter.repository.CmsRepository;
import com.kariqu.common.pagenavigator.Page;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-8-23
 *        Time: 下午2:46
 */
@ContextConfiguration(locations = {"/cmsCenter.xml"})
public class CmsRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private  CmsRepository cmsRepository;

    @Test
    public void testCategory() {

        Category category = new Category();
        category.setName("父级Category");
        category.setParent(-1);
        cmsRepository.createCategory(category);

        Category subCategory = new Category();
        subCategory.setName("子Category");
        subCategory.setParent(category.getId());
        cmsRepository.createCategory(subCategory);

        Category other = cmsRepository.queryCategoryById(category.getId());
        assertEquals(category.getName(),other.getName());

        category.setName("父级Cateogry修改名称");
        cmsRepository.updateCategory(category);
        Category anOther = cmsRepository.queryCategoryById(category.getId());
        assertEquals("父级Cateogry修改名称",anOther.getName());

        List querySubCategoryResult = cmsRepository.querySubCategory(category.getId());
        assertEquals(1,querySubCategoryResult.size());
        assertEquals("子Category",((Category)querySubCategoryResult.get(0)).getName());

        int subCategoryId = ((Category)querySubCategoryResult.get(0)).getId();
        cmsRepository.deleteCategoryById(subCategoryId);
        cmsRepository.deleteCategoryById(category.getId());
        assertNull(cmsRepository.queryCategoryById(subCategoryId));
        assertNull(cmsRepository.queryCategoryById(other.getId()));
    }

    @Test
    public void testContent(){
        Category categoryOfContent = new Category();
        categoryOfContent.setName("父级Category");
        categoryOfContent.setParent(-1);
        cmsRepository.createCategory(categoryOfContent);

        Category subCategoryOfContent = new Category();
        subCategoryOfContent.setName("子级Category");
        subCategoryOfContent.setParent(categoryOfContent.getId());
        cmsRepository.createCategory(subCategoryOfContent);

        Content content = new Content();
        content.setTemplateId(2);
        content.setContent("父级Category内容");
        content.setTitle("父级Category标题");
        content.setUrl("www.yijushang.net:8080");
        content.setCategoryId(categoryOfContent.getId());
        cmsRepository.createContent(content);

        Content subContent = new Content();
        content.setTemplateId(2);
        subContent.setContent("子级Category内容");
        subContent.setTitle("子级Category标题");
        subContent.setUrl("www.yijushang.net:8080");
        subContent.setCategoryId(subCategoryOfContent.getId());
        cmsRepository.createContent(subContent);

        Content contentOfQueryResult = cmsRepository.queryContentById(content.getId());
        assertEquals("父级Category内容",contentOfQueryResult.getContent());
        assertEquals(2,contentOfQueryResult.getTemplateId());

        content.setContent("修改后的内容");
        cmsRepository.updateContent(content);

        contentOfQueryResult = cmsRepository.queryContentById(content.getId());
        assertEquals("修改后的内容",contentOfQueryResult.getContent());

        List <Content> contentOfQueryResultList  = cmsRepository.queryContentByCategoryId(subCategoryOfContent.getId());
        assertEquals(1,contentOfQueryResultList.size());
        assertEquals("子级Category内容",((Content)contentOfQueryResultList.get(0)).getContent());

        cmsRepository.deleteContentById(subContent.getId());
        assertEquals(0,cmsRepository.queryContentByCategoryId(subCategoryOfContent.getId()).size());

        cmsRepository.deleteContentByCategoryId(categoryOfContent.getId());
        assertEquals(0,cmsRepository.queryContentByCategoryId(categoryOfContent.getId()).size());

        for(int i =0 ;i< 20;i++){
            Content pageContent = new Content();
            pageContent.setContent("pageContent"+i);
            pageContent.setCategoryId(categoryOfContent.getId());
            pageContent.setUrl("www.yijushang.net:8080/"+i);
            pageContent.setTitle("title"+i);
            cmsRepository.createContent(pageContent);
        }
        Page<Content> page = new Page();
        page.setPageNo(1);
        page.setPageSize(5);
        Page result = cmsRepository.queryContentByPage(page,categoryOfContent.getId());
        assertEquals(20,result.getTotalCount());
        assertEquals(5,result.getResult().size());
    }

    @Test
    public void testRenderTemplate(){
        RenderTemplate template = new RenderTemplate();
        template.setName("name");
        template.setTemplateType(TemplateType.Index);

        template.setTemplateContent("我们的祖国是花园！");
        cmsRepository.createRenderTemplate(template);

        Category category = new Category();
        category.setName("花园的花都真鲜艳");
        cmsRepository.createCategory(category);
        category.setName("修改");
        cmsRepository.updateCategory(category);
        assertEquals("修改",cmsRepository.queryCategoryById(category.getId()).getName());

        template.setTemplateContent("我们的祖国是花园,花园的花朵真鲜艳");
        cmsRepository.updateRenderTemplate(template);

        assertEquals(1,cmsRepository.queryAllRenderTemplate().size());
        cmsRepository.deleteRenderTemplateById(template.getId());
        assertEquals(0,cmsRepository.queryAllRenderTemplate().size());

    }

    @Test
    public void testCategoryByName(){
        Category category = new Category();
        category.setName("宝贝怎么买");
        cmsRepository.createCategory(category);

        Category categoryInDb = cmsRepository.queryCategoryByName(category.getName());

        assertEquals("宝贝怎么买", categoryInDb.getName());
    }
}
