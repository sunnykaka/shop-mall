package com.kariqu.tradecenter.repository.impl.ibatis;

import com.kariqu.tradecenter.domain.payment.SkuTradeResult;
import com.kariqu.tradecenter.repository.SkuTradeResultRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

/**
 * @author Athens(刘杰)
 * @Time 2012-11-16 17:30
 * @since 1.0.0
 */
public class SkuTradeResultRepositoryImpl extends SqlMapClientDaoSupport implements SkuTradeResultRepository {

    public void insert(SkuTradeResult result) {
        getSqlMapClientTemplate().insert("insertSkuTradeResult", result);
    }

    public void update(SkuTradeResult result) {
        getSqlMapClientTemplate().update("updateSkuTradeResult", result);
    }

    public SkuTradeResult getById(long id) {
        return (SkuTradeResult) getSqlMapClientTemplate().queryForObject("selectSkuTradeResult", id);
    }

    public SkuTradeResult getBySkuId(long skuId) {
        return (SkuTradeResult) getSqlMapClientTemplate().queryForObject("selectBySkuId", skuId);
    }

    public SkuTradeResult getByProductId(int productId) {
        return (SkuTradeResult) getSqlMapClientTemplate().queryForObject("selectProductTradeResult", productId);
    }

    public void delete(long id) {
        getSqlMapClientTemplate().delete("deleteSkuTradeResult", id);
    }

}
