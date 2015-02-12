package com.kariqu.productcenter.service.impl;

import com.kariqu.productcenter.domain.AttentionInfo;
import com.kariqu.productcenter.repository.AttentionInfoRepository;
import com.kariqu.productcenter.service.AttentionInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: wendy
 * Date: 11-7-13
 * Time: 下午4:36
 */

@Transactional
public class AttentionInfoServiceImpl implements AttentionInfoService {

    @Autowired
    private AttentionInfoRepository attentionInfoRepository;


    @Override
    public List<AttentionInfo> queryAllUseByProductId(int productId) {
        List<AttentionInfo> attentionInfoList = attentionInfoRepository.queryAttentionInfo(productId,AttentionInfo.Type.Use.name());
        return attentionInfoList;
    }

    @Override
    public List<AttentionInfo> queryAllMaintenanceByProductId(int productId) {
        List<AttentionInfo> attentionInfoList = attentionInfoRepository.queryAttentionInfo(productId,AttentionInfo.Type.Maintenance.name());
        return attentionInfoList;
    }

    @Override
    public void deleteAttentionById(int id) {
        attentionInfoRepository.deleteAttentionById(id);
    }

    @Override
    public void deleteAttentionByProductId(int productId) {
        attentionInfoRepository.deleteAttentionByProductId(productId);
    }

    @Override
    public void updateAttention(AttentionInfo attentionInfo) {
        attentionInfoRepository.updateAttention(attentionInfo);
    }

    @Override
    public void createAttention(AttentionInfo attentionInfo) {
        attentionInfoRepository.createAttention(attentionInfo);
    }
}
