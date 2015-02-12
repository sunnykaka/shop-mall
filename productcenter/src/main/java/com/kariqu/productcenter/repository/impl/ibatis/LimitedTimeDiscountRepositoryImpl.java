package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.LimitedTimeDiscount;
import com.kariqu.productcenter.query.LimitedTimeDiscountQuery;
import com.kariqu.productcenter.repository.LimitedTimeDiscountRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LimitedTimeDiscountRepositoryImpl extends SqlMapClientDaoSupport implements LimitedTimeDiscountRepository {

    @Override
    public void insert(LimitedTimeDiscount limitedTimeDiscount) {
        getSqlMapClientTemplate().insert("insertLimitedTimeDiscount", limitedTimeDiscount);
    }

    @Override
    public void update(LimitedTimeDiscount limitedTimeDiscount) {
        getSqlMapClientTemplate().update("updateLimitedTimeDiscount", limitedTimeDiscount);
    }

    @Override
    public void delete(long id) {
        getSqlMapClientTemplate().delete("deleteLimitedTimeDiscount", id);
    }

    @Override
    public Page<LimitedTimeDiscount> select(LimitedTimeDiscountQuery query) {
        Page<LimitedTimeDiscount> page = new Page<LimitedTimeDiscount>(query.getPageNo(), query.getPageSize());
        Map param = new HashMap();
        param.put("start", query.getStart());
        param.put("limit", query.getLimit());
        param.put("productId", query.getProductId());
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountLimitedTimeDiscount", param));
        page.setResult(getSqlMapClientTemplate().queryForList("selectLimitedTimeDiscount", param));
        return page;
    }

    @Override
    public LimitedTimeDiscount selectById(long id) {
        return (LimitedTimeDiscount) getSqlMapClientTemplate().queryForObject("selectLimitedTimeDiscountById", id);
    }

    @Override
    public List<LimitedTimeDiscount> selectByProductId(int productId) {
        return getSqlMapClientTemplate().queryForList("selectLimitedTimeDiscountByProductId", productId);
    }

    @Override
    public LimitedTimeDiscount selectByProductIdAndTime(long productId, Date date) {
        Map map = new HashMap();
        map.put("productId", productId);
        map.put("dateInterval", date);
        return (LimitedTimeDiscount) getSqlMapClientTemplate().queryForObject("selectLimitedTimeDiscountByProductIdAndTime", map);
    }

}
