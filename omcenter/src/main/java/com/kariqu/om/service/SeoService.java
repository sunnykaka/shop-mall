package com.kariqu.om.service;

import com.kariqu.om.domain.Seo;
import com.kariqu.om.domain.SeoType;

/**
 * SEO 推广
 * User: Alec
 * Date: 13-10-9
 * Time: 下午2:35
 */
public interface SeoService {

    void insertSeo(Seo seo);

    void updateSeo(Seo seo);

    Seo querySeoByObjIdAndType(String objId, SeoType seoType);

    Seo querySeoById(int id);
}
