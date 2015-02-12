package com.kariqu.usercenter.repository.impl.ibatis;

import com.kariqu.common.DateUtils;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.*;
import com.kariqu.usercenter.repository.UserRepository;
import com.kariqu.usercenter.service.UserQuery;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static junit.framework.Assert.*;

/**
 * User: Asion
 * Date: 11-7-29
 * Time: 上午11:27
 */
@SpringApplicationContext({"classpath:userCenter.xml"})
public class UserRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("userRepository")
    private UserRepository userRepository;

    @Test
    public void testUserRepository() {
        User user = new User();
        user.setEmail("aasge@hp.com");
        user.setPhone("13012345678");
        user.setPassword("passowrd");
        user.setUserName("oojdon");
        user.setRegisterDate(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR));
        user.setRegisterIP("127.0.0.1");
        user.setAccountType(AccountType.Anonymous);
        userRepository.createUser(user);
        UserOuter userOuter = new UserOuter();
        userOuter.setAccountType(AccountType.QQ);
        userOuter.setOuterId("asdfhasdhfasdflaskdjflkdsf");
        userOuter.setLocked(false);
        userOuter.setUserId(user.getId());
        userRepository.createUserOuter(userOuter);
        userOuter = userRepository.getUserOuterByOuterIdAndType("asdfhasdhfasdflaskdjflkdsf", AccountType.QQ);
        assertEquals(user.getId(), userOuter.getUserId());
        assertEquals("oojdon", userRepository.getUserByEmail("aasge@hp.com").getUserName());
        assertEquals("oojdon", userRepository.getUserByPhone("13012345678").getUserName());

        assertEquals(1, userRepository.queryAllUser().size());
        user.setEmail("xxx@hp.com");
        user.setPassword("ppppppppppp");
        user.setUserName("xxxx");
        user.setActive(true);
        user.setAccountType(AccountType.KRQ);
        userRepository.updateUser(user);
        userRepository.updateUserForbiddenStatus(user.getId(), true);
        assertEquals(true, userRepository.getUserById(user.getId()).isHasForbidden());

        int i = 0;
        while (i < 10) {
            User user2 = new User();
            user2.setEmail("" + i);
            user2.setPassword("" + i);
            user2.setUserName("" + i);
            user2.setRegisterDate(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR));
            user2.setRegisterIP("127.0.0.1");
            user2.setAccountType(AccountType.Anonymous);
            userRepository.createUser(user2);
            System.out.println(i + "：" + user2.getAccountType().toDesc());
            i++;
        }
        UserQuery userQuery = new UserQuery();
        userQuery.setStart(2);
        userQuery.setLimit(2);
        Page<User> page = userRepository.queryUserByPage(userQuery);
        assertEquals("1", page.getResult().get(0).getUserName());
        assertEquals(11, page.getTotalCount());
        assertEquals(6, page.getTotalPages());

        /**
         *  用户基本资料
         */
        UserData userData = new UserData();
        userData.setUserId(1);
        userRepository.createUserData(userData);

        userData.setName("Alec");
        userData.setBirthday("2013年1月18日");
        userData.setFamilyNumber(0);
        userData.setSex(0);
        userData.setHasMarried(0);
        userData.setPhoneNumber("13434433434");
        userRepository.updateUserData(userData);

        assertEquals("Alec", userRepository.queryUserDataByUserId(1).getName());

        userRepository.updateUserGrade(1, UserGrade.D);
        assertEquals(UserGrade.D, userRepository.getUserById(1).getGrade());

        UserGradeRule rule = new UserGradeRule();
        rule.setGrade(UserGrade.D);
        rule.setName("金饭碗");
        rule.setOnceExpense(1);
        rule.setTotalExpense(1);
        rule.setValuationRatio(1.2);
        userRepository.createUserGradeRule(rule);

        assertNotNull(userRepository.getOnceExpenseReachGradeRule(1));
        assertNotNull(userRepository.getTotalExpenseReachGradeRule(1));

        assertNull(userRepository.getOnceExpenseReachGradeRule(0));
        assertNull(userRepository.getTotalExpenseReachGradeRule(0));

        assertEquals("金饭碗", userRepository.getGradeRule(UserGrade.D).getName());
        assertEquals(1, userRepository.queryAllGradeRule().size());

        UserGradeHistory history = new UserGradeHistory();
        history.setGrade(UserGrade.D);
        history.setUserId(2);
        userRepository.addUserGradeHistory(history);
        assertEquals(1, userRepository.getUserGradeHistory(2).size());

        userRepository.increaseUserTotalExpense(1, 1);
        assertEquals(1, userRepository.getUserById(1).getExpenseTotal());

        userRepository.increaseUserPoint(1, 1);
        assertEquals(1, userRepository.getUserById(1).getPointTotal());

        userRepository.decreaseUserPoint(1, 1);
        assertEquals(0, userRepository.getUserById(1).getPointTotal());

    }

    @Test
    public void testUnSubscribe() {
        String email = "a@b.c";
        userRepository.createUnSubscribe(new UnSubscribe(email));
        assertEquals(1, userRepository.queryAllUnSubscribe().size());

        userRepository.deleteUnSubscribe(email);
        assertEquals(0, userRepository.queryAllUnSubscribe().size());
    }

    @Test
    public void testUserSession() {
        User user = new User();
        user.setEmail("aasge@hp.com");
        user.setPhone("13012345678");
        user.setPassword("passowrd");
        user.setUserName("oojdon");
        user.setRegisterDate(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR));
        user.setRegisterIP("127.0.0.1");
        user.setAccountType(AccountType.Anonymous);
        userRepository.createUser(user);

        UserSessionInfo userSessionInfo = new UserSessionInfo();
        userSessionInfo.setUserId(String.valueOf(user.getId()));
        userSessionInfo.setCheckInDate(DateUtils.getCurrentDateStr(DateUtils.DateFormatType.DATE_FORMAT_STR));
        userSessionInfo.setCookieValue("dgegege");
        userRepository.createUserSessionIfo(userSessionInfo);

        user = userRepository.getUserSession(userSessionInfo.getCookieValue());
        assertEquals("aasge@hp.com", user.getEmail());
        userRepository.deleteUserSession("1");

    }


    @Test
    public void testUserRepositoryOfAddUser() {
        ExecutorService exec = Executors.newCachedThreadPool();
        //模拟100个线程同时访问
        final Semaphore semp = new Semaphore(100);
        // 模拟1000个客户端访问
        for (int index = 0; index < 1000; index++) {
            final int NO = index;
            Runnable run = new Runnable() {
                public void run() {
                    try {
                        semp.acquire();
                        System.out.println("Thread:" + NO);
                        addUser(NO);
                        semp.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            exec.execute(run);
        }
        exec.shutdown();

    }

    private void addUser(final int i) {
        final User user = new User();
        user.setEmail("aasge@hp.com");
        user.setPhone("13012345678");
        user.setPassword("passowrd");
        user.setUserName("oojdon");
        user.setRegisterDate(DateUtils.formatDate(new Date(), DateUtils.DateFormatType.DATE_FORMAT_STR));
        user.setRegisterIP("127.0.0.1");
        user.setAccountType(AccountType.Anonymous);
        userRepository.createUser(user);

        System.out.println("Thread:" + i + "插入成功");

    }


}
