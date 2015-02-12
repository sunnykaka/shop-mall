package com.kariqu.buyer.web.common;

import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.designcenter.client.domain.model.RenderResult;
import com.kariqu.designcenter.client.service.HeadFootRenderService;
import com.kariqu.designcenter.domain.model.RenderConstants;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Athens(刘杰)
 * @Time 2013-04-19 14:08
 * @since 1.0.0
 */
public class HeadAndFoot {

    @Autowired
    private HeadFootRenderService headFootRenderService;

    /**
     * 加载头尾数据
     *
     * @param request
     */
    public void renderHeadFoot(HttpServletRequest request) {
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(LoginInfo.USER_SESSION_KEY, LoginInfo.getLoginUser(request));
        RenderResult headResult = headFootRenderService.renderHead(RenderConstants.shopId, context);
        request.setAttribute("shopHead", headResult.getPageContent());
        RenderResult footResult = headFootRenderService.renderFoot(RenderConstants.shopId, context);
        request.setAttribute("shopFoot", footResult.getPageContent());
    }
}
