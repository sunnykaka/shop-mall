package com.kariqu.usercenter.repository;

import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.*;
import com.kariqu.usercenter.service.UserQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Asion
 * Date: 11-5-11
 * Time: 下午6:00
 */


public class UserRepository extends SqlMapClientDaoSupport  {


    public void createUser(User user) {
        getSqlMapClientTemplate().insert("insertUser", user);
    }

    public User getUserById(Integer id) {
        return (User) getSqlMapClientTemplate().queryForObject("selectUser", id);
    }

    public void updateUser(User user) {
        getSqlMapClientTemplate().update("updateUser", user);
    }

    public void deleteUser(Integer id) {
        getSqlMapClientTemplate().update("deleteUser", id);
    }

    public List<User> queryAllUser() {
        return getSqlMapClientTemplate().queryForList("selectAllUsers");
    }

    public User getUserByUserName(String userName) {
        return (User) getSqlMapClientTemplate().queryForObject("selectUserByUserName", userName);
    }

    public UserGradeRule getUserGradeByGrade(String grade) {
        return (UserGradeRule) getSqlMapClientTemplate().queryForObject("selectUserGradeByGrade", grade);
    }

    public User getUserByEmail(String email) {
        return (User) getSqlMapClientTemplate().queryForObject("selectUserByEmail", email);
    }

    public User getUserByPhone(String phone) {
        return (User) getSqlMapClientTemplate().queryForObject("selectUserByPhone", phone);
    }

    public int selectCountForSameIPToday(String registerIP) {
        List<User> users = getSqlMapClientTemplate().queryForList("selectCountForSameIPToday", registerIP);

        if (null != users) {
            return users.size();
        }
        return 0;
    }

    public User getUserSession(String sessionId) {

        return (User) getSqlMapClientTemplate().queryForObject("selectUserByCookieValue", sessionId);
    }

    public void deleteUserSession(String userName) {
        getSqlMapClientTemplate().delete("deleteUserSession", userName);
    }

    public void createUserSessionIfo(UserSessionInfo info) {
        getSqlMapClientTemplate().insert("insertUserSession", info);
    }

    public Page<User> queryUserByPage(UserQuery userQuery) {
        Map param = new HashMap();
        param.put("start", userQuery.getStart());
        param.put("limit", userQuery.getLimit());
        param.put("userName", userQuery.getUserName());
        param.put("email", userQuery.getEmail());
        param.put("phone", userQuery.getPhone());
        param.put("sortMode", userQuery.getSortMode());
        param.put("type", userQuery.getType());
        param.put("userId", NumberUtils.toInt(userQuery.getUserId()));

        if (StringUtils.isNotEmpty(userQuery.getStartDate())) {
            Date start_date = DateUtils.parseDate((userQuery.getStartDate()).replace("T", " "), DateUtils.DateFormatType.DATE_FORMAT_STR);
            param.put("startDate", start_date);
        }

        if (StringUtils.isNotEmpty(userQuery.getEndDate())) {
            Date end_date = DateUtils.parseDate((userQuery.getEndDate()).replace("T", " "), DateUtils.DateFormatType.DATE_FORMAT_STR);
            param.put("endDate", end_date);
        }

        List<User> queryUserByPage = getSqlMapClientTemplate().queryForList("queryUserByPage", param);
        Page<User> page = Page.createFromStart(userQuery.getStart(), userQuery.getLimit());
        page.setResult(queryUserByPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountForUser", param));
        return page;
    }

    
    public void updateUserForbiddenStatus(int id, boolean hasForbidden) {
        Map param = new HashMap();
        param.put("id", id);
        param.put("hasForbidden", hasForbidden);
        getSqlMapClientTemplate().update("updateUserForbiddenStatus", param);
    }

    
    public void updateUserIsActive(int id, boolean isActive) {
        Map param = new HashMap();
        param.put("id", id);
        param.put("isActive", isActive);
        getSqlMapClientTemplate().update("updateUserIsActive", param);
    }

    
    public UserOuter getUserOuterByOuterIdAndType(String outerId, AccountType accountType) {
        Map param = new HashMap();
        param.put("outerId", outerId);
        param.put("accountType", accountType);
        return (UserOuter) getSqlMapClientTemplate().queryForObject("queryUserOuterByOuterIdAndType", param);
    }

    
    public void createUserOuter(UserOuter userOuter) {
        getSqlMapClientTemplate().insert("createUserOuter", userOuter);
    }

    
    public void updateUserLoginInfo(int id) {
        getSqlMapClientTemplate().update("updateUserLoginInfo", id);
    }

    
    public void createUserData(UserData userData) {
        getSqlMapClientTemplate().insert("createUserData", userData);
    }

    
    public void updateUserData(UserData userData) {
        getSqlMapClientTemplate().update("updateUserData", userData);
    }

    
    public UserData queryUserDataByUserId(int userId) {
        return (UserData) getSqlMapClientTemplate().queryForObject("getUserDataByUserId", userId);
    }

    
    public void updateUserGrade(int userId, UserGrade grade) {
        Map map = new HashMap();
        map.put("userId", userId);
        map.put("grade", grade);
        getSqlMapClientTemplate().update("updateUserGrade", map);
    }

    
    public void createUserGradeRule(UserGradeRule rule) {
        getSqlMapClientTemplate().insert("insertUserGradeRule", rule);
    }

    
    public void updateUserGradeRule(UserGradeRule rule) {
        getSqlMapClientTemplate().update("updateUserGradeRule", rule);
    }

    
    public UserGradeRule getGradeRule(UserGrade grade) {
        return (UserGradeRule) getSqlMapClientTemplate().queryForObject("getGradeRule", grade);
    }

    
    public void addUserGradeHistory(UserGradeHistory history) {
        getSqlMapClientTemplate().insert("insertUserGradeHistory", history);
    }

    
    public List<UserGradeHistory> getUserGradeHistory(int userId) {
        return getSqlMapClientTemplate().queryForList("getUserGradeHistory", userId);
    }

    
    public void increaseUserTotalExpense(int userId, long money) {
        Map map = new HashMap();
        map.put("userId", userId);
        map.put("money", money);
        getSqlMapClientTemplate().update("increaseUserTotalExpense", map);
    }

    
    public void increaseUserPoint(int userId, long point) {
        Map map = new HashMap();
        map.put("userId", userId);
        map.put("point", point);
        getSqlMapClientTemplate().update("increaseUserPoint", map);
    }

    
    public void decreaseUserPoint(int userId, long point) {
        Map map = new HashMap();
        map.put("userId", userId);
        map.put("point", point);
        getSqlMapClientTemplate().update("decreaseUserPoint", map);
    }

    
    public List<UserGradeRule> queryAllGradeRule() {
        return getSqlMapClientTemplate().queryForList("queryAllGradeRule");
    }

    
    public UserGradeRule getOnceExpenseReachGradeRule(long money) {
        return (UserGradeRule) getSqlMapClientTemplate().queryForObject("getOnceExpenseReachGradeRule", money);
    }

    
    public UserGradeRule getTotalExpenseReachGradeRule(long expenseTotal) {
        return (UserGradeRule) getSqlMapClientTemplate().queryForObject("getTotalExpenseReachGradeRule", expenseTotal);
    }


    
    public void createUnSubscribe(UnSubscribe unSubscribe) {
        getSqlMapClientTemplate().insert("insertUnSubscribe", unSubscribe);
    }

    
    public void deleteUnSubscribe(String email) {
        getSqlMapClientTemplate().delete("deleteUnSubscribe", email);
    }

    
    public int queryUnSubscribeCountByEmail(String email) {
        return (Integer) getSqlMapClientTemplate().queryForObject("getUnSubscribeByEmail", email);
    }

    
    public List<UnSubscribe> queryAllUnSubscribe() {
        return getSqlMapClientTemplate().queryForList("getAllUnSubscribe");
    }


}
