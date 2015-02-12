package com.kariqu.buyer.web.controller.shop;

import com.google.common.collect.Lists;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.designcenter.client.domain.model.RenderResult;
import com.kariqu.designcenter.client.service.ProdRenderPageService;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.shop.PageStatus;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.service.ShopPageService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.kariqu.common.lib.Collections4.map;

/**
 * 店铺页面控制器，输出各种页面
 * User: Asion
 * Date: 12-5-18
 * Time: 上午10:33
 */
@Controller
public class ShopPageController {

    @Autowired
    private ProdRenderPageService prodRenderPageService;

    @Autowired
    private ShopPageService shopPageService;


    /**
     * 直接通过页面ID进行页面渲染
     * 通过模块动态组装的页面通过请求这个url来渲染
     *
     * @param pageId
     * @return
     */
    @RenderHeaderFooter
    @RequestMapping(value = "/page/{pageId}")
    public String displayPage(@PathVariable("pageId") long pageId, Model model, HttpServletResponse response, HttpServletRequest request) throws IOException {
        ShopPage shopPage = shopPageService.getShopPageById(pageId);
        if (shopPage == null) {
            response.sendError(404);
            return null;
        }
        if (shopPage.getPageStatus() == PageStatus.INVALID) {
            model.addAttribute("msg", "请求的页面已失效");
            return "error";
        }
        SessionUserInfo userInfo = LoginInfo.getLoginUser(request);
        Map<String, Object> context = map();
        if (userInfo != null) {
            context.put("curUserId", userInfo.getId()); //把当前用户信息传入
        }
        RenderResult renderResult = prodRenderPageService.prodRenderPage(pageId, context);

        // 页面模块所有的 css
        List<String> cssList = Lists.newArrayList();
        String pageCss = renderResult.getResourceParam(RenderConstants.CSS_ID_CONTEXT_KEY);
        if (StringUtils.isNotBlank(pageCss)) {
            for (String css : pageCss.split("\n")) {
                if (StringUtils.isNotBlank(css)) cssList.add(css);
            }
            model.addAttribute("pageCss", cssList);
        }

        model.addAttribute("pageContent", renderResult.getPageContent());
        model.addAttribute("pageId", pageId);
        model.addAttribute("site_title", shopPage.getTitle());
        model.addAttribute("site_keywords", shopPage.getKeywords());
        model.addAttribute("site_description", shopPage.getDescription());
        return "shop/shopLayout";
    }


}
