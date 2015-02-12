package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.payment.SkuTradeResult;
import com.kariqu.tradecenter.repository.SkuTradeResultRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.assertEquals;

/**
 * @author Athens(刘杰)
 * @Time 13-10-9 上午11:06
 */
@SpringApplicationContext({"classpath:tradeContext.xml"})
public class SkuTradeResultRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("skuTradeResultRepository")
    private SkuTradeResultRepository skuTradeResultRepository;

    @Test
    public void testSkuTradeResult() {
        SkuTradeResult result = new SkuTradeResult();
        result.setProductId(123);
        result.setSkuId(1234);
        result.setPayNumber(6);
        result.setNumber(2);
        skuTradeResultRepository.insert(result);
        assertEquals(2, skuTradeResultRepository.getByProductId(result.getProductId()).getNumber());

        result = skuTradeResultRepository.getBySkuId(result.getSkuId());
        assertEquals(6, result.getPayNumber());

        SkuTradeResult tradeResult = new SkuTradeResult();
        tradeResult.setProductId(result.getProductId());
        tradeResult.setSkuId(12345);
        tradeResult.setPayNumber(5);
        tradeResult.setNumber(3);
        skuTradeResultRepository.insert(tradeResult);
        assertEquals(5, skuTradeResultRepository.getByProductId(tradeResult.getProductId()).getNumber());

        tradeResult = skuTradeResultRepository.getBySkuId(tradeResult.getSkuId());
        assertEquals(3, tradeResult.getNumber());

        result.setBackNumber(1);
        skuTradeResultRepository.update(result);

        tradeResult.setBackNumber(1);
        skuTradeResultRepository.update(tradeResult);
        assertEquals(1, skuTradeResultRepository.getBySkuId(tradeResult.getSkuId()).getBackNumber());

        assertEquals(2, skuTradeResultRepository.getByProductId(tradeResult.getProductId()).getBackNumber());
    }

}
