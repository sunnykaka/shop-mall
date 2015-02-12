package com.kariqu.usercenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.UserSignIn;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Athens(刘杰)
 */
public class SignInRepository extends SqlMapClientDaoSupport {

    public Page<UserSignIn> queryAllUserSignIn(Page page) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("start", page.getStart());
        map.put("limit", page.getLimit());
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("queryAllCountUserSignIn", map));
        page.setResult(getSqlMapClientTemplate().queryForList("queryAllUserSignIn", map));
        return page;
    }

    public UserSignIn queryUserSignInByUserId(int userId) {
        return (UserSignIn) getSqlMapClientTemplate().queryForObject("queryUserSignInByUserId", userId);
    }

    public void insertUserSignIn(UserSignIn userSignIn) {
        getSqlMapClientTemplate().insert("insertUserSignIn", userSignIn);
    }

    public int updateUserSignIn(UserSignIn userSignIn) {
        return getSqlMapClientTemplate().update("updateUserSignIn", userSignIn);
    }

    public void deleteUserSignIn(int id) {
        getSqlMapClientTemplate().delete("deleteUserSignIn", id);
    }

}
