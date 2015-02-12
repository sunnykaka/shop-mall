package com.kariqu.tradecenter.service.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.tradecenter.domain.Valuation;
import com.kariqu.tradecenter.repository.ValuationRepository;
import com.kariqu.tradecenter.service.ValuationQuery;
import com.kariqu.tradecenter.service.ValuationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 13-3-4
 * Time: 下午5:38
 */
public class ValuationServiceImpl implements ValuationService {

    @Autowired
    private ValuationRepository valuationRepository;

    @Autowired
    private ProductService productService;


    @Override
    public Valuation getValuationByUserIdAndOrderItemId(int userId, long orderItemId) {
        return valuationRepository.getValuationByUserIdAndOrderItemId(userId, orderItemId);
    }

    @Override
    @Transactional
    public void createValuation(Valuation valuation) {
        valuationRepository.createValuation(valuation);
        productService.notifyProductUpdate(valuation.getProductId());
    }

    @Override
    @Transactional
    public void deleteValuation(int id) {
        valuationRepository.deleteValuation(id);
    }

    @Override
    @Transactional
    public void deleteImportValuation(int id) {
        valuationRepository.deleteImportValuation(id);
    }

    @Override
    public boolean isValuation(int userId, long orderItemId) {
        return valuationRepository.isValuation(userId, orderItemId);
    }

    @Override
    public int queryValuationCountByProductId(int productId) {
        return valuationRepository.queryValuationCountByProductId(productId);
    }

    @Override
    public Page<Map<String, Long>> queryValuationGroup(int start, int limit) {
        return valuationRepository.queryValuationGroup(start, limit);
    }

    @Override
    public Page<Valuation> queryValuation(ValuationQuery query) {
        return valuationRepository.queryValuation(query);
    }

    @Override
    public Page<Valuation> queryImportValuation(ValuationQuery query) {
        return valuationRepository.queryImportValuation(query);
    }

    @Override
    public Page<Valuation> queryValuationAndImportValuation(ValuationQuery query) {
        return valuationRepository.queryValuationAndImportValuation(query);
    }

    @Override
    public List<Integer> queryPointByProductId(int productId) {
        return valuationRepository.queryPointByProductId(productId);
    }

    @Override
    @Transactional
    public void deleteValuationByProductId(int productId) {
        valuationRepository.deleteValuationByProductId(productId);
    }

    @Override
    public void createValuationReply(Valuation valuation) {
        valuationRepository.updateValuationReply(valuation);
    }

    @Override
    @Transactional
    public void createImportValuation(Valuation valuation) {
        valuationRepository.createImportValuation(valuation);
    }
}
