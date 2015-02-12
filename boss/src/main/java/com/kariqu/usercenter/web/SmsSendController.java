package com.kariqu.usercenter.web;

import com.kariqu.common.CheckUtils;
import com.kariqu.common.JsonResult;
import com.kariqu.usercenter.helper.SendContent;
import com.kariqu.usercenter.service.MessageTaskService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class SmsSendController {

    private final Log logger = LogFactory.getLog(SmsSendController.class);

    @Autowired
    private MessageTaskService messageTaskService;

    private boolean developMode;


    @RequestMapping(value = "/sms/send", method = RequestMethod.POST)
    public void smsSend(MultipartFile uploadFile, String mouldContent, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        try {
            if (!developMode) {
                List<SendContent> sendContentList = createSmsSend(uploadFile, mouldContent);
                for (SendContent sendContent : sendContentList) {
                    if (StringUtils.isEmpty(sendContent.getSendTime())) {
                        messageTaskService.sendSmsMessage(sendContent.getContactTels(), sendContent.getSmsContent());
                    } else {
                        messageTaskService.sendTimeSmsMessage(sendContent.getContactTels(), sendContent.getSmsContent(), sendContent.getSendTime());
                    }
                }
            }
            new JsonResult(true).toJson(response);
        } catch (Exception e) {
            logger.error("短信发送异常" + e);
            new JsonResult(false, "短息发送出错").toJson(response);
        }
    }

    @RequestMapping(value = "/sms/send/preview", method = RequestMethod.POST)
    public void smsSendPreview(MultipartFile uploadFile, String mouldContent, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        try {
            List<SendContent> sendContentList = createSmsSend(uploadFile, mouldContent);
            new JsonResult(true).addData("result", sendContentList).toJson(response);
        } catch (Exception e) {
            logger.error("短信预览异常" + e);
            new JsonResult(false, e.getMessage()).toJson(response);
        }
    }


    private List<SendContent> createSmsSend(MultipartFile uploadFile, String mouldContent) throws Exception {
        List<SendContent> sendContentList = new ArrayList<SendContent>();
        InputStream inp = null;
        try {
            if (!isExcel(uploadFile.getOriginalFilename())) {
                throw new Exception("该文件不是Excel文件，请重新选择");
            }
            inp = uploadFile.getInputStream();
            Workbook workbook = WorkbookFactory.create(inp);
            // 获取文件的指定工作表，默认为第一个
            Sheet sheet = workbook.getSheetAt(0);
            // 标题行
            Row row0 = sheet.getRow(sheet.getFirstRowNum());

            StringBuilder sbd = new StringBuilder();
            String content, diff, cellValue, cellName;
            boolean hasMobile;
            Row row;
            Cell cell;
            List<String> valueList = new ArrayList<String>();
            // 从第 2 行开始
            for (int i = sheet.getFirstRowNum() + 1; i < sheet.getLastRowNum() + 1; i++) {
                row = sheet.getRow(i);
                if (row == null) continue;

                SendContent sendContent = new SendContent();
                content = mouldContent.toLowerCase();
                hasMobile = false;
                sbd.delete(0, sbd.length());

                for (int j = row.getFirstCellNum(); j < row0.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    if (cell == null || StringUtils.isBlank(getCellDate(cell))) {
                        throw new Exception("第 " + (i + 1) + " 行, 第 " + (j + 1) + " 列的数据为空, 请检查数据是否正确");
                    }
                    //获取弟i行，第j列的值
                    cellValue = getCellDate(cell);

                    cellName = getCellDate(row0.getCell(j)).toLowerCase();
                    if (sbd.toString().contains(cellName)) {
                        throw new Exception("标题列 " + cellName + " 存在多个");
                    }
                    sbd.append(cellName);

                    // 手机号
                    if ("mobile".equalsIgnoreCase(cellName) && StringUtils.isNotBlank(cellValue)) {
                        if (!CheckUtils.checkPhone(cellValue)) {
                            throw new Exception("第 " + (i + 1) + " 行, 第 " + (j + 1) + " 列的数据并非手机号, 请检查数据是否合理");
                        }

                        hasMobile = true;
                        sendContent.setContactTels(cellValue);
                    }
                    // 发送时间
                    if ("sendTime".equalsIgnoreCase(cellName)) {
                        sendContent.setSendTime(cellValue);
                    }
                    // 发送内容
                    String name = "{#" + cellName + "#}";
                    if (content.contains(name)) {
                        valueList.add(name);
                        name = "\\{#" + cellName + "#\\}";
                        content = content.replaceAll(name, cellValue);
                    } else if (!"mobile".equalsIgnoreCase(cellName) && !"sendTime".equalsIgnoreCase(cellName)) {
                        throw new Exception("第 " + (j + 1) + " 列字符信息 " + cellName + " 多余");
                    }
                }
                if (!hasMobile) {
                    throw new Exception("Excel 文件里不存在 mobile(联系电话)字符信息");
                }

                diff = diff(count(mouldContent), valueList);
                if (StringUtils.isNotEmpty(diff)) {
                    throw new Exception("模板中需要的数据 Excel 文件中并未提供: " + diff);
                }
                sendContent.setSmsContent(content);
                sendContentList.add(sendContent);
            }
        } finally {
            try {
                if (inp != null) {
                    inp.close();
                }
            } catch (IOException e) {
                if (inp != null) {
                    inp = null;
                }
            }
        }
        return sendContentList;
    }

    private List<String> count(String content) {
        List<String> list = new ArrayList<String>();

        Matcher m = Pattern.compile("\\{#.*?#\\}").matcher(content);
        while (m.find()) {
            list.add(m.group().toLowerCase());
        }
        return list;
    }

    private String diff(List<String> materList, List<String> minorList) {
        StringBuilder sbd = new StringBuilder();
        for (String str : materList) {
            if (!minorList.contains(str)) {
                sbd.append(str).append(",");
            }
        }
        if (sbd.length() != 0) sbd.delete(sbd.length() - 1, sbd.length());

        return sbd.toString();
    }

    /**
     * 读取cell单元格的数据
     *
     * @param cell
     * @return
     */
    private String getCellDate(Cell cell) {
        String cellValue;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = Boolean.toString(cell.getBooleanCellValue());
                break;
            //数值
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = String.valueOf(cell.getDateCellValue());
                } else {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cellValue = cell.getStringCellValue().trim();
                    //判断是否包含小数点，如果不含小数点，则以字符串读取，如果含小数点，则转换为Double类型的字符串
                    if (cellValue.contains("."))
                        cellValue = String.valueOf(new Double(cellValue));
                }
                break;
            case Cell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue().trim();
                break;
            case Cell.CELL_TYPE_FORMULA:
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cellValue = cell.getStringCellValue().trim().replaceAll("#N/A", "");
                break;
            // 错误、空 及 默认, 都返回 空字符串
            case Cell.CELL_TYPE_ERROR:
            case Cell.CELL_TYPE_BLANK:
            default:
                cellValue = StringUtils.EMPTY;
                break;
        }
        return cellValue;
    }

    /**
     * 检查是否是Excel文件
     *
     * @param name
     * @return
     */
    public static boolean isExcel(String name) {
        List<String> pictureFile = Arrays.asList(".xls", ".xlsx");
        name = name.toLowerCase();
        for (String type : pictureFile) {
            if (name.endsWith(type))
                return true;
        }
        return false;
    }

    public void setDevelopMode(boolean developMode) {
        this.developMode = developMode;
    }
}
