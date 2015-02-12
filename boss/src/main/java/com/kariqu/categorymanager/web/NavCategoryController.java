package com.kariqu.categorymanager.web;

import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategorySettings;
import com.kariqu.categorycenter.domain.service.NavigateCategoryService;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.json.JsonUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 前台类目控制器
 * User: Asion
 * Date: 11-7-13
 * Time: 上午11:30
 */

@Controller
@RequestMapping("/navigate/category")
public class NavCategoryController {

    private final Log logger = LogFactory.getLog(NavCategoryController.class);

    @Autowired
    private NavigateCategoryService navigateCategoryService;

    /**
     * 加载前台类目对象的json格式
     *
     * @param navId
     * @return
     */
    @RequestMapping(value = "/{navId}")
    public void loadNavigateCategory(@PathVariable("navId") int navId, HttpServletResponse response) throws IOException {
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);
        new JsonResult(true).addData("object", navigateCategory).toJson(response);
    }

    /**
     * 加载所有的前台根类目
     *
     * @return
     */
    @RequestMapping(value = "/root/list")
    public void loadRootCategories(HttpServletResponse response) throws IOException {
        List<NavigateCategory> navigateCategories = navigateCategoryService.queryAllRootCategories();
        new JsonResult(true).addData("totalCount", navigateCategories.size()).addData("result", navigateCategories).toJson(response);
    }

    /**
     * 加载某个前台类目的所有直接子类目
     *
     * @return
     */
    @RequestMapping(value = "/subCategories/list/{navId}")
    public void querySubCategoriesByNavId(@PathVariable("navId") int navId, HttpServletResponse response) throws IOException {
        List<NavigateCategory> navigateCategories = navigateCategoryService.querySubCategories(navId);
        new JsonResult(true).addData("totalCount", navigateCategories.size()).addData("result", navigateCategories).toJson(response);
    }

    /**
     * 创建前台类目
     * 可以用逗号隔断多个值
     *
     * @param parentId
     * @param navigateCategory
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public void createNavCategory(@RequestParam("parentId") int parentId, NavigateCategory navigateCategory, HttpServletResponse response) throws IOException {
        try {
            NavigateCategory parent = new NavigateCategory();
            parent.setId(parentId);
            navigateCategory.setParent(parent);
            String name = navigateCategory.getName();
            int newId = 0;
            for (String theName : name.split(",|，")) {
                int totalSize = navigateCategoryService.queryNavigateCategoryByNameAndParentId(theName, parent.getId());
                if (totalSize >= 1) {
                    new JsonResult(false, "类目" + theName + "已存在").toJson(response);
                    return;
                } else {
                    NavigateCategory nc = new NavigateCategory();
                    nc.setName(theName);
                    nc.setDescription(navigateCategory.getDescription());
                    nc.setKeyWord(navigateCategory.getKeyWord());
                    nc.setParent(parent);
                    navigateCategoryService.createNavigateCategory(nc);
                    newId = nc.getId();
                }
            }
            new JsonResult(true).addData("newId", newId).toJson(response);
        } catch (Exception e) {
            logger.error("创建前台类目错误：" + e);
            new JsonResult(false, "创建失败").toJson(response);
        }

    }


    /**
     * 更新前台类目
     *
     * @param navigateCategory
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Permission("更改前台类目")
    public void updateNavCategory(NavigateCategory navigateCategory, HttpServletResponse response) throws IOException {
        try {
            NavigateCategory currentNavigateCategory = navigateCategoryService.getNavigateCategory(navigateCategory.getId());

            String name = navigateCategory.getName().trim();
            // 修改了类目名称
            if (!name.equals(currentNavigateCategory.getName())) {
                // 根据新类目名称和父类目ID，查询该新类目是否已存在
                int resultCount = navigateCategoryService.queryNavigateCategoryByNameAndParentId(name, currentNavigateCategory.getParent().getId());
                // 判断该类目是否已经存在
                if (resultCount >= 1) {
                    new JsonResult(false, "类目" + navigateCategory.getName() + "已存在").toJson(response);
                    return;
                } else {
                    currentNavigateCategory.setName(navigateCategory.getName());
                    currentNavigateCategory.setDescription(navigateCategory.getDescription());
                    currentNavigateCategory.setKeyWord(navigateCategory.getKeyWord());
                    navigateCategoryService.updateNavigateCategory(currentNavigateCategory);
                    new JsonResult(true).addData("newId", navigateCategory.getId()).toJson(response);
                }
            } else {  // 没有修改类目名称
                currentNavigateCategory.setName(navigateCategory.getName());
                currentNavigateCategory.setDescription(navigateCategory.getDescription());
                currentNavigateCategory.setKeyWord(navigateCategory.getKeyWord());
                navigateCategoryService.updateNavigateCategory(currentNavigateCategory);
                new JsonResult(true).addData("newId", navigateCategory.getId()).toJson(response);
            }
        } catch (Exception e) {
            logger.error("更新前台类目出错：" + e);
            new JsonResult(false, "编辑失败").toJson(response);
        }
    }


    /**
     * 更新一个前台类目的优先级
     *
     * @param navigateCategory
     */
    @RequestMapping(value = "/priority/update", method = RequestMethod.POST)
    @Permission("更改前台类目优先级")
    public void updateNavCategoryPriority(NavigateCategory navigateCategory, HttpServletResponse response) throws IOException {
        try {
            NavigateCategory currentNavigateCategory = navigateCategoryService.getNavigateCategory(navigateCategory.getId());
            currentNavigateCategory.setPriority(navigateCategory.getPriority());
            navigateCategoryService.updateNavigateCategory(currentNavigateCategory);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("更新一个前台类目的优先级错误：" + e);
            new JsonResult(false, "更新失败，请确保你输入合法").toJson(response);
        }

    }


    /**
     * 删除前台类目
     * 后台类目当其下有商品的时候是不允许删除的
     *
     * @param navId
     * @param response
     * @throws IOException
     */
    @Permission("删除前台类目")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void deleteNavCategory(int navId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            navigateCategoryService.deleteNavigateCategory(navId);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("删除前台类目错误：" + e);
            new JsonResult(false, "删除失败").toJson(response);
        }

    }

    @Permission("设置前台热点类目")
    @RequestMapping(value = "/setHot", method = RequestMethod.POST)
    public void changeNavHotSettings(int navId, int hot, HttpServletResponse response) throws IOException {
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);
        NavigateCategorySettings navigateCategorySettings = navigateCategory.settingsObject();
        navigateCategorySettings.setHot(hot == 0 ? false : true);
        navigateCategorySettings.setNavId(navId);
        navigateCategoryService.updateNavigateCategorySettings(navigateCategorySettings);
        new JsonResult(true).toJson(response);
    }


    @RequestMapping(value = "/setChannelImg", method = RequestMethod.POST)
    public void changeNavMainSettings(NavigateCategorySettings settings, HttpServletResponse response) throws IOException {
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(settings.getNavId());
        NavigateCategorySettings navigateCategorySettings = navigateCategory.settingsObject();
        navigateCategorySettings.setMainImg(settings.getMainImg());
        navigateCategorySettings.setAdImg(settings.getAdImg());
        navigateCategorySettings.setMainImgLink(settings.getMainImgLink());
        navigateCategorySettings.setMainProduct(settings.getMainProduct());
        navigateCategory.setSettings(JsonUtil.objectToJson(navigateCategorySettings));
        navigateCategorySettings.setNavId(settings.getNavId());
        navigateCategoryService.updateNavigateCategory(navigateCategory);
        new JsonResult(true).toJson(response);
    }


    @RequestMapping(value = "/setCategoryRecommendProduct", method = RequestMethod.POST)
    public void changeNavSettings(int navId, String minorProduct, HttpServletResponse response) throws IOException {
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);
        NavigateCategorySettings navigateCategorySettings = navigateCategory.settingsObject();
        navigateCategorySettings.setMinorProduct(minorProduct);
        navigateCategorySettings.setNavId(navId);
        navigateCategory.setSettings(JsonUtil.objectToJson(navigateCategorySettings));
        navigateCategoryService.updateNavigateCategory(navigateCategory);
        new JsonResult(true).toJson(response);
    }

    //设置logo图
    @RequestMapping(value = "/setCategoryLogoImg", method = RequestMethod.POST)
    public void setCategoryLogoImg(int navId, String logoImg, HttpServletResponse response) throws IOException {
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);
        NavigateCategorySettings navigateCategorySettings = navigateCategory.settingsObject();
        navigateCategorySettings.setLogoImg(logoImg);
        navigateCategorySettings.setNavId(navId);
        navigateCategory.setSettings(JsonUtil.objectToJson(navigateCategorySettings));
        navigateCategoryService.updateNavigateCategory(navigateCategory);
        new JsonResult(true).toJson(response);
    }


    /**
     * 加载类目设置
     *
     * @param navId
     * @param response
     */
    @RequestMapping(value = "/settings/{navId}")
    public void loadNavSettings(@PathVariable("navId") int navId, HttpServletResponse response) throws IOException {
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);
        NavigateCategorySettings settingsObject = navigateCategory.settingsObject();
        new JsonResult(true).addData("object", settingsObject).toJson(response);
    }

    @RequestMapping(value = "/settings/mainImg/number/{navId}")
    public void loadNavSettingsMainImgNumber(@PathVariable("navId") int navId, HttpServletResponse response) throws IOException {
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(navId);
        NavigateCategorySettings settingsObject = navigateCategory.settingsObject();
        int number = 1;

        if (settingsObject.getMainImg() != null && settingsObject.getMainImg().size() > 1) {
            number = settingsObject.getMainImg().size();
        }
        new JsonResult(true).addData("number", number).toJson(response);
    }


}
