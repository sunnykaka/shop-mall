package com.kariqu.suppliersystem.logistics.web;

import com.kariqu.common.file.PictureValidateUtil;
import com.kariqu.common.uri.URLBrokerFactory;
import com.kariqu.suppliercenter.domain.DeliveryInfo;
import com.kariqu.suppliercenter.domain.LogisticsPrintInfo;
import com.kariqu.suppliercenter.domain.Supplier;
import com.kariqu.suppliercenter.domain.SupplierAccount;
import com.kariqu.suppliersystem.common.JsonResult;
import com.kariqu.suppliersystem.orderManager.web.BaseController;
import com.kariqu.suppliersystem.supplierManager.vo.SessionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * 物流设计页面功能处理
 *
 * @author:Wendy
 * @since:1.0.0 Date: 13-3-13
 * Time: 上午9:33
 */
@Controller
@RequestMapping("/logisticDesign")
public class LogisticDesignController extends BaseController {

    private final Log logger = LogFactory.getLog(LogisticDesignController.class);

    @Autowired
    private URLBrokerFactory urlBrokerFactory;


    private String logisticPicturePath;

    /**
     * 物流设计页面跳转
     * logisticsInfo
     *
     * @return
     */
    @RequestMapping(value = "/page")
    public String logisticDesign(Model model) {
        model.addAttribute("deliveryTypeList", DeliveryInfo.DeliveryType.values());
        return "logistics_design";
    }

    @RequestMapping(value="/company/list")
    public void logisticList(HttpServletRequest request,HttpServletResponse response) throws IOException {
        SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
        Supplier customer = supplierService.queryCustomerById(supplierAccount.getCustomerId()); //运营商家
        List<LogisticsPrintInfo> logisticsPrintInfoList = logisticsPrintInfoService.queryAllLogisticsPrintInfoByCustomerId(supplierAccount.getCustomerId());

        for(LogisticsPrintInfo li :  logisticsPrintInfoList){
            li.setDeliveryTypeName(li.getName().toDesc());
        }

        new JsonResult(JsonResult.SUCCESS).addData(JsonResult.RESULT_TYPE_LIST,logisticsPrintInfoList).addData(JsonResult.RESULT_TYPE_SINGLE_OBJECT,customer.getDefaultLogistics()).toJson(response);
    }

    /**
     * 添加物流公司
     * logisticsInfo
     *
     * @return
     */
    @RequestMapping(value = "/company/add")
    public void createLogisticsPrintInfo(LogisticsPrintInfo logisticsInfo, MultipartFile uploadFile, HttpServletResponse response, HttpServletRequest request) throws Exception {
        InputStream inputStream = null;
        try {
            SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
            String fileName = uploadFile.getOriginalFilename();
            if (!PictureValidateUtil.isPicture(fileName)) {
                logger.info("文件格式不对，请确保为图片格式");
                new JsonResult(JsonResult.FAILURE).setMsg("文件格式不对，请确保为图片格式").toJson(response);
                return;
            }
            String imageType = PictureValidateUtil.getFileType(fileName);
            imageType = imageType.toLowerCase();
            fileName = logisticsInfo.getName().name() + supplierAccount.getCustomerId() + imageType;

            if (savePicture(uploadFile.getInputStream(), fileName)) {
                String pictureLocalUrl = urlBrokerFactory.getUrl("LogisticUploadFile").addQueryData("fileName", fileName).toString();
                String printHtml = "LODOP.PRINT_INITA(0,0,1000,600,\"初始化打印控件\");\n" +
                        "LODOP.ADD_PRINT_SETUP_BKIMG(\"<img border='0' src='" + pictureLocalUrl + "'>\");";
                logisticsInfo.setPrintHtml(printHtml);
                logisticsInfo.setLogisticsPicturePath(pictureLocalUrl);
                logisticsInfo.setCustomerId(supplierAccount.getCustomerId());
                logisticsPrintInfoService.createLogisticsPrintInfo(logisticsInfo);

            }
        } catch (Exception e) {
            logger.error("上传图片失败",e);
            new JsonResult(JsonResult.FAILURE).setMsg("上传图片失败").toJson(response);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
            }
        }
        new JsonResult(JsonResult.SUCCESS).toJson(response);
        return;
    }


    /**
     * 删除打印物流信息
     *
     * @param id
     * @param response
     * @throws java.io.IOException
     */
    @RequestMapping(value = "/delete/{id}")
    public void deleteLogisticsPrintInfoById(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        try {
            logisticsPrintInfoService.deleteLogisticsPrintInfoById(id);
        } catch (Exception e) {
            logger.error("删除物流公司信息出错" + e);
            new JsonResult(false, "删除物流公司失败").toJson(response);
            return;
        }
        new JsonResult(true).toJson(response);
    }


    /**
     * 修改物流公司设计界面的信息
     * logisticsInfo
     *
     * @return
     */
    @RequestMapping(value = "/company/update/design_page")
    public void updatePrintHtml(LogisticsPrintInfo logisticsInfo,  HttpServletResponse response) throws IOException {
        try {
            LogisticsPrintInfo logisticsPrintInfo = logisticsPrintInfoService.queryLogisticsPrintInfoById(logisticsInfo.getId());
            String html = logisticsInfo.getPrintHtml();
            logisticsPrintInfo.setPrintHtml(html);
            logisticsPrintInfoService.updateLogisticsPrintInfo(logisticsPrintInfo);
        } catch (Exception e) {
            logger.error("商家管理修改物流信息出错",e);
        }
        new JsonResult(JsonResult.SUCCESS).toJson(response);
    }


    /**
     * 修改物流公司信息
     * logisticsInfo
     *
     * @return
     */
    @RequestMapping(value = "/company/update")
    public void updateLogisticsPrintInfo(LogisticsPrintInfo logisticsInfo,  MultipartFile uploadFile, HttpServletResponse response, HttpServletRequest request) throws IOException {
        try {
            LogisticsPrintInfo logisticsPrintInfo = logisticsPrintInfoService.queryLogisticsPrintInfoById(logisticsInfo.getId());
            logisticsPrintInfo.setLaw(logisticsInfo.getLaw());


            //如果有图片上传，即要更改物流单的图片
            if(uploadFile != null  && !uploadFile.isEmpty()){
                logger.info("更改物流公司信息时上传了图片");
                SupplierAccount supplierAccount = SessionUtils.getLoginAccount(request.getSession());
                String fileName = uploadFile.getOriginalFilename();
                if (!PictureValidateUtil.isPicture(fileName)) {
                    logger.info("文件格式不对，请确保为图片格式");
                    new JsonResult(JsonResult.FAILURE).setMsg("文件格式不对，请确保为图片格式").toJson(response);
                    return;
                }
                String imageType = PictureValidateUtil.getFileType(fileName);
                imageType = imageType.toLowerCase();
                fileName = logisticsPrintInfo.getName().name() + supplierAccount.getCustomerId() + imageType;
                if (savePicture(uploadFile.getInputStream(), fileName)) {
                    String pictureLocalUrl = urlBrokerFactory.getUrl("LogisticUploadFile").addQueryData("fileName", fileName).toString();
                    String printHtml = "LODOP.PRINT_INITA(0,0,1000,600,\"初始化打印控件\");\n" +
                            "LODOP.ADD_PRINT_SETUP_BKIMG(\"<img border='0' src='" + pictureLocalUrl + "'>\");";
                    logisticsPrintInfo.setPrintHtml(printHtml);
                    logisticsPrintInfo.setLogisticsPicturePath(pictureLocalUrl);
                }
            }
            logisticsPrintInfoService.updateLogisticsPrintInfo(logisticsPrintInfo);
        } catch (Exception e) {
            logger.error("商家管理修改物流递增规律出错",e);
        }
        new JsonResult(true).toJson(response);
    }


    /**
     * 查询物流界面里的打印代码  用于物流面单设计
     * logisticsInfo
     *
     * @return
     */
    @RequestMapping(value = "/company/{id}")
    public void queryLogisticsPrintInfoById(@PathVariable("id") int id, HttpServletResponse response) throws IOException {
        try {
            LogisticsPrintInfo logisticsPrintInfo = logisticsPrintInfoService.queryLogisticsPrintInfoById(id);
            if (logisticsPrintInfo.getPrintHtml() == null) {
                logisticsPrintInfo.setPrintHtml("");
            }
            //response.getWriter().write(logisticsPrintInfo.getPrintHtml());
            new JsonResult(JsonResult.SUCCESS).addData(JsonResult.RESULT_TYPE_SINGLE_OBJECT,logisticsPrintInfo.getPrintHtml()).toJson(response);
        } catch (Exception e) {
            logger.error("商家管理获取物流信息出错" , e);
        }
    }


    /*
    * 保存物流单设计里面的图片
    * @param
    * @param
    * @return
    */
    public boolean savePicture(InputStream inputStream, String fileName) {
        OutputStream outputStream = null;
        boolean upload;
        try {
            File pictureFile = new File(logisticPicturePath + fileName);
            File parentFile = new File(pictureFile.getParent());
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!pictureFile.exists()) {
                pictureFile.createNewFile();
            }
            outputStream = new FileOutputStream(pictureFile);

            int readBytes = 0;
            byte[] buffer = new byte[10000];
            while ((readBytes = inputStream.read(buffer, 0, 10000)) != -1) {
                outputStream.write(buffer, 0, readBytes);
            }
            upload = true;
        } catch (Exception e) {
            logger.error("图片写入失败" + e);
            upload = false;
        }finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {

            }
        }
        return upload;
    }

    public void setLogisticPicturePath(String logisticPicturePath) {
        this.logisticPicturePath = logisticPicturePath;
    }
}
