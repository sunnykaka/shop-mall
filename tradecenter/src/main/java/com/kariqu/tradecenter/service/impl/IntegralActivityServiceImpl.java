package com.kariqu.tradecenter.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.service.ProductIntegralConversionService;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.productcenter.service.ProductSuperConversionService;
import com.kariqu.productcenter.service.SkuService;
import com.kariqu.tradecenter.domain.CanntJoinActivityResult;
import com.kariqu.tradecenter.repository.OrderRepository;
import com.kariqu.tradecenter.service.IntegralActivityService;
import com.kariqu.usercenter.domain.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 积分活动, 包括积分兑换, 积分优惠购
 * Created by Canal.wen on 2014/7/15 16:55.
 */
public class IntegralActivityServiceImpl implements IntegralActivityService {
    private static final Log LOG = LogFactory.getLog(IntegralActivityServiceImpl.class);

    @Autowired
    private ProductIntegralConversionService productIntegralConversionService;

    @Autowired
    private ProductSuperConversionService productSuperConversionService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SkuService skuService;


    @Override
    public int userHasJoinCount(User user, String activityType, int activityId) {
        if (ProductActivityType.IntegralConversion.toString().equalsIgnoreCase(activityType)) {
            ProductIntegralConversion activity = fetchIntegralConversionInfo(activityId);
            List<Long> skuIds = Lists.newArrayList((long)activity.getSkuId());
            return orderRepository.selectUserJoinSepecialActivityCount(user.getId(), skuIds, ProductActivityType.IntegralConversion, activity.getId());
        }

        if (ProductActivityType.SuperConversion.toString().equalsIgnoreCase(activityType)) {
            ProductSuperConversion activity = fetchSuperConversionInfo(activityId);
            List<Long> skuIds = Lists.newArrayList((long)activity.getSkuId());
            return orderRepository.selectUserJoinSepecialActivityCount(user.getId(), skuIds, ProductActivityType.SuperConversion, activity.getId());
        }

        return 0;
    }

    @Override
    public CanntJoinActivityResult checkUserCanJoin(User user, String activityType, int activityId) {
        long userKa = user.getPointTotal();
        if (ProductActivityType.IntegralConversion.toString().equalsIgnoreCase(activityType)) {
            ProductIntegralConversion activity = fetchIntegralConversionInfo(activityId);
            if (activity == null) {
                return CanntJoinActivityResult.OtherReason;
            }

            int integralCount = activity.getIntegralCount();
            if (userKa < integralCount) { //积分不足
                return CanntJoinActivityResult.NoEnoughKaMoney;
            }
            List<Long> skuIds = Lists.newArrayList((long)activity.getSkuId());
            int count = orderRepository.selectUserJoinSepecialActivityCount(user.getId(), skuIds, ProductActivityType.IntegralConversion, activity.getId());
            if (count >= activity.getUserBuyCount()) { //超过活动次数
                return CanntJoinActivityResult.JoinTooManyTime;
            }

            return CanntJoinActivityResult.OK;
        }

        if (ProductActivityType.SuperConversion.toString().equalsIgnoreCase(activityType)) {
            ProductSuperConversion activity = fetchSuperConversionInfo(activityId);
            if (activity == null) {
                return CanntJoinActivityResult.OtherReason;
            }
            int integralCount = activity.getIntegralCount();
            if (userKa < integralCount) { //积分不足
                return CanntJoinActivityResult.NoEnoughKaMoney;
            }
            List<Long> skuIds = Lists.newArrayList((long)activity.getSkuId());
            int count = orderRepository.selectUserJoinSepecialActivityCount(user.getId(), skuIds, ProductActivityType.SuperConversion, activity.getId());
            if (count >= activity.getUserBuyCount()) { //超过活动次数
                return CanntJoinActivityResult.JoinTooManyTime;
            }

            return CanntJoinActivityResult.OK;
        }

        return CanntJoinActivityResult.OtherReason;
    }


    @Override
    public List<Map<String, Object>> fetchIntegralConversions() {
        List<ProductIntegralConversion> piconversions = productIntegralConversionService.fetchActivityInToday();
        List<Map<String,Object>> resultList = Lists.newArrayListWithCapacity(piconversions.size());
        for (ProductIntegralConversion activity : piconversions) { //活动
            Map<String, Object> item = fetchIntegralConversionById(activity.getId());
            if (item != null) {
                resultList.add(item);
            }
        }

        return resultList;
    }

    @Override
    public ProductIntegralConversion fetchIntegralConversionInfo(int activityId) {
        return productIntegralConversionService.queryProductIntegralConversionById(activityId);
    }

    @Override
    public Map<String,Object> fetchIntegralConversionById(int activityId) {
        ProductIntegralConversion activity = fetchIntegralConversionInfo(activityId);
        if (activity != null) {
            int productId = activity.getProductId();
            Map<String, Object> productMap = productService.getProductMapWithproductInfo(productId, activity.getSkuId());
            StockKeepingUnit sku = skuService.getStockKeepingUnit(activity.getSkuId()); //sku
            String skuDesc = skuService.getSkuPropertyValueToStr(sku); //sku描述
            if (productMap != null && sku != null) {
                Product product = (Product) productMap.get("productInfo");

                //得到squids
                List<Long> squIds = Lists.newArrayList((long) activity.getSkuId());

                //得到购买的人数
                int purchaseCount = orderRepository.selectCountOrderItemByMarketingId(squIds, ProductActivityType.IntegralConversion, activity.getId());
                activity.setRealSale(purchaseCount);

                Map<String,Object> item = Maps.newHashMap();
                item.put("url", productMap.get("url"));
                item.put("picture", productMap.get("picture"));
                item.put("price", changeHumanPrice(sku.getPrice()));
                item.put("productName", product.getName() + "  " + skuDesc);
                item.put("productDesc", product.getDescription());
                item.put("purchaseCount", activity.fetchSalCount());
                item.put("activity", activity);
                item.put("productId-skuId", product.getId() + "-" + sku.getId());
                return item;
            }
        }

        return null;
    }

    private String changeHumanPrice(long price) {
        return Money.CentToYuan(price).toStringWithFormat("#.##");
    }

    @Override
    public List<Map<String, Object>> fetchSuperConversion() {
        List<ProductSuperConversion> piconversions = productSuperConversionService.fetchActivityInToday();
        List<Map<String,Object>> resultList = Lists.newArrayListWithCapacity(piconversions.size());
        for (ProductSuperConversion activity : piconversions) { //活动
            Map<String, Object> item = fetchSuperConversionById(activity.getId());
            if (item != null) {
                resultList.add(item);
            }
        }


        return resultList;
    }

    @Override
    public ProductSuperConversion fetchSuperConversionInfo(int activityId){
        return productSuperConversionService.queryProductSuperConversionById(activityId);
    }

    @Override
    public Map<String,Object> fetchSuperConversionById(int activityId) {
        ProductSuperConversion activity = fetchSuperConversionInfo(activityId);
        if (activity != null) {
            int productId = activity.getProductId();
            Map<String, Object> productMap = productService.getProductMapWithproductInfo(productId, activity.getSkuId());
            StockKeepingUnit sku = skuService.getStockKeepingUnit(activity.getSkuId()); //sku
            String skuDesc = skuService.getSkuPropertyValueToStr(sku); //sku描述

            if (productMap != null && sku != null) {
                Product product = (Product) productMap.get("productInfo");

                List<Long> squIds = Lists.newArrayList((long)activity.getSkuId());

                //得到购买的人数
                int purchaseCount = orderRepository.selectCountOrderItemByMarketingId(squIds, ProductActivityType.SuperConversion, activity.getId());
                activity.setRealSale(purchaseCount);

                Map<String,Object> item = Maps.newHashMap();
                item.put("url", productMap.get("url"));
                item.put("picture", productMap.get("picture"));
                item.put("price", changeHumanPrice(sku.getPrice()));
                item.put("productName", product.getName() + "  " + skuDesc);
                item.put("productDesc", product.getDescription());
                item.put("purchaseCount", activity.fetchSalCount());
                item.put("activity", activity);
                item.put("productId-skuId", product.getId() + "-" + sku.getId());
                return item;
            }
        }

        return null;
    }
}
