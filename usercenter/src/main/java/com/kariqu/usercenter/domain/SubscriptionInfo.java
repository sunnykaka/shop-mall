package com.kariqu.usercenter.domain;

import java.util.Date;

/**
 * User: kyle
 * Date: 12-12-29
 * Time: 下午6:11
 */
public class SubscriptionInfo {

    private  long  id;
    private long userId;
    private String email;
    private Date createDate;

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    public void setId(long id){
        this.id=id;
    }

    public long getId(){

        return this.id;
    }

    public void setUserId(long userId){
        this.userId=userId;
    }

    public long  getUserId(){
        return  this.userId;
    }

    public void  setEmail(String email){
        this.email =email;
    }

    public String getEmail(){
        return this.email;
    }





}
