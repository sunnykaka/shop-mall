package com.kariqu.categorycenter.domain.repository;

import com.kariqu.categorycenter.domain.model.Value;

import java.util.List;

/**
 * User: Asion
 * Date: 11-6-25
 * Time: 下午2:40
 */
public interface ValueRepository{

    Value getValueByName(String valueName);

    List<Value> queryAllValues();

    void deleteAllValue();

    void deleteValueById(int id);

    void updateValue(Value value);

    Value getValueById(int id);

    void createValue(Value value);
}
