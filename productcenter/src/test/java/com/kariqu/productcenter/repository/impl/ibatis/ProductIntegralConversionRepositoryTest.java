package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductIntegralConversion;
import com.kariqu.productcenter.repository.ProductIntegralConversionRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: Json.zhu
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class ProductIntegralConversionRepositoryTest extends UnitilsJUnit4 {

    @SpringBean("productIntegralConversionRepository")
    private ProductIntegralConversionRepository productIntegralConversionRepository;

    @Test
    public void testRepository() {
        ProductIntegralConversion p1 = new ProductIntegralConversion();
        p1.setProductId(10);
        p1.setSkuId(11);
        p1.setIntegralCount(100);
        p1.setUserBuyCount(2200);
        p1.setStartDate(DateUtils.parseDate("2013-01-01 01:00:00", DateUtils.DateFormatType.DATE_FORMAT_STR));
        p1.setEndDate(DateUtils.parseDate("2013-05-01 01:00:00", DateUtils.DateFormatType.DATE_FORMAT_STR));
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);
        productIntegralConversionRepository.createProductIntegralConversion(p1);

        assertEquals(18,productIntegralConversionRepository.queryAllProductIntegralConversion().size());
       assertEquals(10,productIntegralConversionRepository.queryProductIntegralConversionById(1).getProductId());
        productIntegralConversionRepository.deleteProductIntegralConversionById(1);
        assertEquals(17,productIntegralConversionRepository.queryAllProductIntegralConversion().size());


        Page<ProductIntegralConversion> page =   productIntegralConversionRepository.queryProductIntegralConversionByProductId(1,10,10);
        List<ProductIntegralConversion> list = page.getResult();
        assertEquals(10,list.size());
        assertEquals(10,productIntegralConversionRepository.queryProductIntegralConversionById(2).getProductId());

        ProductIntegralConversion p2 = new ProductIntegralConversion();
        p2.setId(3);
        p2.setProductId(20);
        p2.setSkuId(20);
        p2.setIntegralCount(200);
        p2.setUserBuyCount(2000);
        p2.setStartDate(DateUtils.parseDate("2014-01-01 01:00:00", DateUtils.DateFormatType.DATE_FORMAT_STR));
        p2.setEndDate(DateUtils.parseDate("2014-05-01 01:00:00", DateUtils.DateFormatType.DATE_FORMAT_STR));

        productIntegralConversionRepository.updateProductIntegralConversionById(p2);

        ProductIntegralConversion p = productIntegralConversionRepository.queryProductIntegralConversionById(3);
        /*System.out.println(p.getId()+"  "+p.getUserAuctionCount()+"  "+p.getProductId()+"  "+p.getIntegralCount()
            +p.getCreateDate()+"  "+p.getStartDate()+"  "+p.getEndDate());*/
    }

    @Test
    public void testselectProductIntegralConversionByDate() throws Exception {
        productIntegralConversionRepository.selectProductIntegralConversionByDate(null);
    }

    @Test
    public void testSelectAllProductIntegralConversionBySkuId() throws Exception {
        productIntegralConversionRepository.selectAllProductIntegralConversionBySkuId(1);
    }

    @Test
    public void test_fetchIntegralConversionBySkuIdAndDaytime() throws Exception {
        productIntegralConversionRepository.fetchIntegralConversionBySkuIdAndDaytime(1, 2, new Date());
    }
}
