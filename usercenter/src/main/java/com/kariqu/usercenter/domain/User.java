package com.kariqu.usercenter.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * 网站的用户对象.
 *
 * 用户目前有三种: 普通注册用户, 第三方登录用户, 在其他平台购买后由客服手动生成的用户.
 * 用户信息中最主要的是 用户名、邮箱、手机号 和 密码, 用户名、邮箱、手机号 都有业务上的唯一性(db 中某些字段可为空, 因此并没有唯一约束)
 *
 * 1.用户普通注册时可以使用 <用户名/邮箱/手机号> 中的任意一种, 并且用户名是一定会保存的.
 * 此时, 若输入是邮箱, 则此信息写进邮箱, 手机号同理.
 *
 * 2.第三方登录过来的用户是特殊的. 没有任何信息. 此时的约定是这样: 用户Id 与 用户名 相同.
 * password 在 db 中有非空约束, 其值与第三方平台返回的 outId 保持一致.
 *
 * 3.由客服手动输入的用户数据, 只有手机号. 此时的约定: 用户名 与 手机号一致, 并随机生成一个密码
 *
 * 关于用户信息更改:
 *   用户名 >> 当与邮箱相同、与手机号相同、或者跟用户Id 相同(第三方登录过来的)时, 则可以设置;
 *   密码 >> 当用户Id 和 用户名相同(只发生在第三方登录的情况)时, 则可以设置;
 *   邮箱 >> 为空时, 则可以设置. 这一点也说明了: <span style="color:red">邮箱只能设置一次</span>;
 *   手机号 >> 可以随时设置.
 * </pre>
 */
public class User implements Serializable {

    private static final long serialVersionUID = 7993724590252729908L;

    private int id;

    private AccountType accountType;

    private String userName;

    private String email;

    private String phone;

    private transient String password;

    /**
     * 目前仅代表第三方用户是否有完善资料, 不代表邮件或手机号激活. 目前的注册用户没有激活的步骤
     */
    private boolean isActive = false;

    private String registerDate;

    private String registerIP;

    /**
     * 最新的登录时间
     */
    private Date loginTime;

    /**
     * 登录总次数
     */
    private int loginCount;

    //是否已删除
    private boolean isDelete;

    //是否为禁用
    private boolean hasForbidden;

    //等级
    private UserGrade grade;

    //总积分
    private long pointTotal;

    //累计消费金额
    private long expenseTotal;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getRegisterIP() {
        return registerIP;
    }

    public void setRegisterIP(String registerIP) {
        this.registerIP = registerIP;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    /**
     * 目前仅代表第三方用户是否有完善资料, 不代表邮件或手机号激活. 目前的注册用户没有激活的步骤
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * 目前仅代表第三方用户是否有完善资料, 不代表邮件或手机号激活. 目前的注册用户没有激活的步骤
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isHasForbidden() {
        return hasForbidden;
    }

    public void setHasForbidden(boolean hasForbidden) {
        this.hasForbidden = hasForbidden;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public UserGrade getGrade() {
        return grade;
    }

    public void setGrade(UserGrade grade) {
        this.grade = grade;
    }

    public long getPointTotal() {
        return pointTotal;
    }

    /**
     * 获取积分数
     * @return
     */
    public String getCurrency() {
        return Currency.IntegralToCurrency(pointTotal);
    }

    public void setPointTotal(long pointTotal) {
        this.pointTotal = pointTotal;
    }

    public long getExpenseTotal() {
        return expenseTotal;
    }

    public void setExpenseTotal(long expenseTotal) {
        this.expenseTotal = expenseTotal;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", accountType=" + accountType +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isActive=" + isActive +
                ", registerDate='" + registerDate + '\'' +
                ", registerIP='" + registerIP + '\'' +
                ", loginTime=" + loginTime +
                ", loginCount=" + loginCount +
                ", isDelete=" + isDelete +
                ", hasForbidden=" + hasForbidden +
                '}';
    }
}
