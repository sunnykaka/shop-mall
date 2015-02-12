package com.kariqu.productcenter.repository.impl.ibatis;

import com.google.common.collect.Maps;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.domain.Consultation;
import com.kariqu.productcenter.domain.ConsultationCategory;
import com.kariqu.productcenter.repository.ConsultationRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: enoch
 * @since 1.0.0
 *        Date: 12-8-28
 *        Time: 上午10:32
 */
public class ConsultationRepositoryImpl extends SqlMapClientDaoSupport implements ConsultationRepository {
    @Override
    public void createConsultation(Consultation consultation) {
        getSqlMapClientTemplate().insert("createConsultation", consultation);
    }

    @Override
    public void answerConsultation(int id, String answerContent, Date answerTime) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", id);
        param.put("answerContent", answerContent);
        param.put("answerTime", answerTime);
        getSqlMapClientTemplate().update("answerConsultation", param);
    }

    @Override
    public Page<Consultation> queryConsultation(Page<Consultation> page, ConsultationCategory category, Integer productId, Integer hasAnswer) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        
        param.put("consultationCategory", category);
        param.put("productId", productId);
        param.put("hasAnswer", hasAnswer);
        
        page.setResult(getSqlMapClientTemplate().queryForList("queryConsultation", param));
        page.setTotalCount(queryConsultationNumById(productId, category, hasAnswer));
        return page;
    }

    @Override
    public Page<Consultation> searchConsultation(Page<Consultation> page, ConsultationCategory category, Integer hasAnswer, String askUserId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        param.put("consultationCategory", category);
        param.put("askUserId", askUserId);
        param.put("hasAnswer", hasAnswer);

        List<Consultation> result = getSqlMapClientTemplate().queryForList("searchConsultation", param);
        int totalCount = (Integer) this.getSqlMapClientTemplate().queryForObject("searchConsultationCount", param);
        page.setResult(result);
        page.setTotalCount(totalCount);
        return page;
    }

    @Override
    public int queryConsultationCountByUserId(int userId, boolean hasAnswer) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("askUserId", userId);
        param.put("hasAnswer", hasAnswer);
        return (Integer) getSqlMapClientTemplate().queryForObject("searchConsultationCount", param);
    }

    @Override
    public Consultation queryConsultationById(int id) {
        return (Consultation) getSqlMapClientTemplate().queryForObject("queryConsultationById", id);
    }

    @Override
    public void deleteConsultationById(int id) {
        getSqlMapClientTemplate().update("deleteConsultationById", id);
    }

    @Override
    public void deleteConsultationByProductId(int productId) {
        getSqlMapClientTemplate().update("deleteConsultationByProductId", productId);
    }

    @Override
    public Integer queryConsultationNumById(Integer productId, ConsultationCategory consultationCategory, Integer hasAnswer) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("consultationCategory", consultationCategory);
        param.put("productId", productId);
        param.put("hasAnswer", hasAnswer);
        return (Integer) getSqlMapClientTemplate().queryForObject("queryAmountOfConsultation", param);
    }
}
