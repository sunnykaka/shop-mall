package com.kariqu.buyer.web.controller.shop;

import com.google.common.collect.Lists;
import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.CategoryHotSell;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.buyer.web.util.BoobeeControllerUtil;
import com.kariqu.categorycenter.client.container.CategoryContainer;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.service.CategoryAssociationService;
import com.kariqu.categorycenter.domain.service.NavigateCategoryService;
import com.kariqu.cmscenter.CmsService;
import com.kariqu.cmscenter.domain.Category;
import com.kariqu.cmscenter.domain.Content;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.client.domain.model.RenderResult;
import com.kariqu.designcenter.client.service.ProdRenderPageService;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.service.ShopPageService;
import com.kariqu.productcenter.service.ProductPictureResolver;
import com.kariqu.searchengine.domain.*;
import com.kariqu.searchengine.service.SearchEngineQueryService;
import com.kariqu.usercenter.domain.SubscriptionInfo;
import com.kariqu.usercenter.service.SubscriptionInfoService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 前端店铺浏览渲染控制器
 * 默认会把首页输出去
 *
 * @author Asion
 * @version 1.0.0
 * @since 2011-4-17 下午02:23:22
 */

@Controller
public class ShopIndexController {

    private static final Log LOGGER = LogFactory.getLog(ShopIndexController.class);

    @Autowired
    private ProdRenderPageService prodRenderPageService;

    @Autowired
    private ShopPageService shopPageService;

    @Autowired
    private SubscriptionInfoService subscriptionInfoService;

    @Autowired
    private CategoryAssociationService categoryAssociationService;

    @Autowired
    private SearchEngineQueryService searchEngineQueryService;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private CategoryContainer categoryContainer;

    @Autowired
    private ApplicationContext applicationContext;


    /**
     * 订阅活动
     *
     * @param email
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public void subscribeMarketing(String email, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        SubscriptionInfo subscriptionInfo = new SubscriptionInfo();
        if (sessionUserInfo != null) {
            subscriptionInfo.setUserId(sessionUserInfo.getId());
            subscriptionInfo.setEmail(email);
        }
        subscriptionInfoService.createSubscriptionInfo(subscriptionInfo);
        new JsonResult(true).toJson(response);
    }


    /**
     * 类目的子类目热销数据
     *
     * @param cn 类目名称
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/hotsell")
    public void hotSellProduct(String cn, String pageSize, HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(cn)) {
            new JsonResult(false, "需要有类目名").toJson(response);
            return;
        }
        // 默认只需要五条数据
        int size = NumberUtils.toInt(pageSize);
        if (size <= 0) size = 5;
        try {
            NavigateCategoryService naCategoryService = categoryContainer.getNavigateCategoryService();
            NavigateCategory nc = naCategoryService.queryNavigateCategoryByName(cn);
            if (nc == null) {
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn("没有此类目(" + cn + ")!");
                new JsonResult(false, "无热销排行数据").toJson(response);
                return;
            }
            List<NavigateCategory> childNavigateCategory = naCategoryService.querySubCategories(nc.getId());
            if (childNavigateCategory == null || childNavigateCategory.size() == 0) {
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn("类目(" + nc.getName() + ")没有子类目! 请检查是否设置错误?");

                new JsonResult(false, "无热销排行数据").toJson(response);
                return;
            }

            List<CategoryHotSell> hotSellList = new ArrayList<CategoryHotSell>();
            for (NavigateCategory navigateCategory : childNavigateCategory) {
                List<Integer> backCategoryList = categoryAssociationService.queryAssociationByNavCategoryId(navigateCategory.getId());
                if (backCategoryList == null || backCategoryList.size() == 0) {
                    if (LOGGER.isWarnEnabled())
                        LOGGER.warn("前台类目(" + nc.getName() + ")的子类目(" + navigateCategory.getName() + ")未关联后台类目");

                    continue;
                    //new JsonResult(false, "类目(" + nc.getName() + ")的子类目(" + navigateCategory.getName() + ")未关联后台类目").toJson(response);
                    //return;
                }

                CategoryHotSell categoryHotSell = new CategoryHotSell();
                categoryHotSell.setNavId(navigateCategory.getId());
                categoryHotSell.setNavName(navigateCategory.getName());

                ProductQuery query = new ProductQuery();
                query.setCategoryIds(backCategoryList);
                query.setSort(SortBy.valuation.toFiled());
                query.setOrder(OrderBy.desc.toString());
                query.setPageSize(String.valueOf(size));

                List<ProductInfo> productInfos = searchEngineQueryService.queryProductsByQuery(query).getProducts();
                for (ProductInfo productInfo : productInfos) {
                    Map<String, String> productMap = new LinkedHashMap<String, String>();
                    productMap.put("name", productInfo.getName());
                    productMap.put("url", urlBrokerFactory.getUrl("ProductDetail").addQueryData("productId", productInfo.getId()).toString());
                    productMap.put("picture", ProductPictureResolver.getMinSizeImgUrl(productInfo.getPictureUrl()));
                    productMap.put("price", productInfo.getDisplayPrice());

                    categoryHotSell.addHotSellProductList(productMap);
                }
                hotSellList.add(categoryHotSell);
            }
            new JsonResult(true).addData("result", hotSellList).toJson(response);
        } catch (Exception e) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("查询热销数据时异常: " + e.getMessage());

            new JsonResult(false, "无热销排行数据").toJson(response);
        }
    }


    /**
     * 输出页面中模块合并出来js
     *
     * @param pageId
     * @param response
     * @return
     */
    @RequestMapping(value = "/page/js/{pageId}")
    public void pageJs(@PathVariable("pageId") long pageId, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        ShopPage shopPage = shopPageService.getShopPageById(pageId);
        response.getWriter().write(shopPage == null || StringUtils.isBlank(shopPage.getJsContent()) ? "" : shopPage.getJsContent());
    }

    /**
     * 输出店铺的全局url变量
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/shop/js/var")
    public String shopJs() throws IOException {
        return "urlmap";
    }


    /**
     * ====================
     * FIXME update for boobee
     * ====================
     */

    /**
     * 商城首页
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/")
    @PageTitle("商城首页")
    public String mallIndex(HttpServletRequest request, Model model) {
        ShopPage shopPage = shopPageService.queryIndexShopPage(RenderConstants.shopId);
        return this.renderPage(request, shopPage, model);
    }


//    private String renderPage(HttpServletRequest request, ShopPage shopPage, Model model) {
//        if (shopPage == null) {
//            model.addAttribute("pageContent", "没有页面");
//        } else {
//            Map<String, Object> context = new HashMap<String, Object>();
//
//            context.put(LoginInfo.USER_SESSION_KEY, LoginInfo.getLoginUser(request));
//
//            BoobeeControllerUtil.initHeadAndFootData(applicationContext, model);
//
//            model.addAttribute("shopId", shopPage.getShopId());
//            model.addAttribute("pageId", shopPage.getId());
//            model.addAttribute("site_title", shopPage.getTitle());
//            model.addAttribute("site_keywords", shopPage.getKeywords());
//            model.addAttribute("site_description", shopPage.getDescription());
//
//
//        }
//        return "shop/index";
//    }

    private String renderPage(HttpServletRequest request, ShopPage shopPage, Model model) {
        if (shopPage == null) {
            model.addAttribute("pageContent", "没有页面");
        } else {
            Map<String, Object> context = new HashMap<String, Object>();
            context.put(LoginInfo.USER_SESSION_KEY, LoginInfo.getLoginUser(request));

            RenderResult renderResult = prodRenderPageService.prodRenderPage(shopPage.getId(), context);

            // 页面模块所有的 css
            List<String> cssList = Lists.newArrayList();
            String pageCss = renderResult.getResourceParam(RenderConstants.CSS_ID_CONTEXT_KEY);
            if (StringUtils.isNotBlank(pageCss)) {
                for (String css : pageCss.split("\n")) {
                    if (StringUtils.isNotBlank(css)) cssList.add(css);
                }
                model.addAttribute("pageCss", cssList);
            }

            model.addAttribute("pageContent", renderResult.getPageContent());
            model.addAttribute("shopId", shopPage.getShopId());
            model.addAttribute("pageId", shopPage.getId());
            model.addAttribute("site_title", shopPage.getTitle());
            model.addAttribute("site_keywords", shopPage.getKeywords());
            model.addAttribute("site_description", shopPage.getDescription());
        }
        return "shop/shopLayout";
    }



}
