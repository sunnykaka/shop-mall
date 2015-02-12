package com.kariqu.om.repository;

import com.kariqu.om.domain.Seo;
import com.kariqu.om.domain.SeoType;

/**
 * User: Alec
 * Date: 13-10-9
 * Time: 下午2:26
 */
public interface SeoRepository {

    void insertSeo(Seo seo);

    void updateSeo(Seo seo);

    Seo querySeoByObjIdAndType(String objId, SeoType seoType);

    Seo querySeoById(int id);
}
