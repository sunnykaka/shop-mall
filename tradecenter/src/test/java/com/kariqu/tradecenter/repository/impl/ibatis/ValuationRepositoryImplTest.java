package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Valuation;
import com.kariqu.tradecenter.repository.ValuationRepository;
import com.kariqu.tradecenter.service.ValuationQuery;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * User: Asion
 * Date: 13-3-4
 * Time: 下午5:19
 */
@SpringApplicationContext({"classpath:tradeContext.xml"})
public class ValuationRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("valuationRepository")
    private ValuationRepository valuationRepository;

    @Test
    public void testValuationRepository() {
        Valuation valuation = new Valuation();
        valuation.setUserId(1);
        valuation.setContent("xxxx");
        valuation.setOrderItemId(1l);
        valuation.setPoint(4);
        valuation.setUserName("xxx");
        valuation.setProductId(2);
        valuationRepository.createValuation(valuation);
        valuation.setOrderItemId(2l);
        valuation.setProductId(3);
        valuationRepository.createValuation(valuation);


        assertEquals(1,valuationRepository.queryValuation(ValuationQuery.asProductIdAndLikeFilter(2, ValuationQuery.LikeFilter.Good)).getResult().size());
        assertEquals(0,valuationRepository.queryValuation(ValuationQuery.asProductIdAndLikeFilter(2, ValuationQuery.LikeFilter.Fine)).getResult().size());
        assertEquals(0,valuationRepository.queryValuation(ValuationQuery.asProductIdAndLikeFilter(2, ValuationQuery.LikeFilter.Bad)).getResult().size());


        List<Integer> pointList = valuationRepository.queryPointByProductId(2);
        assertEquals(new Integer(4), pointList.get(0));
        assertEquals(1, valuationRepository.queryValuationCountByProductId(2));

        Page<Valuation> valuationPage = valuationRepository.queryValuation(ValuationQuery.asProductId(2));
        assertEquals(1, valuationPage.getResult().size());
        valuationPage = valuationRepository.queryValuation(ValuationQuery.asUsrId(1));
        assertEquals(2, valuationPage.getResult().size());

        assertEquals("xxxx", valuationRepository.getValuationByUserIdAndOrderItemId(1, 1l).getContent());
        assertEquals("xxx", valuationRepository.getValuationByUserIdAndOrderItemId(1, 1l).getUserName());


        assertTrue(valuationRepository.isValuation(1, 1l));

        valuationRepository.deleteValuation(1);
        valuationPage = valuationRepository.queryValuation(ValuationQuery.asProductId(2));
        assertEquals(0, valuationPage.getResult().size());

        valuationPage = valuationRepository.queryValuation(ValuationQuery.asProductId(3));
        assertEquals(1, valuationPage.getResult().size());

        valuationRepository.deleteValuationByProductId(3);

        valuationPage = valuationRepository.queryValuation(ValuationQuery.asProductId(3));
        assertEquals(0, valuationPage.getResult().size());

        valuationRepository.createValuation(valuation);
        valuation.setProductId(4);
        valuationRepository.createValuation(valuation);

        valuation.setOperator("xxx");
        valuation.setOperatorId(1);
        valuation.setReplyContent("cccc");
        valuationRepository.updateValuationReply(valuation);

        Page<Map<String, Long>> page = valuationRepository.queryValuationGroup(1, 1);
        assertEquals(2, page.getTotalCount());
        assertEquals(1, page.getResult().size());
        assertEquals(new Long(1), page.getResult().get(0).get("count"));
        assertEquals(new Long(4), page.getResult().get(0).get("productId"));

    }


}
