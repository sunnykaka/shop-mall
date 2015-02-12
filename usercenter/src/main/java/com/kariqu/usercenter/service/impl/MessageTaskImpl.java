package com.kariqu.usercenter.service.impl;

import com.kariqu.common.SmsSender;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.MailHeader;
import com.kariqu.usercenter.domain.MessageSendWay;
import com.kariqu.usercenter.domain.MessageTask;
import com.kariqu.usercenter.domain.MessageTemplateName;
import com.kariqu.usercenter.repository.MessageTaskRepository;
import com.kariqu.usercenter.service.MessageTaskService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * User: kyle
 * Date: 13-1-6
 * Time: 下午2:42
 */
public class MessageTaskImpl implements MessageTaskService {

    protected final Log LOGGER = LogFactory.getLog(MessageTaskImpl.class);

    private VelocityEngine velocityEngine;

    private JavaMailSender javaMailSender;

    /** false 表示如果不是在线上, 则不往用户发信息, 不发邮件 */
    private boolean online = false;

    @Autowired
    private MessageTaskRepository messageTaskRepository;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void sendSmsMessage(String contactTels, Map<String, String> params, MessageTemplateName messageTemplateName) {
        if (!online) return;

        String smsContent = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, messageTemplateName.toDesc(), params);
        MessageTask messageTask = new MessageTask();
        messageTask.setContact(contactTels);
        messageTask.setSendWay(MessageSendWay.sms);
        messageTask.setSendCount(1);
        messageTask.setContent(smsContent);
        sendingSms(messageTask);
    }

    @Override
    public void sendSmsMessage(String contactTels, String smsContent) {
        if (!online) return;

        MessageTask messageTask = new MessageTask();
        messageTask.setContact(contactTels);
        messageTask.setSendWay(MessageSendWay.sms);
        messageTask.setSendCount(1);
        messageTask.setContent(smsContent);
        sendingSms(messageTask);
    }

    @Override
    public void sendTimeSmsMessage(String contactTels, String smsContent, String sendTime) {
        if (!online) return;

        MessageTask messageTask = new MessageTask();
        messageTask.setContact(contactTels);
        messageTask.setSendWay(MessageSendWay.sms);
        messageTask.setSendCount(1);
        messageTask.setContent(smsContent);
        sendingTimeSms(messageTask, sendTime);
    }

    @Override
    public void sendHtmlMail(MailHeader mailHeader, MessageTemplateName messageTemplateName) {
        if (!online) return;

        String mailContent = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
        messageTemplateName.toDesc(), mailHeader.getParams());
        sendingEmail(mailHeader, mailContent);
    }

    @Override
    public List<MessageTask> queryNotSendedMessageTask() {
        return this.messageTaskRepository.queryNotSendedMessageTask();
    }

    @Override
    public Page<MessageTask> querySendMessageTaskBySmsPage(Page<MessageTask> page) {
        return messageTaskRepository.querySendMessageTaskBySmsPage(page);
    }

    @Override
    public void deleteMessageTaskById(long id) {
        messageTaskRepository.deleteMessageTaskById(id);
    }

    @Override
    public boolean reSendSmsMessage(MessageTask messageTask) {
        if (!online) return false;

        boolean flag = false;
        try {
            String resultXml = sendSms(messageTask);
            messageTask.setSendSuccess(readXml(resultXml));
            messageTask.setSendCount(messageTask.getSendCount() + 1);
            flag = true;
        } catch (Exception e) {
            messageTask.setSendSuccess(false);
            LOGGER.error("message: " + messageTask + "重新发送失败 !", e);
        }
        messageTaskRepository.updateSendSuccessStateById(messageTask);
        return flag;
    }

    private String sendSms(final MessageTask messageTask) throws Exception {
        try {
            // 只等 3 秒, 逾期不候
            return executor.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return SmsSender.sendSms(messageTask.getContact(), messageTask.getContent());
                }
            }).get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        } catch (TimeoutException e) {
            throw e;
        }
    }

    private void sendingTimeSms(final MessageTask messageTask, final String sendTime) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String resultXml = SmsSender.sendTimeSms(messageTask.getContact(), messageTask.getContent(), sendTime);
                    messageTask.setSendSuccess(readXml(resultXml));
                } catch (Exception e) {
                    messageTask.setSendSuccess(false);
                    LOGGER.error("message: " + messageTask + "定时发送失败 !", e);
                }
                messageTaskRepository.insert(messageTask);
            }
        });
    }

    private void sendingSms(final MessageTask messageTask) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String resultXml = SmsSender.sendSms(messageTask.getContact(), messageTask.getContent());
                    messageTask.setSendSuccess(readXml(resultXml));
                } catch (Exception e) {
                    messageTask.setSendSuccess(false);
                    LOGGER.error("message: " + messageTask + "发送失败 !", e);
                }
                messageTaskRepository.insert(messageTask);
            }
        });
    }

    private void sendingEmail(final MailHeader mailHeader, final String mailContent) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                JavaMailSenderImpl javaMail = (JavaMailSenderImpl) javaMailSender;
                mailHeader.setMailFrom(javaMail.getUsername());

                final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
                try {
                    helper.setSubject(mailHeader.getMailSubject());
                    helper.setTo(mailHeader.getMailToArray());
                    helper.setText(mailContent, true);
                    helper.setFrom(mailHeader.getMailFrom());
                    javaMailSender.send(mimeMessage);
                } catch (Exception e) {
                    LOGGER.error("发送邮件" + mailHeader + "失败!", e);
                }
            }
        });
    }

    private boolean readXml(String xmlContent) {
        boolean hasSendSuccess = false;
        try {
            Document doc = DocumentHelper.parseText(xmlContent);
            Element root = doc.getRootElement();
            /* ((Element)(root.elements("message").get(0))).getText(); //返回的消息信息*/
            String success = ((Element) (root.elements("error").get(0))).getText();
            if (success.equals("0")) {
                hasSendSuccess = true;
            }
        } catch (Exception e) {
            LOGGER.error("读取短信返回信息异常" + e);
        }
        return hasSendSuccess;
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public JavaMailSender getJavaMailSender() {
        return javaMailSender;
    }

    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /** false 表示如果不是在线上, 则不往用户发信息, 不发邮件 */
    public boolean isOnline() {
        return online;
    }

    /** false 表示如果不是在线上, 则不往用户发信息, 不发邮件 */
    public void setOnline(boolean online) {
        this.online = online;
    }

}
