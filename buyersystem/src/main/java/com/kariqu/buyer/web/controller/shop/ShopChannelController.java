package com.kariqu.buyer.web.controller.shop;

import com.google.common.collect.Lists;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.categorycenter.client.container.CategoryContainer;
import com.kariqu.categorycenter.domain.model.navigate.NavigateCategory;
import com.kariqu.categorycenter.domain.service.NavigateCategoryService;
import com.kariqu.designcenter.client.domain.model.RenderResult;
import com.kariqu.designcenter.client.service.ProdRenderPageService;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.service.ShopPageService;
import com.kariqu.om.domain.Seo;
import com.kariqu.om.domain.SeoType;
import com.kariqu.om.service.SeoService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Asion
 * @since 1.0.0
 *        Date: 12-11-3
 *        Time: 上午11:19
 */
@Controller
public class ShopChannelController {

    @Autowired
    private ProdRenderPageService prodRenderPageService;

    @Autowired
    private ShopPageService shopPageService;

    @Autowired
    private SeoService seoService;

    @Autowired
    private CategoryContainer categoryContainer;

    /**
     * 渲染频道页面
     * 点击某个前台类目的一级类目
     *
     * @param categoryId
     * @param model
     * @return
     */
    @RequestMapping(value = "/channel/{cid}")
    public String displayChannelPage(@PathVariable("cid") String categoryId, HttpServletRequest request, Model model,
                                     HttpServletResponse response) throws IOException {
        int cId = NumberUtils.toInt(categoryId);
        if (cId <= 0) {
            response.sendError(404);
            return null;
        }

        NavigateCategoryService navigateCategoryService = categoryContainer.getNavigateCategoryService();
        NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(cId);
        if (navigateCategory == null) {
            response.sendError(404);
            return null;
        }
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(LoginInfo.USER_SESSION_KEY, LoginInfo.getLoginUser(request));
        context.put("cid", navigateCategory.getId());
        ShopPage shopPage = shopPageService.queryChannelShopPage(RenderConstants.shopId);
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
        model.addAttribute("pageId", shopPage.getId());

        //seo推广设置信息
        Seo seo = seoService.querySeoByObjIdAndType(categoryId, SeoType.CHANNEL);
        model.addAttribute("site_title", seo != null && StringUtils.isNotEmpty(seo.getTitle())
                ? seo.getTitle() : navigateCategory.getName() + "【品牌 价格 推荐 商家直发】- 易居尚");
        model.addAttribute("site_keywords", seo != null && StringUtils.isNotEmpty(seo.getKeywords())
                ? seo.getKeywords() : navigateCategory.getKeyWord() + " 易居尚");
        model.addAttribute("site_description", seo != null && StringUtils.isNotEmpty(seo.getDescription())
                ? seo.getDescription() : "" + "易居尚网购平台致力于向顾客提供高品质居家生活用品，本页主要提供频道[" + navigateCategory.getName() + "]下的商品");

        return "shop/shopLayout";
    }
}
