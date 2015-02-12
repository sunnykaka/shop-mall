package com.kariqu.productcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.PromotionTopic;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: Baron.Zhang
 * Date: 2014/10/14
 * Time: 15:36
 */
public interface PromotionTopicRepository {

    void create(PromotionTopic promotionTopic);

    void delete(int id);

    void update(PromotionTopic promotionTopic);

    PromotionTopic get(int id);

    Page<PromotionTopic> queryPromotionTopics(String name,String topic,int pageNo,int limit);

    int queryPromotionTopicsAmount(String name,String topic);

    Page<PromotionTopic> queryAllPromotionTopics(int pageNo,int limit);

    int queryAllPromotionTopicsAmount();
}
