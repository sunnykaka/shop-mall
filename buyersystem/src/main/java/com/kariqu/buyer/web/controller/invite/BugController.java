package com.kariqu.buyer.web.controller.invite;

import com.kariqu.buyer.web.common.CheckFormToken;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.common.Token;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 信息反馈
 *
 * @author: Eli
 * @since 1.0.0
 *        Date:12-11-8
 *        Time:下午4:09
 */

@Controller
public class BugController {

    private final Log logger = LogFactory.getLog(BugController.class);

    @Autowired
    private DataSource dataSource;

    @Token
    @RequestMapping(value = "/feedback/new")
    public String newFeedBack(String pay, Model model) {
        model.addAttribute("pay", pay);
        return "invite/feedback";
    }

    @CheckFormToken
    @RequestMapping(value = "/feedback/submit", method = RequestMethod.POST)
    @RenderHeaderFooter
    public String submitFeedBack(String type, String content, String information, MultipartFile uploadFile, HttpServletRequest request, Model model) throws IOException {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("咨询类型：" + type + "，咨询内容：" + content + "，联系方式：" + information + "，附件：" + uploadFile.getName());
            }
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = dataSource.getConnection();
            String sql = "insert into BugInfo (type,content,information,uploadFile,fileType,fileName) values(?,?,?,?,?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            if ("输入您的邮箱或手机号码".equals(information) || "".equals(information.trim())) {
                information = null;
            }
            pst.setString(1, type);
            pst.setString(2, content);
            pst.setString(3, information);
            if (uploadFile.isEmpty()) {
                pst.setBytes(4, null);
                pst.setString(5, null);
            } else {
                byte[] bt = uploadFile.getBytes();
                pst.setBytes(4, bt);
                pst.setString(5, uploadFile.getContentType());
            }
            pst.setString(6, uploadFile.getOriginalFilename());

            int i = pst.executeUpdate();

            pst.close();
            conn.close();
            if (i > 0) {
                model.addAttribute("succeed", true);
                model.addAttribute("msg", "感谢您的支持，反馈信息已提交");
                return "invite/feedback";
            } else {
                model.addAttribute("succeed", false);
                model.addAttribute("msg", "很抱歉提交失败，请您联系客服人员进行处理！");
                return "invite/feedback";
            }
        } catch (Exception e) {
            logger.error("信息反馈出错！", e);
            model.addAttribute("succeed", false);
            model.addAttribute("msg", "服务器出错了！请您联系客服人员进行处理。");
            return "invite/feedback";
        }
    }


}
