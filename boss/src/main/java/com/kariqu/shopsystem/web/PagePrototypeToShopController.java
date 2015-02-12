package com.kariqu.shopsystem.web;

import com.kariqu.common.DefaultShopService;
import com.kariqu.common.JsonResult;
import com.kariqu.designcenter.domain.model.PageType;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.model.shop.ShopTemplate;
import com.kariqu.designcenter.service.Result;
import com.kariqu.designcenter.service.ShopPageService;
import com.kariqu.designcenter.service.ShopTemplateService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Asion
 * Date: 12-5-16
 * Time: 上午11:20
 */
@Controller
public class PagePrototypeToShopController {

    private final Log logger = LogFactory.getLog(PagePrototypeToShopController.class);

    @Autowired
    private ShopPageService shopPageService;

    @Autowired
    private ShopTemplateService shopTemplateService;

    @Autowired
    private DefaultShopService defaultShopService;

    /**
     * 用原型头初始化一个店铺头
     *
     * @param prototypeId
     */
    @RequestMapping(value = "/page/prototype/initShopHead", method = RequestMethod.POST)
    public void initShopHead(int prototypeId, HttpServletResponse response) throws IOException {
        try {
            Result result = shopPageService.initShopHeadWithHeadPagePrototype(prototypeId, RenderConstants.shopId);
            if (result.isSuccess()) {
                new JsonResult(true).toJson(response);
            } else {
                new JsonResult(false, result.getMessage()).toJson(response);
            }
        } catch (Exception e) {
            logger.error("模板系统的初始化店铺头异常：" + e);
            new JsonResult(false, "初始化店铺头出错").toJson(response);

        }
    }

    /**
     * 用原型头升级一个店铺头
     *
     * @param prototypeId
     */
    @RequestMapping(value = "/page/prototype/applyShopHead", method = RequestMethod.POST)
    public void applyShopHead(int prototypeId, HttpServletResponse response) throws IOException {
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(RenderConstants.shopId);
        if (shopTemplate == null || StringUtils.isEmpty(shopTemplate.getEditHeadConfigContent())) {
            new JsonResult(false, "店铺头还没被初始化").toJson(response);
            return;
        }
        try {
            Result result = shopPageService.applyPagePrototypeToHead(prototypeId, RenderConstants.shopId);
            if (result.isSuccess()) {
                new JsonResult(true).toJson(response);
            } else {
                new JsonResult(false, result.getMessage()).toJson(response);
            }
        } catch (Exception e) {
            logger.error("模板体统的升级店铺头异常：" + e);
            new JsonResult(false, "升级店铺头出错").toJson(response);
        }
    }

    /**
     * 用原型尾初始化一个店铺尾
     *
     * @param prototypeId
     */
    @RequestMapping(value = "/page/prototype/initShopFoot", method = RequestMethod.POST)
    public void initShopFoot(int prototypeId, HttpServletResponse response) throws IOException {
        try {
            Result result = shopPageService.initShopFootWithFootPagePrototype(prototypeId, RenderConstants.shopId);
            if (result.isSuccess()) {
                new JsonResult(true).toJson(response);
            } else {
                new JsonResult(false, result.getMessage()).toJson(response);
            }
        } catch (Exception e) {
            logger.error("模板系统的初始化店铺尾异常：" + e);
            new JsonResult(false, "初始化店铺尾出错").toJson(response);
        }
    }

    /**
     * 用原型尾升级店铺尾
     *
     * @param prototypeId
     */
    @RequestMapping(value = "/page/prototype/applyShopFoot", method = RequestMethod.POST)
    public void applyShopFoot(int prototypeId, HttpServletResponse response) throws IOException {
        ShopTemplate shopTemplate = shopTemplateService.getShopTemplateByShopId(RenderConstants.shopId);
        if (shopTemplate == null || StringUtils.isEmpty(shopTemplate.getEditFootConfigContent())) {
            new JsonResult(false, "店铺尾还没被初始化").toJson(response);
            return;
        }
        try {
            Result result = shopPageService.applyPagePrototypeToFoot(prototypeId, RenderConstants.shopId);
            if (result.isSuccess()) {
                new JsonResult(true).toJson(response);

            } else {
                new JsonResult(false, result.getMessage()).toJson(response);
            }
        } catch (Exception e) {
            logger.error("模板系统的升级店铺尾异常：" + e);
            new JsonResult(false, "升级店铺尾出错").toJson(response);
        }
    }

    /**
     * 用原型页面初始化店铺页面
     *
     * @param prototypeId
     * @param pageType
     */
    @RequestMapping(value = "/page/prototype/initShopPage", method = RequestMethod.POST)
    public void initShopPage(int prototypeId, PageType pageType, String name, HttpServletResponse response) throws IOException {
        try {
            switch (pageType) {
                case index:
                    if (shopPageService.queryIndexShopPage(RenderConstants.shopId) != null) {
                        new JsonResult(false, "首页已经存在了").toJson(response);
                        return;
                    }
                    break;
                case detail:
                    if (shopPageService.queryDetailShopPage(RenderConstants.shopId) != null) {
                        new JsonResult(false, "详情页已经存在了").toJson(response);
                        return;
                    }
                    break;
                case searchList:
                    if (shopPageService.querySearchListShopPage(RenderConstants.shopId) != null) {
                        new JsonResult(false, "列表页已经存在了").toJson(response);
                        return;
                    }
                    break;
                case channel:
                    if (shopPageService.queryChannelShopPage(RenderConstants.shopId) != null) {
                        new JsonResult(false, "频道页已经存在了").toJson(response);
                        return;
                    }
                    break;
                case meal_detail:
                    if (shopPageService.queryMealSetShopPage(RenderConstants.shopId) != null) {
                        new JsonResult(false, "套餐详情页已经存在了").toJson(response);
                        return;
                    }
                    break;
            }
            Result result = shopPageService.initShopPageWithPagePrototype(prototypeId, RenderConstants.shopId, pageType, name);
            if (result.isSuccess()) {
                new JsonResult(true).toJson(response);

            } else {
                new JsonResult(false, result.getMessage()).toJson(response);
            }
        } catch (Exception e) {
            logger.error("模板系统的初始化店铺页面异常：" + e);
            new JsonResult(false, "初始化店铺页面出错").toJson(response);
        }
    }

    /**
     * 用原型页面升级店铺页面
     *
     * @param prototypeId
     * @param pageType
     */
    @RequestMapping(value = "/page/prototype/applyShopPage", method = RequestMethod.POST)
    public void applyShopPage(int prototypeId, PageType pageType, HttpServletResponse response) throws IOException {
        try {
            ShopPage shopPage = null;

            if (pageType == PageType.index) {
                shopPage = shopPageService.queryIndexShopPage(RenderConstants.shopId);
                if (shopPage == null) {
                    new JsonResult(false, "首页不存在").toJson(response);
                    return;
                }
            }
            if (pageType == PageType.searchList) {
                shopPage = shopPageService.querySearchListShopPage(RenderConstants.shopId);
                if (shopPage == null) {
                    new JsonResult(false, "列表页不存在").toJson(response);
                    return;
                }
            }
            if (pageType == PageType.detail) {
                shopPage = shopPageService.queryDetailShopPage(RenderConstants.shopId);
                if (shopPage == null) {
                    new JsonResult(false, "详情页不存在").toJson(response);
                    return;
                }
            }
            if (pageType == PageType.channel) {
                shopPage = shopPageService.queryChannelShopPage(RenderConstants.shopId);
                if (shopPage == null) {
                    new JsonResult(false, "频道页不存在").toJson(response);
                    return;
                }
            }

            if (pageType == PageType.meal_detail) {
                shopPage = shopPageService.queryMealSetShopPage(RenderConstants.shopId);
                if (shopPage == null) {
                    new JsonResult(false, "套餐详情页不存在").toJson(response);
                    return;
                }
            }
            Result result = shopPageService.applyPagePrototypeToShopPage(prototypeId, shopPage.getId());
            if (result.isSuccess()) {
                new JsonResult(true).toJson(response);

            } else {
                new JsonResult(false, result.getMessage()).toJson(response);
            }
        } catch (Exception e) {
            logger.error("模板系统的升级店铺页面异常：" + e);
            new JsonResult(false, "升级店铺页面出错").toJson(response);
        }
    }

}
