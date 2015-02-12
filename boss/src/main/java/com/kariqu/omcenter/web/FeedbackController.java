package com.kariqu.omcenter.web;

import com.kariqu.common.JsonResult;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.om.domain.Feedback;
import com.kariqu.om.service.FeedbackService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: Eli
 * @since 1.0.0
 *        Date:12-11-13
 *        Time:上午10:52
 */

@Controller
public class FeedbackController {

    private final Log logger = LogFactory.getLog(FeedbackController.class);

    @Autowired
    private FeedbackService feedbackService;

    /**
     * 查询所有反馈列表,带分页
     *
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/feedback/list")
    public void feedbackList(@RequestParam("start") int start, @RequestParam("limit") int limit, HttpServletResponse response) throws IOException {
        Page<Feedback> feedbackPage = feedbackService.queryFeedbackByPage(start / limit + 1, limit);
        new JsonResult(true).addData("totalCount", feedbackPage.getTotalCount()).addData("result", feedbackPage.getResult()).toJson(response);
    }

    /**
     * 浏览图片
     *
     * @param id
     * @param response
     * @throws ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/feedback/img/{id}")
    public void queryFeedbackById(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        try {
            Feedback feedback = feedbackService.queryFeedbackById(id);
            if (null != feedback.getUploadFile() && feedback.getUploadFile().length > 0) {
                InputStream inputStream = new ByteArrayInputStream(feedback.getUploadFile());
                // 将文件内容拷贝到一个输出流中
                FileCopyUtils.copy(inputStream, response.getOutputStream());
            }
        } catch (Exception e) {
            logger.error("查询图片出错" + e);
            new JsonResult(false).toJson(response);
            return;
        }
    }

    /**
     * 下载附件
     * 文件名字需要用URL编码一下，不然中文文件名会出现乱码
     *
     * @param id
     * @param response
     * @throws ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/feedback/downloadFile/{id}")
    public void downloadFile(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        Feedback feedback = feedbackService.queryFeedbackById(id);
        if (null == feedback.getUploadFile() || feedback.getUploadFile().length == 0) {
            new JsonResult(false).toJson(response);
            return;
        }
        String fileName = feedback.getFileName();

        response.setCharacterEncoding("UTF-8");
        response.setContentType(feedback.getFileType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");

        InputStream inputStream = new ByteArrayInputStream(feedback.getUploadFile());

        // 将文件内容拷贝到一个输出流中
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }

}
