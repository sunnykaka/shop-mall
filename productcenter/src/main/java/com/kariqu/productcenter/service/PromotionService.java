package com.kariqu.productcenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Promotion;
import com.kariqu.productcenter.domain.PromotionTopic;

import java.util.List;

/**
 * User: Baron.Zhang
 * Date: 2014/10/16
 * Time: 16:46
 */
public interface PromotionService {

    void createPromotion(Promotion promotion);

    void deletePromotion(int id);
    void deletePromotions(int[] ids);

    void updatePromotion(Promotion promotion);

    Promotion getPromotion(int id);

    Page<Promotion> queryPromotionsByTopicId(int topicId,int pageNo,int limit);

    void createPromotionTopic(PromotionTopic promotionTopic);

    void deletePromotionTopic(int id);

    void deletePromotionTopics(int[] ids);

    void updatePromotionTopic(PromotionTopic promotionTopic);

    PromotionTopic getPromotionTopic(int id);

    Page<PromotionTopic> queryPromotionTopics(String name,String topic,int pageNo,int limit);

    Page<PromotionTopic> queryAllPromotionTopics(int pageNo,int limit);

}
