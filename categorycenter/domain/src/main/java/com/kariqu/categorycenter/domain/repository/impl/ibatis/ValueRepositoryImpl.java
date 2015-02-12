package com.kariqu.categorycenter.domain.repository.impl.ibatis;

import com.kariqu.categorycenter.domain.model.Value;
import com.kariqu.categorycenter.domain.repository.ValueRepository;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Date;
import java.util.List;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午3:18
 */
public class ValueRepositoryImpl extends SqlMapClientDaoSupport implements ValueRepository {

    @Override
    public void createValue(Value value) {
        getSqlMapClientTemplate().insert("insertValue", value);
    }

    @Override
    public Value getValueById(int id) {
        return (Value) getSqlMapClientTemplate().queryForObject("selectValue", id);
    }

    @Override
    public Value getValueByName(String valueName) {
        return (Value) getSqlMapClientTemplate().queryForObject("getValueByName", valueName);
    }

    @Override
    public void updateValue(Value value) {
        getSqlMapClientTemplate().update("updateValue", value);
    }

    @Override
    public void deleteValueById(int id) {
        getSqlMapClientTemplate().delete("deleteValue", id);
    }

    @Override
    public void deleteAllValue() {
        getSqlMapClientTemplate().delete("deleteAllValue", new Date());
    }

    @Override
    public List<Value> queryAllValues() {
        return getSqlMapClientTemplate().queryForList("selectAllValues");
    }

}
