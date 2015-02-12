package com.kariqu.accountcenter.service;

import com.kariqu.accountcenter.domain.Account;
import com.kariqu.accountcenter.domain.AccountQuery;
import com.kariqu.common.pagenavigator.Page;

import java.util.List;

/**
 * 内部员工账号系统对外部的接口服务
 */

public interface AccountService {

    /**
     * 指定某个员工的上级
     *
     * @param employeeId
     * @param bossId
     */
    void createEmployeeForBoss(int employeeId, int bossId);

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
     * 目前假设每个人只有一个上级
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

    void createAccount(Account account);

    void updateAccount(Account account);

    Account queryAccountById(int id);

    /**
     * 删除一个账户需要解除它和职位，上级，部门的关联
     * @param id
     */
    void thoroughDeleteAccountById(int id);

    void deleteAccountById(int id);

    void accountHasLeaceOffice(int id);

    Account queryAccountByName(String userName);

    Account queryAccountByEmail(String email);

    Account queryAccountByEnglishName(String englishName);

    /**
     * 分页查询
     *
     * @param page
     * @return
     */
    Page<Account> queryAccountByPage(Page<Account> page);

    Page<Account> queryAccountByPage(AccountQuery accountQuery);

    /**
     * 查询某个人的直接下级
     * @param bossId
     * @return
     */
    List<Account> queryBossEmployee(int bossId);

    /**
     * 查询同部门团队成员
     * @param accountId
     * @return
     */
    List<Account> querySameDepartmentEmployee(int accountId);

    List<Account> queryAllAccount();

    /**
     * 密码检测用户
     * @param accountId
     * @param passWord
     * @return
     */
    boolean checkAccountByLoginPsw(int accountId, String passWord);

    void updateAccountPassWord(int accountId, String passWord);

    boolean positionHasAccount(int positionId);

    boolean departmentHasAccount(int departmentId);
    boolean isAccountIsHr(int accountId);
}
