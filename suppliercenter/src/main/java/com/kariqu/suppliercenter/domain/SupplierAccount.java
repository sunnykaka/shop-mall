package com.kariqu.suppliercenter.domain;

/**
 * 商家账号
 * User: eli
 */
public class SupplierAccount {

    private int id;

    private String accountName;

    private transient String password;

    private boolean isNormal;

    private String email;

    private int customerId;

    private boolean isMainAccount;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public boolean isNormal() {
        return isNormal;
    }

    public void setNormal(boolean normal) {
        isNormal = normal;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isMainAccount() {
        return isMainAccount;
    }

    public void setMainAccount(boolean mainAccount) {
        isMainAccount = mainAccount;
    }

    @Override
    public String toString() {
        return "(id=" + id +
                ", accountName='" + accountName + "'" +
                ", isNormal=" + isNormal +
                ", email='" + email + "'" +
                ", customerId=" + customerId +
                ", isMainAccount=" + isMainAccount +
                ')';
    }
}
