package com.kariqu.usercenter.service.impl;

import com.kariqu.common.oauth.jointlogin.OuterUserInfo;
import com.kariqu.common.CheckUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.*;
import com.kariqu.usercenter.repository.UserRepository;
import com.kariqu.usercenter.service.StatisticsEntryService;
import com.kariqu.usercenter.service.UserQuery;
import com.kariqu.usercenter.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: Asion
 * Date: 11-5-11
 * Time: 下午6:08
 */

public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatisticsEntryService statisticsEntryService;

    @Override
    @Transactional
    public void loginOn(StatisticsEntry entry) {
        statisticsEntryService.createEntry(entry);
    }

    @Override
    public User getUserByUserName(String userName) {
        return userRepository.getUserByUserName(userName);
    }

    @Override
    public UserGradeRule getUserGradeByGrade(String grade) {
        return userRepository.getUserGradeByGrade(grade);
    }

    @Override
    public User getUserByNameOrEmailOrPhone(String userInfo) {
        if (StringUtils.isBlank(userInfo)) return null;

        User user = getUserByUserName(userInfo);
        if (user == null && CheckUtils.checkPhone(userInfo))
            user = getUserByPhone(userInfo);
        if (user == null && CheckUtils.checkEmail(userInfo))
            user = getUserByEmail(userInfo);
        return user;
    }

    @Override
    @Transactional
    public int createUser(User user) {
        // 若用户名是电话号码或是 email, 则分别写进去
        if (CheckUtils.checkPhone(user.getUserName()))
            user.setPhone(user.getUserName());
        else if (CheckUtils.checkEmail(user.getUserName()))
            user.setEmail(user.getUserName());

        userRepository.createUser(user);
        int userId = user.getId();
        userRepository.createUserData(new UserData(userId));
        UserGradeHistory history = new UserGradeHistory();
        history.setGrade(UserGrade.A);
        history.setUserId(userId);
        userRepository.addUserGradeHistory(history);
        return userId;
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        userRepository.updateUser(user);
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public User getUserById(int userId) {
        return userRepository.getUserById(userId);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public User getUserByPhone(String phone) {
        return userRepository.getUserByPhone(phone);
    }

    @Override
    public int selectCountForSameIPToday(String registerIP) {
        return userRepository.selectCountForSameIPToday(registerIP);
    }

    @Override
    public User getUserSession(String sessionId) {
        return userRepository.getUserSession(sessionId);
    }

    @Override
    @Transactional
    public void deleteUserSession(String userName) {
        userRepository.deleteUserSession(userName);
    }

    @Override
    @Transactional
    public void createUserSessionInfo(UserSessionInfo info) {
        userRepository.deleteUserSession(info.getUserId());
        userRepository.createUserSessionIfo(info);
    }

    @Override
    public Page<User> queryUserByPage(UserQuery userQuery) {
        return userRepository.queryUserByPage(userQuery);
    }

    @Override
    public void updateUserForbiddenStatus(int id, boolean hasForbidden) {
        userRepository.updateUserForbiddenStatus(id, hasForbidden);
    }

    @Override
    @Transactional
    public void updateUserIsActive(int id, boolean isActive) {
        userRepository.updateUserIsActive(id, isActive);
    }

    @Override
    public UserOuter getUserOuterByOuterIdAndType(String outerId, AccountType accountType) {
        return userRepository.getUserOuterByOuterIdAndType(outerId, accountType);
    }

    @Override
    @Transactional
    public void createUserOuter(User user, UserOuter userOuter) {
        userRepository.createUser(user);
        userOuter.setUserId(user.getId());
        userRepository.createUserOuter(userOuter);
    }

    @Override
    @Transactional
    public void updateUserLoginInfo(int id) {
        userRepository.updateUserLoginInfo(id);
    }

    @Override
    public void createUserData(UserData userData) {
        userRepository.createUserData(userData);
    }

    @Override
    public User doLoginWhenOauthLater(OuterUserInfo outerUserInfo, String ip) {
        AccountType accountType = AccountType.valueOf(outerUserInfo.getFlag());
        String outerId = outerUserInfo.getOuterId();
        String userName = outerUserInfo.getUserName();
        UserOuter userOuter = getUserOuterByOuterIdAndType(outerId, accountType);
        // 判断此账户是否以前登录过
        if (userOuter == null) {
            // 如果没有登录过, 则创建用户信息
            User user = new User();
            user.setAccountType(accountType);

            user.setUserName(accountType + "_" + outerId);
            user.setPassword(accountType + "_" + outerId);
            user.setRegisterIP(ip);

            //存储第三方账户
            userOuter = new UserOuter();
            userOuter.setOuterId(outerId);
            userOuter.setAccountType(accountType);

            createUserOuter(user, userOuter);

            // 第三方登录的用户 userName 与 userId 保持一致，以此来判断是否要设置用户信息
            user.setUserName(String.valueOf(user.getId()));
            updateUser(user);
        } else {
            int userId = userOuter.getUserId();
            //ToDo 兼容以前第三方账户
            UserData userData = queryUserDataByUserId(userId);
            if (userData == null) {
                createUserData(new UserData(userId));
            }
        }

        User user = new User();
        user.setId(userOuter.getUserId());
        user.setAccountType(accountType);
        user.setUserName(userName);
        return user;
    }

    @Override
    public void updateUserData(UserData userData) {
        userRepository.updateUserData(userData);
    }

    @Override
    public UserData queryUserDataByUserId(int userId) {
        return userRepository.queryUserDataByUserId(userId);
    }

    @Override
    @Transactional
    public void increaseUserTotalExpense(int userId, long money) {

        //增加累计金额
        userRepository.increaseUserTotalExpense(userId, money);

        User user = getUserById(userId);

        UserGrade nextGrade = user.getGrade().next();
        //如果已经最后一级直接返回，其他级别则开始做升级判断
        if (nextGrade == null)
            return;

        UserGradeRule rule = null;

        rule = userRepository.getTotalExpenseReachGradeRule(user.getExpenseTotal());

        //当找到了规则且在当前等级之后
        if (rule != null && user.getGrade().lessThan(rule.getGrade())) {
            changeUserGrade(userId, rule.getGrade());
        }

    }

    private void changeUserGrade(int userId, UserGrade grade) {
        userRepository.updateUserGrade(userId, grade);
        UserGradeHistory history = new UserGradeHistory();
        history.setGrade(grade);
        history.setUserId(userId);
        userRepository.addUserGradeHistory(history);
    }

    @Override
    @Transactional
    public void changeGradeRule(UserGradeRule rule) {
        if (getGradeRule(rule.getGrade()) == null) {
            userRepository.createUserGradeRule(rule);
        } else {
            userRepository.updateUserGradeRule(rule);
        }
    }

    @Override
    public UserGradeRule getGradeRule(int userId) {
        return getGradeRule(getUserById(userId).getGrade());
    }

    @Override
    public UserGradeRule getGradeRule(UserGrade grade) {
        return userRepository.getGradeRule(grade);
    }


    @Override
    public List<UserGradeHistory> getUserGradeHistory(int userId) {
        return userRepository.getUserGradeHistory(userId);
    }

    @Override
    public List<UserGradeRule> queryAllGradeRule() {
        return userRepository.queryAllGradeRule();
    }

    @Override
    @Transactional
    public void addUnSubscribe(String email) {
        userRepository.createUnSubscribe(new UnSubscribe(email));
    }

    @Override
    @Transactional
    public void deleteUnSubscribe(String email) {
        userRepository.deleteUnSubscribe(email);
    }

    @Override
    public int queryUnSubscribe(String email) {
        return userRepository.queryUnSubscribeCountByEmail(email);
    }

    @Override
    public List<UnSubscribe> queryAllUnSubscribe() {
        return userRepository.queryAllUnSubscribe();
    }
}
