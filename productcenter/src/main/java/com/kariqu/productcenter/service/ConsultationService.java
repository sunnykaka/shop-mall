package com.kariqu.productcenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Consultation;
import com.kariqu.productcenter.domain.ConsultationCategory;

import java.util.Date;

/**
 * @author: enoch
 * @since 1.0.0
 *        Date: 12-8-28
 *        Time: 下午4:10
 */
public interface ConsultationService {

    /**
     * 创建咨询
     * @param consultation
     */
    void createConsultation(Consultation consultation);

    /**
     * 答复咨询
     * @param id
     */
    void answerConsultation(int id, String answerContent,Date answerTime);

    /**
     * 分页查询商品下的咨询
     * @param page
     * @param category
     * @param productId
     * @return
     */
    Page<Consultation> queryConsultation(Page<Consultation> page, ConsultationCategory category, Integer productId ,Integer hasAnswer);

    Consultation queryConsultationById(int id);

    Page<Consultation> searchConsultation(Page<Consultation> page,ConsultationCategory category,Integer hasAnswer,String askUserId);

    /**
     * 查询用户 已回复的咨询数量
     *
     * @param userId
     * @param hasAnswer
     * @return
     */
    int queryConsultationCountByUserId(int userId, boolean hasAnswer);

    void  deleteConsultationById(int id);

    /**
     * 查询咨询数量
     *
     * @param productId 商品id
     * @param consultationCategory 咨询类型
     * @param hasAnswer 客服是否有回复, 若有回复则传入 1
     * @return
     */
    int queryConsultationNumById(int productId, ConsultationCategory consultationCategory, int hasAnswer);

}
