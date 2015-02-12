package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.ProductSuperConversion;
import com.kariqu.productcenter.repository.ProductSuperConversionRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * User: Json.zhu
 * 商品超值兑换表
 */
@SpringApplicationContext({"classpath:productCenter.xml"})
public class ProductSuperConversionRepositoryTest extends UnitilsJUnit4 {

    @SpringBean(("productSuperConversionRepository"))
    private ProductSuperConversionRepository productSuperConversionRepository;

    @Test
    public void testRepository() {
        ProductSuperConversion p1 = new ProductSuperConversion();
        p1.setProductId(10);
        p1.setSkuId(11);
        p1.setIntegralCount(100);
        p1.setMoney(50);
        p1.setUserBuyCount(20);
        p1.setStartDate(DateUtils.parseDate("2013-01-01 01:00:00", DateUtils.DateFormatType.DATE_FORMAT_STR));
        p1.setEndDate(DateUtils.parseDate("2013-05-01 01:00:00", DateUtils.DateFormatType.DATE_FORMAT_STR));

        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);
        productSuperConversionRepository.createProductSuperConversion(p1);

        assertEquals(16,productSuperConversionRepository.queryAllProductSuperConversion().size());
        assertEquals(100,productSuperConversionRepository.queryProductSuperConversionById(1).getIntegralCount());
        productSuperConversionRepository.deleteProductSuperConversionById(1);
        assertEquals(15,productSuperConversionRepository.queryAllProductSuperConversion().size());

        Page<ProductSuperConversion> page =   productSuperConversionRepository.queryProductSuperConversionByProductId(0,10,10);
        assertEquals(100,productSuperConversionRepository.queryProductSuperConversionById(2).getIntegralCount());
        List<ProductSuperConversion> list = page.getResult();
        assertEquals(10,list.size());
        int i = 0;
        for(ProductSuperConversion pa : list){
            i++;
            System.out.println(i + " : "+pa.getId());
        }

    }

    @Test
    public void testselectSuperConversionByDate() throws Exception {
        List<ProductSuperConversion> list = productSuperConversionRepository.selectSuperConversionByDate(null);
        assertEquals(0, list.size());
    }

    @Test
    public void testSelectAllProductSuperConversionBySkuId() throws Exception {
        productSuperConversionRepository.selectAllProductSuperConversionBySkuId(1);
    }

    @Test
    public void test_fetchSuperConversionBySkuIdAndDaytime() throws Exception {
        productSuperConversionRepository.fetchSuperConversionBySkuIdAndDaytime(1, 1, new Date());
    }
}
