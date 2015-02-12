package com.kariqu.categorymanager.web;

import com.kariqu.categorycenter.domain.model.navigate.NavCategoryProperty;
import com.kariqu.categorycenter.domain.service.CategoryAssociationService;
import com.kariqu.categorycenter.domain.service.NavigateCategoryPropertyService;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 前台类目属性和值控制器
 * User: Asion
 * Date: 11-10-31
 * Time: 下午4:17
 */
@Controller
public class NavCategoryPropertyController {

    private final Log logger = LogFactory.getLog(NavCategoryController.class);

    @Autowired
    private NavigateCategoryPropertyService navigateCategoryPropertyService;

    @Autowired
    private CategoryAssociationService categoryAssociationService;

    /**
     * 聚合后台类目到前台类目，聚合规则可能是：
     * 多个叶子，多个父亲，父亲和儿子，但是一个节点的父亲被选择了则父亲下的所有子节点都不能被聚合
     * 同理，如果一个父亲下的所有儿子都被选择了，则只能聚合父亲，儿子取消聚合，目前是在前台用js来实现的
     * 后台每个节点都具备了属性和值，搜索引擎只索引后台节点，所有的前台聚合都通过后台节点组成成条件查询
     *
     * @param navId
     * @param categoryIds
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/navigate/category/association", method = RequestMethod.POST)
    @Permission("关联类目")
    public void navCategoryAssociation(@RequestParam("navId") int navId, @RequestParam("categoryIds") String categoryIds, HttpServletResponse response) throws IOException {
        try {
            List<Integer> convertCategoryIds = new LinkedList<Integer>();
            if (StringUtils.isNotBlank(categoryIds)) {
                String[] cidForAssociation = categoryIds.split(",");
                for (String cid : cidForAssociation) {
                    convertCategoryIds.add(Integer.valueOf(cid));
                }
            }
            categoryAssociationService.createNavigateCategoryAssociation(navId, convertCategoryIds);
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("关联后台类目错误：" + e);
            new JsonResult(false, "关联后台类目失败").toJson(response);
        }
    }


    /**
     * 得到某个前台类目叶子所关联的后台属性ID列表
     * 用于EXT属性选择器中判断checkbox是否勾选
     *
     * @param navId
     * @return
     */
    @RequestMapping(value = "/navigate/category/propertyId/list/{navId}")
    public
    @ResponseBody
    List<Integer> NCPIdList(@PathVariable("navId") int navId) {
        List<NavCategoryProperty> navCategoryProperties = navigateCategoryPropertyService.queryNavCategoryProperties(navId);
        List<Integer> ids = new LinkedList<Integer>();
        for (NavCategoryProperty navCategoryProperty : navCategoryProperties) {
            ids.add(navCategoryProperty.getPropertyId());
        }
        return ids;
    }

}
