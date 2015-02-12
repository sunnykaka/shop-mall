package com.kariqu.productcenter.service.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Promotion;
import com.kariqu.productcenter.domain.PromotionTopic;
import com.kariqu.productcenter.repository.PromotionRepository;
import com.kariqu.productcenter.repository.PromotionTopicRepository;
import com.kariqu.productcenter.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: Baron.Zhang
 * Date: 2014/10/16
 * Time: 16:47
 */
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionTopicRepository promotionTopicRepository;


    @Override
    public void createPromotion(Promotion promotion) {
        promotionRepository.create(promotion);
    }

    @Override
    public void deletePromotion(int id) {
        promotionRepository.delete(id);
    }

    @Override
    @Transactional
    public void deletePromotions(int[] ids) {
        for(int id : ids){
            deletePromotion(id);
        }
    }

    @Override
    public void updatePromotion(Promotion promotion) {
        promotionRepository.update(promotion);
    }

    @Override
    public Promotion getPromotion(int id) {
        return promotionRepository.get(id);
    }

    @Override
    public Page<Promotion> queryPromotionsByTopicId(int topicId, int pageNo, int limit) {
        return promotionRepository.queryPromotionsByTopicId(topicId,pageNo,limit);
    }

    @Override
    public void createPromotionTopic(PromotionTopic promotionTopic) {
        promotionTopicRepository.create(promotionTopic);
    }

    @Override
    public void deletePromotionTopic(int id) {
        promotionTopicRepository.delete(id);
    }

    @Override
    @Transactional
    public void deletePromotionTopics(int[] ids) {
        for(int id : ids){
            deletePromotionTopic(id);
        }
    }

    @Override
    public void updatePromotionTopic(PromotionTopic promotionTopic) {
        promotionTopicRepository.update(promotionTopic);
    }

    @Override
    public PromotionTopic getPromotionTopic(int id) {
        return promotionTopicRepository.get(id);
    }

    @Override
    public Page<PromotionTopic> queryPromotionTopics(String name, String topic, int pageNo, int limit) {
        return promotionTopicRepository.queryPromotionTopics(name,topic,pageNo,limit);
    }

    @Override
    public Page<PromotionTopic> queryAllPromotionTopics(int pageNo, int limit) {
        return promotionTopicRepository.queryAllPromotionTopics(pageNo,limit);
    }
}
