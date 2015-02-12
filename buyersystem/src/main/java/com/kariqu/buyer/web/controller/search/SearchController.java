package com.kariqu.buyer.web.controller.search;

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
import com.kariqu.searchengine.domain.ProductQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 响应搜索请求的控制器
 * 控制器将用户的关键字等信息放到上下文中先把页面渲染出来，渲染到模块的时候模块会用上下文中
 * 的信息去调用后台接口获取数据
 *
 * @author Asion
 * @version 1.0.0
 * @since 2011-5-5 下午09:18:02
 */

@Controller
public class SearchController {

    private static final Log LOGGER = LogFactory.getLog(SearchController.class);

    @Autowired
    private ProdRenderPageService prodRenderPageService;

    @Autowired
    private ShopPageService shopPageService;

    @Autowired
    private SeoService seoService;

    /**
     * 直接搜索，请求里包含店铺的信息
     * 搜索条件中类目和关键字必须存在其中一种，不应该出现两个都为空的情况
     * <p/>
     * cat，是用来做导航判断的
     *
     * @param model
     * @param productQuery
     * @return
     */
    @RequestMapping(value = "/products")
    public String search(HttpServletRequest request, Model model, ProductQuery productQuery) {
        PropertyValueHolder propertyValueHolder = new PropertyValueHolder();
        if (StringUtils.isNotEmpty(productQuery.getPv())) {
            for (String pidvid : productQuery.getPv().split(",")) {
                long pvl = NumberUtils.toLong(pidvid);
                if (pvl > 0) {
                    propertyValueHolder.addPV(pvl);
                }
            }
        }
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("propertyValueHolder", propertyValueHolder);

        context.put(LoginInfo.USER_SESSION_KEY, LoginInfo.getLoginUser(request));
        context.put("productQuery", productQuery);
        context.put("keyword", HtmlUtils.htmlEscape(productQuery.getKeyword()));

        context.put("cid", productQuery.getCid());
        context.put("page", productQuery.getPage());
        context.put("sort", productQuery.getSort());
        context.put("order", productQuery.getOrder());
        context.put("hasKeyWord", productQuery.hasKeyWordSearch());

        int shopId = RenderConstants.shopId;
        ShopPage page = shopPageService.querySearchListShopPage(shopId);
        RenderResult renderResult = prodRenderPageService.prodRenderPage(page.getId(), context);

        model.addAttribute("pageContent", renderResult.getPageContent());

        // 页面模块所有的 css
        List<String> cssList = Lists.newArrayList();
        String pageCss = renderResult.getResourceParam(RenderConstants.CSS_ID_CONTEXT_KEY);
        if (StringUtils.isNotBlank(pageCss)) {
            for (String css : pageCss.split("\n")) {
                if (StringUtils.isNotBlank(css)) cssList.add(css);
            }
            model.addAttribute("pageCss", cssList);
        }

        model.addAttribute("shopId", shopId);
        model.addAttribute("pageId", page.getId());
        if (productQuery.hasKeyWordSearch()) {
            model.addAttribute("site_title", productQuery.getKeyword() + "【品牌 价格 推荐 商家直发】- boobee");
            model.addAttribute("site_keywords", productQuery.getKeyword() + ",boobee");
            model.addAttribute("site_description", productQuery.getKeyword() + "搜索结果，boobee");
        } else {
            NavigateCategoryService navigateCategoryService = CategoryContainer.getNavigateCategoryService();
            List<Integer> cidList = productQuery.getCidList();
            if (cidList == null || cidList.size() == 0) {
                model.addAttribute("site_title", "未搜索到商品【品牌 价格 推荐 商家直发】- boobee");
                model.addAttribute("site_keywords", "厨具，锅具，水具，酒具，保温杯");
                model.addAttribute("site_description", "您可以试试搜索：厨具，锅具，水具，茶具，酒具，保温杯");
            } else {
                List<String> nameList = new ArrayList<String>(cidList.size());
                List<String> keyWordList = new ArrayList<String>(cidList.size());
                List<String> descList = new ArrayList<String>(cidList.size());
                for (Integer categoryId : cidList) {
                    //类目描述信息
                    NavigateCategory navigateCategory = navigateCategoryService.getNavigateCategory(categoryId);
                    //seo推广设置信息
                    Seo seo = seoService.querySeoByObjIdAndType(String.valueOf(categoryId), SeoType.CHANNEL);

                    nameList.add(seo != null && StringUtils.isNotEmpty(seo.getTitle()) ? seo.getTitle() : (navigateCategory != null ? navigateCategory.getName() : ""));
                    keyWordList.add(seo != null && StringUtils.isNotEmpty(seo.getKeywords()) ? seo.getKeywords() : (navigateCategory != null ? navigateCategory.getKeyWord() : ""));
                    descList.add(seo != null && StringUtils.isNotEmpty(seo.getDescription()) ? seo.getDescription() : (navigateCategory != null ? navigateCategory.getDescription() : ""));
                }
                model.addAttribute("site_title", subStr(nameList) + "【品牌 价格 推荐 商家直发】- boobee");
                model.addAttribute("site_keywords", subStr(keyWordList));
                model.addAttribute("site_description", subStr(descList));
            }
        }
        return "shop/shopLayout";
    }

    private String subStr(List<String> list) {
        if (list == null || list.size() == 0)
            return StringUtils.EMPTY;

        if (list.size() == 1)
            return list.get(0);

        StringBuilder sbd = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (StringUtils.isBlank(list.get(i)))
                continue;

            sbd.append(list.get(i));
            if (i + 1 != list.size())
                sbd.append("、");
        }
        return sbd.toString();
    }

}
