package com.kariqu.buyer.web.controller.myinfo;

import com.kariqu.buyer.web.common.JsonResult;
import com.kariqu.buyer.web.common.PageTitle;
import com.kariqu.buyer.web.common.RenderHeaderFooter;
import com.kariqu.buyer.web.common.Token;
import com.kariqu.buyer.web.controller.login.LoginInfo;
import com.kariqu.buyer.web.helper.SessionUserInfo;
import com.kariqu.common.pagenavigator.Page;
import com.kariqu.common.pagenavigator.PageProcessor;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.tradecenter.client.TradeCenterUserClient;
import com.kariqu.tradecenter.domain.*;
import com.kariqu.tradecenter.excepiton.BackGoodsBaseException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * 退换货中心
 * User: Alec
 * Date: 12-12-5
 * Time: 上午10:18
 */
@Controller
@PageTitle("退换货中心")
public class BackGoodsController {

    protected final Log logger = LogFactory.getLog(BackGoodsController.class);

    @Autowired
    private TradeCenterUserClient tradeCenterUserClient;

    private String spacePictureSpacePath;

    @Autowired
    private URLBrokerFactory urlBrokerFactory;

    /**
     * 申请退货订单页面
     */
    @Token
    @RenderHeaderFooter
    @RequestMapping(value = "/my/toBack/apply", method = RequestMethod.POST)
    public String toReturnsOrder(String orderNo, Model model, HttpServletRequest request) {
        if (StringUtils.isNotBlank(orderNo)) {
            model.addAttribute("orderNo", orderNo);
            SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
            long orderNumber = NumberUtils.toLong(orderNo);
            try {
                Result<OrderItem, Order> backGoodsResult = tradeCenterUserClient.getCanBackOrder(sessionUserInfo.getId(), orderNumber);
                if (!backGoodsResult.isSuccess()) {
                    model.addAttribute("msg", backGoodsResult.getMessage());
                } else {
                    model.addAttribute("order", backGoodsResult.getObj());
                    //得到此订单中选中的订单项
                    String[] orderItemIds = request.getParameterValues("orderItemIds" + orderNo);
                    //如果页面没有选中要退货的订单项并且没有传过来需要退货的订单项，那么就查询此订单的所有能退货的订单项
                    if (null == orderItemIds || orderItemIds.length == 0) {
                        orderItemIds = this.setOrderItemIds(orderNumber);
                    }
                    //根据选中的orderitem的id将未被选中的orderitem的id从list中删除
                    this.removeNoCheckOrderItem(backGoodsResult.getList(), orderItemIds);
                    //将此订单中的选中需要退货的订单项（也许有多个）以字符串形式放入，用“-”分隔
                    String orderItemsStr = this.orderItemIdsAndReturnsNum(orderItemIds, request, orderNo);
                    model.addAttribute("orderItemIdsAndReturnsNum", orderItemsStr.toString());
                    model.addAttribute("orderItemList", backGoodsResult.getList());
                }
            } catch (BackGoodsBaseException e) {
                model.addAttribute("msg", e.getMessage());
            }
        }
        model.addAttribute("contentVm", "myinfo/selectBackItem.vm");
        return "myinfo/myInfoLayout";
    }

    private String orderItemIdsAndReturnsNum(String[] orderItemIds, HttpServletRequest request, String orderNo) {
        //将此订单中的选中需要退货的订单项（也许有多个）以字符串形式放入，用“-”分隔
        StringBuilder orderItemsStr = new StringBuilder();
        for (int i = 0; i < orderItemIds.length; i++) {
            if (i > 0) {
                orderItemsStr.append(";");
            }
            orderItemsStr.append(orderItemIds[i]).append("-").append(request.getParameter("orderItem" + orderNo + orderItemIds[i]));
        }
        return orderItemsStr.toString();
    }

    private void removeNoCheckOrderItem(List<OrderItem> orderItemList, String[] orderItemIds) {
        if (orderItemIds != null && orderItemIds.length > 0 && orderItemList.size() > 1) {
            List<OrderItem> removeOrderItemList = new ArrayList<OrderItem>();
            for (OrderItem orderItem : orderItemList) {
                for (int i = 0; i < orderItemIds.length; i++) {
                    if (StringUtils.equals(orderItem.getId() + "", orderItemIds[i])) {
                        break;
                    }
                    if (i == orderItemIds.length - 1) {
                        //循环遍历完，还没有break，则将其移除出orderitemlist
                        removeOrderItemList.add(orderItem);
                    }
                }
            }
            orderItemList.removeAll(removeOrderItemList);
        }
    }

    private String[] setOrderItemIds(long orderNumber) {
        //如果页面没有选中要退货的订单项并且没有传过来需要退货的订单项，那么就查询此订单的所有能退货的订单项
        List<OrderItem> orderItemList = tradeCenterUserClient.queryCanBackOrderItemByOrderNo(orderNumber);
        String[] returnOrderItemIds = new String[orderItemList.size()];
        for (int i = 0; i < orderItemList.size(); i++) {
            returnOrderItemIds[i] = orderItemList.get(i).getId() + "";
        }
        return returnOrderItemIds;
    }

    /**
     * 申请退货单
     */
    @RenderHeaderFooter
    @RequestMapping(value = "/my/toBack/submit", method = RequestMethod.POST)
    public String toReturnsProducts(String orderNo, String orderItemIdsAndReturnsNum, String backReason, String backDiscription,
                                    String[] uploadFilesURL, Model model, HttpServletRequest request) throws IOException {
        //String backPhone, String address, String province, String city, String districts, String detailAddress, String processMode,以后增加功能时候方法的参数
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        long orderNumber = NumberUtils.toLong(orderNo);
        List<String> orderItemIdAndReturnsNum = getOrderItemIdAndReturnsNum(orderItemIdsAndReturnsNum);

        String[] flags = validationParam(orderNumber, orderItemIdAndReturnsNum, backReason, backDiscription);
        if (StringUtils.equals("false", flags[0])) {
            model.addAttribute("msg", flags[1]);
            model.addAttribute("contentVm", "myinfo/selectBackItem.vm");
            return "myinfo/myInfoLayout";
        }
        backDiscription = backDiscription.replaceAll("^", "").replaceAll("|", "").replaceAll("$", "").replaceAll("#", "");
        //退货地址
        //String backAddress = this.getBackGoodsAddress(address, province, city, districts, detailAddress);
        //构建退货单项
        List<BackGoodsItem> backGoodsItemList = this.getBackGoodsItemList(orderItemIdAndReturnsNum);
        //处理退货原因
        BackGoods.BackReason backReasonEnum = getBackReason(backReason);
        if (logger.isDebugEnabled()) {
            logger.debug("退货的商品为：" + backGoodsItemList);
        }
        //构建BackGoods对象
        BackGoods backGoods = getBackGoods(orderNo, uploadFilesURL, sessionUserInfo, backGoodsItemList, backReasonEnum, backDiscription);
        try {
            tradeCenterUserClient.submitBackGoods(backGoods);
        } catch (BackGoodsBaseException e) {
            model.addAttribute("msg", e.getMessage());
        }
        model.addAttribute("contentVm", "myinfo/backGoodsSuccess.vm");
        return "myinfo/myInfoLayout";
    }

    //退货处理方式  退货 换货 保修
    private BackGoods.ProcessMode getProcessMode(String processMode) {
        BackGoods.ProcessMode processModeEnum;
        try {
            processModeEnum = Enum.valueOf(BackGoods.ProcessMode.class, processMode);
        } catch (IllegalArgumentException e) {
            processModeEnum = BackGoods.ProcessMode.repair;
        }
        return processModeEnum;
    }

    //处理退货地址
    private String getBackGoodsAddress(String address, String province, String city, String districts, String detailAddress) {
        String backAddress = "";
        if (StringUtils.equals(address, "otherAddress")) {
            backAddress = new StringBuilder().append(province).append(city).append(districts).append(detailAddress).toString();
        } else {
            backAddress = address;
        }
        return backAddress;
    }

    private String[] validationParam(long orderNumber, List<String> orderItemIdAndReturnsNum, String backReason, String backDiscription) {
        String[] flags = new String[]{"true", ""};
        if (orderNumber <= 0) {
            flags[0] = "false";
            flags[1] = "订单号错误";
            return flags;
        }
        if (orderItemIdAndReturnsNum.size() < 1) {
            flags[0] = "false";
            flags[1] = "请选择要退货的商品";
            return flags;
        }
        if (StringUtils.isEmpty(backReason) || StringUtils.isEmpty(backDiscription)) {
            flags[0] = "false";
            flags[1] = "请完善退货资料";
            return flags;
        }
        return flags;
    }

    private List<String> getOrderItemIdAndReturnsNum(String orderItemIdsAndReturnsNum) {
        List<String> orderItemIdAndReturnsNum = new ArrayList<String>();
        for (String itemIdAndReturnsNum : orderItemIdsAndReturnsNum.split(";")) {
            orderItemIdAndReturnsNum.add(itemIdAndReturnsNum);
        }
        return orderItemIdAndReturnsNum;
    }

    private BackGoods.BackReason getBackReason(String backReason) {
        BackGoods.BackReason backReasonEnum;
        try {
            backReasonEnum = Enum.valueOf(BackGoods.BackReason.class, backReason);
        } catch (IllegalArgumentException e) {
            backReasonEnum = BackGoods.BackReason.NoQualityProblem;
        }
        return backReasonEnum;
    }

    private BackGoods getBackGoods(String orderNo, String[] uploadFilesURL, SessionUserInfo sessionUserInfo,
                                   List<BackGoodsItem> backGoodsItemList, BackGoods.BackReason backReasonEnum,
                                   String backDiscription) {
        BackGoods backGoods = new BackGoods();
        backGoods.setOrderNo(Long.parseLong(orderNo.trim()));
        backGoods.setUserName(sessionUserInfo.getUserName());
        backGoods.setAccountType(sessionUserInfo.getAccountType());
        backGoods.setUserId(sessionUserInfo.getId());
        backGoods.setUploadFiles(this.appendUploadFiles(uploadFilesURL));
        backGoods.setBackReason(backDiscription);
        backGoods.setBackReasonReal(backReasonEnum);
        backGoods.setBackGoodsItemList(backGoodsItemList);
//        backGoods.setBackAddress(address);
//        backGoods.setProcessMode(processMode);
//        backGoods.setBackPhone(backPhone);
        return backGoods;
    }

    //拼接上传图片的url地址
    private String appendUploadFiles(String[] uploadFilesURL) {
        StringBuilder uploadFiles = new StringBuilder();
        if (uploadFilesURL != null && uploadFilesURL.length > 0) {
            for (int i = 0; i < uploadFilesURL.length; i++) {
                uploadFiles.append(uploadFilesURL[i]);
                if (i < uploadFilesURL.length - 1) {
                    uploadFiles.append(">");
                }
            }
        }
        return uploadFiles.toString();
    }

    //提交退单时构建退单的退单项
    private List<BackGoodsItem> getBackGoodsItemList(List<String> orderItemIdAndReturnsNum) {
        List<BackGoodsItem> backGoodsItemList = new ArrayList<BackGoodsItem>();
        for (String idAndReturnNum : orderItemIdAndReturnsNum) {
            BackGoodsItem backGoodsItem = new BackGoodsItem();
            backGoodsItem.setOrderItemId(NumberUtils.toLong(idAndReturnNum.split("-")[0]));
            //设置退货单项可退货的数量，此处默认就是订单项的可退货记录
            backGoodsItem.setNumber(NumberUtils.toInt(idAndReturnNum.split("-")[1]));
            backGoodsItemList.add(backGoodsItem);
        }
        return backGoodsItemList;
    }

    /**
     * 用uploadify异步上传多文件
     *
     * @param request
     */
    @RequestMapping(value = "/my/toBack/uploadBackGoodPictures", method = RequestMethod.POST)
    public void uploadBackGoodPictures(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        String addressableURL = this.uploadMultipartFile(multipartRequest.getFileMap());
        try {
            response.setCharacterEncoding("utf-8");
            PrintWriter out = response.getWriter();
            out.write(addressableURL);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("退货上传图片返回数据异常" + addressableURL);
            }
        }
    }

    private String uploadMultipartFile(Map<String, MultipartFile> picture) {
        StringBuilder addressableURL = new StringBuilder();
        for (Map.Entry<String, MultipartFile> entry : picture.entrySet()) {
            MultipartFile mf = entry.getValue();
            String fileName = mf.getOriginalFilename();
            // 后缀
            String suffix = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf("."), fileName.length()) : StringUtils.EMPTY;
            String filePath = spacePictureSpacePath + "backGoodsPicture/" + UUID.randomUUID().toString().replaceAll("-", "") + suffix;

            // 将原文件名及上传的路径返回
            addressableURL.append(fileName).append("|").append(urlBrokerFactory.getUrl("SpaceImageUpload")
                    .addQueryData("fileName", filePath).toString());
            try {
                // 写入服务器
                tradeCenterUserClient.saveUploadPicture(mf.getInputStream(), spacePictureSpacePath + filePath);
            } catch (IOException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("退货生成图片异常" + addressableURL);
                }
            }
        }
        return addressableURL.toString();
    }

    /**
     * 查看退货单列表
     */
    @RenderHeaderFooter
    @RequestMapping(value = "/my/backGoods/list")
    public String backGoodsList(String pageSize, String pageNo, Model model, HttpServletRequest request) {
        int page = NumberUtils.toInt(pageNo);
        int size = NumberUtils.toInt(pageSize);
        if (page < 1) page = 1;
        if (size < 1 || size > 50) size = 5;

        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        Page<BackGoods> backGoodsPage = tradeCenterUserClient.getBackGoodsPageByUserId(sessionUserInfo.getId(), new Page<BackGoods>(page, size));

        if (backGoodsPage.getResult().size() > 0) {
            model.addAttribute("backGoodsPageBar", PageProcessor.process(backGoodsPage));
            model.addAttribute("backGoodsPage", backGoodsPage);
        }
        model.addAttribute("contentVm", "myinfo/backGoodsList.vm");
        model.addAttribute("currentBackGoods", "record");
        return "myinfo/myInfoLayout";
    }

    /**
     * 查看申请退货或保修的订单列表
     *
     * @return
     */
    @RenderHeaderFooter
    @RequestMapping(value = "my/backGoodsApply/list")
    public String backGoodsApplyList(String pageSize, String pageNo, Model model, HttpServletRequest request) {
        int page = NumberUtils.toInt(pageNo);
        int size = NumberUtils.toInt(pageSize);
        if (page < 1) page = 1;
        if (size < 1 || size > 50) size = 5;

        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        Page<Order> backGoodsApplyPage = tradeCenterUserClient.getBackGoodsApplyByUserId(sessionUserInfo.getId(), new Page<Order>(page, size));

        if (backGoodsApplyPage.getResult().size() > 0) {
            model.addAttribute("backGoodsApplyPageBar", PageProcessor.process(backGoodsApplyPage));
            model.addAttribute("backGoodsApplyPage", backGoodsApplyPage);
        }
        model.addAttribute("currentBackGoods", "apply");
        model.addAttribute("contentVm", "myinfo/backGoodsApplyList.vm");
        return "myinfo/myInfoLayout";
    }

    /**
     * 1、退货进度
     * 2、退货单信息
     * 3、退货单商品列表
     * 4、退货单历史记录
     */
    @RenderHeaderFooter
    @RequestMapping(value = "/my/backGoods/detail")
    public String toReturnsOrderDetails(@RequestParam("id") long id, Model model, HttpServletRequest request) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        try {
            BackGoods backGoods = tradeCenterUserClient.getBackGoodsDetails(id, sessionUserInfo.getId());
            List<BackGoodsLog> backGoodsLogs = tradeCenterUserClient.getBackGoodsLog(id);
            model.addAttribute("uploadFiles", this.parseUploadFiles(backGoods.getUploadFiles()));
            model.addAttribute("backGoods", backGoods);
            model.addAttribute("backGoodsItems", backGoods.getBackGoodsItemList());
            model.addAttribute("backGoodsLogs", backGoodsLogs);
            //退货进度
            model.addAttribute("progress", tradeCenterUserClient.getBackGoodsProgress(id));
        } catch (Exception e) {
            model.addAttribute("msg", e.getMessage());
        }

        model.addAttribute("contentVm", "myinfo/backGoodsDetail.vm");
        return "myinfo/myInfoLayout";
    }

    private Map<String, BackGoods.UploadFiles> parseUploadFiles(String uploadFiles) {
        Map<String, BackGoods.UploadFiles> map = new HashMap<String, BackGoods.UploadFiles>();
        try {
            for (String str : uploadFiles.split("\\>")) {
                String originalName = str.split("\\|")[1].substring(0, str.split("\\|")[1].lastIndexOf("."));
                String originalType = str.split("\\|")[1].substring(str.split("\\|")[1].lastIndexOf("."));
                map.put(str.split("\\|")[0], new BackGoods.UploadFiles(str.split("\\|")[1], originalName + "_10" + originalType));
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("发生异常，无法解析退单图片" + uploadFiles);
            }
        }
        return map;
    }

    @RequestMapping(value = "/my/cancelReturnsOrder")
    public void cancelReturnsOrder(@RequestParam("id") long id, HttpServletResponse response, HttpServletRequest request) throws IOException {
        SessionUserInfo sessionUserInfo = LoginInfo.getLoginUser(request);
        try {
            tradeCenterUserClient.cancelBackGoods(id, sessionUserInfo.getId());
            new JsonResult(true, "已取消").toJson(response);
            return;
        } catch (BackGoodsBaseException e) {
            new JsonResult(false, e.getMessage()).toJson(response);
            return;
        }
    }

    public String getSpacePictureSpacePath() {
        return spacePictureSpacePath;
    }

    public void setSpacePictureSpacePath(String spacePictureSpacePath) {
        this.spacePictureSpacePath = spacePictureSpacePath;
    }
}
