package com.kariqu.productcenter.service.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Consultation;
import com.kariqu.productcenter.domain.ConsultationCategory;
import com.kariqu.productcenter.repository.ConsultationRepository;
import com.kariqu.productcenter.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author: enoch
 * @since 1.0.0
 *        Date: 12-8-28
 *        Time: 下午4:12
 */
public class ConsultationServiceImpl implements ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Override
    public void createConsultation(Consultation consultation) {
        consultationRepository.createConsultation(consultation);
    }

    @Override
    public void answerConsultation(int id, String answerContent, Date answerTime) {
        consultationRepository.answerConsultation(id, answerContent, answerTime);
    }

    @Override
    public Page<Consultation> queryConsultation(Page<Consultation> page, ConsultationCategory category, Integer productId, Integer hasAnswer) {
        return consultationRepository.queryConsultation(page, (category == null || category.queryAll() ? null : category), productId, hasAnswer);
    }

    @Override
    public Consultation queryConsultationById(int id) {
        return consultationRepository.queryConsultationById(id);
    }

    @Override
    public Page<Consultation> searchConsultation(Page<Consultation> page, ConsultationCategory category, Integer hasAnswer, String askUserId) {
        return consultationRepository.searchConsultation(page, category, hasAnswer, askUserId);
    }

    @Override
    public int queryConsultationCountByUserId(int userId, boolean hasAnswer) {
        return consultationRepository.queryConsultationCountByUserId(userId, hasAnswer);
    }

    @Override
    public void deleteConsultationById(int id) {
        consultationRepository.deleteConsultationById(id);
    }

    @Override
    public int queryConsultationNumById(int productId, ConsultationCategory consultationCategory, int hasAnswer) {
        return consultationRepository.queryConsultationNumById(productId, consultationCategory, hasAnswer);
    }
}
