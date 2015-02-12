package com.kariqu.usercenter.repository.impl.ibatis;

import com.kariqu.usercenter.domain.MessageSendWay;
import com.kariqu.usercenter.domain.MessageTask;
import com.kariqu.usercenter.repository.MessageTaskRepository;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBean;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * User: kyle
 * Date: 13-1-7
 * Time: 上午10:18
 */
@SpringApplicationContext({"classpath:userCenter.xml"})
public class MessageTaskRepositoryImplTest extends UnitilsJUnit4 {


    @SpringBean("messageTaskRepository")
    private MessageTaskRepository messageTaskRepository;


    @Test
    public void testInsert() {

        MessageTask messageTask = new MessageTask();
        messageTask.setContact("13434437078");
        messageTask.setContent("testtest");
        messageTask.setSendCount(1);
        messageTask.setSendWay(MessageSendWay.sms);
        messageTask.setSendSuccess(false);
        long id = messageTaskRepository.insert(messageTask);
        assertEquals(1, id);

    }

    @Test
    public void testQueryNotSendedMessageTask() throws Exception {
        MessageTask messageTask = new MessageTask();
        messageTask.setContact("18681521152");
        messageTask.setContent("testtest");
        messageTask.setSendCount(1);
        messageTask.setSendWay(MessageSendWay.sms);
        messageTask.setSendSuccess(false);
        long id = messageTaskRepository.insert(messageTask);
        assertEquals(id, 1);

        List<MessageTask> messageTasks = messageTaskRepository.queryNotSendedMessageTask();
        assertNotNull(messageTasks);
        assertEquals(1, messageTasks.size());

    }

    @Test
    public void testUpdateSendedStateById() throws Exception {
        MessageTask messageTask = new MessageTask();
        messageTask.setContact("18681521152");
        messageTask.setContent("testtest");
        messageTask.setSendCount(1);
        messageTask.setSendWay(MessageSendWay.sms);
        messageTask.setSendSuccess(false);
        long id = messageTaskRepository.insert(messageTask);
        assertEquals(1, id);
        MessageTask   sendMessageTask=messageTaskRepository.getMessageTaskById(id);
        assertNotNull(sendMessageTask);
        assertEquals(false,sendMessageTask.isSendSuccess());
        assertEquals(1,sendMessageTask.getSendCount());

        messageTask.setSendSuccess(true);
        messageTaskRepository.updateSendSuccessStateById(messageTask);
        MessageTask   sendedMessageTask=messageTaskRepository.getMessageTaskById(id);
        assertNotNull(sendedMessageTask);
        assertEquals(true,sendedMessageTask.isSendSuccess());
        assertEquals(2,sendedMessageTask.getSendCount());

    }

    @Test
    public void testDeleteMessageTaskById() throws Exception {
        MessageTask messageTask = new MessageTask();
        messageTask.setContact("18681521152");
        messageTask.setContent("testtest");
        messageTask.setSendCount(1);
        messageTask.setSendWay(MessageSendWay.sms);
        messageTask.setSendSuccess(false);
        long id = messageTaskRepository.insert(messageTask);
        assertEquals(id, 1);
        messageTaskRepository.deleteMessageTaskById(1);
        MessageTask deledMessageTask = messageTaskRepository.getMessageTaskById(1);
        assertNull(deledMessageTask);
    }

    @Test
    public void testGetMessageTaskById() throws Exception {
        MessageTask messageTask = new MessageTask();
        messageTask.setContact("18681521152");
        messageTask.setContent("testtest");
        messageTask.setSendCount(1);
        messageTask.setSendWay(MessageSendWay.sms);
        messageTask.setSendSuccess(false);
        long id = messageTaskRepository.insert(messageTask);
        assertEquals(id, 1);
        MessageTask queryMessageTask = messageTaskRepository.getMessageTaskById(1);
        assertNotNull(queryMessageTask);
        assertEquals(1, queryMessageTask.getId());
    }
}
