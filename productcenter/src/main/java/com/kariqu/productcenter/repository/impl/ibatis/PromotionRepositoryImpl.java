package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Promotion;
import com.kariqu.productcenter.repository.PromotionRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Baron.Zhang
 * Date: 2014/10/16
 * Time: 11:21
 */
public class PromotionRepositoryImpl extends SqlMapClientDaoSupport implements PromotionRepository {

    @Override
    public void create(Promotion promotion) {
        getSqlMapClientTemplate().insert("create",promotion);
    }

    @Override
    public void delete(int id) {
        getSqlMapClientTemplate().delete("delete",id);
    }

    @Override
    public void update(Promotion promotion) {
        getSqlMapClientTemplate().update("update",promotion);
    }

    @Override
    public Promotion get(int id) {
        return (Promotion) getSqlMapClientTemplate().queryForObject("get",id);
    }

    @Override
    public Page<Promotion> queryPromotionsByTopicId(int topicId,int pageNo,int limit) {
        Page<Promotion> page = new Page<Promotion>(pageNo,limit);
        page.setTotalCount(queryPromotionsAmountByTopicId(topicId));

        Map map = new HashMap();
        map.put("topicId",topicId);
        map.put("pageNo",pageNo);
        map.put("limit",limit);
        List<Promotion> promotionList = getSqlMapClientTemplate().queryForList("queryPromotionsByTopicId",map);
        page.setResult(promotionList);
        return page;
    }

    @Override
    public int queryPromotionsAmountByTopicId(int topicId) {
        return (Integer)getSqlMapClientTemplate().queryForObject("queryPromotionsAmountByTopicId",topicId);
    }
}
