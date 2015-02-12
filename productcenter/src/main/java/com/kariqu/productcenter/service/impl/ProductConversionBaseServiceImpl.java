package com.kariqu.productcenter.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.kariqu.common.lib.F;
import com.kariqu.productcenter.domain.*;
import com.kariqu.productcenter.repository.ProductIntegralConversionRepository;
import com.kariqu.productcenter.repository.ProductSuperConversionRepository;
import com.kariqu.productcenter.service.ProductConversionBaseService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by Canal.wen on 2014/8/5 11:09.
 */
public class ProductConversionBaseServiceImpl implements ProductConversionBaseService {
    private static final Log LOG = LogFactory.getLog(ProductConversionBaseServiceImpl.class);

    @Autowired
    private ProductIntegralConversionRepository productIntegralConversionRepository;

    @Autowired
    private ProductSuperConversionRepository productSuperConversionRepository;

    @Override
    public ProductConversionBase fetchConversionBySkuIdAndDaytime(int skuId, int productId,  Date specialDate) {
        ProductIntegralConversion conversion = productIntegralConversionRepository.fetchIntegralConversionBySkuIdAndDaytime(skuId, productId, specialDate);
        if (conversion != null) {
            return conversion;
        } else {
            return productSuperConversionRepository.fetchSuperConversionBySkuIdAndDaytime(skuId, productId, specialDate);
        }
    }


    @Override
    public F.T2<Boolean,String> checkConversionAssign(ProductConversionBase pbase){
        int skuId = pbase.getSkuId();
        int productId = pbase.getProductId();
        String reason = "";
        Boolean sucess = Boolean.TRUE;

        //1. 把活动日期做为range包装起来
        List<F.T2<String, Range<Date>>> rangeList = Lists.newArrayList();
        List<ProductIntegralConversion> productIntegralConversions = productIntegralConversionRepository.selectAllProductIntegralConversionBySkuId(skuId);
        for (ProductIntegralConversion p : productIntegralConversions) {
            rangeList.add(F.T2(ProductActivityType.IntegralConversion.toDesc() ,Range.closed(p.getStartDate(), p.getEndDate())) );
        }

        List<ProductSuperConversion> productSuperConversions = productSuperConversionRepository.selectAllProductSuperConversionBySkuId(skuId);
        for (ProductSuperConversion p : productSuperConversions) {
            rangeList.add( F.T2( ProductActivityType.SuperConversion.toDesc(), Range.closed(p.getStartDate(), p.getEndDate()) ));
        }

        String format = "此商品(productId=%1$d,skuId=%2$d)的%5$s时间(%7$tF %7$tT)<br/>在这个时间段(%3$tF %3$tT -- %4$tF %4$tT)已经存在<br/>[%6$s]活动";
        //2. 判断sdate跟edate都不在这些range里
        for (F.T2<String, Range<Date>> t2 : rangeList) {
            Range<Date> range = t2._2;
            String typeDesc = t2._1;
            if ( range.contains(pbase.getStartDate())) {
                sucess = Boolean.FALSE;
                reason = String.format(format, productId, skuId, range.lowerEndpoint(), range.upperEndpoint(), "开始", typeDesc, pbase.getStartDate());
            }

            if (range.contains(pbase.getEndDate())) {
                sucess = Boolean.FALSE;
                reason = String.format(format, productId, skuId, range.lowerEndpoint(), range.upperEndpoint(), "结束", typeDesc, pbase.getEndDate());
            }
        }

        return F.T2(sucess, reason);
    }


    @Override
    public F.T2 checkConversionAssignWithoutSelf(ProductConversionBase pbase) {
        String reason = "";
        Boolean sucess = Boolean.TRUE;

        ProductActivityType type = null;

        if (pbase instanceof ProductIntegralConversion) {
            type = ProductActivityType.IntegralConversion;
        } else if (pbase instanceof ProductSuperConversion) {
            type = ProductActivityType.SuperConversion;
        } else {
            LOG.error("注意: 这里还没有处理相应的积分活动类型, 要在代码上进行处理一下");
            throw new RuntimeException("还没有处理相应的类型");
        }

        int skuId = pbase.getSkuId();
        int id = pbase.getId();
        int productId = pbase.getProductId();

        //1. 把活动日期做为range包装起来
        List<F.T2<String, Range<Date>>> rangeList = Lists.newArrayList();
        List<ProductIntegralConversion> productIntegralConversions = productIntegralConversionRepository.selectAllProductIntegralConversionBySkuId(skuId);
        for (ProductIntegralConversion p : productIntegralConversions) {
            if (! (type == ProductActivityType.IntegralConversion && p.getId() == id) ) { //不包含自身
                rangeList.add(F.T2(ProductActivityType.IntegralConversion.toDesc(), Range.closed(p.getStartDate(), p.getEndDate())));
            }
        }

        List<ProductSuperConversion> productSuperConversions = productSuperConversionRepository.selectAllProductSuperConversionBySkuId(skuId);
        for (ProductSuperConversion p : productSuperConversions) {
            if (! (type == ProductActivityType.SuperConversion && p.getId() == id) ) {//不包含自身
                rangeList.add(F.T2(ProductActivityType.SuperConversion.toDesc(), Range.closed(p.getStartDate(), p.getEndDate())));
            }
        }

        String format = "此商品(productId=%1$d,skuId=%2$d)的%5$s时间(%7$tF %7$tT)<br/>在这个时间段(%3$tF %3$tT -- %4$tF %4$tT)已经存在<br/>[%6$s]活动";
        //2. 判断sdate跟edate都不在这些range里
        for (F.T2<String, Range<Date>> t2 : rangeList) {
            Range<Date> range = t2._2;
            String typeDesc = t2._1;
            if (range.contains(pbase.getStartDate())) {
                sucess = Boolean.FALSE;
                reason = String.format(format, productId, skuId, range.lowerEndpoint(), range.upperEndpoint(), "开始", typeDesc, pbase.getStartDate());
            }

            if (range.contains(pbase.getEndDate())) {
                sucess = Boolean.FALSE;
                reason = String.format(format, productId, skuId, range.lowerEndpoint(), range.upperEndpoint(), "结束", typeDesc, pbase.getEndDate());
            }
        }

        return F.T2(sucess, reason);
    }
}
