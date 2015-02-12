package com.kariqu.usercenter.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.MailHeader;
import com.kariqu.usercenter.domain.MessageTask;
import com.kariqu.usercenter.domain.MessageTemplateName;

import java.util.List;
import java.util.Map;

/**
 * User: kyle
 * Date: 13-1-6
 * Time: 下午2:15
 */
public interface MessageTaskService {

    /**
     * 根据短信模板来批量发送短信
     *
     * @param contactTels         用户联系手机号码,多个手机号码用逗号","分隔
     * @param messageTemplateName 模板类型
     */
    void sendSmsMessage(String contactTels, Map<String, String> params, MessageTemplateName messageTemplateName);


    /**
     * 直接发送短信文本
     *
     * @param contactTels 用户联系手机号码,多个手机号码用逗号","分隔
     * @param smsContent  短信文本内容
     */
    void sendSmsMessage(String contactTels, String smsContent);


    /**
     * 定时发送短信文本
     *
     * @param contactTels 用户联系手机号码,多个手机号码用逗号","分隔
     * @param smsContent  短信文本内容
     */
    void sendTimeSmsMessage(String contactTels, String smsContent,String sendTime);

    /**
     * 发送使用VM模板的HTML文件
     *
     * @param mailHeader
     * @param messageTemplateName
     */
    void sendHtmlMail(MailHeader mailHeader, MessageTemplateName messageTemplateName);


    List<MessageTask> queryNotSendedMessageTask();

    Page<MessageTask> querySendMessageTaskBySmsPage(Page<MessageTask> page);

    void deleteMessageTaskById(long id);

    public boolean reSendSmsMessage(MessageTask messageTask);

}
