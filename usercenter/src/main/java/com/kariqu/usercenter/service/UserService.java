package com.kariqu.usercenter.service;

import com.kariqu.common.oauth.jointlogin.OuterUserInfo;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.*;

import java.util.List;

/**
 * 用户服务
 *
 * @author Tiger
 * @version 1.0.0
 * @since 2011-5-6 下午09:15:46
 */
public interface UserService {

    /**
     * 登录成功后的操作.<br/>
     * 1. 更新用户登录次数及时间,<br/>
     * 2. 写入统计登录记录.
     */
    void loginOn(StatisticsEntry entry);

    User getUserByUserName(String userName);

    /**
     * 根据等级查询对应的中文等级name
     * @param grade
     * @return
     */
    UserGradeRule getUserGradeByGrade(String grade);

    /**
     * 获取用户信息
     *
     * @param userInfo 用户名, 或邮箱, 或电话号码
     * @return 用户信息
     */
    User getUserByNameOrEmailOrPhone(String userInfo);

    int createUser(User user);

    void updateUser(User user);

    void deleteUser(int userId);

    User getUserById(int userId);

    User getUserByEmail(String email);

    User getUserByPhone(String phone);

    public int selectCountForSameIPToday(String registerIP);

    public User getUserSession(String sessionId);

    public void deleteUserSession(String userName);

    /**
     * 创建usersession的时候会清楚掉用户之前的记录，保证只有一条
     *
     * @param info
     */
    public void createUserSessionInfo(UserSessionInfo info);

    Page<User> queryUserByPage(UserQuery userQuery);

    void updateUserForbiddenStatus(int id, boolean hasForbidden);

    void updateUserIsActive(int id, boolean isActive);

    UserOuter getUserOuterByOuterIdAndType(String outerId, AccountType accountType);

    void createUserOuter(User user, UserOuter userOuter);

    void updateUserLoginInfo(int id);

    /**
     * 添加用户基本资料
     * 这里用户注册时就自动创建了
     *
     * @param userData
     */
    void createUserData(UserData userData);

    /**
     * 第三方登录成功之后. 返回用户信息(若是第一次登录则新建用户)
     *
     * @param outerUserInfo 第三方返回的账户信息
     * @param ip            用户登录时的 IP
     * @return
     */
    User doLoginWhenOauthLater(OuterUserInfo outerUserInfo, String ip);

    void updateUserData(UserData userData);

    UserData queryUserDataByUserId(int userId);


    /**
     * 增加用户总消费
     * <p/>
     * 这个时候会进行会进行会员等级升级
     * <p/>
     * 先进行一次性消费判断升级，再进行累计消费金额升级
     * <p/>
     * 升级判断依赖数据库的等级规则倒序排列，取出能够到达的最远等级
     *
     * @param userId
     * @param money
     */
    void increaseUserTotalExpense(int userId, long money);

    /**
     * 改变等级规则
     *
     * @param rule
     */
    void changeGradeRule(UserGradeRule rule);

    /**
     * 获取用户的等级信息.
     *
     * @param userId 用户Id
     * @return
     */
    UserGradeRule getGradeRule(int userId);

    /**
     * 得到等级规则
     *
     * @param grade
     * @return
     */
    UserGradeRule getGradeRule(UserGrade grade);


    /**
     * 查询用户等级成长历史
     *
     * @param userId
     * @return
     */
    List<UserGradeHistory> getUserGradeHistory(int userId);


    /**
     * 所有的等级规则
     *
     * @return
     */
    List<UserGradeRule> queryAllGradeRule();


    /**
     * 添加 取消订阅的邮箱
     *
     * @param email
     */
    void addUnSubscribe(String email);

    /**
     * 删除取消订阅的邮箱
     *
     * @param email
     */
    void deleteUnSubscribe(String email);

    /**
     * 查询单个取消订阅的邮箱数量
     *
     * @param email
     * @return
     */
    int queryUnSubscribe(String email);

    /**
     * 查询所有的邮箱
     *
     * @return
     */
    List<UnSubscribe> queryAllUnSubscribe();

}
