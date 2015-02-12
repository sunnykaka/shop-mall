package com.kariqu.om.repository;

import com.kariqu.om.domain.*;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Athens(刘杰)
 * @Time 2013-04-23 10:54
 * @since 1.0.0
 */
public class ConstRepository extends SqlMapClientDaoSupport  {

    public void insertConst(Const constInfo) {
        getSqlMapClientTemplate().insert("insertConst", constInfo);
    }

    public List<Const> getAllConst() {
        return getSqlMapClientTemplate().queryForList("queryAllConst");
    }

    public Const getConstByKey(String constKey) {
        return (Const) getSqlMapClientTemplate().queryForObject("queryConstByKey", constKey);
    }

    public void update(Const constInfo) {
        getSqlMapClientTemplate().update("updateConst", constInfo);
    }

    public void delete(String constKey) {
        getSqlMapClientTemplate().delete("deleteConstByKey", constKey);
    }

}
