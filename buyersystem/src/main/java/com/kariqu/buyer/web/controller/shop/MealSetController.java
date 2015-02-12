package com.kariqu.buyer.web.controller.shop;

import com.google.common.collect.Lists;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.MealView;
import com.kariqu.designcenter.client.domain.model.RenderResult;
import com.kariqu.designcenter.client.service.ProdRenderPageService;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.service.ShopPageService;
import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.domain.Money;
import com.kariqu.productcenter.service.MealSetService;
import com.kariqu.productcenter.service.ProductActivityService;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.tradecenter.service.IntegralService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 套餐相关
 *
 * @author Athens(刘杰)
 * @Time 13-5-6 上午11:29
 */
@Controller
public class MealSetController {

    private static final Log LOGGER = LogFactory.getLog(MealSetController.class);

    @Autowired
    private ProdRenderPageService prodRenderPageService;

    @Autowired
    private ShopPageService shopPageService;


    @Autowired
    private MealSetService mealSetService;

    @Autowired
    private ProductActivityService productActivityService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private IntegralService integralService;

    @RequestMapping(value = "/product/meal/{mealId}")
    public String mealDetail(@PathVariable(value = "mealId") String mealId, Model model,
                             HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!NumberUtils.isNumber(mealId)) {
            response.sendError(404);
            return null;
        }

        // 套餐搭配
        MealSet meal = mealSetService.getMealSetById(Integer.parseInt(mealId));
        if (meal == null) {
            response.sendError(404);
            return null;
        }

        Map<String, Object> context = new HashMap<String, Object>();

        context.put(LoginInfo.USER_SESSION_KEY, LoginInfo.getLoginUser(request));

        MealView mealView = new MealView();
        mealView.setMeal(meal);

        Collection<MealItem> mealItemList = mealSetService.queryMealItemListByMealId(meal.getId());
        if (mealItemList == null || mealItemList.size() == 0) {
            response.sendError(404);
            return null;
        }

        List<MealView.MealItemView> mealItemViewList = new ArrayList<MealView.MealItemView>();

        // 每个商品的主图
        List<String> pictureList = new ArrayList<String>();
        for (MealItem mealItem : mealItemList) {
            MealView.MealItemView itemView = new MealView.MealItemView();
            itemView.setMealItem(mealItem);

            // 商品
            Product product = productService.getProductById(mealItem.getProductId());
            if (product == null || !product.isOnline()) {
                LOGGER.warn("显示套餐详情时, 商品[" + mealItem.getProductId() + "]不存在或已下架了, 在套餐[" + mealId + "]中忽略!");
                continue;
            }

            StockKeepingUnit sku = skuService.getStockKeepingUnit(mealItem.getSkuId());
            if (sku == null || sku.getSkuState() == StockKeepingUnit.SKUState.REMOVED) {
                LOGGER.warn("显示套餐详情时, sku[" + mealItem.getSkuId() + "]不存在或已失效了, 在套餐[" + mealId + "]中忽略!");
                continue;
            }

            // 主图
            ProductPicture pictureDesc = productService.getMainPictureBySKuId(mealItem.getSkuId(), mealItem.getProductId());
            if (pictureDesc == null) {
                LOGGER.warn("显示套餐详情时, sku[" + mealItem.getSkuId() + "]未设置主图, 在套餐[" + mealId + "]中忽略!");
                continue;
            }

            MealView.MealItemView.MealSkuInfo skuInfo = new MealView.MealItemView.MealSkuInfo();
            skuInfo.setProductAndSkuId(mealItem.getProductId() + "-" + mealItem.getSkuId());
            skuInfo.setName(product.getName() + " " + skuService.getSkuPropertyToString(sku));
            skuInfo.setImageUrl(pictureDesc.getPictureUrl());
            // 销售价格(若有参加限时折扣等活动则显示活动价)
            skuInfo.setSkuPrice(productActivityService.getSkuMarketingPrice(sku).getSellPrice());

            itemView.setSkuInfo(skuInfo);
            pictureList.add(pictureDesc.getPictureLocalUrl());

            mealItemViewList.add(itemView);
        }
        mealView.setMealItemViewList(mealItemViewList);

        // 套餐价对应的积分数(100 块钱换 1 积分, 抵扣 1 元钱)
        mealView.setIntegral(integralService.getTradeCurrency(mealView.mealTotalPrice()));
        context.put("mealView", mealView);

        context.put("pictureList", pictureList);
        if (pictureList.size() > 0)
            context.put("defaultProduct", pictureList.get(0));

        ShopPage shopPage = shopPageService.queryMealSetShopPage(RenderConstants.shopId);
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
        model.addAttribute("site_title", meal.getName() + "【品牌 价格 推荐 商家直发】- 易居尚");
        model.addAttribute("site_keywords", meal.getName() + " 易居尚");
        model.addAttribute("site_description", "易居尚网购平台致力于向顾客提供高品质居家生活用品，本页主要提供频道[" + meal.getName() + "]下的商品");
        return "shop/shopLayout";
    }

}
