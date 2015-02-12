package com.kariqu.usercenter.service;

import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * User: Asion
 * Date: 13-4-19
 * Time: 下午3:25
 */
@SpringApplicationContext({"classpath:userCenter.xml"})
public class SpringMailTest extends UnitilsJUnit4 {

    @SpringBean("javaMailSender")
    private JavaMailSender javaMailSender;

    @Test
    public void testSendMail() throws MessagingException {
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
        MimeMessage mailMessage = senderImpl.createMimeMessage();
        //设置utf-8或GBK编码，否则邮件会有乱码
        MimeMessageHelper messageHelper = null;
        messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");
        messageHelper.setTo("839159420@qq.com");//接受者
        messageHelper.setFrom("noreply@yijushang.com");//发送者
        messageHelper.setSubject("测试邮件");//主题

        //邮件内容，注意加参数true，表示启用html格式

        messageHelper.setText("<html><head></head><body><font color='red'>xxx</font></body></html>", true);

        javaMailSender.send(mailMessage);

    }
}
