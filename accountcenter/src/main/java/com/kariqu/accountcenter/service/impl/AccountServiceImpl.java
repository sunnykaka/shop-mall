package com.kariqu.accountcenter.service.impl;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.accountcenter.domain.AccountQuery;
import com.kariqu.accountcenter.repository.AccountRepository;
import com.kariqu.accountcenter.service.AccountService;
import com.kariqu.common.pagenavigator.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

/**
 * User: Asion
 * Date: 12-3-11
 * Time: 上午11:23
 */
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public void createEmployeeForPosition(int employeeId, int positionId) {
        accountRepository.createEmployeeForPosition(employeeId, positionId);
    }

    @Override
    public void createEmployeeForBoss(int employeeId, int bossId) {
        accountRepository.createEmployeeForBoss(employeeId, bossId);
    }

    @Override
    public void createEmployeeForDepartment(int employeeId, int departmentId) {
        accountRepository.createEmployeeForDepartment(employeeId, departmentId);
    }

    @Override
    public List<Integer> queryPositionIdByEmployeeId(int employeeId) {
        return accountRepository.queryPositionIdByEmployeeId(employeeId);
    }

    @Override
    public List<Integer> queryDepartmentIdByEmployeeId(int employeeId) {
        return accountRepository.queryDepartmentIdByEmployeeId(employeeId);
    }

    @Override
    public int queryBossIdByEmployeeId(int employeeId) {
        return accountRepository.queryBossIdByEmployeeId(employeeId);
    }

    @Override
    public void createAccount(Account account) {
        accountRepository.createAccount(account);
    }

    @Override
    public void updateAccount(Account account) {
        accountRepository.updateAccount(account);
    }

    @Override
    public Account queryAccountById(int id) {
        return accountRepository.queryAccountById(id);
    }

    @Override
    public void deleteAccountById(int id) {
        accountRepository.deleteAccountById(id);
    }

    @Override
    public void accountHasLeaceOffice(int id) {
        accountRepository.accountHasLeaceOffice(id);
    }

    @Override
    public void thoroughDeleteAccountById(int id) {
        accountRepository.thoroughDeleteAccountById(id);
    }

    @Override
    public Page<Account> queryAccountByPage(Page<Account> page) {
        return accountRepository.queryAccountByPage(page);
    }

    @Override
    public Page<Account> queryAccountByPage(AccountQuery accountQuery) {
        return accountRepository.queryAccountByPage(accountQuery);
    }

    @Override
    public Account queryAccountByName(String userName) {
        return accountRepository.queryAccountByName(userName);
    }

    @Override
    public Account queryAccountByEmail(String email) {
        return accountRepository.queryAccountByEmail(email);
    }

    @Override
    public Account queryAccountByEnglishName(String englishName) {
        return accountRepository.queryAccountByEnglishName(englishName);
    }

    @Override
    public void deleteAllBossByEmployeeId(int employeeId) {
        accountRepository.deleteAllBossByEmployeeId(employeeId);
    }

    @Override
    public void deleteAllPositionByEmployeeId(int employeeId) {
        accountRepository.deleteAllPositionByEmployeeId(employeeId);
    }

    @Override
    public void deleteAllDepartmentByEmployeeId(int employeeId) {
        accountRepository.deleteAllDepartmentByEmployeeId(employeeId);
    }

    @Override
    public List<Account> queryBossEmployee(int bossId) {
        List<Integer> idList = accountRepository.queryBossEmployee(bossId);
        List<Account> accountList = new LinkedList<Account>();
        for (Integer id : idList) {
            accountList.add(queryAccountById(id));
        }
        return accountList;
    }

    @Override
    public List<Account> querySameDepartmentEmployee(int accountId) {
        return accountRepository.querySameDepartmentEmployee(accountId);
    }

    @Override
    public boolean checkAccountByLoginPsw(int accountId, String passWord) {
        return accountRepository.checkAccountByLoginPsw(accountId, passWord);
    }

    @Override
    public void updateAccountPassWord(int accountId, String passWord) {
        accountRepository.updateAccountPassWord(accountId, passWord);
    }

    @Override
    public boolean positionHasAccount(int positionId) {
        return accountRepository.positionHasAccount(positionId);
    }

    @Override
    public boolean departmentHasAccount(int departmentId) {
        return accountRepository.departmentHasAccount(departmentId);
    }

    @Override
    public List<Account> queryAllAccount() {
        return accountRepository.queryAllAccount();
    }

    @Override
    public boolean isAccountIsHr(int accountId) {
        return accountId == 1 || accountRepository.isAccountIsHr(accountId);
    }
}
