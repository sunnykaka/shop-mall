package com.kariqu.designsystem.web;

import com.google.common.collect.Lists;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.client.domain.model.RenderResult;
import com.kariqu.designcenter.client.service.EditRenderPageService;
import com.kariqu.designcenter.client.service.PreviewRenderPageService;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.service.ShopPageService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺装修设计的控制器接受非二级域名请求
 *
 * @author Asion
 * @version 1.0.0
 * @since 2011-4-17 下午02:23:46
 */

@Controller
public class ShopDesignController {

    private final Log logger = LogFactory.getLog(ShopDesignController.class);

    @Autowired
    private EditRenderPageService editRenderPageService;

    @Autowired
    private PreviewRenderPageService previewRenderPageService;

    @Autowired
    private ShopPageService shopPageService;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;
    /**
     * 装修店铺的装修地址
     *
     * @param response
     */
    @RequestMapping(value = "/design/shop/address")
    public void shopDesignAddress(HttpServletResponse response) throws IOException {
        try {
            String address = urlBrokerFactory.getUrl("DesignShop").addQueryData("shopId", RenderConstants.shopId).toString();
            new JsonResult(true).addData("address", address).toJson(response);
        } catch (Exception e) {
            logger.error("返回缺省的店铺Id异常：" + e);
            new JsonResult(false, "返回店铺装修地址出错").toJson(response);
        }
    }


    /**
     * 通过店铺ID到达装修后台的URL
     *
     * @param shopId
     * @param model
     * @return
     */
    @RequestMapping(value = "/design/shop/{shopId}")
    public String shopDesign(@PathVariable("shopId") int shopId, Model model) {
        // 如果加强了店铺管理, 需要验证此 店铺id 是不是当前用户的
        ShopPage shopPage = shopPageService.queryIndexShopPage(shopId);
        if (shopPage == null) {
            model.addAttribute("pageContent", "没有此店铺");
            return "shop/shopDesignLayout";
        }
        return pageDesign(shopPage.getId(), model);
    }

    /**
     * 用页面ID进入页面装修
     *
     * @param pageId
     * @param model
     * @return
     */
    @RequestMapping(value = "/design/page/{pageId}")
    public String pageDesign(@PathVariable("pageId") long pageId, Model model) {
        // 如果加强了店铺管理, 需要验证此 页面id 是不是当前用户的
        ShopPage shopPage = shopPageService.getShopPageById(pageId);
        if (shopPage == null) {
            model.addAttribute("pageContent", "没有此页面");
            return "shop/shopDesignLayout";
        }
        int shopId = shopPage.getShopId();
        Map<String, Object> context = new HashMap<String, Object>();
        RenderResult editResult = editRenderPageService.editRenderPage(pageId, context);

        model.addAttribute("pageContent", editResult.getPageContent());
        model.addAttribute("shopId", shopId);
        model.addAttribute("pageId", pageId);

        List<String> cssList = Lists.newArrayList();
        String pageCss = editResult.getResourceParam(RenderConstants.CSS_ID_CONTEXT_KEY);
        for (String css : pageCss.split("\n")) {
            if (StringUtils.isNotBlank(css)) cssList.add(css);
        }
        model.addAttribute("pageCss", cssList);

        buildDesignNav(shopId, model);

        return "shop/shopDesignLayout";
    }

    /**
     * 装修完预览店铺的入口URL，默认会把首页输出
     *
     * @param shopId
     * @param model
     * @return
     */
    @RequestMapping(value = "/design/shop/preview/{shopId}")
    public String previewShop(@PathVariable("shopId") int shopId, Model model) {
        ShopPage shopPage = shopPageService.queryIndexShopPage(shopId);
        if (shopPage == null) {
            model.addAttribute("pageContent", "没有首页");
            return "shop/shopPreviewLayout";
        }

        return previewShopPage(shopPage.getId(), model);
    }


    /**
     * 装修完预览店铺页面的URL
     *
     * @param pageId
     * @param model
     * @return
     */
    @RequestMapping(value = "/design/page/preview/{pageId}")
    public String previewShopPage(@PathVariable("pageId") long pageId, Model model) {
        ShopPage shopPage = shopPageService.getShopPageById(pageId);
        int shopId = shopPage.getShopId();
        Map<String, Object> context = new HashMap<String, Object>();
        RenderResult previewResult = previewRenderPageService.previewRenderPage(pageId, context);

        model.addAttribute("pageContent", previewResult.getPageContent());

        List<String> cssList = Lists.newArrayList();
        String pageCss = previewResult.getResourceParam(RenderConstants.CSS_ID_CONTEXT_KEY);
        for (String css : pageCss.split("\n")) {
            if (StringUtils.isNotBlank(css)) cssList.add(css);
        }
        model.addAttribute("pageCss", cssList);
        model.addAttribute("shopId", shopId);

        buildDesignNav(shopId, model);

        return "shop/shopPreviewLayout";
    }


    /**
     * 发布店铺
     *
     * @param shopId
     * @return
     */
    @RequestMapping(value = "/design/shop/publish", method = RequestMethod.POST)
    @Permission("发布店铺")
    public String publishShop(@RequestParam("shopId") int shopId) {
        shopPageService.publishShopPages(shopId);
        return "redirect:" + urlBrokerFactory.getUrl("BuyHome").toString();
    }


    /**
     * 上传文件跳转
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/design/shop/uploadFileJump")
    public String previewShop(HttpServletRequest request) {
        return "shop/uploadConfiguration";

    }


    /**
     * 对主要三个页面进行装修的菜单
     *
     * @param shopId
     * @param model
     */
    private void buildDesignNav(int shopId, Model model) {
        ShopPage indexPage = shopPageService.queryIndexShopPage(shopId);
        ShopPage searchPage = shopPageService.querySearchListShopPage(shopId);
        ShopPage detailPage = shopPageService.queryDetailShopPage(shopId);
        ShopPage channelPage = shopPageService.queryChannelShopPage(shopId);
        if (null != indexPage)
            model.addAttribute("indexPageId", indexPage.getId());
        if (null != searchPage)
            model.addAttribute("searchPageId", searchPage.getId());
        if (null != detailPage)
            model.addAttribute("detailPageId", detailPage.getId());
        if (null != channelPage)
            model.addAttribute("channelPageId", channelPage.getId());
    }


}
