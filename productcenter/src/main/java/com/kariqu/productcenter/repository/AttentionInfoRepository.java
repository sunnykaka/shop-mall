package com.kariqu.productcenter.repository;

import com.kariqu.productcenter.domain.AttentionInfo;
import java.util.List;

/**
 * User: wendy
 * Date: 11-6-27
 * Time: 上午10:39
 */
public interface AttentionInfoRepository {

     List<AttentionInfo>  queryAttentionInfo(int productId,String type);

     void deleteAttentionById(int id);

     void deleteAttentionByProductId(int productId);

     void updateAttention(AttentionInfo attentionInfo);

     void createAttention(AttentionInfo attentionInfo);
}
