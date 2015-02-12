package com.kariqu.tradecenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Valuation;
import com.kariqu.tradecenter.service.ValuationQuery;

import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 13-3-4
 * Time: 下午5:37
 */
public interface ValuationService {

    void createValuation(Valuation valuation);

    void deleteValuation(int id);

    // 删除导出的评论信息
    void deleteImportValuation(int id);

    int queryValuationCountByProductId(int productId);

    boolean isValuation(int userId, long orderItemId);

    Page<Map<String, Long>> queryValuationGroup(int start, int limit);

    /**
     * 一个用户只能对一个item评一次
     *
     * @param userId
     * @param orderItemId
     * @return
     */
    Valuation getValuationByUserIdAndOrderItemId(int userId, long orderItemId);

    Page<Valuation> queryValuation(ValuationQuery query);

    /**
     * 查询导入评论表数据
     * @param query
     * @return
     */
    Page<Valuation> queryImportValuation(ValuationQuery query);

    Page<Valuation> queryValuationAndImportValuation(ValuationQuery query);

    List<Integer> queryPointByProductId(int productId);

    void deleteValuationByProductId(int productId);

    void createValuationReply(Valuation valuation);

    void createImportValuation(Valuation valuation);
}
