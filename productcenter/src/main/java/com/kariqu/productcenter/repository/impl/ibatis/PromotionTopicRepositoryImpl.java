package com.kariqu.productcenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.PromotionTopic;
import com.kariqu.productcenter.repository.PromotionTopicRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Baron.Zhang
 * Date: 2014/10/16
 * Time: 11:20
 */
public class PromotionTopicRepositoryImpl extends SqlMapClientDaoSupport implements PromotionTopicRepository {

    @Override
    public void create(PromotionTopic promotionTopic) {
        getSqlMapClientTemplate().insert("create",promotionTopic);
    }

    @Override
    public void delete(int id) {
        getSqlMapClientTemplate().delete("delete",id);
    }

    @Override
    public void update(PromotionTopic promotionTopic) {
        getSqlMapClientTemplate().update("update",promotionTopic);
    }

    @Override
    public PromotionTopic get(int id) {
        return (PromotionTopic) getSqlMapClientTemplate().queryForObject("get",id);
    }

    @Override
    public Page<PromotionTopic> queryPromotionTopics(String name, String topic,int pageNo,int limit) {

        Page<PromotionTopic> page = new Page<PromotionTopic>(pageNo,limit);
        page.setTotalCount(queryPromotionTopicsAmount(name,topic));

        Map map = new HashMap();
        map.put("name",name);
        map.put("topic",topic);
        map.put("pageNo",pageNo);
        map.put("limit",limit);

        List<PromotionTopic> promotionTopicList =  getSqlMapClientTemplate().queryForList("queryPromotionTopics",map);

        page.setResult(promotionTopicList);

        return page;
    }

    @Override
    public int queryPromotionTopicsAmount(String name, String topic) {
        return (Integer)getSqlMapClientTemplate().queryForObject("queryPromotionTopicsAmount",name,topic);
    }

    @Override
    public Page<PromotionTopic> queryAllPromotionTopics(int pageNo,int limit) {

        Page<PromotionTopic> page = new Page<PromotionTopic>(pageNo,limit);
        page.setTotalCount(queryAllPromotionTopicsAmount());

        Map map = new HashMap();
        map.put("pageNo",pageNo);
        map.put("limit",limit);

        List<PromotionTopic> promotionTopicList = getSqlMapClientTemplate().queryForList("queryAllPromotionTopics",map);
        page.setResult(promotionTopicList);

        return page;
    }

    @Override
    public int queryAllPromotionTopicsAmount() {
        return (Integer)getSqlMapClientTemplate().queryForObject("queryAllPromotionTopicsAmount");
    }

}
