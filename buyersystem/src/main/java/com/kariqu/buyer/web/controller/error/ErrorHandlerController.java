package com.kariqu.buyer.web.controller.error;

import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.productcenter.service.ProductService;
import com.kariqu.searchengine.domain.*;
import com.kariqu.searchengine.service.SearchEngineQueryService;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * User: Asion
 * Date: 13-3-14
 * Time: 下午5:22
 */
@Controller
public class ErrorHandlerController {

    @Autowired
    private SearchEngineQueryService searchEngineQuery;

    @Autowired
    private ProductService productService;

    private boolean online = true;

    @RequestMapping("/do404")
    @RenderHeaderFooter
    @PageTitle("错误信息")
    public String do404(Model model) throws IOException {
        model.addAttribute("msg", "所访问的页面不存在或者已被管理员删除！");
        model.addAttribute("online", online);
        model.addAttribute("flag", "404");
        recommendProduct(model);
        return "error";
    }

    @RequestMapping("/do400")
    @RenderHeaderFooter
    @PageTitle("错误信息")
    public String do400(Model model) throws IOException {
        model.addAttribute("msg", "请求参数错误");
        model.addAttribute("online", online);
        model.addAttribute("flag", "400");
        recommendProduct(model);
        return "error";
    }

    @RequestMapping("/do500")
    @RenderHeaderFooter
    @PageTitle("错误信息")
    public String do500(Model model) throws IOException {
        model.addAttribute("msg", "服务器发生内部错误");
        model.addAttribute("online", online);
        model.addAttribute("flag", "500");
        recommendProduct(model);
        return "error";
    }

    /**
     * 出现错误时的默认推荐商品列表
     * @param model
     * @return
     */
    public void recommendProduct(Model model) {
        ProductQuery recommendQuery = new ProductQuery();
        recommendQuery.setKeyword("*");
        recommendQuery.setPageSize("8");
        recommendQuery.setSort(SortBy.valuation.toFiled());
        recommendQuery.setOrder(OrderBy.desc.toString());
        recommendQuery.setGroupField(ProductQuery.SearchSchemaField.ID);
        SearchResult searchResult = searchEngineQuery.queryProductsByQuery(recommendQuery);
        for (ProductInfo productInfo : searchResult.getProducts()) {
            // 最多显示四张图
            productInfo.setPictureUrlList(productService.getProductPictureUrl(NumberUtils.toInt(productInfo.getId()), 4));
        }
        model.addAttribute("searchResult", searchResult);
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

}
