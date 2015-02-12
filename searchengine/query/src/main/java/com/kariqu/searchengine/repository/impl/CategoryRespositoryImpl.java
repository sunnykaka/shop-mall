package com.kariqu.searchengine.repository.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.mapping.statement.DefaultRowHandler;
import com.kariqu.searchengine.repository.CategoryRespository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.List;
import java.util.Map;

/**
 * Created by play.liu on 2014/7/1.
 * Description:
 */
public class CategoryRespositoryImpl extends SqlMapClientDaoSupport implements CategoryRespository {
    @Override
    public List<String> loadAllCategoryName() {
        List<String> list = getSqlMapClientTemplate().queryForList("queryAllCatetory");
        return list;
    }
    public List<String> loadAllCategoryBrandMapping(){
        List<String> list = getSqlMapClientTemplate().queryForList("queryAllBrandNameBrandNameMapping",String.class);
        return list;
    }
}
