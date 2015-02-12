package com.kariqu.usercenter.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kariqu.common.lib.Money;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.UserPoint;
import com.kariqu.usercenter.domain.UserSignIn;
import com.kariqu.usercenter.repository.SignInRepository;
import com.kariqu.usercenter.repository.UserPointRepository;
import com.kariqu.usercenter.repository.UserRepository;
import com.kariqu.usercenter.service.UserPointQuery;
import com.kariqu.usercenter.service.UserPointService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: Asion
 * Date: 13-3-18
 * Time: 上午10:24
 */
public class UserPointServiceImpl implements UserPointService {

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SignInRepository signInRuleRepository;

    /**
     * 默认的一天签到送积分数
     */
    private static final int DEFAULT_SIGN_COUNT = 10;

    @Override
    @Transactional
    public void createUsePoint(UserPoint userPoint) {
        userPointRepository.createUsePoint(userPoint);
        if (userPoint.getType() == UserPoint.PointType.InComing) {
            userRepository.increaseUserPoint(userPoint.getUserId(), userPoint.getPoint());
        } else {
            userRepository.decreaseUserPoint(userPoint.getUserId(), userPoint.getPoint());
        }
    }

    @Override
    public Page<UserPoint> queryUserPoint(UserPointQuery query) {
        return userPointRepository.queryUserPoint(query);
    }


    Splitter ruleSplitter = Splitter.on(',').omitEmptyStrings().trimResults();

    /**
     * 返回用户今天签到的提示信息. 当 userId 当为null时, 表示未登陆的用户
     * <p/>
     * 包含的key有
     * sign               Boolean    true为今天已签到, false为今天未签到
     * canReceive         Double    可领取的积分数
     * signInCount        Integer    连续签到天数
     * activityDays       Integer    活动连续天数
     * activityTotalPoint Double    连续签到可领取的总积分数
     *
     * @param userId 当为null时, 表示未登陆的用户
     * @return
     */
    @Override
    public Map<String, Object> todaySignPointInfo(Integer userId, String signInRule) {
        List<Double> ruleList = Lists.transform(ruleSplitter.splitToList(signInRule), new Function<String, Double>() {
            @Override
            public Double apply(String input) {
                return NumberUtils.toDouble(input, 0);
            }
        });

        double sumPoint = 0;
        for (Double i : ruleList) {
            sumPoint += i;
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("activityDays", ruleList.size());
        result.put("activityTotalPoint", sumPoint);
        if (userId == null) { //用户没有登陆的情况
            result.put("sign", false);
            result.put("signInCount", 0);
            result.put("canReceive", ruleList.get(0));
        } else {
            UserSignIn userSignIn = queryUserSignInByUserId(userId);
            if (userSignIn != null) {//之前签到过
                Date today = new Date();
                int signInCount = 0;
                boolean todaySign = DateUtils.isSameDay(today, userSignIn.getUpdateDate());//今天是否签到过
                result.put("sign", todaySign);
                if (todaySign || DateUtils.isSameDay(DateUtils.addDays(today, -1), userSignIn.getUpdateDate())) {//今天,昨天签到过,则要取连续签到次数
                    signInCount = userSignIn.getSignInCount();
                }
                result.put("signInCount", signInCount);
                result.put("canReceive", signInCount >= ruleList.size() ? ruleList.get(ruleList.size() - 1) : ruleList.get(signInCount));
            } else { //从来都没有签到过
                result.put("sign", false);
                int signInCount = 0;
                result.put("signInCount", signInCount);
                result.put("canReceive", signInCount >= ruleList.size() ? ruleList.get(ruleList.size() - 1) : ruleList.get(signInCount));
            }
        }

        return result;
    }

    /**
     * 返回的key值有:
     * signInCount  签到次数            Long
     * point        本次签到得到的积分数    double
     * totalPoint   用户总的积分数         double
     * totalChangeMoney  积分可换成的元  double
     * tomorrowPoint 明天得到的积分      double
     * activityDays   活动连续天数    Integer
     * activityTotalPoint 连续签到可领取的总积分数  double
     */
    @Override
    @Transactional
    public Map<String, Number> signIn(int userId, String signInRule, double rate) {
        Map<String, Number> result = new HashMap<String, Number>();
        UserSignIn userSignIn = queryUserSignInByUserId(userId);

        List<Double> ruleList = Lists.transform(ruleSplitter.splitToList(signInRule), new Function<String, Double>() {
            @Override
            public Double apply(String input) {
                return NumberUtils.toDouble(input, 0);
            }
        });

        double sumPoint = 0;
        for (Double i : ruleList) {
            sumPoint += i;
        }
        result.put("activityDays", ruleList.size());
        result.put("activityTotalPoint", sumPoint);

        String[] ruleArr = ruleSplitter.splitToList(signInRule).toArray(new String[]{});
        String point = ruleArr[0];
        int signInCount = 1;
        if (userSignIn == null) {
            insertUserSignIn(new UserSignIn(userId));
        } else {
            Calendar calendar = Calendar.getInstance();
            if (DateUtils.isSameDay(userSignIn.getUpdateDate(), calendar.getTime()))
                throw new RuntimeException("你今天已经签到过啦~ 亲.");

            // 如果昨天签到过, 签到次数增加, 若昨天未签到过, 则次数回归成 1
            calendar.add(Calendar.DAY_OF_YEAR, -1);

            if (DateUtils.isSameDay(userSignIn.getUpdateDate(), calendar.getTime())) {
                int count = userSignIn.getSignInCount();
                // 如果已经到达了规则里面最大的天数, 则以最大那天的规则加积分
                point = (count >= ruleArr.length) ? ruleArr[ruleArr.length - 1] : ruleArr[count];
                // 累加 连续签到天数
                signInCount = count + 1;
            }
            userSignIn.setSignInCount(signInCount);
            if (updateUserSignIn(userSignIn) != 1)
                throw new RuntimeException("您今天已经签到过啦~ 亲.");
        }

        // 加积分
        UserPoint userPoint = new UserPoint();
        userPoint.setUserId(userId);
        userPoint.setPoint(new Money(point.trim()).getCent());
        userPoint.setType(UserPoint.PointType.InComing);
        userPoint.setInOutComingType(UserPoint.InOutComingType.SignIn);
        userPoint.setDescription("签到得积分");
        createUsePoint(userPoint);


        result.put("signInCount", new Long(signInCount));
        result.put("tomorrowPoint", signInCount + 1 >= ruleList.size() ? ruleList.get(ruleList.size() - 1) : ruleList.get(signInCount + 1));
        result.put("point", userPoint.getPoint());
        Double pointTotal = NumberUtils.createDouble(userRepository.getUserById(userId).getCurrency());
        result.put("totalPoint", pointTotal);
        result.put("totalChangeMoney", (pointTotal * 1.0) / rate);
        return result;
    }


    @Override
    public Page<UserSignIn> queryAllUserSignIn(Page page) {
        return signInRuleRepository.queryAllUserSignIn(page);
    }

    @Override
    public UserSignIn queryUserSignInByUserId(int userId) {
        return signInRuleRepository.queryUserSignInByUserId(userId);
    }

    @Override
    public void insertUserSignIn(UserSignIn userSignIn) {
        signInRuleRepository.insertUserSignIn(userSignIn);
    }

    @Override
    public int updateUserSignIn(UserSignIn userSignIn) {
        return signInRuleRepository.updateUserSignIn(userSignIn);
    }

    @Override
    public void deleteUserSignIn(int id) {
        signInRuleRepository.deleteUserSignIn(id);
    }

    @Override
    public void addUserCurrency(int id, long currency, String description) {
        // 加积分
        UserPoint userPoint = new UserPoint();
        userPoint.setUserId(id);
        userPoint.setPoint(currency);
        userPoint.setType(UserPoint.PointType.InComing);
        userPoint.setInOutComingType(UserPoint.InOutComingType.Register);
        userPoint.setDescription(description);
        createUsePoint(userPoint);
    }

}
