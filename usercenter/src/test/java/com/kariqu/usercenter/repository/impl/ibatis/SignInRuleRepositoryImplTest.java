package com.kariqu.usercenter.repository.impl.ibatis;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.UserSignIn;
import com.kariqu.usercenter.repository.SignInRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import static junit.framework.Assert.*;

@SpringApplicationContext({"classpath:userCenter.xml"})
public class SignInRuleRepositoryImplTest extends UnitilsJUnit4 {

    @SpringBean("signInRepository")
    private SignInRepository signInRuleRepository;

    @Test
    public void testSignInRuleRepository() {
        int userId = 2;
        UserSignIn userSignIn = new UserSignIn(userId);
        signInRuleRepository.insertUserSignIn(userSignIn);
        assertEquals(1, signInRuleRepository.queryAllUserSignIn(new Page()).getResult().size());

        userSignIn = signInRuleRepository.queryUserSignInByUserId(userId);
        userSignIn.setSignInCount(userSignIn.getSignInCount() + 1);
        signInRuleRepository.updateUserSignIn(userSignIn);
        assertEquals(2, signInRuleRepository.queryUserSignInByUserId(userId).getSignInCount());
    }
}
