package com.kariqu.tradecenter.repository.impl.ibatis;


import com.kariqu.common.pagenavigator.Page;
import com.kariqu.tradecenter.domain.Valuation;
import com.kariqu.tradecenter.repository.ValuationRepository;
import com.kariqu.tradecenter.service.ValuationQuery;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 13-3-4
 * Time: 下午5:04
 */
public class ValuationRepositoryImpl extends SqlMapClientDaoSupport implements ValuationRepository {

    @Override
    public void createValuation(Valuation valuation) {
        getSqlMapClientTemplate().insert("insertValuation", valuation);
    }

    @Override
    public void updateValuationReply(Valuation valuation) {
        getSqlMapClientTemplate().insert("updateValuationReply", valuation);
    }

    @Override
    public boolean isValuation(int userId, long orderItemId) {
        Map param = new HashMap();
        param.put("userId", userId);
        param.put("orderItemId", orderItemId);
        Integer count = (Integer) getSqlMapClientTemplate().queryForObject("selectValuationCountByUserIdAndOrderItemId", param);
        return count > 0;
    }

    @Override
    public void deleteValuation(int id) {
        getSqlMapClientTemplate().delete("deleteValuation", id);
    }

    @Override
    public void deleteImportValuation(int id) {
        getSqlMapClientTemplate().delete("deleteImportValuation", id);
    }

    @Override
    public Page<Valuation> queryValuation(ValuationQuery query) {
        Map param = new HashMap();
        Page<Valuation> page = new Page<Valuation>(query.getPageNo(), query.getPageSize());
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        param.put("productId", query.getProductId());
        param.put("userId", query.getUserId());
        param.put("orderItemId", query.getOrderItemId());
        param.put("point", query.getPoint());
        List<Valuation> list = getSqlMapClientTemplate().queryForList("queryValuation", param);
        Integer count = (Integer) getSqlMapClientTemplate().queryForObject("selectCountValuation", param);
        page.setResult(list);
        page.setTotalCount(count);
        return page;
    }

    @Override
    public Page<Valuation> queryImportValuation(ValuationQuery query) {
        Map param = new HashMap();
        Page<Valuation> page = new Page<Valuation>(query.getPageNo(), query.getPageSize());
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        param.put("productId", query.getProductId());
        param.put("userName", query.getUserName());
        param.put("point", query.getPoint());
        List<Valuation> list = getSqlMapClientTemplate().queryForList("queryImportValuation", param);
        Integer count = (Integer) getSqlMapClientTemplate().queryForObject("selectCountImportValuation", param);
        page.setResult(list);
        page.setTotalCount(count);
        return page;
    }

    @Override
    public Page<Valuation> queryValuationAndImportValuation(ValuationQuery query) {
        Map param = new HashMap();
        Page<Valuation> page = new Page<Valuation>(query.getPageNo(), query.getPageSize());
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        param.put("productId", query.getProductId());
        param.put("userId", query.getUserId());
        param.put("orderItemId", query.getOrderItemId());
        param.put("point", query.getPoint());
        List<Valuation> list = getSqlMapClientTemplate().queryForList("queryValuationAndImportValuation", param);
        Integer count = (Integer) getSqlMapClientTemplate().queryForObject("selectCountValuationAndImportValuation", param);
        page.setResult(list);
        page.setTotalCount(count);
        return page;
    }

    @Override
    public List<Integer> queryPointByProductId(int productId) {
        return getSqlMapClientTemplate().queryForList("queryPointByProductId", productId);
    }

    @Override
    public void deleteValuationByProductId(int productId) {
        getSqlMapClientTemplate().delete("deleteValuationByProductId", productId);
    }

    @Override
    public int queryValuationCountByProductId(int productId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("queryValuationCountByProductId", productId);
    }

    @Override
    public Page<Map<String, Long>> queryValuationGroup(int start, int limit) {
        Page<Map<String, Long>> page = Page.createFromStart(start, limit);
        Map param = new HashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Map<String, Long>> list = getSqlMapClientTemplate().queryForList("queryValuationGroup", param);
        page.setResult(list);
        Integer count = (Integer) getSqlMapClientTemplate().queryForObject("queryValuationGroupCount");
        page.setTotalCount(count);
        return page;
    }

    @Override
    public Valuation getValuationByUserIdAndOrderItemId(int userId, long orderItemId) {
        Map param = new HashMap();
        param.put("userId", userId);
        param.put("orderItemId", orderItemId);
        return (Valuation) getSqlMapClientTemplate().queryForObject("getValuationByUserIdAndOrderItemId", param);
    }

    @Override
    public void createImportValuation(Valuation valuation) {
        getSqlMapClientTemplate().insert("insertImportValuation", valuation);
    }
}
