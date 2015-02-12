package com.kariqu.designsystem.web;

import com.kariqu.common.DefaultShopService;
import com.kariqu.common.JsonResult;
import com.kariqu.common.Permission;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.designcenter.client.service.PageDesignService;
import com.kariqu.designcenter.domain.model.ModuleInstanceParam;
import com.kariqu.designcenter.domain.model.PageType;
import com.kariqu.designcenter.domain.model.RenderConstants;
import com.kariqu.designcenter.domain.model.shop.PageStatus;
import com.kariqu.designcenter.domain.model.shop.ShopPage;
import com.kariqu.designcenter.service.ModuleInstanceParamService;
import com.kariqu.designcenter.service.Result;
import com.kariqu.designcenter.service.ShopPageService;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 页面装修控制器
 * 负责响应装修时模块的添加，删除，上移，下移
 * User: Asion
 * Date: 12-5-9
 * Time: 下午1:28
 */
@Controller
public class PageDesignController {

    private static final Log logger = LogFactory.getLog(PageDesignController.class);

    private static final Log logForDeleteModule = LogFactory.getLog("RecordDeleteModuleInstanceParam");

    @Autowired
    private PageDesignService pageDesignService;

    @Autowired
    private DefaultShopService defaultShopService;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    @Autowired
    private ShopPageService shopPageService;

    @Autowired
    private ModuleInstanceParamService moduleInstanceParamService;

    /**
     * 发布一个店铺页面
     *
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/design/page/shop/publish", method = RequestMethod.POST)
    @Permission("发布店铺页面")
    public String publishShopPage(@RequestParam("pageId") long pageId) throws IOException {
        shopPageService.publishShopPage(pageId);
        return "redirect:" + urlBrokerFactory.getUrl("BuyHome").toString() + "/page/" + pageId;
    }


    @RequestMapping(value = "/design/page/shop/setToInvalid", method = RequestMethod.POST)
    @Permission("失效掉页面")
    public void invalidShopPage(@RequestParam("pageId") long pageId, HttpServletResponse response) throws IOException {
        ShopPage shopPage = shopPageService.getShopPageById(pageId);
        shopPage.setPageStatus(PageStatus.INVALID);
        shopPageService.updateShopPage(shopPage);
        new JsonResult(true).toJson(response);
    }


    /**
     * ajax发布，用于Ext请求
     *
     * @throws IOException
     */
    @RequestMapping(value = "/design/page/shop/publish/ajax", method = RequestMethod.POST)
    @Permission("发布店铺页面")
    public void ajaxPublishShopPage(@RequestParam("pageId") String pageId, HttpServletResponse response) throws IOException {
        try {
            for (String pid : pageId.split(",")) {
                int id = NumberUtils.toInt(pid);
                if (id > 0) shopPageService.publishShopPage(id);
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled())
                logger.error("内容管理的发布店铺页面异常：" + e);
            new JsonResult(false, "发布店铺页面出错").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }


    /**
     * 用其他原型，非四个主要页面的原型生成店铺页面
     * 用于运营快速制作页面
     *
     * @throws IOException
     */
    @RequestMapping(value = "/design/shop/page/other", method = RequestMethod.POST)
    @Permission("制作了页面")
    public void otherPrototypeInitShopPage(int prototypeId, String name, ShopPage shopPage, HttpServletResponse response) throws IOException {
        try {
            Result result = shopPageService.initShopPageWithPagePrototype(prototypeId, RenderConstants.shopId, PageType.other, name);
            if (result.isSuccess()) {
                int shopPageId = Integer.valueOf(result.getDataEntry("id"));
                ShopPage dbShopPage = shopPageService.getShopPageById(shopPageId);
                dbShopPage.setTitle(shopPage.getTitle());
                dbShopPage.setDescription(shopPage.getDescription());
                dbShopPage.setKeywords(shopPage.getKeywords());
                shopPageService.updateShopPage(dbShopPage);
                new JsonResult(true).toJson(response);
            } else {
                new JsonResult(false, result.getMessage()).toJson(response);
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled())
                logger.error("初始化其他页面异常：" + e);
            new JsonResult(false, "初始化页面出错").toJson(response);
        }
    }

    /**
     * 装修店铺页面的装修地址
     *
     */
    @RequestMapping(value = "/design/shop/page/address")
    public void shopPageDesignAddress(HttpServletResponse response) throws IOException {
        try {
            String address = urlBrokerFactory.getUrl("DesignPageUrl").toString();
            new JsonResult(true).addData("address", address).toJson(response);
        } catch (Exception e) {
            if (logger.isErrorEnabled())
                logger.error("返回页面装修地址出错:" + e);
            new JsonResult(false, "返回页面装修地址出错").toJson(response);
        }
    }

    /**
     * 详情页预览地址
     *
     */
    @RequestMapping(value = "/design/shop/detailPage/{productId}")
    public void shopDetailPageAddress(HttpServletResponse response, @PathVariable("productId") int productId) throws IOException {
        try {
            String address = urlBrokerFactory.getUrl("ProductDetail").addQueryData("productId", productId).toString();
            new JsonResult(true).addData("address", address).toJson(response);
        } catch (Exception e) {
            if (logger.isErrorEnabled())
                logger.error("返回详情页预览地址出错:" + e);
            new JsonResult(false, "返回详情页预览地址出错").toJson(response);
        }
    }

    /**
     * 常看页面的地址
     *
     */
    @RequestMapping(value = "/design/shop/page/view")
    public void viewPageAddress(HttpServletResponse response) throws IOException {
        try {
            String address = urlBrokerFactory.getUrl("ViewPageUrl").toString();
            new JsonResult(true).addData("address", address).toJson(response);
        } catch (Exception e) {
            if (logger.isErrorEnabled())
                logger.error("返回页面查看地址出错:" + e);
            new JsonResult(false, "返回页面查看地址出错").toJson(response);
        }
    }


    @RequestMapping(value = "/design/page/module/head/add", method = RequestMethod.POST)
    @Permission("头部添加模块")
    public void addModuleToHead(HttpServletResponse response, int prototypeId, String regionName) throws IOException {
        Result result = pageDesignService.addModuleToHead(prototypeId, defaultShopService.getDefaultShopTemplate().getId(), regionName);
        if (result.isSuccess()) {
            new JsonResult(true).addData("previewUrl", result.getDataEntry("data-url")).toJson(response);
        } else {
            new JsonResult(false, result.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/design/page/module/foot/add", method = RequestMethod.POST)
    @Permission("尾部添加模块")
    public void addModuleToFoot(HttpServletResponse response, int prototypeId, String regionName) throws IOException {
        Result result = pageDesignService.addModuleToFoot(prototypeId, defaultShopService.getDefaultShopTemplate().getId(), regionName);
        if (result.isSuccess()) {
            new JsonResult(true).addData("previewUrl", result.getDataEntry("data-url")).toJson(response);
        } else {
            new JsonResult(false, result.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/design/page/module/body/add", method = RequestMethod.POST)
    @Permission("Body添加模块")
    public void addModuleToBody(HttpServletResponse response, int prototypeId, int pageId, String regionName) throws IOException {
        Result result = pageDesignService.addModuleToBody(prototypeId, pageId, regionName);
        if (result.isSuccess()) {
            new JsonResult(true).addData("previewUrl", result.getDataEntry("data-url")).toJson(response);
        } else {
            new JsonResult(false, result.getMessage()).toJson(response);
        }
    }


    @RequestMapping(value = "/design/page/module/head/delete", method = RequestMethod.POST)
    @Permission("在网站头部移除模板")
    public void deleteModuleFromHead(HttpServletResponse response, String moduleInstanceId, String regionName) throws IOException {
        if (logForDeleteModule.isWarnEnabled())
            logForDeleteModule.warn("头部移除模版模版：" + this.getDeleteModuleParamsToString(moduleInstanceId));
        Result result = pageDesignService.deleteModuleFromHead(moduleInstanceId, defaultShopService.getDefaultShopTemplate().getId(), regionName);
        if (result.isSuccess()) {
            new JsonResult(true).toJson(response);
        } else {
            new JsonResult(false, result.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/design/page/module/foot/delete", method = RequestMethod.POST)
    @Permission("在网站尾部移除模板")
    public void deleteModuleFromFoot(HttpServletResponse response, String moduleInstanceId, String regionName) throws IOException {
        if (logForDeleteModule.isWarnEnabled())
            logForDeleteModule.warn("尾部移除模版模版：" + this.getDeleteModuleParamsToString(moduleInstanceId));
        Result result = pageDesignService.deleteModuleFromFoot(moduleInstanceId, defaultShopService.getDefaultShopTemplate().getId(), regionName);
        if (result.isSuccess()) {
            new JsonResult(true).toJson(response);
        } else {
            new JsonResult(false, result.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/design/page/module/body/delete", method = RequestMethod.POST)
    @Permission("在网站Body部移除模板")
    public void deleteModuleFromBody(HttpServletResponse response, String moduleInstanceId, int pageId, String regionName) throws IOException {
        if (logForDeleteModule.isWarnEnabled())
            logForDeleteModule.warn("Body部移除模版模版：" + this.getDeleteModuleParamsToString(moduleInstanceId));
        Result result = pageDesignService.deleteModuleFromBody(moduleInstanceId, pageId, regionName);
        if (result.isSuccess()) {
            new JsonResult(true).toJson(response);
        } else {
            new JsonResult(false, result.getMessage()).toJson(response);
        }
    }

    /**
     * 查询要删除模块实例的详情 并转为字符串形式
     */
    private String getDeleteModuleParamsToString(String moduleInstanceId) {
        List<ModuleInstanceParam> paramList = moduleInstanceParamService.queryModuleParamsByModuleInstanceId(moduleInstanceId);
        StringBuilder sb = new StringBuilder();
        for (ModuleInstanceParam param : paramList) {
            sb.append("\n").append("pageId:").append(param.getId()).append("\n")
                    .append("参数名:").append(param.getParamName()).append("\n")
                    .append("内容:").append("\n").append(param.getParamValue()).append("\n")
                    .append("------------------------");
        }
        return sb.toString();
    }

    @RequestMapping(value = "/design/page/module/head/move", method = RequestMethod.POST)
    @Permission("在网站Body移动模块")
    public void moveModuleOfHead(HttpServletResponse response, String moduleInstanceId, String regionName, String direction) throws IOException {
        Result result = pageDesignService.moveModuleOfHead(moduleInstanceId, defaultShopService.getDefaultShopTemplate().getId(), regionName, direction);
        if (result.isSuccess()) {
            new JsonResult(true).toJson(response);
        } else {
            new JsonResult(false, result.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/design/page/module/foot/move", method = RequestMethod.POST)
    @Permission("在网站尾部移动模块")
    public void moveModuleOfFoot(HttpServletResponse response, String moduleInstanceId, String regionName, String direction) throws IOException {
        Result result = pageDesignService.moveModuleOfFoot(moduleInstanceId, defaultShopService.getDefaultShopTemplate().getId(), regionName, direction);
        if (result.isSuccess()) {
            new JsonResult(true).toJson(response);
        } else {
            new JsonResult(false, result.getMessage()).toJson(response);
        }
    }

    @RequestMapping(value = "/design/page/module/body/move", method = RequestMethod.POST)
    @Permission("在网站Body移动模块")
    public void moveModuleOfBody(HttpServletResponse response, String moduleInstanceId, int pageId, String regionName, String direction) throws IOException {
        Result result = pageDesignService.moveModuleOfBody(moduleInstanceId, pageId, regionName, direction);
        if (result.isSuccess()) {
            new JsonResult(true).toJson(response);
        } else {
            new JsonResult(false, result.getMessage()).toJson(response);
        }
    }


}
