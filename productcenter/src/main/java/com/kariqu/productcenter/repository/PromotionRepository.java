package com.kariqu.productcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Promotion;

import java.util.List;

/**
 * User: Baron.Zhang
 * Date: 2014/10/14
 * Time: 15:35
 */
public interface PromotionRepository {

    void create(Promotion promotion);

    void delete(int id);

    void update(Promotion promotion);

    Promotion get(int id);

    Page<Promotion> queryPromotionsByTopicId(int topicId,int pageNo,int limit);

    int queryPromotionsAmountByTopicId(int topicId);

}
