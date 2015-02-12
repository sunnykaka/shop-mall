package com.kariqu.om.repository.impl;

import com.kariqu.om.domain.Seo;
import com.kariqu.om.domain.SeoType;
import com.kariqu.om.repository.SeoRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Map;
import java.util.HashMap;

/**
 * User: Alec
 * Date: 13-10-9
 * Time: 下午2:30
 */
public class SeoRepositoryImpl extends SqlMapClientDaoSupport implements SeoRepository {
    @Override
    public void insertSeo(Seo seo) {
        this.getSqlMapClientTemplate().insert("insertSeo", seo);
    }

    @Override
    public void updateSeo(Seo seo) {
        this.getSqlMapClientTemplate().update("updateSeo", seo);
    }

    @Override
    public Seo querySeoByObjIdAndType(String objId, SeoType seoType) {
        Map param = new HashMap();
        param.put("seoObjectId", objId);
        param.put("seoType", seoType);
        return (Seo) this.getSqlMapClientTemplate().queryForObject("querySeoByObjIdAndType", param);
    }

    @Override
    public Seo querySeoById(int id) {
        return (Seo) this.getSqlMapClientTemplate().queryForObject("querySeoById", id);
    }
}
