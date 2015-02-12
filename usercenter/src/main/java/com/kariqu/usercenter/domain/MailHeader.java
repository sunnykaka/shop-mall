package com.kariqu.usercenter.domain;

import java.util.Map;

/**
 * User: kyle
 * Date: 13-1-7a
 * Time: 下午5:58
 */
public class MailHeader {
    /**
     * 邮件标题
     */
    private String mailSubject;
    /**
     * 发件人地址
     */
    private String mailFrom;
    /**
     * 收件人地址，多个地址使用逗号分隔
     */
    private String mailTo;
    /**
     * 传入邮件模板的参数
     */
    private Map params;


    public Map getParams() {
        return params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public  String[]  getMailToArray(){
        return mailTo.split(",");
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

}
