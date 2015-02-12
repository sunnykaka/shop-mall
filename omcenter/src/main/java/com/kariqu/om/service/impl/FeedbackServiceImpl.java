package com.kariqu.om.service.impl;

import com.kariqu.common.pagenavigator.Page;
import com.kariqu.om.domain.Feedback;
import com.kariqu.om.repository.FeedbackRepository;
import com.kariqu.om.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Eli
 * Date: 12-11-13
 * Time: 上午10:32
 */

public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;


    @Override
    public Page<Feedback> queryFeedbackByPage(int pageNo,int pageSize) {
        return feedbackRepository.queryFeedbackByPage(pageNo,pageSize);
    }

    @Override
    public Feedback queryFeedbackById(int id) {
        return feedbackRepository.queryFeedbackById(id);
    }
}
