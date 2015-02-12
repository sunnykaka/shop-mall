package com.kariqu.accountcenter.repository.impl;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.accountcenter.domain.AccountQuery;
import com.kariqu.accountcenter.repository.AccountRepository;
import com.kariqu.common.pagenavigator.Page;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import static junit.framework.Assert.*;

@ContextConfiguration(locations = {"/accountCenter.xml"})
public class AccountRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @Rollback(false)
    public void testAccountRepository() {
        Account account = new Account();
        account.setUserName("test");
        account.setEnglishName("english");
        account.setEmail("test@yijushang.com");
        account.setPassword("5666666");
        accountRepository.createAccount(account);
        assertEquals("test", accountRepository.queryAccountByName("test").getUserName());
        assertEquals("test@yijushang.com", accountRepository.queryAccountByEmail("test@yijushang.com").getEmail());
        assertEquals("english", accountRepository.queryAccountByEnglishName("english").getEnglishName());
        accountRepository.createEmployeeForBoss(account.getId(), 1000);
        assertEquals(1, accountRepository.queryBossEmployee(1000).size());
        accountRepository.createEmployeeForPosition(account.getId(), 29);
        accountRepository.createEmployeeForDepartment(account.getId(), 30);
        assertEquals(new Integer(30), accountRepository.queryDepartmentIdByEmployeeId(account.getId()).get(0));
        assertEquals(new Integer(29), accountRepository.queryPositionIdByEmployeeId(account.getId()).get(0));
        assertEquals(0, accountRepository.queryDepartmentIdByEmployeeId(3652).size());
        assertEquals(1000, accountRepository.queryBossIdByEmployeeId(account.getId()));
        assertEquals(0, accountRepository.queryBossIdByEmployeeId(999999));
        accountRepository.deleteAllBossByEmployeeId(account.getId());
        assertEquals(0, accountRepository.queryBossIdByEmployeeId(account.getId()));
        accountRepository.deleteAllPositionByEmployeeId(account.getId());
        accountRepository.deleteAllDepartmentByEmployeeId(account.getId());
        assertEquals(0, accountRepository.queryPositionIdByEmployeeId(account.getId()).size());
        assertEquals(0, accountRepository.queryDepartmentIdByEmployeeId(account.getId()).size());
        account.setEmail("xxx@yijushang.com");
        accountRepository.updateAccount(account);
        assertEquals("test", accountRepository.queryAccountById(account.getId()).getUserName());
        assertEquals("xxx@yijushang.com", accountRepository.queryAccountById(account.getId()).getEmail());
        //离职
        accountRepository.accountHasLeaceOffice(account.getId());
        assertNotNull(accountRepository.queryAccountById(account.getId()));
         //假删除
        accountRepository.deleteAccountById(account.getId());
        assertNotNull(accountRepository.queryAccountById(account.getId()));
        //彻底删除
        accountRepository.thoroughDeleteAccountById(account.getId());
        assertNull(accountRepository.queryAccountById(account.getId()));
        assertEquals(true,accountRepository.checkAccountByLoginPsw(1,"f379eaf3c831b04de153469d1bec345e"));

        Account account2 = new Account();
        account2.setUserName("test2");
        account2.setEnglishName("english2");
        account2.setEmail("test@yijushang.com2");
        account2.setPassword("56666662");
        account2.setLeaveOffice(true);
        account2.setDeleteData(false);

        accountRepository.createAccount(account2);
        accountRepository.createEmployeeForDepartment(account2.getId(),2);
        accountRepository.createEmployeeForPosition(account2.getId(),2);
        assertEquals(false,accountRepository.isAccountIsHr(2));
        AccountQuery accountQuery=new AccountQuery();
        accountQuery.setLeaveOffice("1");
        Page<Account> page=accountRepository.queryAccountByPage(accountQuery);

        assertEquals("test2",page.getResult().get(0).getUserName());
        accountRepository.deleteAccountById(account2.getId());
        accountRepository.deleteAllDepartmentByEmployeeId(account2.getId());
        accountRepository.deleteAllPositionByEmployeeId(account2.getId());
    }

}
