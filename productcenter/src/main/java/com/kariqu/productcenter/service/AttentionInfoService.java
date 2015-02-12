package com.kariqu.productcenter.service;

import com.kariqu.productcenter.domain.*;
import java.util.List;

/**
 * User: wendy
 * Date: 11-6-27
 * Time: 上午10:37
 */
public interface AttentionInfoService {

    /**
     * 判断注意事项表里面的isNot值是否为0，为0就为使用注意
     * @param productId
     * @return
     */
    List<AttentionInfo>  queryAllUseByProductId(int productId);

    /**
     * 判断注意事项表里面的isNot值是否为1，为1就为保养注意
     * @param productId
     * @return
     */
    List<AttentionInfo>  queryAllMaintenanceByProductId(int productId);

    void deleteAttentionById(int id);

    void deleteAttentionByProductId(int productId);

    void updateAttention(AttentionInfo attentionInfo);

    void createAttention(AttentionInfo attentionInfo);

}
