package com.kariqu.shopsystem.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.service.ShopPageService;
import com.kariqu.shopsystem.helper.ShopPageView;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Asion
 * Date: 12-5-18
 * Time: 上午8:58
 */
@Controller
public class ShopPageManager {

    private final Log logger = LogFactory.getLog(ShopPageManager.class);

    @Autowired
    private ShopPageService shopPageService;


    /**
     * 店铺页面列表
     *
     * @return
     */
    @RequestMapping(value = "/page/shop/list")
    public void shopPageList(HttpServletResponse response) throws IOException {
        List<ShopPage> shopPages = shopPageService.queryAllShopPages();
        List<ShopPageView> list = new ArrayList<ShopPageView>(shopPages.size());
        for (ShopPage shopPage : shopPages) {
            ShopPageView shopPageView = new ShopPageView();
            shopPageView.setId(shopPage.getId());
            shopPageView.setName(shopPage.getName());
            shopPageView.setPageType(shopPage.getPageType());
            shopPageView.setKeywords(shopPage.getKeywords());
            shopPageView.setDescription(shopPage.getDescription());
            shopPageView.setTitle(shopPage.getTitle());
            shopPageView.setPageStatus(shopPage.getPageStatus());
            shopPageView.setRelease(StringUtils.isNotEmpty(shopPage.getProdConfigContent()));
            list.add(shopPageView);
        }
        new JsonResult(true).addData("totalCount", list.size()).addData("result", list).toJson(response);
    }


    /**
     * 删除店铺页面
     *
     * @param ids
     * @param response
     * @throws java.io.IOException
     */
    @Permission("删除了店铺页面")
    @RequestMapping(value = "/page/shop/delete", method = RequestMethod.POST)
    public void deleteShopPage(int[] ids, HttpServletResponse response) throws IOException {
        try {
            for (int id : ids) {
                shopPageService.deleteShopPage(id);
            }
        } catch (Exception e) {
            logger.error("内容管理的删除店铺页面异常：" + e);
            new JsonResult(false, "删除店铺页面出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

    /**
     * 修改店铺元数据
     *
     * @param shopPage
     * @param response
     */
    @RequestMapping(value = "/page/shop/updateShopPage", method = RequestMethod.POST)
    public void updateShopPage(ShopPage shopPage, HttpServletResponse response) throws IOException {
        try {
            ShopPage dbShopPage = shopPageService.getShopPageById(shopPage.getId());
            dbShopPage.setTitle(shopPage.getTitle());
            dbShopPage.setDescription(shopPage.getDescription());
            dbShopPage.setKeywords(shopPage.getKeywords());
            shopPageService.updateShopPage(dbShopPage);
        } catch (Exception e) {
            logger.error("修改店铺元数据异常：" + e);
            new JsonResult(false, "修改店铺元数据出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }

}
