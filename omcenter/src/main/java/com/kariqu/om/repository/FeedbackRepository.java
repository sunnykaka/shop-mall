package com.kariqu.om.repository;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.om.domain.Feedback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Eli
 * @since 1.0.0
 *        Date:12-11-13
 *        Time:上午11:08
 */

public class FeedbackRepository extends SqlMapClientDaoSupport {

    public Page<Feedback> queryFeedbackByPage(int pageNo, int pageSize) {
        Page<Feedback> page = new Page<Feedback>(pageNo, pageSize);
        Map param = new HashMap();
        param.put("start", page.getPageFirst());
        param.put("limit", page.getPageSize());
        List<Feedback> queryFeedbackByPage = getSqlMapClientTemplate().queryForList("queryFeedbackByPage", param);
        page.setResult(queryFeedbackByPage);
        page.setTotalCount((Integer) getSqlMapClientTemplate().queryForObject("selectCountForFeedback"));
        return page;
    }

    public Feedback queryFeedbackById(int id) {
        return (Feedback) getSqlMapClientTemplate().queryForObject("selectFeedbackById", id);
    }
}
