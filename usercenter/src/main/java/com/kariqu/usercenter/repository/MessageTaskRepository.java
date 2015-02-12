package com.kariqu.usercenter.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.usercenter.domain.MessageTask;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: kyle
 * Date: 13-1-6
 * Time: 下午6:17
 */
public class MessageTaskRepository extends SqlMapClientDaoSupport {

    public long insert(MessageTask messageTask) {
        return  (Long)this.getSqlMapClientTemplate().insert("insertMessageTask",messageTask);
    }

    public List<MessageTask> queryNotSendedMessageTask() {
        return  this.getSqlMapClientTemplate().queryForList("queryNotSendedMessageTask");
    }

    public void updateSendSuccessStateById(MessageTask messageTask) {
        this.getSqlMapClientTemplate().update("updateSendedStateById",messageTask);
    }

    public void deleteMessageTaskById(long id) {
        this.getSqlMapClientTemplate().update("deleteMessageTaskById",id);
    }

    public MessageTask getMessageTaskById(long id) {
        return  (MessageTask)this.getSqlMapClientTemplate().queryForObject("getMessageTaskById",id);

    }

    public Page<MessageTask> querySendMessageTaskBySmsPage(Page<MessageTask> page) {
        Map param = new HashMap();
        param.put("start", page.getStart());
        param.put("limit", page.getLimit());
        List<MessageTask> messageTaskBySmsPage = getSqlMapClientTemplate().queryForList("querySendMessageTaskBySmsPage", param);
        page.setResult(messageTaskBySmsPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("querySendMessageTaskBySmsCount"));
        return page;
    }
}
