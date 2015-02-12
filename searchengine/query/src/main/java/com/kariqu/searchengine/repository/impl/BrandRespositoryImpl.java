package com.kariqu.searchengine.repository.impl;

import com.kariqu.searchengine.repository.BrandRespository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;

/**
 * Created by play.liu on 2014/7/1.
 * Description:
 */
public class BrandRespositoryImpl extends SqlMapClientDaoSupport implements BrandRespository {
    @Override
    public List<String> loadAllBrandName() {

        List<String> list = getSqlMapClientTemplate().queryForList("queryAllBrand");

        return list;
    }
}
