package com.kariqu.designcenter.client.monitor;

import com.kariqu.designcenter.client.container.DesignCenterEmbedClientSpringContainer;
import com.kariqu.designcenter.domain.model.prototype.CommonModule;
import com.kariqu.designcenter.service.CommonModuleContainer;
import com.kariqu.designcenter.service.CommonModuleService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 对公共模块进行同步的Servlet，公共模块缓存在了渲染逻辑客户端的内存中
 * 所有如果模板系统对公共模块做的改动，要让渲染逻辑知道最新的更改必须请求这个Servlet进行数据拉取
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-8-7 下午1:14
 */
public class DesignCenterClientMonitorServlet extends HttpServlet {

    private static Log logger = LogFactory.getLog(DesignCenterClientMonitorServlet.class);

    private static final String SYNC_ALL = "all";

    private static final String SYNC_SINGLE = "single";

    private static final String SYNC_TEST = "test";

    private final CommonModuleContainer commonModuleClientContainer = (CommonModuleContainer) DesignCenterEmbedClientSpringContainer.getBean("commonModuleContainer");

    private final CommonModuleService commonModuleService = (CommonModuleService) DesignCenterEmbedClientSpringContainer.getBean("commonModuleService");


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        final String queryString = req.getQueryString();
        if (queryString != null && queryString.contains("help")) {
            writeHelpInfo(resp);
            return;
        }
        final String syncType = req.getParameter("type");
        final String prototypeId = req.getParameter("id");

        //线上可见到效果
        if (SYNC_ALL.equals(syncType)) {
            try {
                commonModuleClientContainer.reloadAll();
                if (logger.isDebugEnabled()) {
                    logger.debug("同步完全部公共模块");
                }
            } catch (Exception e) {
                throw new ServletException(e);
            }
            writeMessage(resp, "同步成功");
            return;
        }

        //线上可见到效果
        if (SYNC_SINGLE.equals(syncType) && !StringUtils.isBlank(prototypeId)) {
            final CommonModule commonModule = commonModuleService.getCommonModuleById(Integer.parseInt(prototypeId));
            if (null != commonModule) {
                commonModuleClientContainer.resetSingle(commonModule);
                if (logger.isDebugEnabled()) {
                    logger.debug("同步完公共模块:" + commonModule.getName());
                }
            } else {
                writeMessage(resp, "模块原型prototypeId:" + prototypeId + "不存在");
            }
            return;
        }

        //这个应该要同步到测试机器，拷贝模块的编辑部分到产品渲染
        if (SYNC_TEST.equals(syncType) && !StringUtils.isBlank(prototypeId)) {
            final CommonModule commonModule = commonModuleService.getCommonModuleById(Integer.parseInt(prototypeId));
            if (null != commonModule) {
                commonModule.setLogicCode(commonModule.getEditLogicCode());
                commonModule.setModuleContent(commonModule.getEditModuleContent());
                commonModule.setFormContent(commonModule.getEditFormContent());
                commonModuleClientContainer.resetSingle(commonModule);
                if (logger.isDebugEnabled()) {
                    logger.debug("测试环境下同步完公共模块:" + commonModule.getName());
                }
            } else {
                writeMessage(resp, "模块原型prototypeId:" + prototypeId + "不存在");
            }
        }

    }

    private void writeMessage(HttpServletResponse resp, String message) throws IOException {
        try {
            resp.getWriter().write(message);
        } catch (IOException e) {
            throw e;
        }

    }

    private void writeHelpInfo(HttpServletResponse resp) {
        StringBuilder builder = new StringBuilder();
        builder.append("请采用如下方式同步模块").append("<br>")
                .append("type={syncType}&id={prototypeId}").append("<br>")
                .append("其中type的取值如下:").append("<br>")
                .append("all:同步所有的模块").append("<br>")
                .append("single:同步注定的单个模块，此时需要指定prototypeId").append("<br>")
                .append("test:同步测试的模块，此时需要指定prototypeId").append("<br>");

        try {
            resp.getWriter().write(builder.toString());
        } catch (IOException e) {
            logger.error("写入客户端帮助信息出错", e);
        }

    }
}
