package com.kariqu.buyer.web.controller.shop;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import com.kariqu.buyer.web.common.CartTrackUtil;
import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.MealView;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.json.JsonUtil;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.pagenavigator.PageProcessor;
import com.kariqu.designcenter.client.domain.model.RenderResult;
import com.kariqu.designcenter.client.service.ProdRenderPageService;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.domain.open.module.Product;
import com.kariqu.designcenter.service.ShopPageService;
import com.kariqu.om.domain.Const;
import com.kariqu.om.domain.Seo;
import com.kariqu.om.domain.SeoType;
import com.kariqu.om.service.ConstService;
import com.kariqu.om.service.SeoService;
import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.service.*;
import com.kariqu.tradecenter.domain.Valuation;
import com.kariqu.tradecenter.service.ValuationQuery;
import com.kariqu.tradecenter.service.ValuationService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.runtime.typehandling.BigDecimalMath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.RoundingMode;
import java.util.*;

/**
 * 商品详情页的控制器
 * User: Asion
 * Date: 11-6-12
 * Time: 上午10:29
 */

@Controller
public class ShopDetailController {

    private final Log logger = LogFactory.getLog(ShopDetailController.class);

    @Autowired
    private ProdRenderPageService prodRenderPageService;

    @Autowired
    private ShopPageService shopPageService;

    @Autowired
    private com.kariqu.designcenter.domain.open.api.ProductService openProductService;

    @Autowired
    private ConsultationService consultationService;


    @Autowired
    private ValuationService valuationService;

    // =================== 套餐 相关 ===================
    @Autowired
    private MealSetService mealSetService;

    @Autowired
    private ProductActivityService productActivityService;

    @Autowired
    private ProductConversionBaseService productConversionBaseService;

    @Autowired
    private ProductSuperConversionService productSuperConversionService;

    @Autowired
    private ProductIntegralConversionService productIntegralConversionService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private ProductService productService;

    @Autowired
    private BrowsingHistoryService browsHistoryService;

    @Autowired
    private SeoService seoService;

    @Autowired
    private ConstService constService;

    // =================== 套餐 相关 ===================

    /**
     * 进入商品详情页
     * 参数是productId-skuId
     * 如果发现商品没找到或者参数格式错误，返回没找到商品
     * 如果解析到正确的skuId，放入当前sku,以在详情页定位sku
     *
     * @param model
     * @param productId_skuId
     * @return
     */
    @RequestMapping(value = "/product/{productId-skuId}")
    @RenderHeaderFooter
    @PageTitle("商品详情页面")
    public String product(HttpServletRequest request, Model model,
                          @PathVariable("productId-skuId") String productId_skuId) throws Exception {
        String[] ps = productId_skuId.split("-");
        int productId = NumberUtils.toInt(ps[0]);
        if (productId <= 0) {
            model.addAttribute("msg", "商品不存在");
            return "error";
        }
        Product product = openProductService.buildOpenProduct(productId);
        if (product == null) {
            model.addAttribute("msg", "商品不存在");
            return "error";
        }

        // 加入浏览历史
        addHistory(productId, request);

        Map<String, Object> context = new HashMap<String, Object>();
        // 初始化模块中用到的数据
        initParam(ps, product, request, context);
        // 品牌所属地
        String productAddress = getProductAddress(product);
        context.put("productAddress", productAddress);

        // 根据店铺id(目前只有一家店铺)获取详情页面
        ShopPage page = shopPageService.queryDetailShopPage(RenderConstants.shopId);
        RenderResult renderResult = prodRenderPageService.prodRenderPage(page.getId(), context);
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
        model.addAttribute("shopId", RenderConstants.shopId);
        model.addAttribute("pageId", page.getId());

        // title keyword 等
        seoInfo(model, product, productAddress);
        return "shop/shopLayout";
    }

    /** seo 信息 */
    private void seoInfo(Model model, Product product, String productAddress) {
        //seo推广设置信息
        Seo seo = seoService.querySeoByObjIdAndType(String.valueOf(product.getId()), SeoType.PRODUCT);
        String title = productAddress + product.getBrandName() + product.getName() + "【品牌 价格 推荐 商家直发】- 易居尚";
        String keywords = product.getDescription();
        String desc = product.getName() + "的价格，材质及保养方法。易居尚保证提供的["
                + product.getName() + "]为100%%正品，并且由品牌商直接发货，易居尚承诺终生退换货";

        if (seo != null) {
            if (StringUtils.isNotBlank(seo.getTitle())) title = seo.getTitle();
            if (StringUtils.isNotBlank(seo.getKeywords())) keywords = seo.getKeywords();
            if (StringUtils.isNotBlank(seo.getDescription())) desc = seo.getDescription();
        }
        model.addAttribute("site_title", title);
        model.addAttribute("site_keywords", keywords);
        model.addAttribute("site_description", desc);
    }

    private void initParam(String[] ps, Product product, HttpServletRequest request, Map<String, Object> context) {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        // 用户信息
        context.put(LoginInfo.USER_SESSION_KEY, sessionUserInfo);

        // 用户是否关注
        if (sessionUserInfo != null) {
            // 是否已关注
            ProductCollect productCollect = new ProductCollect();
            productCollect.setProductId(product.getId());
            productCollect.setUserId(String.valueOf(sessionUserInfo.getId()));
            context.put("wasFollow", productService.hadProductCollect(productCollect));
        }
        // 默认sku, 如果没有传入则使用价格最低的那个
        long skuId = product.getDefaultSkuObject().getId();
        Product.SKU skuDetail = product.getDefaultSkuObject();
        if (ps.length > 1 && NumberUtils.toLong(ps[1]) > 0 && product.getSkuDetail(NumberUtils.toLong(ps[1])) != null) {
            skuId = NumberUtils.toLong(ps[1]);
            skuDetail = product.getSkuDetail(skuId);
            if (skuDetail != null) {
                product.setDefaultSkuObject(skuDetail);
            }
        }
        context.put(RenderConstants.SKU_CONTEXT_KEY, JsonUtil.objectToJson(skuDetail));

        // sku 属性值
        for (StockKeepingUnit sku : product.getStockKeepingUnitList()) {
            if (sku.getId() == skuId) {
                context.put("skuDesc", skuService.getSkuPropertyValueToStr(sku));
                break;
            }
        }

        // 商品所在的套餐
        List<MealView> mealViewList = getMealByProduct(product);
        if (mealViewList != null && mealViewList.size() > 0) {
            context.put("mealView", mealViewList.get(0));
        }
        // 商品信息
        context.put(RenderConstants.PRODUCT_CONTEXT_KEY, product);

        // 商品咨询个数
        context.put("consultationNum", consultationService.queryConsultationNumById(product.getId(), null, 1));

        // 商品评价
        List<Integer> pointList = valuationService.queryPointByProductId(product.getId());
        int sum = 0, goodsNum = 0;
        for (Integer integer : pointList) {
            sum += integer;
            if (integer > 3) goodsNum++;
        }
        // 评价评分(5 分为满分)
        context.put("valuationScore", pointList.size() == 0 ? 0 : String.format("%.2f", sum / (double) pointList.size()));
        // 好评率, 百分比(四舍五入取整)
        context.put("valuationPercent", goodsNum == 0 ? 0 : DoubleMath.roundToInt(100 * goodsNum / (double) pointList.size(), RoundingMode.UP));
        // 评价数量
        context.put("valuationNum", pointList.size());
    }

    /** 获取商品的品牌所属地 */
    private String getProductAddress(Product product) {
        Const constInfo = constService.getConstByKey("productAddress");
        List<String> productAddressList = Lists.newArrayList();
        if (constInfo != null) {
            Splitter ruleSplitter = Splitter.on(',').omitEmptyStrings().trimResults();
            productAddressList = ruleSplitter.splitToList(constInfo.getConstValue());
        }

        // TODO 品牌所属地应该设置上品牌上, 不应该以一个属性值挂在商品上!
        String productAddress = StringUtils.EMPTY;
        List<Product.PropertyValuePair> propertyValuePairList = product.getKeyProperty();
        for (Product.PropertyValuePair propertyValuePair : propertyValuePairList) {
            if (Arrays.asList("品牌所属地", "品牌归属地").contains(propertyValuePair.getProperty().trim()) &&
                    productAddressList.contains(propertyValuePair.getValue())) {
                productAddress = propertyValuePair.getValue();
                break;
            }
        }
        return productAddress.trim();
    }

    private List<MealView> getMealByProduct(Product product) {
        // 套餐搭配
        List<MealSet> mealSetList = mealSetService.queryMealByProductId(product.getId());
        List<MealView> mealViews = new ArrayList<MealView>();
        for (MealSet meal : mealSetList) {
            // 目前, 只显示第一个套餐
            //MealSet meal = mealSetList.get(0);

            MealView mealView = new MealView();
            mealView.setMeal(meal);
            // 查询出价格最低的套餐项
            Collection<MealItem> mealItemList = mealSetService.queryMealItemListByMealId(meal.getId());
            List<MealView.MealItemView> mealItemViewList = new ArrayList<MealView.MealItemView>();

            for (MealItem mealItem : mealItemList) {
                StockKeepingUnit sku = skuService.getStockKeepingUnit(mealItem.getSkuId());
                // 若 sku 此时已被删除, 则忽略
                if (sku == null || sku.getSkuState() == StockKeepingUnit.SKUState.REMOVED) {
                    logger.warn("显示套餐详情时, sku[" + mealItem.getSkuId() + "]不存在或已失效了, 在套餐[" + meal.getId() + "]中忽略!");
                    continue;
                }

                MealView.MealItemView itemView = new MealView.MealItemView();
                itemView.setMealItem(mealItem);

                MealView.MealItemView.MealSkuInfo skuInfo = new MealView.MealItemView.MealSkuInfo();
                skuInfo.setProductAndSkuId(mealItem.getProductId() + "-" + mealItem.getSkuId());
                // 原始价格(若有参加限时折扣等活动则显示活动价)
                String oldPrice = productActivityService.getSkuMarketingPrice(sku).getSellPrice();
                skuInfo.setSkuPrice(oldPrice);

                // sku 属性说明
                String skuProperties = skuService.getSkuPropertyToString(sku);
                // 主图
                ProductPicture pictureDesc = productService.getMainPictureBySKuId(mealItem.getSkuId(), mealItem.getProductId());
                // 如果是从这个 商品Id 过来的, 则此商品在套餐中放在第一位
                if (product.getId() == mealItem.getProductId()) {
                    skuInfo.setName(product.getName() + " " + skuProperties);
                    skuInfo.setImageUrl(pictureDesc.getPictureUrl());
                    itemView.setSkuInfo(skuInfo);
                    // 当前此商品排在第一位
                    mealItemViewList.add(0, itemView);
                    continue;
                }

                // 商品名
                com.kariqu.productcenter.domain.Product pro = productService.getProductById(mealItem.getProductId());
                skuInfo.setName(pro.getName() + " " + skuProperties);
                skuInfo.setImageUrl(pictureDesc.getPictureUrl());

                itemView.setSkuInfo(skuInfo);
                mealItemViewList.add(itemView);
            }

            if (mealItemViewList.size() != 0) {
                mealView.setMealItemViewList(mealItemViewList);
                mealViews.add(mealView);
            }
        }
        return mealViews;
    }


    @RequestMapping(value = "/product/consandvaluation/{productId}")
    public String viewConsultationAndValuation(Model model, @PathVariable("productId") int productId) throws Exception {

        //1.查询资讯，默认查询所有
        Page<Consultation> consultationPage = new Page<Consultation>(1, 5);
        consultationPage = consultationService.queryConsultation(consultationPage, null, productId, 1);
        model.addAttribute("productId", productId);
        model.addAttribute("consultationPage", consultationPage);


        ValuationQuery valuationQuery = ValuationQuery.asProductId(productId);
        valuationQuery.setPageNo(1);
        valuationQuery.setPageSize(5);
        Page<Valuation> valuationPage = valuationService.queryValuation(valuationQuery);
        model.addAttribute("valuationPage", valuationPage);


        if (consultationPage.getResult().size() > 0) {
            model.addAttribute("consultationPageBar", PageProcessor.process(consultationPage));
        }

        if (valuationPage.getResult().size() > 0) {
            model.addAttribute("valuationPageBar", PageProcessor.process(valuationPage));
        }
        this.calculateValuation(productId, model);
        //2.查询评价，默认查询所有
        return "consAndValuation";
    }

    private void calculateValuation(int productId, Model model) {
        List<Integer> points = valuationService.queryPointByProductId(productId);
        Collection<Integer> likes = Collections2.filter(points, new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return input >= 4;
            }
        });


        Collection<Integer> unlikes = Collections2.filter(points, new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return input < 3;
            }
        });

        Collection<Integer> fines = Collections2.filter(points, new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return input == 3;
            }
        });
        model.addAttribute("allCount", points.size());
        model.addAttribute("likesCount", likes.size());
        model.addAttribute("finesCount", fines.size());
        model.addAttribute("unlikesCount", unlikes.size());
        if (points.size() > 0) {
            int likesPercent = DoubleMath.roundToInt(BigDecimalMath.divide(likes.size(),
                    points.size()).doubleValue() * 100, RoundingMode.UP);
            model.addAttribute("likesPercent", likesPercent);

            int finesPercent = DoubleMath.roundToInt(BigDecimalMath.divide(fines.size(),
                    points.size()).doubleValue() * 100, RoundingMode.UP);

            // 避免出现舍入计算后的失误
            if (likesPercent + finesPercent > 100)
                finesPercent = 100 - likesPercent;

            model.addAttribute("finesPercent", finesPercent);
            model.addAttribute("unlikesPercent", 100 - likesPercent - finesPercent);
        } else {
            model.addAttribute("likesPercent", 0);
            model.addAttribute("finesPercent", 0);
            model.addAttribute("unlikesPercent", 0);

        }
    }

    /**
     * 添加浏览历史记录
     *
     * @param productId
     * @param request
     */
    private void addHistory(int productId, HttpServletRequest request) {
        try {
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            browsHistoryService.addBrowsHistory(null != sessionUserInfo ? sessionUserInfo.getId() : 0, CartTrackUtil.readOrWriteTrackId(request), productId);
        } catch (Exception ignore) {
            logger.error("添加用户浏览历史记录出错", ignore);
        }
    }

    /**
     * 商品的积分兑换详情页
     */
    @RequestMapping(value = "/product/currency/{productId-skuId}")
    @RenderHeaderFooter
    @PageTitle("积分兑换详情页面")
    public String productCurrency(HttpServletRequest request, Model model,
                                  @PathVariable("productId-skuId") String productId_skuId) throws Exception {

        Map<String, Object> context = new HashMap<String, Object>();

        Splitter splitter = Splitter.on('-').trimResults().omitEmptyStrings();
        List<String> psList = splitter.splitToList(productId_skuId);

        if (psList.size() < 2) {
            model.addAttribute("msg", "参数错误");
            return "error";
        }
        if (!NumberUtils.isNumber(psList.get(0))) {
            model.addAttribute("msg", "商品不存在");
            return "error";
        }
        int productId = NumberUtils.toInt(psList.get(0), 0);
        if (productId <= 0) {
            model.addAttribute("msg", "商品不存在");
            return "error";
        }
        Product product = openProductService.buildOpenProduct(productId);
        if (product == null) {
            model.addAttribute("msg", "商品不存在");
            return "error";
        }
        if (!NumberUtils.isNumber(psList.get(1))) {
            model.addAttribute("msg", "参数错误");
            return "error";
        }
        long skuId = NumberUtils.toLong(psList.get(1), 0);
        if (skuId <= 0) {
            model.addAttribute("msg", "活动不存在");
            return "error";
        }

        //查找商品活动
        ProductConversionBase conversionBase = productConversionBaseService.fetchConversionBySkuIdAndDaytime((int) skuId, productId, new Date());
        if (conversionBase == null) {
            model.addAttribute("msg", "活动不存在");
            return "error";
        }

        ProductActivityType activityType = null;

        if (conversionBase instanceof ProductIntegralConversion) {
            activityType = ProductActivityType.IntegralConversion;
        } else if (conversionBase instanceof ProductSuperConversion) {
            activityType = ProductActivityType.SuperConversion;
        }

        Product.SKU skuDetail = product.getSkuDetail(skuId);
        if (skuDetail != null) {
            context.put(RenderConstants.SKU_CONTEXT_KEY, JsonUtil.objectToJson(skuDetail));
            product.setDefaultSkuObject(skuDetail);
        }
        List<StockKeepingUnit> stockKeepingUnits = product.getStockKeepingUnitList();
        for (StockKeepingUnit stockKeepingUnit : stockKeepingUnits) {
            if (stockKeepingUnit.getId() == skuId) {
                context.put("skuDesc", skuService.getSkuPropertyValueToStr(stockKeepingUnit));
            }
        }

        context.put("activityType", activityType.toString());
        context.put("activityId", conversionBase.getId());

        String productAddress = getProductAddress(product);
        context.put("productAddress",productAddress);

        // 加入浏览历史
        addHistory(productId, request);

        SessionUserInfo loginUser = LoginInfo.getLoginUser(request);
        context.put(LoginInfo.USER_SESSION_KEY, loginUser);
        context.put(RenderConstants.PRODUCT_CONTEXT_KEY, product);

        context.put("curUserId", loginUser == null ? null : loginUser.getId());

        int shopId = RenderConstants.shopId;
        ShopPage page = shopPageService.queryDetailIntegralShopPage(shopId);

        // 商品咨询个数
        context.put("consultationNum", consultationService.queryConsultationNumById(product.getId(), null, 1));

        // 商品评价
        List<Integer> pointList = valuationService.queryPointByProductId(product.getId());
        int sum = 0, goodsNum = 0;
        for (Integer integer : pointList) {
            sum += integer;
            if (integer > 3) goodsNum++;
        }
        // 评价评分(5 分为满分)
        context.put("valuationScore", pointList.size() == 0 ? 0 : String.format("%.2f", sum / (double) pointList.size()));
        // 好评率, 百分比(四舍五入取整)
        context.put("valuationPercent", goodsNum == 0 ? 0 : DoubleMath.roundToInt(100 * goodsNum / (double) pointList.size(), RoundingMode.UP));
        // 评价数量
        context.put("valuationNum", pointList.size());

        RenderResult renderResult = prodRenderPageService.prodRenderPage(page.getId(), context);

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
        model.addAttribute("shopId", shopId);
        model.addAttribute("pageId", page.getId());

        seoInfo(model, product, productAddress);
        return "shop/shopLayout";
    }

}
