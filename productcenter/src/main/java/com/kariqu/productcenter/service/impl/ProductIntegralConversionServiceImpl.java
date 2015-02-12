package com.kariqu.productcenter.service.impl;

import com.kariqu.common.DateUtils;
import com.kariqu.common.lib.F;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductIntegralConversion;
import com.kariqu.productcenter.domain.SkuStorage;
import com.kariqu.productcenter.repository.ProductIntegralConversionRepository;
import com.kariqu.productcenter.service.ProductActivityException;
import com.kariqu.productcenter.service.ProductConversionBaseService;
import com.kariqu.productcenter.service.ProductIntegralConversionService;
import com.kariqu.productcenter.service.SkuStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * User: Json.zhu
 * Date: 14-01-02
 * Time: 上午 11:09
 * 积分兑换
 */
public class ProductIntegralConversionServiceImpl implements ProductIntegralConversionService {

    @Autowired
    private ProductIntegralConversionRepository productIntegralConversionRepository;

    @Autowired
    private SkuStorageService skuStorageService;

    @Autowired
    private ProductConversionBaseService productConversionBaseService;

    @Override
    public List<ProductIntegralConversion> queryAllProductIntegralConversion() {
        return productIntegralConversionRepository.queryAllProductIntegralConversion();
    }

    @Override
    public ProductIntegralConversion queryProductIntegralConversionById(int id) {
        return productIntegralConversionRepository.queryProductIntegralConversionById(id);
    }

    @Override
    public ProductIntegralConversion queryProductIntegralConversionBySkuId(long skuId, Date specificDate) {
        return productIntegralConversionRepository.getProductIntegralConversionByGivingTime(skuId, specificDate);
    }

    @Override
    public Page<ProductIntegralConversion> queryProductIntegralConversionByProductId(int start, int limit, int productId) {
        return  productIntegralConversionRepository.queryProductIntegralConversionByProductId(start,limit,productId);
    }

    @Override
    @Transactional
    public void createProductIntegralConversion(ProductIntegralConversion productIntegralConversion) throws ProductActivityException {
        //检查是否有参加其他的活动, 时间是否被占用
        F.T2<Boolean, String> t2 = productConversionBaseService.checkConversionAssign(productIntegralConversion);
        if (! t2._1) {
            throw new ProductActivityException(t2._2);
        }

        //还要检查一下这个sku是否有库存
        SkuStorage skuStorage = skuStorageService.getSkuStorage(productIntegralConversion.getSkuId());
        if (skuStorage.getStockQuantity() <= 0) {
            throw new ProductActivityException("此商品sku已没有库存");
        }

        productIntegralConversionRepository.createProductIntegralConversion(productIntegralConversion);
    }

    @Override
    @Transactional
    public void deleteProductIntegralConversionById(int id) {
       productIntegralConversionRepository.deleteProductIntegralConversionById(id);
    }

    @Override
    @Transactional
    public void updateProductIntegralConversionById(ProductIntegralConversion productIntegralConversion) throws ProductActivityException {
        //检查是否有参加其他的活动, 时间是否被占用
        F.T2<Boolean, String> t2 = productConversionBaseService.checkConversionAssignWithoutSelf(productIntegralConversion);
        if (! t2._1) {
            throw new ProductActivityException(t2._2);
        }

        productIntegralConversionRepository.updateProductIntegralConversionById(productIntegralConversion);
    }

    @Override
    public List<ProductIntegralConversion> fetchActivityInToday() {
        return productIntegralConversionRepository.selectProductIntegralConversionByDate(DateUtils.getCurrentDate());
    }


}
