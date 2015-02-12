package com.kariqu.buyer.web.common;

import com.kariqu.common.uri.URLBrokerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 修改版本号 用于清空 页面 css js 缓存
 * User: Alec
 * Date: 12-11-22
 * Time: 下午4:57
 */
public class ChangeVersion extends HttpServlet {

    private String urlBrokerFactory = "urlBrokerFactory";

    /**
     * Constructor of the object.
     */
    public ChangeVersion() {
        super();
    }

    /**
     * Destruction of the servlet. <br>
     */
    public void destroy() {
        super.destroy();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebApplicationContext webApplicationContext = RequestContextUtils.getWebApplicationContext(request, request.getSession().getServletContext());
        URLBrokerFactory urlBrokerFactory = (URLBrokerFactory) webApplicationContext.getBean(this.urlBrokerFactory);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<HTML>");
        out.println("  <HEAD><TITLE>修订版本号</TITLE></HEAD>");
        out.println("  <BODY>");
        out.print("<center><br/><br/>");
        if (null != urlBrokerFactory) {
            urlBrokerFactory.setVersion(System.currentTimeMillis());
            out.print("版本号修改为：" + urlBrokerFactory.getVersion());
        } else {
            out.print("版本号修改失败！");
        }
        out.println("<br/><br/></center>");
        out.println("  </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }

    public void init() throws ServletException {
    }
}
