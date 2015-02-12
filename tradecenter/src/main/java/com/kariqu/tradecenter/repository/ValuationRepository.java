package com.kariqu.tradecenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Valuation;
import com.kariqu.tradecenter.service.ValuationQuery;

import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 13-3-4
 * Time: 下午4:58
 */
public interface ValuationRepository {

    void createValuation(Valuation valuation);

    //创建评论回复，目前把数据打包到valuation中
    void updateValuationReply(Valuation valuation);

    Page<Valuation> queryValuation(ValuationQuery query);

    // 查询导入评价表数据
    Page<Valuation> queryImportValuation(ValuationQuery query);

    Page<Valuation> queryValuationAndImportValuation(ValuationQuery query);

    List<Integer> queryPointByProductId(int productId);

    boolean isValuation(int userId, long orderItemId);

    void deleteValuation(int id);

    // 删除导入的商品评论信息
    void deleteImportValuation(int id);

    void deleteValuationByProductId(int productId);

    Page<Map<String, Long>> queryValuationGroup(int start, int limit);

    int queryValuationCountByProductId(int productId);

    Valuation getValuationByUserIdAndOrderItemId(int userId, long orderItemId);

    void createImportValuation(Valuation valuation);
}
