package com.kariqu.om.service.impl;

import com.kariqu.om.domain.Seo;
import com.kariqu.om.domain.SeoType;
import com.kariqu.om.repository.SeoRepository;
import com.kariqu.om.service.SeoService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Alec
 * Date: 13-10-9
 * Time: 下午2:37
 */
public class SeoServiceImpl implements SeoService {

    @Autowired
    private SeoRepository seoRepository;

    @Override
    public void insertSeo(Seo seo) {
        seoRepository.insertSeo(seo);
    }

    @Override
    public void updateSeo(Seo seo) {
        seoRepository.updateSeo(seo);
    }

    @Override
    public Seo querySeoByObjIdAndType(String objId, SeoType seoType) {
        return seoRepository.querySeoByObjIdAndType(objId, seoType);
    }

    @Override
    public Seo querySeoById(int id) {
        return seoRepository.querySeoById(id);
    }
}
