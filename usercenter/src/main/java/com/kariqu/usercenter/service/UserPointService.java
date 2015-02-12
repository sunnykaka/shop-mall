package com.kariqu.usercenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.UserPoint;
import com.kariqu.usercenter.domain.UserSignIn;

import java.util.Map;

/**
 * 用户积分服务
 * User: Asion
 * Date: 13-3-18
 * Time: 上午10:20
 */
public interface UserPointService {

    void createUsePoint(UserPoint userPoint);

    Page<UserPoint> queryUserPoint(UserPointQuery query);


    Map<String, Object> todaySignPointInfo(Integer userId, String signInRule);

    /**
     * 用户签到
     *
     * @param userId 用户Id
     * @param signInRule 签到规则
     * @param rate 换算值
     * @return 签到成功后用户获得的积分
     */
    Map<String, Number> signIn(int userId, String signInRule, double rate);



    /** 查询所有的用户签到记录
     * @param page*/
    Page<UserSignIn> queryAllUserSignIn(Page page);

    /** 查询单个用户的签到记录(签到天数和上次签到时间等) */
    UserSignIn queryUserSignInByUserId(int userId);

    /** 新增用户签到 */
    void insertUserSignIn(UserSignIn userSignIn);

    /** 更新用户签到
     *
     * @param userSignIn
     */
    int updateUserSignIn(UserSignIn userSignIn);

    /** 删除用户签到 */
    void deleteUserSignIn(int id);

    /**
     * 赠送用户积分
     *
     * @param id
     * @param currency
     */
    void addUserCurrency(int id, long currency,String description);
}
