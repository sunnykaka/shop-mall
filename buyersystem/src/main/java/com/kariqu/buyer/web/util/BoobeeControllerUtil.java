package com.kariqu.buyer.web.util;

import com.kariqu.categorycenter.client.container.CategoryContainer;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.cmscenter.CmsService;
import com.kariqu.cmscenter.domain.Category;
import com.kariqu.cmscenter.domain.Content;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 15-1-29.
 */
public class BoobeeControllerUtil {

    public static void initHeadAndFootData(ApplicationContext applicationContext, Model model) {
        //data for header
        List<NavigateCategory> categoryTree = CategoryContainer.getCategoryTree().loadNavCategoryTree();

        model.addAttribute("categoryTree", categoryTree);

        CmsService cmsService = applicationContext.getBean(CmsService.class);

        //data for footer
        Map<String, List<Content>> categoryMap = new LinkedHashMap<>();
        Category rootCategory = cmsService.queryCategoryByName("帮助中心");
        if (rootCategory != null) {
            // 查询帮助中心的所有子类目
            List<Category> categoryList = cmsService.querySubCategory(rootCategory.getId());

            for (Category category : categoryList) {
                // 查询子类目的所有内容
                List<Content> contentList = cmsService.queryContentByCategoryId(category.getId());
                categoryMap.put(category.getName(), contentList);
            }
        }

        model.addAttribute("categoryMap", categoryMap);
    }


}
