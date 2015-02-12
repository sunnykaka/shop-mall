package com.kariqu.om.service;

import com.kariqu.om.domain.*;

import java.util.List;

/**
 * @author Athens(刘杰)
 */
public interface ConstService {

    void insertConst(Const constInfo);

    List<Const> getAllConst();

    Const getConstByKey(String constKey);

    void updateConst(Const constInfo);

    void deleteConst(String constKey);

}
