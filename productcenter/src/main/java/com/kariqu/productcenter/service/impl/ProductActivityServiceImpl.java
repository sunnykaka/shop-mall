package com.kariqu.productcenter.service.impl;

import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.repository.ProductActivityRepository;
import com.kariqu.productcenter.service.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * User: Asion
 * Date: 13-4-1
 * Time: 下午6:32
 */
public class ProductActivityServiceImpl implements ProductActivityService {

    @Autowired
    private ProductActivityRepository productActivityRepository;

    @Autowired
    private SkuService skuService;

    @Autowired
    private LimitedTimeDiscountService limitedTimeDiscountService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductIntegralConversionService productIntegralConversionService;

    @Autowired
    private ProductSuperConversionService productSuperConversionService;

    @Override
    public void createProductActivity(int productId, int activityId, ProductActivityType activityType, Date startDate, Date endDate, String activityPrice) {
        // 创建商品折扣
        ProductActivity productActivity = new ProductActivity();
        productActivity.setActivityId(activityId);
        productActivity.setProductId(productId);
        productActivity.setActivityType(activityType);
        productActivity.setStartDate(startDate);
        productActivity.setEndDate(endDate);
        productActivity.setActivityPrice(activityPrice);

        productActivityRepository.createProductActivity(productActivity);
    }

    @Override
    public void updateProductActivity(ProductActivity productActivity) {
        productActivityRepository.updateProductActivity(productActivity);
        productService.notifyProductUpdate(productActivity.getProductId());
    }

    @Override
    public void checkProductActivityAssign(int productId, Date start, Date end) throws ProductActivityException {
        //如果找不到开始时间的落脚点
        if (productActivityRepository.getActivityCountWrapperGivingDate(productId, start) > 0) {
            throw new ProductActivityException("此商品(" + productId + ")的开始时间在这个时间段已经存在活动");
        }

        //结束时间找不到落脚点
        if (productActivityRepository.getActivityCountWrapperGivingDate(productId, end) > 0) {
            throw new ProductActivityException("此商品(" + productId + ")的结束时间在这个时间段已经存在活动");
        }

        //如果找到了落脚点，需要判断区间内是否有活动
        if (productActivityRepository.getActivityCountInDateRange(productId, start, end) > 0) {
            throw new ProductActivityException("此商品(" + productId + ")的时间断中间已经存在活动");
        }
    }

    @Override
    public void checkProductActivityAssignExceptSelf(int productId, Date start, Date end, int id) throws ProductActivityException {
        //如果找不到开始时间的落脚点
        if (productActivityRepository.getActivityCountWrapperGivingDateExceptSelf(productId, start, id) > 0) {
            throw new ProductActivityException("此商品[" + productId + "]的开始时间在这个时间段已经存在活动");
        }

        //结束时间找不到落脚点
        if (productActivityRepository.getActivityCountWrapperGivingDateExceptSelf(productId, end, id) > 0) {
            throw new ProductActivityException("此商品[" + productId + "]的结束时间在这个时间段已经存在活动");
        }

        //如果找到了落脚点，需要判断区间内是否有活动
        if (productActivityRepository.getActivityCountInDateRangeExceptSelf(productId, start, end, id) > 0) {
            throw new ProductActivityException("此商品[" + productId + "]的时间断中间已经存在活动");
        }
    }

    /**
     * 通过 sku 查询当前参加的活动, 若无则返回 null
     *
     * @param skuId
     * @return
     */
    @Override
    public ProductActivity getProductActivityBySkuIdWithNow(long skuId) {
        StockKeepingUnit sku = skuService.getStockKeepingUnit(skuId);
        if (sku != null)
            return getProductActivity(sku.getProductId(), new Date());

        return null;
    }

    private ProductActivity getProductActivity(int productId, Date date) {
        return productActivityRepository.getProductActivityByGivingTime(productId, date);
    }

    @Override
    public ProductActivity getProductActivityByActivityIdAndType(int activityId, ProductActivityType activityType) {
        return productActivityRepository.getProductActivityByActivityIdAndType(activityId, activityType);
    }

    @Override
    public SkuPriceDetail getSkuMarketingPrice(StockKeepingUnit sku) {
        SkuPriceDetail result = new SkuPriceDetail();
        result.setSkuId(sku.getId());
        result.setOriginalPrice(Money.getMoneyString(sku.getPrice()));

        Date now = new Date();
        ProductActivity productActivity = getProductActivity(sku.getProductId(), now);
        if (productActivity != null) {
            result.setActivityType(productActivity.getActivityType());

            switch (productActivity.getActivityType()) {
                case LimitTime: {
                    LimitedTimeDiscount limitedTimeDiscount = limitedTimeDiscountService.
                            queryLimitedTimeDiscountByProductIdAndTime(sku.getProductId(), now);
                    // 若没有限时折扣信息, 则使用 sku 价格
                    if (limitedTimeDiscount != null) {
                        result.setMarketingId(limitedTimeDiscount.getId());
                        result.setActivityPrice(limitedTimeDiscount.calculatePriceWithStr(sku.getPrice()));
                        result.setActivityStartDate(limitedTimeDiscount.getBeginDate());
                        result.setActivityEndDate(limitedTimeDiscount.getEndDate());
                        result.setActivityDateStr(formatDate(limitedTimeDiscount.getEndDate(), now));
                        result.setActivity(true);

                        // 有修改 sku 价格则使用
                        if (limitedTimeDiscount.isUpdate()) {
                            for (LimitedTimeDiscount.SkuDetail skuDetail : limitedTimeDiscount.json2Details()) {
                                if (sku.getId() == skuDetail.getSkuId()) {
                                    result.setActivityPrice(Money.getMoneyString(skuDetail.getSkuPrice()));
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
                case IntegralConversion : {
                    ProductIntegralConversion productIntegralConversion =
                            productIntegralConversionService.queryProductIntegralConversionBySkuId(sku.getId(), now);
                    if(productIntegralConversion != null){
                        result.setMarketingId(productIntegralConversion.getId());
                        result.setActivityPrice(Money.getMoneyString(0));
                        result.setActivityStartDate(productIntegralConversion.getStartDate());
                        result.setActivityEndDate(productIntegralConversion.getEndDate());
                        result.setActivityDateStr(formatDate(productIntegralConversion.getEndDate(), now));

                        result.setActivityIntegral(Long.valueOf(productIntegralConversion.getIntegralCount()));
                        result.setUserBuyCount(productIntegralConversion.getUserBuyCount());

                        result.setActivity(false);
                        result.setIsActivity(true);
                    }
                    break;
                }

                case SuperConversion : {
                    ProductSuperConversion productSuperConversion =
                            productSuperConversionService.queryProductSuperConversionBySkuId(sku.getId(), now);
                    if(productSuperConversion != null){
                        result.setMarketingId(productSuperConversion.getId());
                        result.setActivityPrice(productSuperConversion.getMoneyForPrice());
                        result.setActivityStartDate(productSuperConversion.getStartDate());
                        result.setActivityEndDate(productSuperConversion.getEndDate());
                        result.setActivityDateStr(formatDate(productSuperConversion.getEndDate(), now));

                        result.setActivityIntegral(Long.valueOf(productSuperConversion.getIntegralCount()));
                        result.setUserBuyCount(productSuperConversion.getUserBuyCount());

                        result.setActivity(false);
                        result.setIsActivity(true);
                    }
                    break;
                }
            }
        }
        return result;
    }

    private String formatDate(Date date, Date when) {
        if (when.after(date)) return "";

        long minute = (date.getTime() - when.getTime()) / 1000 / 60;
        // 除以小时后会丢失分钟的精度
        long hour = minute / 60;
        // 小于一天则显示分
        if (hour < 24) {
            return hour + "小时" + (minute % 60 == 0 ? "" : minute % 60 + "分");
        } else {
            return (hour / 24) + "天" + (hour % 24 == 0 ? "" : hour % 24 + "小时");
        }
    }

    @Override
    public boolean checkProductIfJoinActivity(int productId) {
        return productActivityRepository.getCountOfProductJoinActivityAfterCurrentTime(productId) > 0;
    }

    @Override
    public void deleteProductActivity(int productId, long activityId) {
        productActivityRepository.deleteProductActivity(productId, activityId);
    }

    @Override
    public Integer getUserBuyCountForIntegralAndSuperConversionBySkuId(Long skuId, Date startDate) {
        return productActivityRepository.getUserBuyCountForIntegralAndSuperConversionBySkuId(skuId,startDate);
    }

    public ProductActivityRepository getProductActivityRepository() {
        return productActivityRepository;
    }

    public void setProductActivityRepository(ProductActivityRepository productActivityRepository) {
        this.productActivityRepository = productActivityRepository;
    }

    public LimitedTimeDiscountService getLimitedTimeDiscountService() {
        return limitedTimeDiscountService;
    }

    public void setLimitedTimeDiscountService(LimitedTimeDiscountService limitedTimeDiscountService) {
        this.limitedTimeDiscountService = limitedTimeDiscountService;
    }
}
