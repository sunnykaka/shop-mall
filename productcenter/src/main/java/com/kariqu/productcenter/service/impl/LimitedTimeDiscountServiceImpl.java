package com.kariqu.productcenter.service.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.LimitedTimeDiscount;
import com.kariqu.productcenter.domain.ProductActivityType;
import com.kariqu.productcenter.domain.StockKeepingUnit;
import com.kariqu.productcenter.query.LimitedTimeDiscountQuery;
import com.kariqu.productcenter.repository.LimitedTimeDiscountRepository;
import com.kariqu.productcenter.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 2013-03-29 17:14
 * @since 1.0.0
 */
public class LimitedTimeDiscountServiceImpl implements LimitedTimeDiscountService {

    @Autowired
    private LimitedTimeDiscountRepository limitedTimeDiscountRepository;

    @Autowired
    private SkuService skuService;

    @Autowired
    private ProductActivityService productActivityService;

    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public void createLimitedTimeDiscount(LimitedTimeDiscount limitedTimeDiscount) throws ProductActivityException {
        // 检查是否有参加其他的活动, 时间是否被占用
        productActivityService.checkProductActivityAssign(limitedTimeDiscount.getProductId(),
                limitedTimeDiscount.getBeginDate(), limitedTimeDiscount.getEndDate());

        // 创建限时折扣
        limitedTimeDiscount.setSkuDetailsJsonBySku(skuService.queryValidSkuForSell(limitedTimeDiscount.getProductId()));
        limitedTimeDiscountRepository.insert(limitedTimeDiscount);

        productActivityService.createProductActivity(limitedTimeDiscount.getProductId(),
                (int) limitedTimeDiscount.getId(), ProductActivityType.LimitTime,
                limitedTimeDiscount.getBeginDate(), limitedTimeDiscount.getEndDate(), limitedTimeDiscount.getSkuMinPrice());

        //通知商品，这样搜索引擎方可感知更新
        productService.notifyProductUpdate(limitedTimeDiscount.getProductId());
    }

    @Override
    @Transactional
    public void updateLimitedTimeDiscount(LimitedTimeDiscount limitedTimeDiscount) {
        limitedTimeDiscountRepository.update(limitedTimeDiscount);
    }

    @Override
    @Transactional
    public void updateLimitedTimeDiscountSkuPrice(long limitedTimeDiscountId, long skuId, long skuPrice) {
        // 将 sku 价格重新赋值回去
        LimitedTimeDiscount limitedTimeDiscount = queryLimitedTimeDiscount(limitedTimeDiscountId);
        List<LimitedTimeDiscount.SkuDetail> skuDetailList = new ArrayList<LimitedTimeDiscount.SkuDetail>();
        for (LimitedTimeDiscount.SkuDetail skuDetail : limitedTimeDiscount.json2Details()) {
            if (skuId == skuDetail.getSkuId()) {
                skuDetail.setSkuPrice(skuPrice);
            }
            skuDetailList.add(skuDetail);
        }
        limitedTimeDiscount.setSkuDetailsJsonByDetail(skuDetailList);

        updateLimitedTimeDiscount(limitedTimeDiscount);

    }

    @Override
    @Transactional
    public void deleteLimitedTimeDiscount(long limitedTimeDiscountId) {
        // 删除商品折扣
        LimitedTimeDiscount limitedTimeDiscount = queryLimitedTimeDiscount(limitedTimeDiscountId);
        productActivityService.deleteProductActivity(limitedTimeDiscount.getProductId(), limitedTimeDiscountId);

        limitedTimeDiscountRepository.delete(limitedTimeDiscountId);

        //通知商品，这样搜索引擎方可感知更新
        productService.notifyProductUpdate(limitedTimeDiscount.getProductId());
    }

    @Override
    public Page<LimitedTimeDiscount> queryLimitedTimeDiscount(LimitedTimeDiscountQuery query) {
        return limitedTimeDiscountRepository.select(query);
    }

    @Override
    public List<LimitedTimeDiscount> queryLimitedTimeDiscountByProductId(int productId) {
        return limitedTimeDiscountRepository.selectByProductId(productId);
    }

    @Override
    public LimitedTimeDiscount queryLimitedTimeDiscountByProductIdAndTime(int productId, Date date) {
        return limitedTimeDiscountRepository.selectByProductIdAndTime(productId, date);
    }

    @Override
    public LimitedTimeDiscount queryLimitedTimeDiscount(long limitedTimeDiscountId) {
        return limitedTimeDiscountRepository.selectById(limitedTimeDiscountId);
    }

    @Override
    public long querySkuPriceByLimitedTimeDiscount(StockKeepingUnit sku) {
        LimitedTimeDiscount limitedTimeDiscount =
                queryLimitedTimeDiscountByProductIdAndTime(sku.getProductId(), new Date());
        // 若没有限时折扣信息, 则使用 sku 价格
        if (limitedTimeDiscount == null) return sku.getPrice();

        // 有修改 sku 价格则使用
        if (limitedTimeDiscount.isUpdate()) {
            for (LimitedTimeDiscount.SkuDetail skuDetail : limitedTimeDiscount.json2Details()) {
                if (sku.getId() == skuDetail.getSkuId())
                    return skuDetail.getSkuPrice();
            }
        }
        return limitedTimeDiscount.calculatePrice(sku.getPrice());
    }

}
