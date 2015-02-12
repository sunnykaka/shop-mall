package com.kariqu.productcenter.service.impl;


import com.kariqu.common.DateUtils;
import com.kariqu.common.lib.F;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductSuperConversion;
import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.productcenter.repository.ProductSuperConversionRepository;
import com.kariqu.productcenter.service.ProductActivityException;
import com.kariqu.productcenter.service.ProductConversionBaseService;
import com.kariqu.productcenter.service.ProductSuperConversionService;
import com.kariqu.productcenter.service.SkuStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 积分优惠购
 * User: Json.zhu
 * Date: 14-01-02
 * Time: 上午 11:09
 */

@Transactional
public class ProductSuperConversionServiceImpl implements ProductSuperConversionService {

    @Autowired
    private ProductSuperConversionRepository productSuperConversionRepository;

    @Autowired
    private SkuStorageService skuStorageService;

    @Autowired
    private ProductConversionBaseService productConversionBaseService;

    @Override
    public List<ProductSuperConversion> queryAllProductSuperConversion() {
        return productSuperConversionRepository.queryAllProductSuperConversion();
    }

    @Override
    public ProductSuperConversion queryProductSuperConversionById(int id) {
        return productSuperConversionRepository.queryProductSuperConversionById(id);
    }

    @Override
    public ProductSuperConversion queryProductSuperConversionBySkuId(long skuId, Date date) {
        return productSuperConversionRepository.getProductSuperConversionByGivingTime(skuId, date);
    }

    @Override
    public Page<ProductSuperConversion> queryProductSuperConversionByProductId(int start, int limit, int productId) {
        return productSuperConversionRepository.queryProductSuperConversionByProductId(start,limit,productId);
    }

    @Override
    public void createProductSuperConversion(ProductSuperConversion productSuperConversion) throws ProductActivityException {
        //检查是否有参加其他的活动, 时间是否被占用
        F.T2<Boolean, String> t2 = productConversionBaseService.checkConversionAssign(productSuperConversion);
        if (! t2._1) {
            throw new ProductActivityException(t2._2);
        }

        //还要检查一下这个sku是否有库存
        SkuStorage skuStorage = skuStorageService.getSkuStorage(productSuperConversion.getSkuId());
        if (skuStorage.getStockQuantity() <= 0) {
            throw new ProductActivityException("此商品sku已没有库存");
        }

        productSuperConversionRepository.createProductSuperConversion(productSuperConversion);
    }

    @Override
    public void deleteProductSuperConversionById(int id) {
        productSuperConversionRepository.deleteProductSuperConversionById(id);
    }

    @Override
    public void updateProductSuperConversionById(ProductSuperConversion productSuperConversion) throws ProductActivityException {
        //检查是否有参加其他的活动, 时间是否被占用
        F.T2<Boolean, String> t2 = productConversionBaseService.checkConversionAssignWithoutSelf(productSuperConversion);
        if (! t2._1) {
           throw new ProductActivityException(t2._2);
        }

        productSuperConversionRepository.updateProductSuperConversionById(productSuperConversion);
    }

    @Override
    public List<ProductSuperConversion> fetchActivityInToday() {
        return productSuperConversionRepository.selectSuperConversionByDate(DateUtils.getCurrentDate());
    }
}
