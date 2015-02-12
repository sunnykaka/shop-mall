package com.kariqu.accountcenter.repository;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.accountcenter.domain.AccountQuery;
import com.kariqu.common.pagenavigator.Page;

import java.util.List;


public interface AccountRepository {

    void createAccount(Account account);

    /**
     * 给员工指定职位
     *
     * @param employeeId
     * @param positionId
     */
    void createEmployeeForPosition(int employeeId, int positionId);

    /**
     * 给员工指定部门
     *
     * @param employeeId
     * @param departmentId
     */
    void createEmployeeForDepartment(int employeeId, int departmentId);

    /**
     * 指定某个员工的上级
     *
     * @param employeeId
     * @param bossId
     */
    void createEmployeeForBoss(int employeeId, int bossId);

    /**
     * 解除某个人与其上级的关联
     *
     * @param employeeId
     */
    void deleteAllBossByEmployeeId(int employeeId);

    /**
     * 解除某个人与职位的关联
     *
     * @param employeeId
     */
    void deleteAllPositionByEmployeeId(int employeeId);

    /**
     * 解除某个人与部门的关联
     *
     * @param employeeId
     */
    void deleteAllDepartmentByEmployeeId(int employeeId);

    /**
     * 查询某个人的直接上级
     *
     * @param employeeId
     * @return
     */
    int queryBossIdByEmployeeId(int employeeId);

    /**
     * 查询某个人的职位
     *
     * @param employeeId
     * @return
     */
    List<Integer> queryPositionIdByEmployeeId(int employeeId);

    /**
     * 查询某个人的部门
     *
     * @param employeeId
     * @return
     */
    List<Integer> queryDepartmentIdByEmployeeId(int employeeId);

    void updateAccount(Account account);

    Account queryAccountById(int id);

    Account queryAccountByName(String userName);

    Account queryAccountByEmail(String email);

    Account queryAccountByEnglishName(String englishName);

    Page<Account> queryAccountByPage(Page<Account> page);

    Page<Account> queryAccountByPage(AccountQuery accountQuery);

    void thoroughDeleteAccountById(int id);

    void deleteAccountById(int id);

    void accountHasLeaceOffice(int id);

    List<Integer> queryBossEmployee(int bossId);

    List<Account> querySameDepartmentEmployee(int accountId);

    boolean checkAccountByLoginPsw(int accountId, String passWord);

    void updateAccountPassWord(int accountId, String passWord);

    boolean positionHasAccount(int positionId);

    boolean departmentHasAccount(int departmentId);
    //查询所有当前在职用户
    List<Account> queryAllAccount();

    boolean isAccountIsHr(int accountId);
}
