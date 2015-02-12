package com.kariqu.om.service.impl;

import com.kariqu.om.domain.Const;
import com.kariqu.om.repository.ConstRepository;
import com.kariqu.om.service.ConstService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Athens(刘杰)
 * @Time 13-10-11 下午6:22
 */
public class ConstServiceImpl implements ConstService {
    
    @Autowired
    private ConstRepository constRepository;
    
    @Override
    public void insertConst(Const constInfo) {
        constRepository.insertConst(constInfo);
    }

    @Override
    public List<Const> getAllConst() {
        return constRepository.getAllConst();
    }

    @Override
    public Const getConstByKey(String constKey) {
        return constRepository.getConstByKey(constKey);
    }

    @Override
    public void updateConst(Const constInfo) {
        constRepository.update(constInfo);
    }

    @Override
    public void deleteConst(String constKey) {
        constRepository.delete(constKey);
    }
}
