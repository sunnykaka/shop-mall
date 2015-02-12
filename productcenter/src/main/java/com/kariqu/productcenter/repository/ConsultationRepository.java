package com.kariqu.productcenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Consultation;
import com.kariqu.productcenter.domain.ConsultationCategory;

import java.util.Date;

/**
 * @author: enoch
 * @since 1.0.0
 *        Date: 12-8-28
 *        Time: 上午10:25
 */
public interface ConsultationRepository  {

    void createConsultation(Consultation consultation);

    void answerConsultation(int id, String answerContent,Date answerTime);

    Page<Consultation> queryConsultation(Page<Consultation> page, ConsultationCategory category ,Integer productId ,Integer hasAnswer);

    Page<Consultation> searchConsultation(Page<Consultation> page,ConsultationCategory category,Integer hasAnswer,String askUserId);

    /**
     * 查询用户所有已回复的咨询.
     *
     * @param userId
     * @param hasAnswer
     * @return
     */
    int queryConsultationCountByUserId(int userId, boolean hasAnswer);

    Consultation queryConsultationById(int id);

    void deleteConsultationById(int id);

    void deleteConsultationByProductId(int productId);

    Integer queryConsultationNumById(Integer productId, ConsultationCategory consultationCategory, Integer hasAnswer);

}
