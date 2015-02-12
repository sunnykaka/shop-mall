package com.kariqu.om.service;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.om.domain.Feedback;

public interface FeedbackService {

    /**
     * 分页查询
     *
     * @return
     */
    Page<Feedback> queryFeedbackByPage(int pageNo,int pageSize);

    /**
     * 根据编号查询反馈信息
     * @param id
     * @return
     */
    public Feedback queryFeedbackById(int id);

}