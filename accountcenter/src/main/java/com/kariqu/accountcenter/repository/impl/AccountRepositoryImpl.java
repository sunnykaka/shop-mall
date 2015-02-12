package com.kariqu.accountcenter.repository.impl;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.accountcenter.domain.AccountQuery;
import com.kariqu.accountcenter.repository.AccountRepository;
import com.kariqu.common.pagenavigator.Page;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AccountRepositoryImpl extends SqlMapClientDaoSupport implements AccountRepository {

    @Override
    public void createAccount(Account account) {
        getSqlMapClientTemplate().insert("insertAccount", account);
    }

    @Override
    public void updateAccount(Account account) {
        getSqlMapClientTemplate().update("updateAccount", account);
    }

    @Override
    public void createEmployeeForBoss(int employeeId, int bossId) {
        Map map = new HashMap();
        map.put("employeeId", employeeId);
        map.put("bossId", bossId);
        getSqlMapClientTemplate().insert("insertEmployeeBoss", map);
    }

    @Override
    public void createEmployeeForPosition(int employeeId, int positionId) {
        Map map = new HashMap();
        map.put("employeeId", employeeId);
        map.put("positionId", positionId);
        getSqlMapClientTemplate().insert("insertEmployeePosition", map);
    }

    @Override
    public void createEmployeeForDepartment(int employeeId, int departmentId) {
        Map map = new HashMap();
        map.put("employeeId", employeeId);
        map.put("departmentId", departmentId);
        getSqlMapClientTemplate().insert("insertEmployeeDepartment", map);
    }

    @Override
    public void deleteAllBossByEmployeeId(int employeeId) {
        getSqlMapClientTemplate().delete("deleteEmployeeBoss", employeeId);
    }

    @Override
    public void deleteAllPositionByEmployeeId(int employeeId) {
        getSqlMapClientTemplate().delete("deleteEmployeePosition", employeeId);
    }

    @Override
    public void deleteAllDepartmentByEmployeeId(int employeeId) {
        getSqlMapClientTemplate().delete("deleteEmployeeDepartment", employeeId);
    }

    @Override
    public int queryBossIdByEmployeeId(int employeeId) {
        Object id = getSqlMapClientTemplate().queryForObject("selectEmployeeBossId", employeeId);
        if (null == id) {
            return 0;
        }
        return (Integer) id;
    }

    @Override
    public List<Integer> queryPositionIdByEmployeeId(int employeeId) {
        return getSqlMapClientTemplate().queryForList("selectEmployeePositionId", employeeId);
    }

    @Override
    public List<Integer> queryDepartmentIdByEmployeeId(int employeeId) {
        return getSqlMapClientTemplate().queryForList("selectEmployeeDepartmentId", employeeId);
    }

    @Override
    public Page<Account> queryAccountByPage(Page<Account> page) {
        Map param = new HashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Account> queryAccountByPage = getSqlMapClientTemplate().queryForList("queryAccountByPage", param);
        page.setResult(queryAccountByPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountForAccount"));
        return page;
    }

    @Override
    public Account queryAccountByName(String userName) {
        return (Account) getSqlMapClientTemplate().queryForObject("queryAccountByName", userName);
    }

    @Override
    public Account queryAccountByEmail(String email) {
        return (Account) getSqlMapClientTemplate().queryForObject("queryAccountByEmail", email);
    }

    @Override
    public Account queryAccountByEnglishName(String englishName) {
        return (Account) getSqlMapClientTemplate().queryForObject("queryAccountByEnglishName", englishName);
    }

    @Override
    public Account queryAccountById(int id) {
        return (Account) getSqlMapClientTemplate().queryForObject("selectAccountById", id);
    }

    @Override
    @Transactional
    public void thoroughDeleteAccountById(int id) {
        //删除用户资料
        getSqlMapClientTemplate().delete("thoroughDeleteAccountById", id);
        //删除用户上级
        getSqlMapClientTemplate().delete("deleteEmployeeBoss", id);
        //删除用户职位
        getSqlMapClientTemplate().delete("deleteEmployeePosition", id);
        //删除用户部门
        getSqlMapClientTemplate().delete("deleteEmployeeDepartment", id);
    }

    @Override
    public void deleteAccountById(int id) {
        getSqlMapClientTemplate().update("deleteAccountById", id);
    }

    @Override
    public void accountHasLeaceOffice(int id) {
        getSqlMapClientTemplate().update("setAccountLeave", id);
    }

    @Override
    public List<Integer> queryBossEmployee(int bossId) {
        return getSqlMapClientTemplate().queryForList("selectBossEmployeeId", bossId);
    }

    @Override
    public List<Account> querySameDepartmentEmployee(int accountId) {
        return getSqlMapClientTemplate().queryForList("selectSameDepartmentEmployee", accountId);
    }

    @Override
    public boolean checkAccountByLoginPsw(int accountId, String passWord) {
        Map map = new HashMap();
        map.put("id", accountId);
        map.put("password", passWord);
        return getSqlMapClientTemplate().queryForObject("checkAccountByLoginPsw", map) != null;
    }

    @Override
    public void updateAccountPassWord(int accountId, String passWord) {
        Map map = new HashMap();
        map.put("id", accountId);
        map.put("password", passWord);
        getSqlMapClientTemplate().update("updateAccountPsw", map);
    }

    @Override
    public Page<Account> queryAccountByPage(AccountQuery accountQuery) {
        Page<Account> page = new Page<Account>();
        page.setPageNo(accountQuery.getPageNo());
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("queryAccountCount", accountQuery));
        accountQuery.setStart(page.getPageFirst());
        accountQuery.setLimit(page.getPageSize());
        page.setResult(getSqlMapClientTemplate().queryForList("queryAccount", accountQuery));
        return page;
    }

    @Override
    public boolean positionHasAccount(int positionId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("isPositionHasEmployee", positionId) > 0;
    }

    @Override
    public boolean departmentHasAccount(int departmentId) {
        return (Integer) getSqlMapClientTemplate().queryForObject("isDepartmentHasEmployee", departmentId) > 0;
    }

    @Override
    public List<Account> queryAllAccount() {
        return getSqlMapClientTemplate().queryForList("selectAllAccounts");
    }

    @Override
    public boolean isAccountIsHr(int accountId) {
        return getSqlMapClientTemplate().queryForObject("isAccountIsHr", accountId) != null;
    }
}
